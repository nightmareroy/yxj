package com.wanniu.game.data; 

public class LuxurySignCO { 

	/** ID */
	public int id;
	/** 名称 */
	public String name;
	/** 奖励内容1 */
	public String item1code;
	/** 数量 */
	public int item1count;
	/** 奖励内容2 */
	public String item2code;
	/** 数量 */
	public int item2count;
	/** 奖励内容3 */
	public String item3code;
	/** 数量 */
	public int item3count;
	/** 奖励内容4 */
	public String item4code;
	/** 数量 */
	public int item4count;

	/** 主键 */
	public int getKey() {
		return this.id; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}