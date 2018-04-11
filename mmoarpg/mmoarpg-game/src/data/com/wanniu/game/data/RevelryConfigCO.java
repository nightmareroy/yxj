package com.wanniu.game.data; 

public class RevelryConfigCO { 

	/** 活动顺序 */
	public int id;
	/** 活动类型 */
	public String type;
	/** 参数注释 */
	public String notes1;
	/** 参数1 */
	public int parameter1;
	/** 参数2 */
	public int parameter2;
	/** 资源1 */
	public String item1code;
	/** 数量1 */
	public int num1;
	/** 资源2 */
	public String item2code;
	/** 数量2 */
	public int num2;
	/** 资源3 */
	public String item3code;
	/** 数量3 */
	public int num3;
	/** 资源4 */
	public String item4code;
	/** 数量4 */
	public int num4;
	/** 邮件ID */
	public int mailID;

	/** 主键 */
	public int getKey() {
		return this.id; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}