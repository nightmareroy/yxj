package com.wanniu.game.mail.data;

import java.util.Date;

import com.wanniu.game.common.Const;

/**
 * GM邮件
 * 
 * @author lxm
 *
 */
public class MailGmData extends MailData {
	public String mailSender;
	public String mailTitle;
	public String mailText;
	public Date createRoleDate;// 最后创角时间
	public int minLevel;
	/**
	 * GM邮件会持续发送的时间 7天
	 */
	public static final long lastTime = 1000 * 60 * 60 * 24 * 7;
	// public static final long lastTime = 1000 * 60 * 3;

	public MailGmData() {
		this.mailType = Const.MailType.MAIL_GM_TYPE.getValue();
	}

}
