package com.wanniu.game.data; 

public class MailOperateCO { 

	/** 邮件ID */
	public int mailId;
	/** 邮件类型 */
	public int mailType;
	/** 服务器ID */
	public int server;
	/** 渠道ID */
	public int channel;
	/** 接收对象 */
	public String accept;
	/** 等级需求 */
	public int level;
	/** VIP等级 */
	public int vipLevel;
	/** 发件人 */
	public String mailSender;
	/** 发署日期 */
	public String sendDay;
	/** 邮件标题 */
	public String mailTitle;
	/** 邮件内容 */
	public String mailText;
	/** 附件 */
	public String mailAttach;
	/** 开始时间 */
	public String startTime;
	/** 结束时间 */
	public String endTime;
	/** 删除时间 */
	public String deleteTime;
	/** 阅读处理 */
	public int mailRead;

	/** 主键 */
	public int getKey() {
		return this.mailId; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}