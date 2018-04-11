package com.wanniu.game.friend.data;

import java.util.ArrayList;

import com.wanniu.game.common.CurrencyData;
import com.wanniu.game.data.SShopCO;

public class FriendShop extends SShopCO {

	public ArrayList<CurrencyData> moneyReqList;

	@Override
	public void initProperty() {
		super.initProperty();
		ArrayList<CurrencyData> moneyReqList = new ArrayList<>();
		boolean has = true;
		int type = 0;
		int value = 0;
		for (int i = 1; i <= 3; ++i) {
			switch (i) {
			case 1:
				if (this.type1 > 0 && this.value1 > 0) {
					has = true;
					type = this.type1;
					value = this.value1;
				} else {
					has = false;
				}
				break;
			case 2:
				if (this.type2 > 0 && this.value2 > 0) {
					has = true;
					type = this.type2;
					value = this.value2;
				} else {
					has = false;
				}
				break;
			case 3:
				if (this.type3 > 0 && this.value3 > 0) {
					has = true;
					type = this.type3;
					value = this.value3;
				} else {
					has = false;
				}
				break;
			}
			if (has) {
				moneyReqList.add(new CurrencyData(type, value));
			}
		}
		this.moneyReqList = moneyReqList;

	}

}
