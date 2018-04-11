package com.wanniu.game.data; 

public class ArmourAttributeCO { 

	/** 编号 */
	public int iD;
	/** 圣甲部位 */
	public String name;
	/** 奖励属性 */
	public String prop;
	/** ICON名称 */
	public String icon;
	/** 部位图片名称 */
	public String picName;
	/** 成就类型ID */
	public int typeId;
	/** 模型文件 */
	public String avatarId;
	/** 模型缩放倍数 */
	public float modelPercent;
	/** 模型旋转偏移 */
	public float roteY;
	/** 模型高度偏移 */
	public float modelY;
	/** 模型远近偏移 */
	public float modelZ;

	/** 主键 */
	public int getKey() {
		return this.iD; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}