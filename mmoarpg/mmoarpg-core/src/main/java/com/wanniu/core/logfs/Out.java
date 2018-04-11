package com.wanniu.core.logfs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.wanniu.core.GConfig;
import com.wanniu.core.GGame;
import com.wanniu.core.GGlobal;
import com.wanniu.core.game.PoolFactory;
import com.wanniu.core.util.DateUtil;

/**
 * 日志输出类
 * 
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
public final class Out {

	/** 异步日志线程 */
	private static final ExecutorService ANSYC_LOG_EXEC = Executors.newSingleThreadExecutor(new PoolFactory("ansyc-log"));
	/** 是否输出到控制台 */
	private static final boolean CONSOLE = GConfig.getInstance().getBoolean("log.console", true);

	// 单线程不需要ThreadLocal来保护...
	/** 日志中输出的时间格式 */
	private static final DateTimeFormatter DEFAULT_DATE_FORMATER = DateTimeFormatter.ofPattern(DateUtil.F_FULL);
	/** 日志文件名中的时间格式 */
	private static final DateTimeFormatter FILE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH");
	// 单线程不需要ThreadLocal来保护...
	/** 拼接日志所用的缓存区 */
	private static final StringBuilder DEFUALT_LOG_BUILDER = new StringBuilder(512);
	private static Level LEVEL = Level.INFO;
	/** 设置 */
	private static boolean setting;

	public static synchronized void setting() {
		if (setting) {
			return;
		}
		setting = true;
		LEVEL = Level.valueOf(GConfig.getInstance().get("log.level", "INFO").toUpperCase());
		File dir = new File(GGlobal.DIR_LOG);
		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdir();
		}
	}

	public static void setLevel(Level level) {
		LEVEL = level;
	}

	private static class Log implements Runnable {
		private final Level type;
		private final Object[] args;
		private final LocalDateTime date;
		private final String threadName;
		private String fileName;
		private int lineNumber;

		Log(Level type, Object... args) {
			this.args = args;
			this.type = type;
			this.date = LocalDateTime.now();

			Thread thread = Thread.currentThread();
			this.threadName = thread.getName();

			if (LEVEL == Level.DEBUG) {
				StackTraceElement stackTraceElement = thread.getStackTrace()[4];
				fileName = stackTraceElement.getFileName();
				lineNumber = stackTraceElement.getLineNumber();
			}
		}

		@Override
		public void run() {
			try {
				String text = build(type, args);
				if (CONSOLE) {
					switch (type) {
					case DEBUG:
					case INFO:
						System.out.print(text);
						break;
					default:
						System.err.print(text);
						break;
					}
				}
				Out.writer(date, text);
			} catch (Exception e) {
				Out.error("Out run", e);
			}
		}

		private String build(Level type, Object... args) {
			DEFUALT_LOG_BUILDER.setLength(0);
			// 2017-11-11 19:59:42.538 [main] INFO Test.java:18 - test
			DEFUALT_LOG_BUILDER.append(DEFAULT_DATE_FORMATER.format(date));
			// 线程名称+输出级别
			DEFUALT_LOG_BUILDER.append(" [").append(threadName).append("] ").append(type);
			// Debug状态，输出线程等细节信息
			if (LEVEL == Level.DEBUG) {
				DEFUALT_LOG_BUILDER.append(" ").append(fileName).append(":").append(lineNumber);
			}
			DEFUALT_LOG_BUILDER.append(" - ");

			for (Object object : args) {
				if (object instanceof Throwable) {
					try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
						((Throwable) object).printStackTrace(pw);
						DEFUALT_LOG_BUILDER.append("\n").append(sw.toString());
					} catch (Exception e) {
						DEFUALT_LOG_BUILDER.append(object);
					}
				} else {
					DEFUALT_LOG_BUILDER.append(object);
				}
			}
			DEFUALT_LOG_BUILDER.append("\n");
			return DEFUALT_LOG_BUILDER.toString();
		}
	}

	private static int lastWriterHour = -1;
	private static BufferedWriter fileWriter = null;

	private static void writer(LocalDateTime date, String x) throws IOException {
		// 不是同一时间，切换输出目标
		if (fileWriter == null || date.getHour() != lastWriterHour) {

			// 如果上一个输出流存在，先关闭...
			if (fileWriter != null) {
				fileWriter.flush();
				fileWriter.close();
			}

			StringBuilder filename = new StringBuilder(128);
			filename.append(GGlobal.DIR_LOG).append(File.separator);
			filename.append(GGame.__SERVER_ID).append(File.separator);
			filename.append("game-").append(date.format(FILE_FORMATTER)).append(".log");
			File file = new File(filename.toString());
			if (!file.exists()) {
				File fileParent = file.getParentFile();
				if (!fileParent.exists()) {
					fileParent.mkdirs();
				}
				file.createNewFile();
			}

			fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"));
		}

		fileWriter.write(x);
		fileWriter.flush();
	}

	private static void put(Level type, Object... args) {
		ANSYC_LOG_EXEC.execute(new Log(type, args));
	}

	public static boolean isEnable(Level level) {
		return LEVEL.compareTo(level) <= 0;
	}

	public static boolean isEnableDebug() {
		return LEVEL == Level.DEBUG;
	}

	public static boolean isEnableInfo() {
		return isEnable(Level.INFO);
	}

	public static void debug(Object... args) {
		if (isEnableDebug()) {
			put(Level.DEBUG, args);
		}
	}

	public static void info(Object... args) {
		if (isEnableInfo()) {
			put(Level.INFO, args);
		}
	}

	public static void warn(Object... args) {
		put(Level.WARN, args);
	}

	public static void error(Object... args) {
		put(Level.ERROR, args);
	}

	public static void shutdown() {
		ANSYC_LOG_EXEC.shutdown();
		if (fileWriter != null) {
			try {
				fileWriter.close();
			} catch (IOException e) {}
		}
	}
}