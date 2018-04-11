/*
 * Copyright © 2017 qeng.cn All Rights Reserved.
 * 
 * 感谢您加入清源科技，不用多久，您就会升职加薪、当上总经理、出任CEO、迎娶白富美、从此走上人生巅峰
 * 除非符合本公司的商业许可协议，否则不得使用或传播此源码，您可以下载许可协议文件：
 * 
 * 		http://www.noark.xyz/qeng/LICENSE
 *
 * 1、未经许可，任何公司及个人不得以任何方式或理由来修改、使用或传播此源码;
 * 2、禁止在本源码或其他相关源码的基础上发展任何派生版本、修改版本或第三方版本;
 * 3、无论你对源代码做出任何修改和优化，版权都归清源科技所有，我们将保留所有权利;
 * 4、凡侵犯清源科技相关版权或著作权等知识产权者，必依法追究其法律责任，特此郑重法律声明！
 */
package cn.qeng.gm.module.maintain.domain;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.conversions.Bson;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import cn.qeng.gm.module.maintain.metalib.Metalib;
import cn.qeng.gm.module.maintain.service.MLogExportService;
import cn.qeng.gm.util.DateUtils;

/**
 * 
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
public class CreateMlogRunnable implements Runnable {
	private final static Logger logger = LogManager.getLogger(CreateMlogRunnable.class);

	private String struct;
	private String[] entry;
	private Set<String> entrySet;
	private LocalDate start;
	private LocalDate end;
	private String url;
	private int id;
	private MongoClient mongo;
	private MLogExportService service;
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	private static final DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public CreateMlogRunnable(String struct, String[] entry, LocalDate start, LocalDate end, String url, int id, MongoClient mongo, MLogExportService service) {
		this.struct = struct;
		this.entry = entry;
		this.start = start;
		this.end = end;
		this.url = url;
		this.id = id;
		this.mongo = mongo;
		this.service = service;
		this.entrySet = new HashSet<>();
		for (String s : this.entry) {
			this.entrySet.add(s);
		}
	}

	public void createFile() throws Exception {
		Metalib metalib = service.getMlog().get(struct);
		if (metalib == null) {// 没有该日志，则直接返回
			return;
		}
		if (url == null || url.isEmpty()) {
			return;
		}
		// 创建文件
		File file = new File(url);
		if (file.exists()) {
			file.delete();
		}
		file.getParentFile().mkdirs();
		// 特殊处理，entry中如果有EventID，则要改为mongo中对应的_id
		for (int i = 0; i < entry.length; i++) {
			if (entry[i].equals("EventID")) {
				entry[i] = "_id";
			}
		}

		List<MongoCollection<org.bson.Document>> docs = new ArrayList<>();
		// 如果日志是每天一份，则要拿到所有的doc
		if (metalib.isOneDay()) {
			LocalDate s = start;
			while (!s.isAfter(end)) {
				docs.add(mongo.getDatabase("agame").getCollection(struct + formatter.format(s)));
				s = s.plusDays(1);
			}
		} else {
			// 日志没有按天分割，查询一个就好了
			docs.add(mongo.getDatabase("agame").getCollection(struct));
		}
		// 开始mongo查询
		Iterator<MongoCollection<org.bson.Document>> iterator = docs.iterator();
		// 文件第一行，表头
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < entry.length; i++) {
			if (i == 0) {
				sb.append(entry[i]);
			} else {
				sb.append(",").append(entry[i]);
			}
		}
		// 凡是mlog中有Id字段的，句末新增一列：角色ID转为用户ID
		if (this.entrySet.contains("Id")) {
			sb.append(",").append("用户ID");
		}
		sb.append("\n");

		Date startDate = null;
		Date endDate = null;
		try {
			startDate = DateUtils.parseyyyyMMddHHmmss(start.format(formatter2) + " 00:00:00");
			endDate = DateUtils.parseyyyyMMddHHmmss(end.format(formatter2) + " 23:59:59");
		} catch (ParseException e1) {
			throw e1;
		}
		Bson match = Filters.and(Filters.gte("EventTime", DateUtils.cvtToGmt(DateUtils.calStartTime(startDate))), Filters.lte("EventTime", DateUtils.cvtToGmt(DateUtils.calEndTime(endDate))));
		// 循环查询mongo获取日志
		while (iterator.hasNext()) {
			MongoCollection<org.bson.Document> doc = (MongoCollection<org.bson.Document>) iterator.next();
			// 获取所需要查询的字段，1标记所需字段0标记不需要的字段
			org.bson.Document field = new org.bson.Document();
			field.append("_id", 0);
			for (int i = 0; i < entry.length; i++) {
				field.append(entry[i], 1);
			}
			// 查询mongo方法
			doc.find(match).projection(field).forEach(new Block<org.bson.Document>() {
				@Override
				public void apply(org.bson.Document t) {
					// 拼装日志,格式XXXXX,XXXXXXXX,tXXXXX,tX,tXXXXXXXXXXX
					for (int i = 0; i < entry.length; i++) {
						if (i == 0) {
							sb.append(object2String(t.get(entry[i])));
						} else {
							sb.append(",").append(object2String(t.get(entry[i])));
						}
					}
					// 你提我改特别处理，角色ID转为用户ID
					if (entrySet.contains("Id")) {
						sb.append(",").append(t.getLong("Id"));
					}

					sb.append("\n");// 换行
					if (sb.length() > 10240) {
						writeFile(file, sb);
					}
				}
			});
		}

		// 将剩余数据写入文件
		this.writeFile(file, sb);

		// 压缩成zip
		String zipUrl = url.replaceAll("csv", "zip");
		logger.info("正在生成mlog压缩文件：" + zipUrl);
		zip(zipUrl, file);
		// 删除csv文件
		file.delete();

		// 生成完毕后根据主键ID更新状态
		service.updateLogStatus(id, 1);
		logger.info("mlog压缩文件文件已就绪：" + zipUrl);
	}

	public void writeFile(File file, StringBuffer sb) {
		// 如果stringBuffer的长度大于10000，则创建文件追加写入，并清空stringbuffer
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "gb2312"))) {
			// BufferedWriter
			writer.write(sb.toString());
			sb.setLength(0);
			writer.flush();
		} catch (Exception e) {
			throw new RuntimeException("写入mlog文件失败");
		}
	}

	@Override
	public void run() {
		try {
			this.createFile();
		} catch (Exception e) {
			logger.error("CreateMlog error:" + e.getMessage());
			service.updateLogStatus(id, 2);
		}
	}

	private void zip(String zipFileName, File inputFile) throws Exception {
		try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName)); BufferedOutputStream bo = new BufferedOutputStream(out); FileInputStream in = new FileInputStream(inputFile); BufferedInputStream bi = new BufferedInputStream(in);) {
			out.putNextEntry(new ZipEntry(inputFile.getName())); // 创建zip压缩进入点base
			int b;
			while ((b = bi.read()) != -1) {
				bo.write(b); // 将字节流写入当前zip目录
			}
		}
	}

	private static String object2String(Object o) {
		if (o == null)
			return null;
		if (o.getClass() == Date.class) {
			return DateUtils.formatyyyyMMddHHmmss(DateUtils.gmtToCvt((Date) o));
		} else {
			return o.toString();
		}
	}
}
