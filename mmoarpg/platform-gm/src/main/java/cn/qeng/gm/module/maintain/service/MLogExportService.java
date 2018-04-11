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
package cn.qeng.gm.module.maintain.service;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import com.mongodb.MongoClient;

import cn.qeng.gm.core.NamedThreadFactory;
import cn.qeng.gm.module.maintain.domain.CreateMlogRunnable;
import cn.qeng.gm.module.maintain.domain.Mlog;
import cn.qeng.gm.module.maintain.domain.MlogListResult;
import cn.qeng.gm.module.maintain.domain.MlogRepository;
import cn.qeng.gm.module.maintain.metalib.Metalib;
import cn.qeng.gm.module.maintain.metalib.MetalibField;
import cn.qeng.gm.util.DateUtils;
/**
 * 
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@Service
public class MLogExportService {
	private final static Logger logger = LogManager.getLogger(MLogExportService.class);

	private static final Map<String, Metalib> caches = new HashMap<>();
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	private static ExecutorService pool;// 生成日志文件线程

	@Autowired
	private MlogRepository mlogRepository;
	@Autowired
	private MongoClient mongo;

	/**
	 * 加载mlog.xml
	 */
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void loadMlog() throws DocumentException {

		// 如果拿不到服务器足够的权限可能无法获取到绝对路径导致异常
		File mlog = new File(MLogExportService.class.getClassLoader().getResource("").getPath() + "mlog.xml");
		// 创建SAXReader对象
		SAXReader reader = new SAXReader();
		// 读取文件 转换成Document
		Document document = reader.read(mlog);
		// 获取根节点元素对象
		Element root = document.getRootElement();
		for (Element struct : (List<Element>) root.elements("struct")) {
			String name = struct.attributeValue("name");
			boolean notstorage = Boolean.parseBoolean(struct.attributeValue("notstorage"));
			boolean one_day = Boolean.parseBoolean(struct.attributeValue("one_day"));
			boolean publish = Boolean.parseBoolean(struct.attributeValue("publish"));
			String desc = struct.attributeValue("desc");
			boolean extra = getBoolean(struct, "extra", false);

			Metalib metalib = new Metalib(name, one_day, publish, notstorage, extra, desc);
			int index = 1;
			for (Element entry : (List<Element>) struct.elements("entry")) {
				metalib.addField(new MetalibField(index++, entry.attributeValue("name"), entry.attributeValue("type"), entry.attributeValue("desc")));
			}
			caches.put(metalib.getName(), metalib);
		}

		// 初始化线程池
		// 产生一个 ExecutorService 对象，这个对象带有一个大小为 poolSize 的线程池，若任务数量大于 poolSize
		// ，任务会被放在一个 queue 里顺序执行。
		pool = Executors.newFixedThreadPool(4, new NamedThreadFactory("mlog-analyzer"));
	}

	private static boolean getBoolean(Element struct, String key, boolean defaultValue) {
		String value = struct.attributeValue(key);
		if (value == null) {
			return defaultValue;
		}
		return Boolean.parseBoolean(value);
	}

	public Map<String, Metalib> getMlog() {
		return caches;
	}

	/**
	 * 根据传输的参数生成日志文件
	 * 
	 * @param struct 日志类型名称
	 * @param entry 日志字段
	 * @param start 开始时间
	 * @param end 结束时间
	 */
	public void createLog(String struct, String[] entry, LocalDate start, LocalDate end, String url, int id) {
		Thread t = new Thread(new CreateMlogRunnable(struct, entry, start, end, url, id, mongo, this));
		t.setDaemon(true);
		pool.execute(t);
	}

	/**
	 * 将日志信息记录到数据库
	 */
	public int insertLog(String name, String entry, LocalDate start, LocalDate end, Date createTime, String uri, String note, String fileName, String url) {
		Mlog log = new Mlog();
		log.setName(name);
		log.setEntry(entry);
		log.setStart(formatter.format(start));
		log.setEnd(formatter.format(end));
		log.setCreatedate(createTime);
		log.setUri(uri);
		log.setStatus(0);
		log.setNote(note);
		log.setFilename(fileName);
		log.setUrl(url);
		log = mlogRepository.saveAndFlush(log);
		return log.getId();
	}

	public void updateLogStatus(int id, int status) {
		Mlog mlog = mlogRepository.findOne(id);
		mlog.setStatus(status);
		mlogRepository.save(mlog);
	}

	public MlogListResult getInfo() {
		MlogListResult result = new MlogListResult();
		result.getData().addAll(mlogRepository.findAll());
		return result;
	}

	/**
	 * 删除日志文件和记录
	 * 
	 * @param request 接口上下文
	 * @param id 日志ID
	 */
	public void delete(HttpServletRequest request, int id)throws Exception{
		Mlog mlog = mlogRepository.findOne(id);
		if (mlog == null) {
			return;
		}
		String url = getZipUrl(request, mlog);
		File file = new File(url);
		if (file.exists()) {
			file.delete();
		}
		mlogRepository.delete(id);
	}

	public void initCreateMlog() {
		mlogRepository.findAllByStatus(0).forEach(m -> {
			CreateMlogRunnable runnable = new CreateMlogRunnable(m.getName(), m.getEntry().split("::"), LocalDate.parse(m.getStart(), formatter), LocalDate.parse(m.getEnd(), formatter), m.getUrl(), m.getId(), mongo, this);
			pool.execute(runnable);
		});
	}
	
	public String getZipUrl(HttpServletRequest request, Mlog mlog)throws Exception{
		if(mlog == null)
			throw new Exception("create url which mlog not exist");
		
		return request.getSession().getServletContext().getRealPath("/mlog/" + DateUtils.formatyyyyMMdd(mlog.getCreatedate()) + "/") + mlog.getFilename().replace(".csv", ".zip");
	}

	public void downloadFile(HttpServletRequest request, HttpServletResponse response, int id) throws Exception{
		Mlog mlog = mlogRepository.findOne(id);
		if (mlog == null) {
			return;
		}
		String url = getZipUrl(request, mlog);
		File file = new File(url);
		if (!file.exists()) {
			return;
		}
		String mimeType = URLConnection.guessContentTypeFromName(file.getName());
		if (mimeType == null) {
			logger.info("mimetype is not detectable, will take default");
			mimeType = "application/octet-stream";
		}

		response.setContentType(mimeType);
		response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));
		response.setContentLength((int) file.length());
		try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file));){
			FileCopyUtils.copy(inputStream, response.getOutputStream());
		} 
	}
}