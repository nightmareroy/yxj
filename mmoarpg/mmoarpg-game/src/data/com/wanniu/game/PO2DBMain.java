package com.wanniu.game;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import com.wanniu.core.db.handler.DBHandler;
import com.wanniu.core.game.entity.GEntity;
import com.wanniu.core.logfs.Out;

/**
 * @author agui
 *
 */
public class PO2DBMain {

	private static final String MODEL_PACKAGE = "com.wanniu.game.poes";

	private static final String HOST = "192.168.102.84";
	private static final int PORT = 3306;
	private static final String DB_NAME = "xmds_game";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "root";

	private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME + "?useSSL=true";

	public static void main(String[] args) throws Exception {
		Out.setting();
		Set<String> tables = existsTables();

		doDropTables(tables); // 清表重建

		Set<Class<?>> classes = getClasses(MODEL_PACKAGE);
		doCreateTable(tables, classes);
		System.exit(1);
	}

	static void doDropTables(Set<String> tables) throws InterruptedException {
		CountDownLatch down = new CountDownLatch(tables.size());
		for (String table : tables) {
			new Thread(() -> {
				execute("DROP TABLE " + table);
				down.countDown();
			}).start();
		}
		down.await();
		tables.clear();
	}

	private static boolean isEntity(Class<?> clz) {
		if (clz.getSuperclass() == GEntity.class) {
			return true;
		}
		Class<?>[] clzs = clz.getInterfaces();
		for (Class<?> c : clzs) {
			if (c == GEntity.class) {
				return true;
			}
		}
		return false;
	}

	private static void doCreateTable(Set<String> tables, Set<Class<?>> classes) {
		for (Class<?> clz : classes) {
			if (!isEntity(clz)) {
				Out.error(clz.getName() , " is not Entity PO");
				continue;
			}
			boolean isPlayerTable = clz.isAnnotationPresent(DBTable.class);
			String tableName = classNameToDBName(clz.getSimpleName());
			if (isPlayerTable) {
				tableName = clz.getAnnotation(DBTable.class).value();
				if (tableName.startsWith("tb")) {
					Out.warn(tableName, " must be remove tb prefix!!!");
				} else {
					tableName = "tb_" + tableName;
				}
			}
			Field[] fields = clz.getFields();
			StringBuilder builder = new StringBuilder();
			Map<String, DBField> map = getFieldAnnotion(clz);
			/** 验证是否存在 */
			if (!tables.contains(tableName)) {
				String primaryKey = null;
				try {
					primaryKey = getPrimaryKey(clz, isPlayerTable);
				} catch (Exception e) {
					Out.error(e);
					continue;
				}
				builder.append("CREATE TABLE IF NOT EXISTS " + "`" + tableName + "`" + " (");
				if (isPlayerTable && primaryKey == null) {
					primaryKey = "playerId";
					builder.append("`" + primaryKey + "` char(36) NOT NULL, ");
				}
				for (int i = 0; i < fields.length; i++) {
					Field field = fields[i];
					String fieldName = field.getName();
					String comment = "";
					DBField df = map.get(fieldName);
					if (df != null) {
						if (!df.include()) {
							continue;
						}
						if (!"".equals(df.comment())) {
							comment = " COMMENT '" + df.comment() + "'";
						}
					}
					String fieldType = getFieldType(field);
					builder.append("`" + classFieldToDBField(fieldName) + "` " + fieldType + comment + ",");
				}
				builder.append(" PRIMARY KEY (`" + classFieldToDBField(primaryKey) + "`)");
				builder.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;");
			} else {
				List<Field> addFields = getAddFields(tableName, fields);
				if (addFields.size() > 0) {
					builder.append("ALTER TABLE " + tableName + " ADD COLUMN ");
					boolean alter = false;
					for (int i = 0; i < addFields.size(); i++) {
						Field field = addFields.get(i);
						String fieldName = field.getName();
						String fieldType = getFieldType(field);
						String comment = "";
						DBField df = map.get(fieldName);
						if (df != null) {
							if (!df.include()) {
								continue;
							}
							if (!"".equals(df.comment())) {
								comment = " COMMENT '" + df.comment() + "'";
							}
						}
						if (i == addFields.size() - 1) {
							builder.append("`" + classFieldToDBField(fieldName) + "` " + fieldType + comment);
						} else {
							builder.append("`" + classFieldToDBField(fieldName) + "` " + fieldType + comment + ",");
						}
						alter = true;
					}
					if(!alter) {
						builder.setLength(0);
					}
				}
			}
			if (builder.length() > 0) {
				execute(builder.toString());
			}
		}
	}

	/**
	 * 比较表(若持久化类里面包含但是mysql中不包含则返回需要新增的字段名称)
	 * 
	 * @param tableName
	 * @param fields
	 * @return
	 */
	private static List<Field> getAddFields(String tableName, Field[] fields) {
		List<Field> addFields = new ArrayList<Field>();
		List<String> tableCols = getTableFields(tableName);
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			String fieldName = field.getName();
			fieldName = classFieldToDBField(fieldName);
			if (!tableCols.contains(fieldName)) {
				addFields.add(field);
			}
		}
		return addFields;
	}

	private static void execute(String sql) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			ps = conn.prepareStatement(sql);
			ps.executeUpdate(sql);
		} catch (Exception e) {
			Out.error(e, sql);
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					Out.error(e);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					Out.error(e);
				}
			}
		}
		System.out.println(sql);
	}

	private static Set<Class<?>> getClasses(String pkage) {
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
		boolean recursive = true;// 是否循环迭代
		String packageName = pkage;
		String packageDirName = packageName.replace('.', '/');
		/** 定义一个枚举的集合 并进行循环来处理这个目录下的things */
		Enumeration<URL> dirs;
		try {
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			boolean isHas = false;
			while (dirs.hasMoreElements()) {
				isHas = true;
				URL url = dirs.nextElement();
				String protocol = url.getProtocol();
				if ("file".equals(protocol)) {// 如果是以文件的形式保存在服务器上
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					/** 以文件的方式扫描整个包下的文件 并添加到集合中 */
					findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
				}
			}
			if (!isHas) {
				Out.error("包:" , pkage , " 下找不到类文件,请检查是否存在");
			}
		} catch (IOException e) {
			Out.error(e);
		}
		return classes;
	}

	/**
	 * 以文件的形式获取包下的所有Class
	 * 
	 * @param packageName
	 * @param packagePath
	 * @param recursive
	 * @param classes
	 */
	private static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, Set<Class<?>> classes) {
		File dir = new File(packagePath);
		if (!dir.exists() || !dir.isDirectory()) {
			Out.error("用户定义包名 " , packageName , " 找不到或者不是一个目录");
			return;
		}
		FileFilter filter = new FileFilter() {
			@Override
			public boolean accept(File file) {
				return ((recursive && file.isDirectory()) || (file.getName().endsWith(".class"))) && (file.getName().indexOf("$") == -1);// 不包含内部类
			}
		};
		File[] dirfiles = dir.listFiles(filter);

		/** 循环所有文件 */
		for (File file : dirfiles) {
			if (file.isDirectory()) {// 如果是目录 则继续扫描
				findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
			} else {// 如果是java类文件 去掉后面的.class 只留下类名
				String className = file.getName().substring(0, file.getName().length() - 6);
				try {
					classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
				} catch (ClassNotFoundException e) {
					Out.error("添加用户自定义视图类错误 找不到此类的.class文件");
					Out.error(e);
				}
			}
		}
	}

	private static String classNameToDBName(String className) {
		if (className.endsWith("PO")) {
			int lastIndex = className.lastIndexOf("PO");
			className = className.substring(0, lastIndex);
		}
		return "tb" + tableName(className);
	}

	/**
	 * 类字段转成数据库字段名
	 * 
	 * @param str
	 * @return
	 */
	private static String tableName(String str) {
		StringBuilder sb = new StringBuilder();
		if (str != null) {
			for (int i = 0; i < str.length(); i++) {
				char c = str.charAt(i);
				if (Character.isUpperCase(c)) {
					sb.append("_");
					sb.append(Character.toLowerCase(c));
				} else {
					sb.append(c);
				}
			}
		}
		return sb.toString();
	}

	private static String classFieldToDBField(String str) {
		return str;
	}

	/**
	 * 获得类每个字段字段名和该字段上的注解
	 * 
	 * @param cl
	 * @return
	 */
	private static Map<String, DBField> getFieldAnnotion(Class<?> cl) {
		Map<String, DBField> map = new HashMap<String, DBField>();
		Field[] fields = cl.getFields();
		for (Field field : fields) {
			DBField dbf = field.getAnnotation(DBField.class);
			if (dbf != null) {
				map.put(field.getName(), dbf);
			}
		}
		return map;
	}

	/**
	 * 获得类的主键
	 * 
	 * @param cl
	 * @return
	 * @throws Exception
	 */
	private static String getPrimaryKey(Class<?> cl, boolean playerTable) throws Exception {
		Field[] fields = cl.getFields();
		for (Field field : fields) {
			boolean isKey = false;
			if (field.getAnnotation(DBField.class) != null) {
				isKey = field.getAnnotation(DBField.class).isPKey();
			}

			if (isKey) {
				return field.getName();
			}
		}
		if (!playerTable) {
			throw new Exception("类: " + cl.getName() + " non-existent primaryKey");
		}
		return null;
	}

	/**
	 * 获得类的字段类型
	 * 
	 * @param field
	 * @return
	 */
	private static String getFieldType(Field field) {
		DBField df = field.getAnnotation(DBField.class);
		if (df == null || "".equals(df.fieldType())) {
			return getSelfFieldType(field);
		}
		return df.fieldType().indexOf("blob") >= 0 ? df.fieldType() : df.fieldType() + "(" + df.size() + ")";
	}

	/**
	 * 获取自身的字段类型
	 * 
	 * @param field
	 * @return
	 */
	private static String getSelfFieldType(Field field) {
		String fieldType = field.getType().getSimpleName();
		String type = null;
		if (fieldType.equalsIgnoreCase("Byte") || fieldType.equalsIgnoreCase("Short") || fieldType.equalsIgnoreCase("int")
				|| fieldType.equalsIgnoreCase("Integer")) {
			type = "int(11) NOT NULL default 0";
		} else if (fieldType.equalsIgnoreCase("Float")) {
			type = "float(3) NOT NULL default 0";
		} else if (fieldType.equals("String")) {
			DBField df = field.getAnnotation(DBField.class);
			int size = 500;
			if (df != null) {
				if (df.size() > 0) {
					size = df.size();
				} else if (df.isPKey()) {
					size = 64;
				}
			}
			type = "varchar(" + size + ")";
		} else if (fieldType.equals("Date")) {
			type = "datetime";
		} else if (fieldType.equalsIgnoreCase("Long")) {
			type = "bigint NOT NULL default 0";
		} else if (fieldType.equals("Double")) {
			type = "double(6) NOT NULL default 0";
		} else
			type = "blob";

		return type;
	}

	/**
	 * 查询已经存在的表的所有字段
	 * 
	 * @param url
	 * @param tableName
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private static List<String> getTableFields(String tableName) {
		List<String> fieldNames = new ArrayList<String>();
		String queryAllTableFieldsSql = "show full fields FROM " + tableName;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			pstmt = conn.prepareStatement(queryAllTableFieldsSql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				fieldNames.add(rs.getString(1));
			}
		} catch (Exception e) {
			Out.error(e);
		} finally {
			DBHandler.free(rs, pstmt, conn);
		}
		return fieldNames;
	}

	/**
	 * 验证表是否存在
	 * 
	 * @param userId
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	private static Set<String> existsTables() {
		Set<String> tableNames = new HashSet<>();
		Connection conn = null;
		PreparedStatement pstms = null;
		ResultSet rs = null;
		try {
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			pstms = conn.prepareStatement("SHOW tables");
			rs = pstms.executeQuery();
			while (rs.next()) {
				tableNames.add(rs.getString(1));
			}
		} catch (Exception e) {
			Out.error(e);
		} finally {
			DBHandler.free(rs, pstms, conn);
		}
		return tableNames;
	}
}
