package com.wanniu.game.onlineGift;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.OlGiftExt;

public class OlGiftConfig {

	private static OlGiftConfig instance;

	public static OlGiftConfig getInstance() {
		if (instance == null) {
			instance = new OlGiftConfig();
		}
		return instance;
	}

	private OlGiftConfig() {
		this.olgTable = GameData.OlGifts;
	}

	private Map<Integer, OlGiftExt> olgTable;

	/*** 礼物列表 */
	public List<OlGiftExt> getPropList() {
		return new ArrayList<OlGiftExt>(olgTable.values());
	}

	/*** 查找礼物 */
	public OlGiftExt getPropById(int olgId) {
		if (olgTable.containsKey(olgId)) {
			return olgTable.get(olgId);
		}
		return null;
	}

	public List<OlGiftExt> getPropListByLevel(int giftType, int upLevel, int level) {
		List<OlGiftExt> lstOlg = new ArrayList<OlGiftExt>();
		for (OlGiftExt gift : olgTable.values()) {
			if (giftType != gift.type) {
				continue;
			}

			if (upLevel > 0) { // 有进阶等级
				if (upLevel < gift.downOrder || upLevel > gift.upOrder) {
					continue;
				}
			} else {
				if (level < gift.lvDown || level > gift.lvUp) {
					continue;
				}
			}
			lstOlg.add(gift);
		}
		return lstOlg;
	};

	public OlGiftExt getPropByLevelAndTime(int upLevel, int level, int time) {
		// 不知道此函数是否翻译对。原逻辑注释在下面。。。。
		for (OlGiftExt gift : olgTable.values()) {
			if (gift.upOrder == upLevel && gift.time == time) {
				if (upLevel == 0) {
					if (gift.lvDown <= level && gift.lvUp >= level) {
						return gift;
					}
				}
				return gift;
			}

		}

		return null;
		// var opts = { UpOrder: upLevel, Time:time };
		// if(!upLevel){
		// opts.LvDown = { '$lte':level };
		// opts.LvUp = { '$gte':level };
		// }
		// var list = dataAccessor.onlineGiftProps.find(opts, {$decouple: false});
		// if(list.length > 0){
		// return list[0];
		// }
		// return null;
	};

}
