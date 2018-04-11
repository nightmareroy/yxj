package com.wanniu.game.data.ext;

import java.util.ArrayList;
import java.util.List;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.ClassUtil;
import com.wanniu.game.data.GShopCO;

import pomelo.area.GuildShopHandler.ShopMoneyInfo;

public class GShopExt extends GShopCO {
	public List<ShopMoneyInfo> moneyReqList;

	public void initProperty() {
		List<ShopMoneyInfo> _moneyReqList = new ArrayList<ShopMoneyInfo>();
		for (int i = 1; i <= 3; ++i) {
			String typeKey = "type" + i;
			String valueKey = "value" + i;

			try {
				if (null != ClassUtil.getProperty(this, typeKey) && null != ClassUtil.getProperty(this, valueKey)) {
					ShopMoneyInfo.Builder moneyInfo = ShopMoneyInfo.newBuilder();
					moneyInfo.setType((int) ClassUtil.getProperty(this, typeKey));
					moneyInfo.setValue((int) ClassUtil.getProperty(this, valueKey));
					_moneyReqList.add(moneyInfo.build());
				}
			} catch (Exception e) {
				Out.error(e);
			}
		}
		this.moneyReqList = _moneyReqList;
	}
}
