package com.wanniu.game.data.ext;

import java.util.ArrayList;
import java.util.List;

import com.wanniu.core.logfs.Out;
import com.wanniu.game.bag.WNBag.SimpleItemInfo;
import com.wanniu.game.common.CommonUtil;
import com.wanniu.game.data.OlGiftCO;
import com.wanniu.game.item.ItemUtil;

import pomelo.item.ItemOuterClass.MiniItem;

public class OlGiftExt extends OlGiftCO {
	public List<SimpleItemInfo> items;

	@Override
	public void initProperty() {
		items = new ArrayList<>();
		items = CommonUtil.parseItems(reward);
	}

	public final List<MiniItem> getMiniItems() {
		// 创建MiniItem对象
		List<MiniItem> itemList = new ArrayList<MiniItem>();
		if (this.items != null) {
			for (int i = 0; i < this.items.size(); i++) {
				SimpleItemInfo item = this.items.get(i);
				if (null == item) {
					continue;
				}
				MiniItem.Builder miniItem = ItemUtil.getMiniItemData(item.itemCode, item.itemNum);
				if (null == miniItem) {
					Out.error(this.getClass(), "->[" , item.itemCode , "] is not found");
					continue;
				}

				itemList.add(miniItem.build());
			}
		}
		return itemList;
	}

}
