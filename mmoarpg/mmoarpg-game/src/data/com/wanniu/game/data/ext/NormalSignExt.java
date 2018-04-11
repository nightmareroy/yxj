package com.wanniu.game.data.ext;

import java.util.ArrayList;
import java.util.List;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.ClassUtil;
import com.wanniu.game.bag.WNBag.SimpleItemInfo;
import com.wanniu.game.data.NormalSignCO;

import pomelo.item.ItemOuterClass.MiniItem;

public class NormalSignExt extends NormalSignCO {

	public List<SimpleItemInfo> items = null;

	@Override
	public void initProperty() {
		items = new ArrayList<>();
		for (int i = 1; i <= 1; ++i) {
			String codeKey = "item" + i + "code";
			String countKey = "item" + i + "count";
			try {
				if (ClassUtil.getProperty(this, codeKey) != null && ClassUtil.getProperty(this, countKey) != null) {
					SimpleItemInfo item = new SimpleItemInfo();
					item.itemCode = ClassUtil.getProperty(this, codeKey).toString();
					item.itemNum = (int) ClassUtil.getProperty(this, countKey);
					items.add(item);
				}
			} catch (Exception e) {
				Out.error(e);
			}
		}
	}

	public final MiniItem.Builder[] getMiniItems() {
		if (this.items != null && this.items.size() > 0) {
			MiniItem.Builder[] miniItems = new MiniItem.Builder[this.items.size()];
			for (int i = 0; i < this.items.size(); i++) {
				miniItems[i] = MiniItem.newBuilder();
				miniItems[i].setCode(items.get(i).itemCode);
				miniItems[i].setGroupCount(items.get(i).itemNum);
			}
			return miniItems;
		}
		return null;
	}

}
