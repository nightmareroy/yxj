package com.wanniu.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * 
 * @author agui
 */
public final class ZipUtil {
	public static final String EXT = ".zip";
	private static final int BUFFER = 1024;

	/**
	 * 文件 解压缩
	 * 
	 * @param srcPath
	 *            源文件路径
	 * 
	 * @throws Exception
	 */
	public static void decompress(String srcPath) throws Exception {
		File srcFile = new File(srcPath);
		decompress(srcFile);
	}

	/**
	 * 解压缩
	 * 
	 * @param srcFile
	 * @throws Exception
	 */
	public static void decompress(File srcFile) throws Exception {
		String basePath = srcFile.getParent();
		decompress(srcFile, basePath);
	}

	/**
	 * 解压缩
	 * 
	 * @param srcFile
	 * @param destFile
	 * @throws Exception
	 */
	public static void decompress(File srcFile, File destFile) throws Exception {
		fileProber(destFile);
		CheckedInputStream cis = new CheckedInputStream(new FileInputStream(srcFile), new CRC32());
		ZipInputStream zis = new ZipInputStream(cis);
		decompress(destFile, zis);
		zis.closeEntry();
		zis.close();
	}

	/**
	 * 解压缩
	 * 
	 * @param srcFile
	 * @param destPath
	 * @throws Exception
	 */
	public static void decompress(File srcFile, String destPath) throws Exception {
		decompress(srcFile, new File(destPath));
	}

	/**
	 * 文件 解压缩
	 * 
	 * @param srcPath
	 *            源文件路径
	 * @param destPath
	 *            目标文件路径
	 * @throws Exception
	 */
	public static void decompress(String srcPath, String destPath) throws Exception {
		File srcFile = new File(srcPath);
		decompress(srcFile, destPath);
	}

	/**
	 * 文件 解压缩
	 * 
	 * @param destFile
	 *            目标文件
	 * @param zis
	 *            ZipInputStream
	 * @throws Exception
	 */
	private static void decompress(File destFile, ZipInputStream zis) throws Exception {
		ZipEntry entry = null;
		while ((entry = zis.getNextEntry()) != null) {
			// 文件
			String dir = destFile.getPath() + File.separator + entry.getName();
			File dirFile = new File(dir);
			if (entry.isDirectory()) {
				dirFile.mkdirs();
			} else {
				decompressFile(dirFile, zis);
			}
		}
	}

	/**
	 * 文件探针
	 * 
	 * <pre>
	 * 当文件目录不存在时，创建目录！
	 * </pre>
	 * 
	 * @param dirFile
	 */
	private static void fileProber(File dirFile) {
		File parentFile = dirFile.getParentFile();
		if (!parentFile.exists()) {
			fileProber(parentFile);
			return;
		}
		if (!dirFile.exists()) {
			dirFile.mkdir();
		}
	}

	/**
	 * 文件解压缩
	 * 
	 * @param destFile
	 *            目标文件
	 * @param zis
	 *            ZipInputStream
	 * @throws Exception
	 */
	private static void decompressFile(File destFile, ZipInputStream zis) throws Exception {
		fileProber(destFile.getParentFile());
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFile));
		int count;
		byte data[] = new byte[BUFFER];
		while ((count = zis.read(data, 0, BUFFER)) != -1) {
			bos.write(data, 0, count);
		}
		bos.write(data);
		bos.close();
	}

	public static void readZipFile(String zipFile) {
		try {
			ZipFile zf = new ZipFile(zipFile);
			Enumeration<?> entries = zf.entries();

			while (entries.hasMoreElements()) {
				ZipEntry ze = (ZipEntry) entries.nextElement();
				System.out.println("读取：" + ze.getName());
				long size = ze.getSize();
				if (size > 0) {
					System.out.println("长度：" + size);
					BufferedReader br = new BufferedReader(new InputStreamReader(
							zf.getInputStream(ze)));
					StringBuilder context = new StringBuilder();
					while (br.ready()) {
						context.append(br.readLine());
					}
					System.out.println(context);
					br.close();
				}
			}
			
			zf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<String> readHttpZip(String sURL) {
		List<String> mapJsons = new ArrayList<String>();
		ZipInputStream in = null;
		try {
			URL url = new URL(sURL);
			HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
			DataInputStream input = new DataInputStream(httpConnection.getInputStream());

			in = new ZipInputStream(input);
			while (in.getNextEntry() != null) {
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				StringBuilder context = new StringBuilder();
				while (br.ready()) {
					context.append(br.readLine());
				}
				mapJsons.add(context.toString());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.closeEntry();
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return mapJsons;
	}

	/** 压缩文本 */
	public static String compress(String text) throws IOException {
		if (text == null || text.length() == 0) {
			return text;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(text.getBytes());
		gzip.close();
		return out.toString("ISO-8859-1");
	}

	/** 解压文本 */
	public static String uncompress(String text) throws IOException {
		if (text == null || text.length() == 0) {
			return text;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(text.getBytes("ISO-8859-1"));
		GZIPInputStream gunzip = new GZIPInputStream(in);
		byte[] buffer = new byte[256];
		int n;
		while ((n = gunzip.read(buffer)) >= 0) {
			out.write(buffer, 0, n);
		}
		return out.toString();
	}

	/** 压缩字符串到字节数组 */
	public static byte[] encode(String text) throws IOException {
		if (text == null || text.length() == 0) {
			return new byte[0];
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(text.getBytes());
		gzip.close();
		return out.toByteArray();
	}

	/** 解压字节数组为字符串 */
	public static String decode(byte[] bytes) throws IOException {
		if (bytes == null || bytes.length== 0) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		GZIPInputStream gunzip = new GZIPInputStream(in);
		byte[] buffer = new byte[256];
		int n;
		while ((n = gunzip.read(buffer)) >= 0) {
			out.write(buffer, 0, n);
		}
		return out.toString();
	}
	
}
