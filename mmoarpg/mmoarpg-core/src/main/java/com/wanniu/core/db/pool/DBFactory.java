package com.wanniu.core.db.pool;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.sql.DataSource;

import com.wanniu.core.GGlobal;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.xml.XW3CParser;

/**
 * 连接池类厂，该类常用来保存多个数据源名称和数据库连接池对应的哈希
 * 
 * @author agui
 */
public final class DBFactory {

	/**
	 * 是否已初始化
	 */
	private static boolean inited;

	/**
	 * 该哈希表用来保存数据源名和连接池对象的关系表
	 */
	static Map<String, DBSource> dsPools = null;

	/**
	 * 从连接池工厂中获取指定名称对应的连接池对象
	 * 
	 * @param dataSource
	 *            连接池对象对应的名称
	 * @return DataSource 返回名称对应的连接池对象
	 * @throws NameNotFoundException
	 *             无法找到指定的连接池
	 */
	public static DBSource lookup(String dataSource) {
		DBSource ds=null;
		if (dsPools != null) {
			ds=dsPools.get(dataSource);
		}
		return ds;
	}

	/**
	 * 将指定的名字和数据库连接配置绑定在一起并初始化数据库连接池
	 * 
	 * @param name
	 *            对应连接池的名称
	 * @param param
	 *            连接池的配置参数，具体请见类ConnectionParam
	 * @return DataSource 如果绑定成功后返回连接池对象
	 * @throws NameAlreadyBoundException
	 *             如果name已经绑定则抛出该异常
	 * @throws ClassNotFoundException
	 *             无法找到连接池的配置中的驱动程序类
	 * @throws IllegalAccessException
	 *             连接池配置中的驱动程序类有误
	 * @throws InstantiationException
	 *             无法实例化驱动程序类
	 * @throws SQLException
	 *             无法正常连接指定的数据库
	 */
	public static DBSource start(String name, DBAttribute param) throws NameAlreadyBoundException, SQLException {
		if (lookup(name) != null) {
			throw new NameAlreadyBoundException(name);
		}
		DBSource source = new DBSource(param);
		if(dsPools==null){
			dsPools=new ConcurrentHashMap<>();
		}
		dsPools.put(name, source);
		return source;
	}

	/**
	 * 重新绑定数据库连接池
	 * 
	 * @param name
	 *            对应连接池的名称
	 * @param param
	 *            连接池的配置参数，具体请见类ConnectionParam
	 * @return DataSource 如果绑定成功后返回连接池对象
	 * @throws NameAlreadyBoundException
	 *             一定名字name已经绑定则抛出该异常
	 * @throws ClassNotFoundException
	 *             无法找到连接池的配置中的驱动程序类
	 * @throws IllegalAccessException
	 *             连接池配置中的驱动程序类有误
	 * @throws InstantiationException
	 *             无法实例化驱动程序类
	 * @throws SQLException
	 *             无法正常连接指定的数据库
	 */
	public static DBSource restart(String name, DBAttribute param) throws NameAlreadyBoundException, ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
		try {
			shutdown(name);
		} catch (Exception e) {
		}
		return start(name, param);
	}

	/**
	 * 删除一个数据库连接池对象
	 * 
	 * @param name
	 * @throws NameNotFoundException
	 */
	public static void shutdown(String name) throws NameNotFoundException {
		if(dsPools==null){
			return;
		}
		DataSource dataSource = lookup(name);

		if (dataSource!=null && dataSource instanceof DBSource) {
			DBSource datasource = (DBSource) dataSource;
			datasource.shutdown();
			datasource = null;
		}

		dsPools.remove(name);
	}

	/**
	 * 从配置文件加载数据源
	 * 
	 * @param props
	 */
	public synchronized static void init() {
		if (!inited) {
			// 读取数据库配置文件
			dsPools = new ConcurrentHashMap<String, DBSource>(2, 0.75F);
			List<DBAttribute> drivers = null;
			try {
				drivers = XW3CParser.parse(GGlobal.FILE_CONF_DS, DBAttribute.class);
			} catch (Exception e) {
				Out.error("读取连接池配置文件错误 -> " , GGlobal.FILE_CONF_DS);
				e.printStackTrace();
				System.exit(-1);
			}

			if (drivers.size() == 0) {
				Out.warn("连接池配置文件中未配置 -> " , GGlobal.FILE_CONF_DS);
				return;
			}

			StringBuilder dsInfo = new StringBuilder();
			DBAttribute cp = null;

			for (int i = 0; i < drivers.size(); i++) {
				try {
					cp = drivers.get(i);
					start(cp.getDsName(), cp);
					dsInfo.append(cp.getDsName());

					if (i < drivers.size() - 1) {
						dsInfo.append(" & ");
					}
				} catch (Exception e) {
					e.printStackTrace();
					Out.error("绑定连接池 [" , drivers.get(i).getDsName() , "] 错误！");
					System.exit(-1);
				}
			}
			inited = true;
			Out.info(String.format("\n\n初始化成功连接池数量：%d\n数据库连接池【  %s 】\n", drivers.size(), dsInfo.toString()));
		}
	}

}