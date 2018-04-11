package com.wanniu.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 类反射调用工具类
 * @author agui
 */
public class ClassUtil {

	public static Field getDeclaredField(Object object, String fieldName) {
		Field field = null;
		for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
			try {
				field = clazz.getDeclaredField(fieldName);
				return field;
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * 获取对象的属性
	 */
	public static Object getProperty(Object owner, String fieldName) throws Exception {
		Field field = getDeclaredField(owner, fieldName);
		field.setAccessible(true);
		Object property = field.get(owner);
		return property;
	}

	/**
	 * 赋值给对象的私有属性
	 */
	public static void setProperty(Object owner, String fieldName, Object value) throws Exception {
		Field field = getDeclaredField(owner, fieldName);
		field.setAccessible(true);
		field.set(owner, value);
	}

	/**
	 * 获取类的静态属性
	 */
	public static Object getStaticProperty(Class<?> clz, String fieldName) throws Exception {
		Field field = clz.getField(fieldName);
		Object property = field.get(clz);
		return property;
	}

	/**
	 * 执行对象的方法
	 */
	public static Object invokeMethod(Object owner, String methodName, Object... args)
			throws Exception {
		Class<?> ownerClass = owner.getClass();
		Class<?>[] argsClass = new Class[args.length];
		for (int i = 0, j = args.length; i < j; i++) {
			argsClass[i] = args[i].getClass();
		}
		Method method = ownerClass.getMethod(methodName, argsClass);
		return method.invoke(owner, args);
	}

	/**
	 * 执行类的静态方法
	 */
	public static Object invokeStaticMethod(String className, String methodName, Object... args)
			throws Exception {
		Class<?> ownerClass = Class.forName(className);
		Class<?>[] argsClass = new Class[args.length];
		for (int i = 0, j = args.length; i < j; i++) {
			argsClass[i] = args[i].getClass();
		}
		Method method = ownerClass.getMethod(methodName, argsClass);
		return method.invoke(null, args);
	}

	/**
	 * 新建实例
	 */
	public static Object newInstance(String className, Object... args) throws Exception {
		Class<?> newoneClass = Class.forName(className);
		Class<?>[] argsClass = new Class[args.length];
		for (int i = 0, j = args.length; i < j; i++) {
			argsClass[i] = args[i].getClass();
		}
		Constructor<?> cons = newoneClass.getConstructor(argsClass);
		return cons.newInstance(args);
	}
	/**
     * 
     * @Description: 根据包名获得该包以及子包下的所有类不查找jar包中的
     * @param pageName 包名
     * @return List<Class<?>> 包下所有类
     */
    public static List<Class<?>> getClasses(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace(".", "/");
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        for (File directory : dirs) {
            classes.addAll(findClass(directory, packageName));
        }
        return classes;
    }

    private static List<Class<?>> findClass(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClass(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(
                        Class.forName(packageName + "." + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
}
