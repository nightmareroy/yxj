package com.wanniu.game.request.bag;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.bag.BagUtil;
import com.wanniu.game.bag.WNBag;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.ItemIdConfigExt;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.BagHandler.PackUpBagRequest;
import pomelo.area.BagHandler.PackUpBagResponse;

/**
 * 整理背包
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.bagHandler.packUpBagRequest")
public class PackUpBagHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		PackUpBagRequest req = PackUpBagRequest.parseFrom(pak.getRemaingBytes());
		final int _type = req.getC2SType(); // 类型来源
		WNPlayer player = (WNPlayer) pak.getPlayer();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				PackUpBagResponse.Builder res = PackUpBagResponse.newBuilder();
				
				// 不可整理
				WNBag store = BagUtil.getStoreByType(player, _type);
				if (store == null) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				
				// 操作太频繁
				if (!store.canPackUp()) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("BAG_PACKUP_CDTIME"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				packUpBag(store);

				res.setS2CCode(OK);
				res.setS2CType(_type);
				res.addAllS2CBagGrids(store.getGrids4PayLoad());
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

	public void packUpBag(WNBag bag) {
		bag.bagGridPackUpTime = System.currentTimeMillis();
		
		// 对data进行合并
		List<NormalItem> data = bag._packUpStackNum();
		Collections.sort(data, new Comparator<NormalItem>() {
			@Override
			public int compare(NormalItem left, NormalItem right) {
				if (left.prop == null) {
					Out.error("Item compare prop error, code:", left.itemDb.code, ", left.prop:", left.prop);
				}
//				ItemTypeConfigExt itemtypeconfig_l = GameData.ItemTypeConfigs.get(left.prop.itemType);
//				ItemTypeConfigExt itemtypeconfig_r = GameData.ItemTypeConfigs.get(right.prop.itemType);
				int flag = 0;
//				if (itemtypeconfig_l.order == itemtypeconfig_r.order) {
					ItemIdConfigExt itemidconfig_l = GameData.ItemIdConfigs.get(left.prop.type);
					ItemIdConfigExt itemidconfig_r = GameData.ItemIdConfigs.get(right.prop.type);
					if (itemidconfig_l.order == itemidconfig_r.order) {
						if (left.prop.qcolor == right.prop.qcolor) {
							if (left.itemDb.gotTime.getTime() == right.itemDb.gotTime.getTime()) {
								flag = 0;
							} else if (left.itemDb.gotTime.getTime() < right.itemDb.gotTime.getTime()) {
								flag = -1;
							} else {
								flag = 1;
							}
						} else if (left.prop.qcolor < right.prop.qcolor) {
							flag = 1;
						} else {
							flag = -1;
						}
					} else if (itemidconfig_l.order < itemidconfig_r.order) {
						flag = -1;
					} else {
						flag = 1;
					}
//				} else if (itemtypeconfig_l.order < itemtypeconfig_r.order) {
//					flag = -1;
//				} else {
//					flag = 1;
//				}
				return flag;
			}
//					if (left.prop.qcolor == right.prop.qcolor) {
//						if (left.prop.itemType == right.prop.itemType) {
//							if (left.prop.itemSecondType == right.prop.itemSecondType) {
//								if (left.itemDb.gotTime.getTime() == right.itemDb.gotTime.getTime()) {
//									flag = 0;
//								} else if (left.itemDb.gotTime.getTime() < right.itemDb.gotTime.getTime()) {
//									flag = -1;
//								} else {
//									flag = 1;
//								}
//							} else if (left.prop.itemSecondType < right.prop.itemSecondType) {
//								flag = -1;
//							} else {
//								flag = 1;
//							}
//						} else if (left.prop.itemType < right.prop.itemType) {
//							flag = -1;
//						} else {
//							flag = 1;
//						}
//					} else if (left.prop.qcolor < right.prop.qcolor) {
//						flag = -1;
//					} else {
//						flag = 1;
//					}

		});

		bag.bagGrids.clear();
		for (int i = 0; i < data.size(); ++i) {
			bag.bagGrids.put(i + 1, data.get(i));
		}

		// for(int i = 0; i < wnPlayer.getPlayerAttach().bagGridCount; ++i){
		// if(i < newData.size()) {
		// bag.bagGrids.put(i + 1, newData.get(i));
		// } else {
		// bag.bagGrids.put(i + 1, null);
		// }
		// }
		bag.usedGridCount = data.size();
		bag._gridNumChangePush(true);
	};
}
