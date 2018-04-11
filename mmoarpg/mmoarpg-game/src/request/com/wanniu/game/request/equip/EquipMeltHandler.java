package com.wanniu.game.request.equip;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.bag.WNBag;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.BiLogType;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const.TaskType;
import com.wanniu.game.data.BloodListCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.MeltConfigCO;
import com.wanniu.game.data.base.DEquipBase;
import com.wanniu.game.equip.NormalEquip;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.item.po.ItemSpeData;
import com.wanniu.game.item.po.PlayerItemPO;
import com.wanniu.game.mail.MailUtil;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.mail.data.MailData;
import com.wanniu.game.mail.data.MailSysData;
import com.wanniu.game.player.BILogService;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.EquipHandler.EquipMeltRequest;
import pomelo.area.EquipHandler.EquipMeltResponse;
import pomelo.item.ItemOuterClass.MiniItem;

/**
 * 熔炼装备或血脉
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.equipHandler.equipMeltRequest")
public class EquipMeltHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();
		WNBag wnBag = player.getWnBag();

		EquipMeltRequest req = EquipMeltRequest.parseFrom(pak.getRemaingBytes());
		List<Integer> indexs = new ArrayList<>(new HashSet<>(req.getC2SIndexsList()));

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				EquipMeltResponse.Builder res = EquipMeltResponse.newBuilder();

				if (indexs.size() == 0) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("MELT_NOTHING"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				int totalGold = 0;
				List<Object[]> tcCodes = new ArrayList<>();
				// List<Integer> Qcolor = new ArrayList<>();
				for (int i = 0; i < indexs.size(); ++i) {
					NormalItem item = wnBag.getItem(indexs.get(i));
					if (item == null || (!item.isEquip() && !item.isBlood())) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("PARAM_INVALID"));
						body.writeBytes(res.build().toByteArray());
						return;
					}

					if (item.isEquip()) {
						NormalEquip equip = (NormalEquip) item;
						if (((DEquipBase) equip.prop).noMelt == 1) {
							res.setS2CCode(FAIL);
							res.setS2CMsg(LangService.getValue("CAN_NOT_MELT"));
							body.writeBytes(res.build().toByteArray());
							return;
						}

						MeltConfigCO prop = ItemUtil.getMeltProp(((DEquipBase) equip.prop).meltLevel, equip.getQColor());
						if (prop == null) {
							res.setS2CCode(FAIL);
							res.setS2CMsg(LangService.getValue("PARAM_INVALID"));
							body.writeBytes(res.build().toByteArray());
							return;
						}
						totalGold += prop.costGold;
						tcCodes.add(new Object[] { prop.tcCode, equip.isBinding() });

						// BI
						if (equip.getQColor() >= Const.ItemQuality.ORANGE.getValue()) {
							ItemSpeData speData = equip.itemDb.speData;
							StringBuffer sb = new StringBuffer();
							if (speData != null) {
								sb.append(speData.baseAtts.toString());
								if (speData.extAtts != null) {
									sb.append("|||").append(speData.extAtts.toString());
								}
								if (speData.legendAtts != null) {
									sb.append("|||").append(speData.legendAtts.toString());
								}
							}
							PlayerUtil.bi(this.getClass(), BiLogType.Smelt, player, equip.itemDb.code, equip.itemDb.id, sb.toString());
						}
					} else if (item.isBlood()) {
						BloodListCO bloodListCO = GameData.BloodLists.get(GameData.Quests.get(item.itemCode()).min);
						tcCodes.add(new Object[] { bloodListCO.melting, false });
					}
				}

				List<NormalItem> tcItems = new ArrayList<>();
				for (Object[] v : tcCodes) {
					List<NormalItem> items = ItemUtil.createItemsByTcCode((String) v[0]);
					for (NormalItem item : items) {
						if (item.isEquip()) {
							Const.ForceType forceType = (boolean) v[1] ? Const.ForceType.BIND : Const.ForceType.UN_BIND;
							int bindType = ItemUtil.getPropBindType(item.prop, forceType);
							item.setBind(bindType);
						}
						tcItems.add(item);
					}
				}

				List<NormalItem> addItems = ItemUtil.getPackUpItems(tcItems);

				int len = ItemUtil.getPackUpItemsNum(addItems) - indexs.size();
				if (len < 0) {
					len = 0;
				}

				if (!player.moneyManager.costGold(totalGold, Const.GOODS_CHANGE_TYPE.melt)) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GOLD_NOT_ENOUGH"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				for (int i = 0; i < indexs.size(); ++i) {
					wnBag.discardItemByPos(indexs.get(i), 1, false, Const.GOODS_CHANGE_TYPE.melt);
				}

				List<PlayerItemPO> notAddItems = new ArrayList<>();
				List<MiniItem> showItems = new ArrayList<>();
				int getExp = 0;
				for (int i = 0; i < addItems.size(); ++i) {
					NormalItem addItem = addItems.get(i);
					if (addItem.itemDb.code.equals("exp")) {
						getExp += addItem.getWorth();
					} else if (addItem.itemDb.code.equals("gold")) {
						// getGold += addItem.getWorth();
					} else {
						MiniItem.Builder item = ItemUtil.getMiniItemData(addItem.itemDb.code, addItem.itemDb.groupCount);
						if (item != null) {
							showItems.add(item.build());
						}
					}

					if (wnBag.testAddEntityItem(addItems.get(i), true)) {
						wnBag.addEntityItem(addItems.get(i), Const.GOODS_CHANGE_TYPE.melt, null, false, true);
					} else {
						notAddItems.add(addItem.itemDb);
					}
				}
				if (notAddItems.size() > 0) {
					MailData mailData = new MailSysData(SysMailConst.BAG_FULL_MELTING);
					mailData.entityItems = notAddItems;
					MailUtil.getInstance().sendMailToOnePlayer(player.getId(), mailData, GOODS_CHANGE_TYPE.melt);
					player.sendSysTip(LangService.getValue("BAG_FULL_SMELTING_CHEST"), Const.TipsType.BLACK);
				}

				player.taskManager.dealTaskEvent(TaskType.EQUIP_MELT, 1);

				// 上报自动
				BILogService.getInstance().ansycReportMeltCultivate(player.getPlayer(), addItems);

				res.setS2CCode(OK);
				res.setS2CExp(getExp);
				res.addAllS2CItem(showItems);

				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}