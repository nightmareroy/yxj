package com.wanniu.game.data; 

public class OpenLvCO { 

	/** 功能编号 */
	public int iD;
	/** 功能名称 */
	public String funName;
	/** 是否开放 */
	public int isOpen;
	/** 等级和VIP等级关系 */
	public int lvVIPRelations;
	/** 开放等级 */
	public int openLv;
	/** 开放阶级 */
	public int openUpLv;
	/** VIP等级 */
	public int openVIPLv;
	/** 是否事件开启 */
	public int isReq;
	/** 事件值 */
	public String openReq;
	/** 菜单名称(服务器用) */
	public String fun;
	/** 父级菜单功能编号 */
	public int fatherID;
	/** 类型 */
	public int type;
	/** 图标 */
	public String icon;
	/** 控件名称 */
	public String comp;
	/** 未开放系统提示 */
	public String tips;
	/** 红点控件名 */
	public String redDot;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}