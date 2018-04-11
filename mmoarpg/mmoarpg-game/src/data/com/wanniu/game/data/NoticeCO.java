package com.wanniu.game.data; 

public class NoticeCO { 

	/** 公告编号 */
	public int iD;
	/** 公告标题 */
	public String noticeTitle;
	/** 发布时间 */
	public String releaseTime;
	/** 发布人 */
	public String releasePerson;
	/** 公告内容 */
	public String content;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}