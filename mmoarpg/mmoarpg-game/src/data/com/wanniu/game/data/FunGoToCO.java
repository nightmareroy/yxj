package com.wanniu.game.data; 

public class FunGoToCO { 

	/** 功能跳转ID */
	public int funGoID;
	/** 标题 */
	public String funTitle;
	/** 正文提示 */
	public String funTips;
	/** 取消按钮 */
	public String cancelBtn;
	/** 确定按钮 */
	public String okBtn;
	/** 确定按钮跳转 */
	public String okBtnGoto;

	/** 主键 */
	public int getKey() {
		return this.funGoID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}