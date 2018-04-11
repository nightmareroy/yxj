package com.wanniu.game.data; 

public class SystemConfigCO { 

	/** 设置编号 */
	public int configID;
	/** 标签页编号 */
	public int tabID;
	/** 标签页文字 */
	public String tabName;
	/** 分类编号 */
	public int typeID;
	/** 分类文字 */
	public String typeName;
	/** 设置 */
	public String config;
	/** 默认设置 */
	public int default_v;
	/** 关联 */
	public int relation;
	/** 控件样式 */
	public int style;
	/** 对应值 */
	public int value;
	/** 最大值 */
	public int max;
	/** 键值 */
	public String key;

	/** 主键 */
	public int getKey() {
		return this.configID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}