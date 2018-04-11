package com.wanniu.core.util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.wanniu.core.logfs.Out;

/**
 * Class工具类.
 *
 * @author 小流氓(zhoumingkai@qeng.cn)
 */
public class ClassUtils {
	/** URL protocol for a file in the file system: "file" */
	private static final String URL_PROTOCOL_FILE = "file";
	/** URL protocol for an entry from a jar file: "jar" */
	private static final String URL_PROTOCOL_JAR = "jar";

	/**
	 * 使用当前线程的ClassLoader加载给定的类
	 * 
	 * @param className
	 *            类的全称
	 * @return 给定的类
	 */
	public static Class<?> loadClass(String className) {
		try {
			return Thread.currentThread().getContextClassLoader().loadClass(className);
		} catch (ClassNotFoundException e) {
		}
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
		}
		throw new RuntimeException("无法加载指定类" + className);
	}

	/**
	 * 创建一个指定类的对象,调用默认的构造函数.
	 * 
	 * @param <T>
	 *            Class
	 * @param klass
	 *            类
	 * @return 指定类的对象
	 */
	public static <T> T newInstance(final Class<T> klass) {
		try {
			return klass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("无法创建实例对象" + klass.getName(), e);
		}
	}

	public static void scanPackage(String packagePath, ResourceCallback callback) {
		// 处理一下包名到目录
		packagePath = packagePath.replace('.', '/').replace('\\', '/');
		if (!packagePath.endsWith("/")) {
			packagePath += "/";
		}

		try {
			Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(packagePath);
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				switch (url.getProtocol()) {
				// "file"
				case URL_PROTOCOL_FILE:
					doFindFileResources(packagePath, new File(url.getFile()), callback);
					break;
				// "jar"
				case URL_PROTOCOL_JAR:
					doFindJarResources(url, callback, packagePath);
					break;
				default:
					break;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("扫描过程中出异常啦", e);
		}
	}

	private static void doFindJarResources(URL url, ResourceCallback callback, String rootEntryPath)
			throws IOException {
		JarURLConnection jarCon = (JarURLConnection) url.openConnection();
		try (JarFile jarFile = jarCon.getJarFile()) {
			for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
				String entryPath = entries.nextElement().getName();
				if (entryPath.startsWith(rootEntryPath)) {
					findJarFile(entryPath, callback);
				}
			}
		}
	}

	/**
	 * 递归扫描目录文件.
	 */
	private static void doFindFileResources(String packagePath, File file, ResourceCallback callback) {
		String path = file.getAbsolutePath();

		// 这个目录不存在，忽略
		if (!file.exists()) {
			Out.debug("Skipping [", path, "] because it does not exist");
			return;
		}

		// 这个目录不可以读，忽略
		if (!file.canRead()) {
			Out.warn("Cannot search for matching files underneath directory [", path,
					"] because the application is not allowed to read the directory");
			return;
		}

		// 如果这是一个目录，继续向下找
		else if (file.isDirectory()) {
			findDir(packagePath, file, callback);
		}
		// 如果是一个文件，交作业
		else if (file.isFile()) {
			findFile(packagePath, file, callback);
		}
	}

	/**
	 * 查找到一个目录.
	 * 
	 * @param dir
	 *            文件
	 * @param callback
	 *            发现文件的回调接口
	 */
	private static void findDir(String packagePath, File dir, ResourceCallback callback) {
		File[] dirContents = dir.listFiles();

		// 目录下没有任何东东，忽略
		if (dirContents == null) {
			Out.warn("Could not retrieve contents of directory [", dir.getAbsolutePath(), "]");
			return;
		}

		for (File content : dirContents) {
			// 文件
			if (content.isFile()) {
				findFile(packagePath, content, callback);
			}
			// 是目录则继续
			else if (content.isDirectory()) {
				// 处理目录下的文件，需要把目录修正
				findDir(packagePath + content.getName() + "/", content, callback);
			}
		}
	}

	/**
	 * 查找到一个Jar文件.
	 * 
	 * @param entryPath
	 *            Jar的资源路径
	 * @param callback
	 *            发现文件的回调接口
	 */
	private static void findJarFile(String entryPath, ResourceCallback callback) {
		analysisResource(callback, entryPath);
	}

	/**
	 * 查找到一个文件.
	 * 
	 * @param packagePath
	 *            文件所在目录
	 * @param file
	 *            文件
	 * @param callback
	 *            发现文件的回调接口
	 */
	private static void findFile(String packagePath, File file, ResourceCallback callback) {
		analysisResource(callback, packagePath + file.getName());
	}

	private static void analysisResource(ResourceCallback callback, String resourceName) {
		// 忽略 package-info.class
		if ("package-info.class".equals(resourceName)) {
			return;
		}
		// 忽略非Class文件
		if (!resourceName.endsWith(".class")) {
			return;
		}
		// Class快速载入
		callback.handleResource(
				ClassUtils.loadClass(resourceName.substring(0, resourceName.length() - 6).replaceAll("[/\\\\]", ".")));
	}

	/**
	 * 扫描资源后的回调接口.
	 */
	public interface ResourceCallback {
		public void handleResource(Class<?> klass);
	}
}