package com.wanniu.game.vip;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pomelo.area.VipHandler.VipBuyGiftData;
import pomelo.area.VipHandler.VipGetGiftData;

public final class VipData {

	public final List<VipGetGiftData> getGift;

	public final List<VipBuyGiftData> buyGift;

	public final String vipTime;

	public VipData(Date date) {
		this.getGift = new ArrayList<>();
		this.buyGift = new ArrayList<>();
		this.vipTime = String.valueOf(date != null ? date.getTime() : System.currentTimeMillis());
	}

}