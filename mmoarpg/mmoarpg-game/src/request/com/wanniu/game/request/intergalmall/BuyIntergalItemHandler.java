package com.wanniu.game.request.intergalmall;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const.ForceType;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const.IntergalMallType;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.base.IntergalShopBase;
import com.wanniu.game.intergalmall.IntergalMallConfig;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.player.bi.LogReportService;
import com.wanniu.game.poes.IntergalMallPO;

import pomelo.area.IntergalMallHandler.BuyIntergalItemRequest;
import pomelo.area.IntergalMallHandler.BuyIntergalItemResponse;

/**
 * 购买积分商城道具
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.intergalMallHandler.buyIntergalItemRequest")
public class BuyIntergalItemHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();

		BuyIntergalItemRequest req = BuyIntergalItemRequest.parseFrom(pak.getRemaingBytes());

		int shopType = req.getC2SType();
		int itemId = req.getC2SItemId();
		int buyNum = req.getC2SNum();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				BuyIntergalItemResponse.Builder res = BuyIntergalItemResponse.newBuilder();

				// 购买数量判定.
				if (buyNum <= 0 || buyNum > 9999) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				IntergalShopBase prop = IntergalMallConfig.getInstance().getIntergalMallProp(shopType, itemId);

				if (prop == null) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("ITEM_NULL"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				// 道具 无效
				if (prop.isShow == 0) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				// 未开始
				if (prop.periodStartDate != null && prop.periodStartDate.getTime() > System.currentTimeMillis()) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				// 已结束
				if (prop.periodEndDate != null && prop.periodEndDate.getTime() < System.currentTimeMillis()) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				IntergalMallPO intergalMallPO = player.getIntergalMallManager().intergalMallPO;
				// 已经购买的次数
				Map<Integer, Integer> shopHasBuyMap = intergalMallPO.hasBuyItem.get(shopType);
				if (shopHasBuyMap == null) {
					shopHasBuyMap = new HashMap<>();
					intergalMallPO.hasBuyItem.put(shopType, shopHasBuyMap);
				}
				int hasBuyNum = shopHasBuyMap.containsKey(itemId) ? shopHasBuyMap.get(itemId) : 0;

				//vip增加的次数
				int vip = player.baseDataManager.getVip();
				int add = 0;
				if (vip > 0) {
					add = GameData.Cards.get(vip).prv7;
				}
				
				// 购买次数限制
				if (prop.buyTimes != -1 && hasBuyNum + buyNum > prop.buyTimes + add) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SHOPMALL_REMAINNUM_NOT_ENOUGH"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				// 验证积分
				int needNum = prop.price;
				if (prop.price2 > 0) {
					needNum = prop.price2;
				}

				// 添加溢出保护
				if (1L * needNum * buyNum > Integer.MAX_VALUE) {
					needNum = Integer.MAX_VALUE;
				} else {
					needNum = needNum * buyNum;
				}

				boolean pointEnough = false;
				String msg = null;
				if (shopType == IntergalMallType.MallShop) {
					pointEnough = player.moneyManager.enoughConsumePoint(needNum);
					msg = LangService.getValue("PLAER_CONSUMEPOINT_NOT_ENOUGH");
				} else if (shopType == IntergalMallType.FateShop) {
					pointEnough = player.moneyManager.enoughXianYuan(needNum);
					msg = LangService.getValue("PLAER_XIANYUAN_NOT_ENOUGH");
				} else if (shopType == IntergalMallType.AthleticShop) {
					pointEnough = player.soloManager.enoughSolopoint(needNum);
					msg = LangService.getValue("SOLOPOINT_NOT_ENOUGH");
				} else if (shopType == IntergalMallType.GuildShop) {
					pointEnough = player.guildManager.enoughContribution(needNum);
					msg = LangService.getValue("GUILD_POINT_NOT_ENOUGH");
				} else if (shopType == IntergalMallType.SundryShop) {
					pointEnough = player.moneyManager.enoughGold(needNum);
					msg = LangService.getValue("GOLD_NOT_ENOUGH");
				}
				if (!pointEnough) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(msg);
					body.writeBytes(res.build().toByteArray());
					return;
				}

				Out.info("积分商城购买道具 playerId=", player.getId(), ",itemId=", prop.itemCode, ",count=", prop.num * buyNum);

				// 扣除消耗货币
				int currencyNum = 0;
				if (shopType == IntergalMallType.MallShop) {
					player.moneyManager.costConsumePoint(needNum, GOODS_CHANGE_TYPE.intergalmall);
					currencyNum = player.moneyManager.getConsumePoint();
				} else if (shopType == IntergalMallType.FateShop) {
					player.moneyManager.costXianYuan(needNum, GOODS_CHANGE_TYPE.intergalmall);
					currencyNum = player.moneyManager.getXianYuan();
				} else if (shopType == IntergalMallType.AthleticShop) {
					player.soloManager.costSolopoint(needNum, GOODS_CHANGE_TYPE.intergalmall);
					currencyNum = player.soloManager.getSolopoint();
				} else if (shopType == IntergalMallType.GuildShop) {
					player.guildManager.costContribution(needNum, GOODS_CHANGE_TYPE.intergalmall);
					currencyNum = player.guildManager.getContribution();
				} else if (shopType == IntergalMallType.SundryShop) {
					player.moneyManager.costGold(needNum, GOODS_CHANGE_TYPE.intergalmall);
					currencyNum = player.baseDataManager.baseData.gold;
				}

				// 发放道具
				player.bag.addCodeItemMail(prop.itemCode, prop.num * buyNum, ForceType.getE(prop.isBind), GOODS_CHANGE_TYPE.intergalmall, SysMailConst.BAG_FULL_COMMON);

				// 更新购买次数
				hasBuyNum += buyNum;
				shopHasBuyMap.put(itemId, hasBuyNum);

				// // 更新全服购买次数
				// globalNum += 1;
				// IntergalMallGlobalService.getInstance().update(shopType, prop.iD, globalNum);

				if (prop.buyTimes != -1) {
					res.setLastcount(prop.buyTimes - hasBuyNum + add);
				} else {
					res.setLastcount(-1);
				}

				// 上报
				LogReportService.getInstance().ansycReportShop(player, prop.itemCode, prop.num * buyNum, shopType + 10, needNum);

				res.setCurrencyNum(currencyNum);
				res.setTotalNum(player.bag.findItemNumByCode(prop.itemCode));
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
