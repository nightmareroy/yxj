package com.wanniu.game.shopMall;

import com.alibaba.fastjson.JSON;

import pomelo.area.ShopMallHandler.MallScoreItem;

public class ShopMallItemData {
	public String id = "";
	public String code = "";
	public int groupCount;
	public int consumeScore; // 消费积分
	public int isSellOut; // 是否卖完 1.卖完 0.没卖完
	public int bindType;
	public int isSold;

	public ShopMallItemData() {

	}

	public final MallScoreItem createMallScoreItem() {
		MallScoreItem.Builder builder = MallScoreItem.newBuilder();
		builder.setId(id);
		builder.setCode(code);
		builder.setGroupCount(groupCount);
		builder.setConsumeScore(consumeScore);
		builder.setIsSellOut(isSellOut);
		builder.setBindType(bindType);
		return builder.build();
	}

	public final String toJsonString() {
		return JSON.toJSONString(this);
	}
}
