/*
 * Copyright © 2016 qeng.cn All Rights Reserved.
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
package cn.qeng.gm.module.monitor.service;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.mail.internet.InternetAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.qeng.common.gm.monitor.MonitorConst;
import cn.qeng.common.gm.monitor.PayMonitor;
import cn.qeng.gm.config.EmailTplConfig;
import cn.qeng.gm.core.MailManager;
import cn.qeng.gm.core.NamedThreadFactory;
import cn.qeng.gm.core.RedisManager;
import cn.qeng.gm.module.game.service.PublishService;
import cn.qeng.gm.module.maintain.domain.Email;
import cn.qeng.gm.module.maintain.service.EmailService;
import cn.qeng.gm.module.maintain.service.MaintainChatService;
import cn.qeng.gm.module.monitor.domain.MoneyStat;
import cn.qeng.gm.module.monitor.domain.PacketStat;
import cn.qeng.gm.websocket.WebSocketHander;
import redis.clients.jedis.JedisPubSub;

/**
 * 监控相关的业务逻辑类.
 *
 * @author 任成龙(chenglong.ren@qeng.net)
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
@Service
public class MonitorService {
	private final static Logger logger = LogManager.getLogger(MonitorService.class);
	@Resource
	private PublishService publishService;
	@Autowired
	private EmailService emailService;
	@Resource
	private MaintainChatService maintainChatService;
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private EmailTplConfig emailTplConfig;
	@Autowired
	private RedisManager redisManager;
	@Autowired
	private ChargeService chargeService;
	@Autowired
	private ShareService shareService;

	private ExecutorService pool;

	@PostConstruct
	public void initChat() {
		// 初始化线程池
		pool = Executors.newFixedThreadPool(4, new NamedThreadFactory("monitor-analyzer"));

		// 订阅redis，有监听，所以新建个线程去做
		Thread t = new Thread(() -> redisManager.getMsgRedis().subscribe(new SubscribeHandle(), //
				MonitorConst.REDIS_PUBLISH_CHAT_MONITOR, // 聊天监控
				MonitorConst.REDIS_PUBLISH_PAY_MONITOR, // 充值监控
				"data.MoneyMonitor", // 货币监控...
				"data.PacketMonitor"// 封包
		), "MonitorPubSubThread");
		t.setDaemon(true);
		t.start();
	}

	class SubscribeHandle extends JedisPubSub {
		/**
		 * 接收到消息的时候进行处理的方法
		 */
		@Override
		public void onMessage(String channel, String message) {
			if (!pool.isShutdown() && !StringUtils.isEmpty(message)) {// 将redis中的数据推到线程池中处理
				// 聊天
				switch (channel) {
				case MonitorConst.REDIS_PUBLISH_CHAT_MONITOR:
					pool.execute(new ChatAnalyzer(message));
					break;
				case MonitorConst.REDIS_PUBLISH_PAY_MONITOR:
					pool.execute(new PayAnalyzer(message));
					break;
				case "data.MoneyMonitor":// 货币
					pool.execute(new MoneyAnalyzer(message));
					break;
				case "data.PacketMonitor":// 封包
					pool.execute(new PacketAnalyzer(message));
					break;
				default:
					break;
				}
			}
		}
	}

	/**
	 * 封包内容分析类.
	 */
	class PacketAnalyzer implements Runnable {
		private String message;

		public PacketAnalyzer(String message) {
			this.message = message;
		}

		@Override
		public void run() {
			try {
				JSONObject json = JSON.parseObject(message);
				List<Email> emails = emailService.getEmailByMonitor();
				if (!emails.isEmpty()) {// 发送邮件
					String roleId = json.getString("id");
					String EventTime = json.getString("date");
					String roleName = json.getString("name");
					int level = json.getIntValue("level");
					int sid = json.getIntValue("sid");
					String route = json.getString("route");

					String title = "【逸仙诀】复制封包预警邮件 (" + EventTime + ")";
					String pid = "国内安卓";

					Object[] args = new Object[15];
					args[0] = title;// 标题
					args[1] = pid;// 平台
					args[2] = roleName;// 角色名称
					args[3] = roleId;// 用户ID
					args[4] = level;// 角色等级
					args[5] = sid;// sid
					args[6] = route;// 路由

					List<PacketStat> result = shareService.getPacketDataByRoleId(roleId);
					StringBuilder sb = new StringBuilder(1024);
					for (PacketStat stat : result) {
						Object[] arguments = new Object[7];
						arguments[0] = pid;
						arguments[1] = roleName;
						arguments[2] = stat.getRoute();
						arguments[3] = stat.getCount();
						sb.append(MessageFormat.format("<tr><td>{0}</td><td>{1}</td><td>{2}</td><td>{3}</td></tr>", arguments));
					}
					args[7] = sb.toString();
					String content = MessageFormat.format(emailTplConfig.getPacketTemplate(), args);

					// 发送配置
					InternetAddress sender = new InternetAddress("jsfunny_system@163.com", "清源科技监控", "UTF-8");
					String password = "ztgame@123";

					// 接收配置
					List<InternetAddress> receivelist = new ArrayList<>(emails.size());
					for (Email e : emails) {
						receivelist.add(new InternetAddress(e.getAddr(), e.getName(), "UTF-8"));
					}

					// 构建发送
					new MailManager(sender, password, receivelist).send(title, content);
				}
			} catch (Exception e) {
				logger.error("处理一条货币信息时发生了异常情况.", e);
			}
		}
	}

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private static final Map<String, Long> caches = new ConcurrentHashMap<>();

	/**
	 * 货币内容分析类.
	 */
	class MoneyAnalyzer implements Runnable {
		private String message;

		public MoneyAnalyzer(String message) {
			this.message = message;
		}

		@Override
		public void run() {
			try {
				JSONObject json = JSON.parseObject(message);
				List<Email> emails = emailService.getEmailByMonitor();
				if (!emails.isEmpty()) {// 发送邮件
					String roleId = json.getString("id");
					String EventTime = json.getString("date");
					String roleName = json.getString("name");
					String type = json.getString("type");
					int level = json.getIntValue("level");
					int money = json.getIntValue("today");
					int threshold = json.getIntValue("threshold");
					int sid = json.getIntValue("sid");

					// 添加一个过滤
					long now = System.currentTimeMillis();
					Long cache = caches.get(roleId);
					if (cache != null && now <= cache + 5L * 60 * 1000) {
						return;// 30分钟CD
					}
					caches.put(roleId, now);

					String title = "【逸仙诀】货币异常预警邮件 (" + EventTime + ")";
					String pid = "国内安卓";

					Object[] args = new Object[15];
					args[0] = title;// 标题
					args[1] = pid;// 平台
					args[2] = roleName;// 角色名称
					args[3] = roleId;// 用户ID
					args[4] = level;// 角色等级
					args[5] = sid;// sid
					args[6] = messageSource.getMessage("i18n.item." + type, new Object[] {}, Locale.getDefault());// 货币对应的国际化名称
					args[7] = money;// 总收益
					args[8] = threshold;// 阀值

					List<MoneyStat> result = shareService.getMoneyUseDataByRoleId(LocalDate.parse(EventTime, formatter), type, roleId);
					StringBuilder sb = new StringBuilder(1024);
					for (MoneyStat stat : result) {
						Object[] arguments = new Object[7];
						arguments[0] = pid;
						arguments[1] = roleName;
						arguments[2] = args[6];
						arguments[3] = "获得";
						arguments[4] = messageSource.getMessage("i18n.func.code." + stat.getReason(), new Object[] {}, Locale.getDefault());// 货币对应的国际化名称
						arguments[5] = stat.getTotalNum();
						arguments[6] = stat.getCount();
						sb.append(MessageFormat.format("<tr><td>{0}</td><td>{1}</td><td>{2}</td><td>{3}</td><td>{4}</td><td>{5}</td><td>{6}</td></tr>", arguments));
					}
					args[9] = sb.toString();
					String content = MessageFormat.format(emailTplConfig.getTemplate(), args);

					// 发送配置
					InternetAddress sender = new InternetAddress("jsfunny_system@163.com", "清源科技监控", "UTF-8");
					String password = "ztgame@123";

					// 接收配置
					List<InternetAddress> receivelist = new ArrayList<>(emails.size());
					for (Email e : emails) {
						receivelist.add(new InternetAddress(e.getAddr(), e.getName(), "UTF-8"));
					}

					// 构建发送
					new MailManager(sender, password, receivelist).send(title, content);
				}
			} catch (Exception e) {
				logger.error("处理一条货币信息时发生了异常情况.", e);
			}
		}
	}

	/**
	 * 聊天内容分析类.
	 */
	class ChatAnalyzer implements Runnable {
		private String message;

		public ChatAnalyzer(String message) {
			this.message = message;
		}

		@Override
		public void run() {
			try {
				ChatMontiorExt chat = JSON.parseObject(message, ChatMontiorExt.class);
				if (StringUtils.isEmpty(chat.getText())) {
					return;
				}
				if (chat.getText().startsWith("|") && chat.getText().endsWith("|")) {
					return;
				}
				if (chat.getText().startsWith("<") && chat.getText().endsWith(">")) {
					return;
				}

				boolean flag = maintainChatService.findSensitiveWord(chat.getText());
				chat.setLeft(!flag);// 测试用hasLXB(msgs[10])

				// 如果有当前正在查看聊天监控的WEB
				if (!WebSocketHander.socketSessionMap.isEmpty()) {
					WebSocketMessage<String> msg = new TextMessage(JSON.toJSONString(chat).getBytes());

					for (WebSocketSession s : WebSocketHander.socketSessionMap.values()) {
						if (s.isOpen()) {
							try {
								s.sendMessage(msg);
							} catch (Exception e) {}
						}
					}
				}

				// 自动禁言...
				if (flag) {
					publishService.autoPublish(chat.getSid(), chat.getId(), chat.getName(), chat.getText(), chat.getIp());
				}
			} catch (Exception e) {
				logger.error("处理一条聊天信息时发生了异常情况.", e);
			}
		}
	}

	/**
	 * 充值内容分析类.
	 */
	class PayAnalyzer implements Runnable {
		private String message;

		public PayAnalyzer(String message) {
			this.message = message;
		}

		@Override
		public void run() {
			try {
				chargeService.updatePayInfo(JSON.parseObject(message, PayMonitor.class));
			} catch (Exception e) {
				logger.error("处理一条充值信息时发生了异常情况.", e);
			}
		}
	}
}