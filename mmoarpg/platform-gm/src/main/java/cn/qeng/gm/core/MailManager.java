/*
 * Copyright © 2015 www.noark.xyz All Rights Reserved.
 * 
 * 感谢您选择Noark框架，希望我们的努力能为您提供一个简单、易用、稳定的服务器端框架 ！
 * 除非符合Noark许可协议，否则不得使用该文件，您可以下载许可协议文件：
 * 
 * 		http://www.noark.xyz/LICENSE
 *
 * 1.未经许可，任何公司及个人不得以任何方式或理由对本框架进行修改、使用和传播;
 * 2.禁止在本项目或任何子项目的基础上发展任何派生版本、修改版本或第三方版本;
 * 3.无论你对源代码做出任何修改和改进，版权都归Noark研发团队所有，我们保留所有权利;
 * 4.凡侵犯Noark版权等知识产权的，必依法追究其法律责任，特此郑重法律声明！
 */
package cn.qeng.gm.core;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * 邮件管理器.
 *
 * @since 2.3.20
 * @author 小流氓(176543888@qq.com)
 */
public class MailManager {
	private final InternetAddress sender;
	private final String password;
	private final List<InternetAddress> receivelist;

	public MailManager(InternetAddress sender, String password, List<InternetAddress> receivelist) {
		this.sender = sender;
		this.password = password;
		this.receivelist = receivelist;
	}

	public void send(String title, String content) {
		Session session = createSession();
		// session.setDebug(true); // 设置为debug模式, 可以查看详细的发送 log
		try {
			MimeMessage message = createMimeMessage(session, title, content);
			Transport transport = session.getTransport();
			transport.connect(sender.getAddress(), password);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Session createSession() {
		Properties props = new Properties(); // 参数配置
		props.setProperty("mail.transport.protocol", "smtp"); // 使用的协议（JavaMail规范要求）
		props.setProperty("mail.host", "smtp.163.com"); // 发件人的SMTP服务器地址
		props.setProperty("mail.smtp.auth", "true"); // 请求认证，参数名称与具体实现有关
		return Session.getDefaultInstance(props);
	}

	/**
	 * 创建一封只包含文本的简单邮件
	 */
	public MimeMessage createMimeMessage(Session session, String title, String content) throws Exception {
		MimeMessage message = new MimeMessage(session);
		message.setFrom(sender);
		message.addRecipients(MimeMessage.RecipientType.TO, receivelist.toArray(new Address[0]));
		message.setSubject(title, "UTF-8");
		message.setContent(content, "text/html;charset=UTF-8");
		message.setSentDate(new Date());
		message.saveChanges();
		return message;
	}
}