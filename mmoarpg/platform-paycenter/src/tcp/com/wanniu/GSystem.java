package com.wanniu;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 系统扩展类
 * 
 * @author agui
 */
public final class GSystem {
	private final static Logger logger = LogManager.getLogger(GSystem.class);

	/**
	 * 操作系统类别枚举
	 */
	public static enum OsType {
		OS_UNKNOWN, OS_NT, OS_9X, OS_LINUX, OS_UNIX,
	}

	/** 系统相关的换行符号 */
	public static final String CRLF = System.getProperty("line.separator");

	/** 操作系统名称 */
	public final static String OS_NAME = System.getProperty("os.name").toUpperCase();

	/**
	 * 执行命令序列并返回执行后的回应字符串
	 * 
	 * @param execCmds 待执行的命令序列
	 * @return
	 */
	public static String execCmd(String[] execCmds) {
		int ret = 0;
		Process porc = null;
		InputStream perr = null, pin = null;
		StringBuilder sb = new StringBuilder();
		String line = null;
		BufferedReader br = null;
		try {
			// 执行编译操作
			porc = Runtime.getRuntime().exec(execCmds, null, null);
			perr = porc.getErrorStream();
			pin = porc.getInputStream();
			// 获取屏幕输出显示
			br = new BufferedReader(new InputStreamReader(pin));
			while ((line = br.readLine()) != null) {
				// System.out.println("exec()O: "+line);
				sb.append(line).append(CRLF);
			}
			// 获取错误输出显示
			br = new BufferedReader(new InputStreamReader(perr));
			while ((line = br.readLine()) != null) {
				System.err.println("exec()E: " + line);
			}
			porc.waitFor(); // 等待编译完成
			ret = porc.exitValue(); // 检查javac错误代码
			if (ret != 0) {
				System.err.println("porc.exitValue() = " + ret);
			}

			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			porc.destroy();
		}
		return "";
	}

	/**
	 * 获取指定目录的磁盘空闲空间字节数
	 * 
	 * @param dir 目录名
	 * @return
	 */
	public static long getFreeDiskSize(String dir) {
		String[] cmds = null;
		long freeSize = -1;
		OsType osType = getOSType();
		switch (osType) {
		case OS_NT:
			cmds = new String[] { "cmd.exe", "/c", "dir", dir };
			freeSize = parseWindowsFreeSize(execCmd(cmds));
			break;
		case OS_9X:
			cmds = new String[] { "command.exe", "/c", "dir", dir };
			freeSize = parseWindowsFreeSize(execCmd(cmds));
			break;
		case OS_LINUX:
		case OS_UNIX:
			cmds = new String[] { "df", dir };
			freeSize = parseUnixFreeSize(execCmd(cmds));
			break;
		default:
		}
		return freeSize;
	}

	/**
	 * 获取s的最后一行字符串
	 * 
	 * @param txt
	 * @return
	 */
	public static String getLastLine(String txt) {
		StringBuilder builder = new StringBuilder(txt);
		if (builder.length() > CRLF.length() * 2) {// 包括CRLF本身至少有2个CRLF和1个字符才有意义
			// 删除末尾的CRLF
			builder.delete(builder.lastIndexOf(CRLF), builder.length());
			return builder.substring(builder.lastIndexOf(CRLF) + CRLF.length(), builder.length());
		}
		return "";
	}

	/**
	 * 返回按空格劈开字符串列表，并过滤掉不可见字符串
	 * 
	 * @param msg ( 15 个目录 1,649,696,768 可用字节 )
	 * @return 0(15) 1(个目录) 2(1,649,696,768) 3(可用字节)
	 */
	private static List<String> split(String msg) {
		String[] splited = msg.split(" "); // 空格分隔；
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < splited.length; i++) {
			if (splited[i] != null) {
				splited[i] = splited[i].trim();
				if (splited[i].length() > 0) {
					list.add(splited[i]);
				}
			}
		}

		return list;
	}

	/**
	 * 根据Linux终端返回的内容解析空闲空间
	 * 
	 * @param cmdReturnContent 终端返回的内容
	 * @return 可用空间字节数 发生异常返回－1
	 */
	private static long parseUnixFreeSize(String cmdReturnContent) {
		// 获取最后一行(15 个目录 1,649,696,768 可用字节)
		String lastLine = getLastLine(cmdReturnContent);
		List<String> elements = split(lastLine);
		if (elements.size() < 6) {
			return -1;
		}
		System.out.println("os_freesize_unix() 目录:\t" + elements.get(0));
		System.out.println("os_freesize_unix() 总共:\t" + elements.get(1));
		System.out.println("os_freesize_unix() 已用:\t" + elements.get(2));
		System.out.println("os_freesize_unix() 可用:\t" + elements.get(3));
		System.out.println("os_freesize_unix() 可用%:\t" + elements.get(4));
		System.out.println("os_freesize_unix() 挂接:\t" + elements.get(5));

		return Long.parseLong(elements.get(3)) * 1024;// 把kb数转换成字节数
	}

	/**
	 * 根据Windows命令行返回的内容解析空闲空间
	 * 
	 * @param cmdReturnContent 命令行返回的内容
	 * @return 可用空间字节数 发生异常返回－1
	 */
	private static long parseWindowsFreeSize(String cmdReturnContent) {
		// 获取最后一行(15 个目录 1,649,696,768 可用字节)
		String lastLine = getLastLine(cmdReturnContent);
		List<String> elements = split(lastLine);
		if (elements.size() < 4) {
			return -1;
		}
		// 取出可用字节数（1,649,696,768）并替换成（1649696768）
		String freeSize = elements.get(2).replaceAll(",", "");

		return Long.parseLong(freeSize);
	}

	/**
	 * 获取当前运行的操作系统类别
	 * 
	 * @return 返回OsType枚举
	 */
	public static OsType getOSType() {
		if (OS_NAME.startsWith("WINDOWS")) {
			if (OS_NAME.endsWith("7") || OS_NAME.endsWith("XP") || OS_NAME.endsWith("8") || OS_NAME.endsWith("2000") || OS_NAME.endsWith("NT")) {
				return OsType.OS_NT;
			} else {
				return OsType.OS_9X;
			}
		} else if (OS_NAME.indexOf("LINUX") > 0) {
			return OsType.OS_LINUX;
		} else if (OS_NAME.indexOf("UX") > 0) {
			return OsType.OS_UNIX;
		} else {
			return OsType.OS_UNKNOWN;
		}
	}

	/**
	 * 杀死所有前缀名为threadNamePrefix的线程
	 * 
	 * @param threadNamePrefix 线程名前缀
	 */
	public static void destoryThread(String threadNamePrefix) {
		Thread[] threads = new Thread[Thread.activeCount()];

		int n = Thread.enumerate(threads);
		for (int i = 0; i < n; i++) {
			String tName = threads[i].getName();
			if (tName.startsWith(threadNamePrefix)) {
				if (!threads[i].isInterrupted()) {
					threads[i].interrupt();
					System.out.println(tName + ">>>>>>>>>>>>已销毁...");
				}
			}
		}
	}

	/**
	 * 执行计算机本地可执行文件
	 * 
	 * @param fileName 可执行文件名（不包含路径即为当前目录）
	 * @return 发生异常即返回false
	 */
	public static boolean execScriptFile(String fileName) {
		try {
			Runtime.getRuntime().exec(fileName);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static void waitMills(int mills) {
		try {
			Thread.sleep(mills);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void waitSeconds(int second) {
		waitMills(second * 1000);
	}

	public static long getUsedMemoryMB() {
		return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576;
	}

	public static long getFreeMemoryMB() {
		return (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory()) / 1048576;
	}

	public static long getMaxMemoryMB() {
		return Runtime.getRuntime().maxMemory() / 1048576;
	}

	public static void free() {
		{
			// 当前剩余内存
			long freeMem = getFreeMemoryMB();
			// 给虚拟分配的最大内存
			long totalMem = getMaxMemoryMB();
			logger.info("回收前空闲内存 {} M / {} M", freeMem, totalMem);
		}

		// 仅此调用而已(其它地方误用)
		System.gc();

		{
			// 当前剩余内存
			long freeMem = getFreeMemoryMB();
			// 给虚拟分配的最大内存
			long totalMem = getMaxMemoryMB();
			logger.info("回收后空闲内存 {} M / {} M", freeMem, totalMem);
		}
	}
}