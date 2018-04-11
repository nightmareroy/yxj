/*
 * Copyright © 2017 qeng.cn All Rights Reserved.
 * 
 * 感谢您加入清源科技，不用多久，您就会升职加薪、当上总经理、出任CEO、迎娶白富美、从此走上人生巅峰
 * 除非符合本公司的商业许可协议，否则不得使用或传播此源码，您可以下载许可协议文件：
 * 
 * 		http://www.noark.xyz/qeng/LICENSE
 *
 * 1、未经许可，任何公司及个人不得以任何方式或理由来修改、使用或传播此源码;
 * 2、禁止在本源码或其他相关源码的基础上发展任何派生版本、修改版本或第三方版本;
 * 3、无论你对源代码做出任何修改和优化，版权都归清源科技所有，我们将保留所有权利;
 * 4、凡侵犯清源科技相关版权或著作权等知识产权者，必依法追究其法律责任，特此郑重法律声明！
 */
package com.wanniu.game.item;

/**
 * 虚拟物品类型.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
public enum VirtualItemType {
	GOLD("gold", "银两"), //
	DIAMOND("diamond", "元宝"), //
	CASH("cash", "绑元"), //
	EXP("exp", "经验"), //
	UPEXP("upexp", "修为"), //
	SP("sp", "技能点"), //
	PRESTIGE("prestige", "声望"), //
	RINGPRES("ringpres", "魔界威望"), //
	REMAIN("remain", "计数道具"), //
	FRIENDLY("friendly", "友情点"), //
	ACHIPOINT("achipoint", "成就点数"), //
	SOLOPOINT("solopoint", "单挑积分"), //
	GUILDPOINT("guildpoint", "仙盟副本积分"), //
	CONSUMEPOINT("consumepoint", "商城积分"), //
	WINGEXP("wingexp", "灵气"), //
	TREASURESPOINT("treasurespoint", "君王宝藏积分"), //
	PEQUIP("pequip", "紫色装备"), //
	OEQUIP("oequip", "橙色装备"), //
	GEMS("gems", "宝石"), //
	VEXP("vexp", "大量经验"), //
	FATE("fate", "轩辕值");

	private final String itemcode;
	private final String desc;

	private VirtualItemType(String itemcode, String desc) {
		this.itemcode = itemcode;
		this.desc = desc;
	}

	public String getItemcode() {
		return itemcode;
	}

	public String getDesc() {
		return desc;
	}
}