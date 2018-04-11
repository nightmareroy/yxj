package com.wanniu.core.db.handler;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanniu.core.db.GDao;
import com.wanniu.core.logfs.Out;

/**
 * 数据库持久化操纵基类
 * @author agui
 */
public abstract class DBHandler implements  Runnable {

	private static Map<String, Boolean> TABLE_STATUS = new HashMap<String, Boolean>();

	protected DBHandler() {
	}

	public static void free(ResultSet rs, PreparedStatement pstmt, Connection conn) {
		close(rs);
		close(pstmt);
		close(conn);
	}

	public static void free(ResultSet rs, CallableStatement cs, Connection conn) {
		close(rs);
		close(cs);
		close(conn);
	}
	
	protected static void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	protected static void close(PreparedStatement pstmt) {
		if (pstmt != null) {
			try {
				pstmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	protected static void close(CallableStatement cs) {
		if (cs != null) {
			try {
				cs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	protected static void close(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean checkTable(String tableName) {
		Boolean exist = TABLE_STATUS.get(tableName);
		if (exist != null && exist) {
			return true;
		}
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = GDao.getSlave();
			DatabaseMetaData metaData = conn.getMetaData();
			if (tableName.indexOf(".") > -1) {
				rs = metaData.getTables(tableName.substring(0, tableName.indexOf('.')), null,
						tableName.substring(tableName.indexOf('.')), null);
			} else {
				rs = metaData.getTables(null, null, tableName, null);
			}
			if (rs.next()) {
				TABLE_STATUS.put(tableName, true);
				return true;
			}
		} catch (SQLException e) {
			Out.info(e);
		} finally {
			free(rs, null, conn);
		}
		return false;
	}

	public static void putTable(String tableName, boolean exist) {
		TABLE_STATUS.put(tableName, exist);
	}

	public static void createTable(String sql) {
		execute(sql);
	}
	
	private static ISQLExceptionHandler __exception__;
	public static void setExceptionHandler(ISQLExceptionHandler exceptionHandler) {
		__exception__ = exceptionHandler;
	}
	
	public static void exceptionSQL(String logName, Exception e){
		if(__exception__ != null) {
			__exception__.exceptionSQL(logName, e);
		} else {
			Out.error(logName, e);
		}
	}
	
	public static void exceptionSQL(SQLException e) {
		if(__exception__ != null) {
			__exception__.exceptionSQL(e);
		} else {
			e.printStackTrace();
		}
	}

	public static int execute(String sql) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = GDao.getMaster();
			pstmt = conn.prepareStatement(sql);
			return pstmt.executeUpdate();
		} catch (SQLException e) {
			exceptionSQL(e);
		} finally {
			free(null, pstmt, conn);
		}
		return -1;
	}
	
	/**
	 * 变更
	 */
	public static int update(String sql, Object... args) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = GDao.getMaster();
			pstmt = conn.prepareStatement(sql);
			setParams(pstmt, args);
			return pstmt.executeUpdate();
		} catch (SQLException e) {
			exceptionSQL(e);
		} finally {
			free(null, pstmt, conn);
		}
		return -1;
	}
	
	/**
	 * 变更
	 */
	public static int update(String sql, ParamMapper param) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = GDao.getMaster();
			pstmt = conn.prepareStatement(sql);
			param.setParams(pstmt, 0);
			return pstmt.executeUpdate();
		} catch (SQLException e) {
			exceptionSQL(e);
		} finally {
			free(null, pstmt, conn);
		}
		return -1;
	}

	/**
	 * 保存并生产主键
	 */
	public static int generated(String sql, Object... args) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = GDao.getMaster();
			pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			setParams(pstmt, args);
			if(pstmt.executeUpdate() > 0) {
				rs = pstmt.getGeneratedKeys();
				if (rs.next()) {
					return rs.getInt(1);
				}
			}
		} catch (SQLException e) {
			exceptionSQL(e);
		} finally {
			free(rs, pstmt, conn);
		}
		return -1;
	}
	
	/**
	 * 保存并生产主键
	 */
	public static int generated(String sql, ParamMapper param) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = GDao.getMaster();
			pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			param.setParams(pstmt, 0);
			if(pstmt.executeUpdate() > 0) {
				rs = pstmt.getGeneratedKeys();
				if (rs.next()) {
					return rs.getInt(1);
				}
			}
		} catch (SQLException e) {
			exceptionSQL(e);
		} finally {
			free(rs, pstmt, conn);
		}
		return -1;
	}
	
	/**
	 * 保存或者更新
	 * return 1:保存，0：更新
	 */
	public static int saveOrUpdate(ISaveOrUpdate iSaveOrUpdate) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = GDao.getMaster();
			pstmt = conn.prepareStatement(iSaveOrUpdate.getUpdateSQL());
			setParams(pstmt, iSaveOrUpdate.getUpdateParams());
			if (pstmt.executeUpdate() == 0) {
				close(pstmt);
				pstmt = conn.prepareStatement(iSaveOrUpdate.getSaveSQL());
				setParams(pstmt, iSaveOrUpdate.getSaveParams());
				return pstmt.executeUpdate();
			}
		} catch (SQLException e) {
			exceptionSQL(e);
		} finally {
			free(null, pstmt, conn);
		}
		return 0;
	}
	
	/**
	 * 批量变更
	 */
	public static void batchUpdate(String sql, List<Object[]> batch) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = GDao.getMaster();
			pstmt = conn.prepareStatement(sql);
			for (Object[] par : batch) {
				setParams(pstmt, par);
				pstmt.addBatch();
			}
			pstmt.executeBatch();
		} catch (SQLException e) {
			exceptionSQL(e);
		} finally {
			free(null, pstmt, conn);
		}
	}
	
	/**
	 * 批量变更
	 */
	public static void batchUpdate(String sql, ParamMapper mapper,  int size) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = GDao.getMaster();
			pstmt = conn.prepareStatement(sql);
			for (int row = 0; row < size; row++) {
				mapper.setParams(pstmt, row);
				pstmt.addBatch();
				if (row % 500 == 0) {
					pstmt.executeBatch();
					pstmt.clearBatch();
				}
			}
			if(size % 500 != 0) {
				pstmt.executeBatch();
				pstmt.clearBatch();
			}
		} catch (SQLException e) {
			exceptionSQL(e);
		} finally {
			free(null, pstmt, conn);
		}
	}
	
	/**
	 * 查询集合
	 */
	public static <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... args) {
		List<T> values = new ArrayList<T>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = GDao.getMaster();
			pstmt = conn.prepareStatement(sql);
			setParams(pstmt, args);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				values.add(rowMapper.mapRow(rs));
			}
		} catch (SQLException e) {
			exceptionSQL(e);
		} finally {
			free(rs, pstmt, conn);
		}
		return values;
	}
	
	/**
	 * 查询集合，自定义PreparedStatement的参数<br>
	 * 需要重载setParams方法
	 */
	public static <T> List<T> query(String sql, RowMapper<T> rowMapper) {
		List<T> values = new ArrayList<T>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = GDao.getSlave();
			pstmt = conn.prepareStatement(sql);
			rowMapper.setParams(pstmt);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				values.add(rowMapper.mapRow(rs));
			}
		} catch (SQLException e) {
			exceptionSQL(e);
		} finally {
			free(rs, pstmt, conn);
		}
		return values;
	}
	
	/**
	 * 查询集合
	 */
	public static void query(String sql, RowReader rowReader, Object... args) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = GDao.getSlave();
			pstmt = conn.prepareStatement(sql);
			setParams(pstmt, args);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				rowReader.mapRow(rs);
			}
		} catch (SQLException e) {
			exceptionSQL(e);
		} finally {
			free(rs, pstmt, conn);
		}
	}
	
	/**
	 * 查询集合，自定义PreparedStatement的参数<br>
	 * 需要重载setParams方法
	 */
	public static void query(String sql, RowReader rowReader) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = GDao.getSlave();
			pstmt = conn.prepareStatement(sql);
			rowReader.setParams(pstmt);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				rowReader.mapRow(rs);
			}
		} catch (SQLException e) {
			exceptionSQL(e);
		} finally {
			free(rs, pstmt, conn);
		}
	}

	public static int queryForInt(String sql, RowMapper<Integer> rowMapper) {
		Integer value = queryForObject(sql, rowMapper);
		return value == null ? 0 :  value.intValue();
	}
	
	public static int queryForInt(String sql, RowMapper<Integer> rowMapper, Object... args) {
		Integer value = queryForObject(sql, rowMapper, args);
		return value == null ? 0 :  value.intValue();
	}
	
	/**
	 * 查询单体
	 */
	public static <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = GDao.getSlave();
			pstmt = conn.prepareStatement(sql);
			setParams(pstmt, args);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rowMapper.mapRow(rs);
			}
		} catch (SQLException e) {
			exceptionSQL(e);
		} finally {
			free(rs, pstmt, conn);
		}
		return null;
	}
	
	/**
	 * 查询单体，自定义PreparedStatement的参数<br>
	 * 需要重载setParams方法
	 */
	public static <T> T queryForObject(String sql, RowMapper<T> rowMapper) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = GDao.getSlave();
			pstmt = conn.prepareStatement(sql);
			rowMapper.setParams(pstmt);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rowMapper.mapRow(rs);
			}
		} catch (SQLException e) {
			exceptionSQL(e);
		} finally {
			free(rs, pstmt, conn);
		}
		return null;
	}

	private static void setParams(PreparedStatement pstmt, Object[] args) throws SQLException {
		if(args == null) return;
		for (int i = 0; i < args.length; i++) {
			if (args[i] instanceof Integer) {
				pstmt.setInt(i + 1, (Integer) args[i]);
			} else if (args[i] instanceof String) {
				pstmt.setString(i + 1, (String) args[i]);
			} else if (args[i] instanceof Byte) {
				pstmt.setByte(i + 1, (Byte) args[i]);
			}  else if (args[i] instanceof Boolean) {
				pstmt.setByte(i + 1, (byte) (((Boolean) args[i]) ? 1 : 0));
			} else if (args[i] instanceof Short) {
				pstmt.setShort(i + 1, (Short) args[i]);
			} else if (args[i] instanceof Timestamp) {
				pstmt.setTimestamp(i + 1, (Timestamp) args[i]);
			} else if (args[i] instanceof Date) {
				pstmt.setDate(i + 1, new java.sql.Date(((Date) args[i]).getTime()));
			} else if (args[i] instanceof java.sql.Date) {
				pstmt.setDate(i + 1, new java.sql.Date(((Date) args[i]).getTime()));
			} else if (args[i] instanceof Double) {
				pstmt.setDouble(i + 1, (Double) args[i]);
			} else if (args[i] instanceof byte[]) {
				pstmt.setBytes(i + 1, (byte[]) args[i]);
			} else if (args[i] instanceof Long) {
				pstmt.setLong(i + 1, (Long) args[i]);
			} else {
				if(args[i] != null) {
					Out.warn("暂未支持的持久化数据类型，请确认：", args[i].getClass());
				}
				pstmt.setObject(i + 1, args[i]);
			}
		}
	}
	
	public static boolean supportsBatchUpdates(Connection conn) throws SQLException {
		DatabaseMetaData dbmd = conn.getMetaData();
		if (dbmd != null) {
			if (dbmd.supportsBatchUpdates()) {
				return true;
			}
		}
		return false;
	}
	
}
