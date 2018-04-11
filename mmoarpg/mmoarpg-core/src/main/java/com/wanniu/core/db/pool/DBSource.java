package com.wanniu.core.db.pool;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.wanniu.core.GSystem;
import com.wanniu.core.logfs.Out;

/**
 * 实现了接口javax.sql.DataSource的类，该类维护着一个连接池的对象
 * 
 * @author agui
 */
public final class DBSource implements DataSource {

	private final DBAttribute dbAttribute;

	private List<DBConn> pool = new ArrayList<DBConn>();

	public final Map<String, String> sqls = new HashMap<String, String>();
	
	private ScheduledFuture<?> timer;

	/**
	 * 构造函数
	 * 
	 * @param dbAttribute
	 * @throws SQLException 
	 */
	public DBSource(DBAttribute dbAttribute) throws SQLException {
		this.dbAttribute = dbAttribute;
		this.initPool();
		if (dbAttribute.isCheckAble()) {
			timer = GSystem.addFixedRateJob(new Runnable() {
				@Override
				public void run() {
					check();
				}
			}, dbAttribute.getCheckInterval(), dbAttribute.getCheckInterval());
		}
	}

	/**
	 * 检查空闲连接，如果空闲超时就关闭此连接
	 */
	private void check() {
		// 避免在关闭的空闲连接的时候，其它线程获取了这个关闭的连接
		synchronized (pool) {
			Iterator<DBConn> it = pool.iterator();
			while (it.hasNext()) {
				DBConn con = it.next();
				// 当前时间离此连接的最后访问时间间隔大于超时值，就关闭此连接并从池里面删除
				if (System.currentTimeMillis() - con.lastAccessTime > dbAttribute.getTimeout()) {
					if (pool.size() <= dbAttribute.getMinConnection())
						break;
					if (con.isUse && pool.size() <= dbAttribute.getMaxConnection() / 2)
						continue;
					try {
						con.internalClose();
						if (con.isUse) {
							Out.warn("强制关闭未正常关闭数据库连接：" , con.getSQL());
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
					it.remove();
				}
			}
			ping();
		}
	}

	private void ping() {
		for (DBConn _conn : pool) {
			if (!_conn.isUse && System.currentTimeMillis() - _conn.lastAccessTime >= dbAttribute.getCheckInterval()) {
				PreparedStatement stmt = null;
				ResultSet rs = null;
				_conn.isUse = true;
				try {
					stmt = _conn.prepareStatement("SELECT CURRENT_DATE");
					rs = stmt.executeQuery();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					if (rs != null) {
						try {
							rs.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					if (stmt != null) {
						try {
							stmt.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					_conn.isUse = false;
				}
			}
		}
	}

	public synchronized Connection getConnection(String user, String password) throws SQLException {
		// 首先从连接池中找出空闲的对象
		Connection conn = getFreeConnection(0);
		if (conn == null) {
			// 判断是否超过最大连接数,如果超过最大连接数
			// 则等待一定时间查看是否有空闲连接,否则抛出异常告诉用户无可用连接
			if (getConnectionCount() >= dbAttribute.getMaxConnection()) {
				conn = getFreeConnection(dbAttribute.getWaitTime());
			} else {
				// 没有超过连接数，重新获取一个数据库的连接
				conn = createConnection(dbAttribute, true);
			}
		}
		return conn;
	}

	/**
	 * 获取连接总数
	 * 
	 * @return
	 */
	private int getConnectionCount() {
		return pool.size();
	}

	/**
	 * 初始化连接池
	 * 
	 * @throws SQLException
	 */
	protected void initPool() throws SQLException {
		try {
			Class.forName(dbAttribute.getDriver());
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		for (int i = 0; i < dbAttribute.getMinConnection(); i++) {
			createConnection(dbAttribute, false);
		}
	}

	/**
	 * 创建一个新的连接并入池,返回代理连接
	 * 
	 * @param connParam
	 * @param inUse
	 *            此新建连接是否在使用
	 * @return
	 * @throws SQLException
	 */
	protected Connection createConnection(DBAttribute connParam, boolean inUse) throws SQLException {
		Connection conn = DriverManager.getConnection(connParam.getUrl(), connParam.getUserName(), connParam.getPassword());
		// 代理将要返回的连接对象
		DBConn _conn = new DBConn(conn, inUse);
		synchronized (pool) {
			pool.add(_conn);
		}
		return _conn;
	}

	/**
	 * 关闭该连接池中的所有数据库连接 并取消检查空闲连接的时钟
	 * 
	 * @throws SQLException
	 */
	public void shutdown() {
		if (timer != null) {
			timer.cancel(true);
		}

		for (DBConn _conn : pool) {
			try {
				_conn.internalClose();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		pool.clear();
	}

	/**
	 * 从连接池中取一个空闲的连接
	 * 
	 * @param waitTime
	 *            如果该参数值为0则没有连接时只是返回一个null 否则的话等待nTimeout毫秒看是否还有空闲连接，如果没有抛出异常
	 * @return Connection
	 * @throws SQLException
	 */
	protected Connection getFreeConnection(long waitTime) throws SQLException {
		Connection conn = null;
		for (DBConn _conn : pool) {
			if (!_conn.isUse) {
				_conn.isUse = true;
				_conn.lastAccessTime = System.currentTimeMillis();
				return _conn;
			}
		}

		if (conn == null && waitTime > 0) {
			// 等待nTimeout毫秒以便看是否有空闲连接
			if (waitTime > 1) {
				check();
				Out.warn("尝试进行一次可用连接检测...");
				return getFreeConnection(1);
			}
			try {
				Out.error("等待" , dbAttribute.getWaitTime() , "毫秒后尝试再获取可用连接...");
				Thread.sleep(dbAttribute.getWaitTime());
			} catch (Exception e) {
			}
			conn = getFreeConnection(0);
			if (conn == null) {
				throw new SQLException("没有可用的数据库连接");
			}
		}
		return conn;
	}

	public Connection getConnection() throws SQLException {
		return getConnection(dbAttribute.getUserName(), dbAttribute.getPassword());
	}

	public PrintWriter getLogWriter() throws SQLException {
		return null;
	}

	public void setLogWriter(PrintWriter out) throws SQLException {

	}

	public void setLoginTimeout(int seconds) throws SQLException {
		dbAttribute.setWaitTime(seconds);
	}

	public int getLoginTimeout() throws SQLException {
		return dbAttribute.getWaitTime();
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

}
