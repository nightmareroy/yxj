package com.wanniu.game.data; 

public class MailSystemCO { 

	/** 参数名称 */
	public String paramName;
	/** 邮件类型 */
	public int mailType;
	/** 发件人 */
	public String mailSender;
	/** 邮件标题 */
	public String mailTitle;
	/** 邮件内容 */
	public String mailText;
	/** 阅读处理 */
	public int mailRead;

	/** 主键 */
	public String getKey() {
		return this.paramName; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}