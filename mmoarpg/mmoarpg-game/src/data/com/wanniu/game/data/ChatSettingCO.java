package com.wanniu.game.data; 

public class ChatSettingCO { 

	/** 频道ID */
	public int channelID;
	/** 频道 */
	public String channel;
	/** 频道简称 */
	public String channelShort;
	/** 等级 */
	public int openLv;
	/** 发言间隔 */
	public int coolDown;
	/** 相同发言额外冷却 */
	public float coolDownExtra;
	/** 默认字色 */
	public String fontColor;
	/** 语音支持 */
	public int isVoice;
	/** 自动语音 */
	public int autoVoice;
	/** 匿名 */
	public int anonymous;
	/** 留言 */
	public int callSomeone;
	/** 留言次数 */
	public int callTimes;
	/** 是否可屏蔽 */
	public int showConfigure;
	/** 设置字色 */
	public int fontConfigure;

	/** 主键 */
	public int getKey() {
		return this.channelID; 
	}

	/** 构造属性 */
	public void initProperty() { }

	/** 构造前置属性 */
	public void beforeProperty() { }

}