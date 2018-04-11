package com.wanniu.core.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.wanniu.core.GGlobal;
import com.wanniu.core.logfs.Out;

/**
 * 文件操作类
 * 
 * @author agui
 */
public class FileUtil {

	public static interface FileReader {
		/** 读取一行 */
		void read(String line);
	}

	public static interface FileChecker {
		/** 命中后结束 */
		boolean check(String line);
	}

	public static List<String> readLines(String filename) {
		File file = new File(filename);
		return readLines(file);
	}

	public static void eachLine(String file, FileReader cloure) {
		eachLine(new File(file), cloure);
	}

	public static void eachLine(File file, FileReader cloure) {
		BufferedReader bufferReader = null;
		InputStreamReader reader = null;
		if (file.exists()) {
			try {
				reader = new InputStreamReader(new FileInputStream(file), GGlobal.UTF_8);
				bufferReader = new BufferedReader(reader);
				String line = null;
				while ((line = bufferReader.readLine()) != null) {
					cloure.read(line);
				}
			} catch (Exception e) {
				Out.error("FileUtil eachLine", e);
			} finally {
				try {
					if (reader != null) {
						reader.close();
					}
				} catch (IOException e) {
					Out.error("FileUtil eachLine", e);
				}
				try {
					if (bufferReader != null) {
						bufferReader.close();
					}
				} catch (IOException e) {
					Out.error("FileUtil eachLine", e);
				}
			}
		} else {
			Out.error("FileUtil eachLine", String.format("Not found %s!", file.getAbsolutePath()));
		}
	}

	public static String checkLine(String file, FileChecker cloure) {
		return checkLine(new File(file), cloure);
	}

	public static String checkLine(File file, FileChecker cloure) {
		BufferedReader bufferReader = null;
		InputStreamReader reader = null;
		if (file.exists()) {
			try {
				reader = new InputStreamReader(new FileInputStream(file), GGlobal.UTF_8);
				bufferReader = new BufferedReader(reader);
				String line = null;
				while ((line = bufferReader.readLine()) != null) {
					if (cloure.check(line)) {
						return line;
					}
				}
			} catch (Exception e) {
				Out.error("FileUtil checkLine", e);
			} finally {
				try {
					if (reader != null) {
						reader.close();
					}
				} catch (IOException e) {
					Out.error("FileUtil checkLine", e);
				}
				try {
					if (bufferReader != null) {
						bufferReader.close();
					}
				} catch (IOException e) {
					Out.error("FileUtil checkLine", e);
				}
			}
		} else {
			Out.error("FileUtil checkLine", String.format("Not found %s!", file.getAbsolutePath()));
		}
		return null;
	}

	public static List<String> readLines(File file) {
		final List<String> lines = new ArrayList<String>();
		eachLine(file, new FileReader() {
			@Override
			public void read(String line) {
				lines.add(line);
			}
		});
		return lines;
	}

	public static String readText(File file) {
		final StringBuilder lines = new StringBuilder();
		eachLine(file, new FileReader() {
			@Override
			public void read(String line) {
				lines.append(line).append('\n');
			}
		});
		return lines.toString();
	}

	/**
	 * 复制一个文件 如果是源文件是目录、或者不存在、或者不可读、或者目标文件已存在返回false
	 * 
	 * @param src 源文件名
	 * @param dest 目标文件名
	 * @return
	 */
	public static boolean copy(String src, String dest) {
		File srcFile = new File(src);
		if (!(srcFile.exists() && srcFile.isFile() && srcFile.canRead())) {
			return false;
		}
		File destFile = new File(dest);
		if (destFile.exists()) {
			destFile.delete();
		}

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(srcFile));
			bos = new BufferedOutputStream(new FileOutputStream(destFile));
			byte[] buf = new byte[1024];
			int len = 0;
			while ((len = bis.read(buf)) != -1) {
				bos.write(buf, 0, len);
			}

			return true;
		} catch (FileNotFoundException e) {
			Out.error("FileUtil copy", e);
		} catch (IOException e) {
			Out.error("FileUtil copy", e);
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {}
			}
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {}
			}
		}

		return false;
	}

	/**
	 * 清除文件所有内容
	 * 
	 * @param file 要清除内容的文件
	 * @return
	 */
	public static boolean clear(File file) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(new FileWriter(file, false));
			writer.close();
		} catch (IOException e) {
			Out.error("FileUtil clear", e);
			return false;
		}
		return true;
	}

	/**
	 * 文件转字节数组
	 */
	public static byte[] file2bytes(File file) {
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(file));
			ByteArrayOutputStream out = new ByteArrayOutputStream(in.available());
			byte[] temp = new byte[1024];
			int size = 0;
			while ((size = in.read(temp)) != -1) {
				out.write(temp, 0, size);
			}
			return out.toByteArray();
		} catch (Exception e) {
			Out.error("FileUtil file2bytes", e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					Out.error("FileUtil file2bytes", e);
				}
			}
		}
		return null;
	}

	/**
	 * 大文件拷贝（推荐）
	 */
	public static void copyNio(final File from, final File to) throws IOException {
		final RandomAccessFile inFile = new RandomAccessFile(from, "r");
		try {
			final RandomAccessFile outFile = new RandomAccessFile(to, "rw");
			try {
				final FileChannel inChannel = inFile.getChannel();
				final FileChannel outChannel = outFile.getChannel();
				long pos = 0;
				long toCopy = inFile.length();
				while (toCopy > 0) {
					final long bytes = inChannel.transferTo(pos, toCopy, outChannel);
					pos += bytes;
					toCopy -= bytes;
				}
			} finally {
				outFile.close();
			}
		} finally {
			inFile.close();
		}
	}

	/**
	 * 逐行写入文件
	 */
	public static void write(File file, Collection<String> lines) {
		try {
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, false), GGlobal.UTF_8), true);
			for (String key : lines) {
				writer.println(key);
			}
			writer.close();
			writer = null;
		} catch (Exception e) {
			Out.error("FileUtil write", e);
		}
	}

	/**
	 * 逐行写入文件
	 */
	public static void write(File file, String content) {
		try {
			if (file.exists()) {
				byte[] text = Files.readAllBytes(file.toPath());
				byte[] newText = content.getBytes("UTF-8");

				boolean same = true;
				if (text.length == newText.length) {
					for (int i = 0; i < text.length; i++) {
						if (text[i] != newText[i]) {
							same = false;
							break;
						}
					}
				} else {
					same = false;
				}

				if (!same) {
					Files.write(file.toPath(), newText);
				} else {
					Out.info("检查未变更，忽略生成", file.getName());
				}
			} else {
				Files.write(file.toPath(), content.getBytes("UTF-8"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}