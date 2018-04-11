package com.wanniu.core.game;

import java.io.File;
import java.io.FileFilter;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.wanniu.core.GServer;
import com.wanniu.core.game.request.GClientEvent;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.tcp.protocol.NetHandler;

public final class GHandlers {

	public static void init(String pakName) {
		Out.info("搜索客户端处理句柄路径 -> ", pakName);
		try {
			Enumeration<URL> urls = GHandlers.class.getClassLoader().getResources(pakName.replace(".", "/"));
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				if (url != null) {
					String protocol = url.getProtocol();
					if (protocol.equals("file")) {
						String pakPath = URLDecoder.decode(url.getPath(), "UTF-8");
						addClassByAnnotation(pakPath, pakName);
					} else if (protocol.equals("jar")) {
						JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
						JarFile jarFile = jarURLConnection.getJarFile();
						Enumeration<JarEntry> jarEntries = jarFile.entries();
						while (jarEntries.hasMoreElements()) {
							JarEntry jarEntry = jarEntries.nextElement();
							String jarEntryName = jarEntry.getName();
							if (jarEntryName.endsWith(".class")) {
								String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
									if(className.startsWith(pakName)) {
									Class<?> cls = Class.forName(className);
									if (cls.isAnnotationPresent(GClientEvent.class)) {
										GServer.getInstance().registerHandler((NetHandler) cls.newInstance());
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void addClassByAnnotation(String pakPath, String pakName) {
		try {
			File[] files = getClassFiles(pakPath);
			if (files != null) {
				for (File file : files) {
					String fileName = file.getName();
					if (file.isFile()) {
						String className = getClassName(pakName, fileName);
						if(className.startsWith(pakName)) {
							Class<?> cls = Class.forName(className);
							if (cls.isAnnotationPresent(GClientEvent.class)) {
								GServer.getInstance().registerHandler((NetHandler) cls.newInstance());
							}
						}
					} else {
						String subPackagePath = getSubPackagePath(pakPath, fileName);
						String subPakName = getSubPackageName(pakName, fileName);
						addClassByAnnotation(subPackagePath, subPakName);
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static File[] getClassFiles(String pakPath) {
		return new File(pakPath).listFiles(new FileFilter() {
			
			public boolean accept(File file) {
				return (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory();
			}
		});
	}

	private static String getClassName(String pakName, String fileName) {
		String className = fileName.substring(0, fileName.lastIndexOf("."));
		if (!isEmpty(pakName)) {
			className = pakName + "." + className;
		}
		return className;
	}

	private static String getSubPackagePath(String pakPath, String filePath) {
		String subPakPath = filePath;
		if (!isEmpty(pakPath)) {
			subPakPath = pakPath + "/" + subPakPath;
		}
		return subPakPath;
	}

	private static String getSubPackageName(String pakName, String filePath) {
		String subPakName = filePath;
		if (!isEmpty(pakName)) {
			subPakName = pakName + "." + subPakName;
		}
		return subPakName;
	}

	private static boolean isEmpty(String tmp) {
		return tmp == null || "".equals(tmp.trim());
	}

}
