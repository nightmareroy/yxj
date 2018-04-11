package com.wanniu.game.request.bag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.area.Area;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.bag.WNBag;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.Const.ItemSecondType;
import com.wanniu.game.common.Const.TaskKind;
import com.wanniu.game.data.FashionItemCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.base.DItemBase;
import com.wanniu.game.data.base.MapBase;
import com.wanniu.game.data.base.TaskBase;
import com.wanniu.game.data.ext.BaseDataExt;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.item.po.PlayerItemPO;
import com.wanniu.game.mail.MailUtil;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.mail.data.MailData;
import com.wanniu.game.mail.data.MailSysData;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.request.task.AcceptTaskHandler.AcceptTaskResult;
import com.wanniu.game.task.TaskUtils;

import Xmds.RefreshPlayerPropertyChange;
import pomelo.area.BagHandler.UseItemRequest;
import pomelo.area.BagHandler.UseItemResponse;
import pomelo.area.FashionHandler.FashionGetPush;
import pomelo.area.PetNewHandler.SummonPetResponse;
import pomelo.item.ItemOuterClass.MiniItem;

/**
 * 使用物品
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.bagHandler.useItemRequest")
public class UseItemHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		UseItemRequest req = UseItemRequest.parseFrom(pak.getRemaingBytes());
		int index = req.getC2SGridIndex();
		int num = req.getC2SNum();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				UseItemResponse.Builder res = UseItemResponse.newBuilder();
				if (null == player) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
					body.writeBytes(res.build().toByteArray());
					PlayerUtil.logWarnIfPlayerNull(pak);
					return;
				}
				WNBag wnBag = player.getWnBag();
				if (num <= 0) {
					res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());
					return;
				}

				NormalItem item = wnBag.getItem(index);

				if (item == null || ItemUtil.isEquipByItemType(item.prop.itemType)) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				// 不可使用
				if (!item.canUse()) {
					res.setS2CCode(PomeloRequest.FAIL);
					res.setS2CMsg(LangService.getValue("ITEM_CAN_NOT_USE"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				// 数量不足
				if (item.itemDb.groupCount < num || num <= 0) {
					res.setS2CCode(PomeloRequest.FAIL);
					res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				// 等级限制
				DItemBase template = (DItemBase) item.getTemplate();
				if (template.levelReq > player.getPlayer().level) {
					res.setS2CCode(PomeloRequest.FAIL);
					res.setS2CMsg(LangService.getValue("BAG_PLAYER_LEVEL_NOT_ENOUGH_CANNOT_USE"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				if (item.prop.code.equals(Const.ITEM_CODE.Changename.value)) {// 改名卡
					res.setS2CCode(OK);
					res.setIsGain(2); // 2表示改名卡
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (item.prop.itemType == Const.ItemType.Chest.getValue()) {// 宝箱
					int needDiamond = template.min;
					// 钻石币不够
					if (needDiamond > 0 && !player.moneyManager.enoughDiamond(needDiamond)) {
						res.setS2CCode(PomeloRequest.FAIL);
						res.setS2CMsg("");
						body.writeBytes(res.build().toByteArray());
						return;
					}

					String tc = template.chestTC.get(player.getPro());
					List<NormalItem> tcItems = new ArrayList<>();
					for (int i = 0; i < num; i++) {
						// String realTc = ItemConfig.getInstance().getRealTC(tc, player.getLevel());
						List<NormalItem> items = ItemUtil.createItemsByRealTC(tc, player.getLevel());
						tcItems.addAll(items);
					}

					List<NormalItem> addItems = ItemUtil.getPackUpItems(tcItems);

					// 检查背包格子数
					int needGridNum = ItemUtil.getPackUpItemsNum(addItems);
					if (needGridNum > 0) {
						if (!wnBag.testEmptyGridLarge(1, false)) {
							res.setS2CCode(PomeloRequest.FAIL);
							res.setS2CMsg("");// 已弹框，不需要再给提示
							body.writeBytes(res.build().toByteArray());
							return;
						}

						if (wnBag.emptyGridNum() < needGridNum) {
							res.setS2CCode(PomeloRequest.FAIL);
							res.setS2CMsg(LangService.getValue("STORE_SPACE_NOT_ENOUGH"));
							body.writeBytes(res.build().toByteArray());
							return;
						}
					}

					wnBag.discardItemByPos(index, num, false, Const.GOODS_CHANGE_TYPE.use);

					List<MiniItem> chest = new ArrayList<>();

					List<PlayerItemPO> notAddItems = new ArrayList<>();
					for (int i = 0; i < addItems.size(); ++i) {
						NormalItem addItem = addItems.get(i);
						// 辉总说去掉：2017-08-18 12:41:41, 由 吴永辉 激活。 在主干上，把bag里的使用物品出来的设置绑定的代码去掉。
						// if (item.isBinding()) {
						// addItem.setBind(1);
						// }

						if (wnBag.testAddEntityItem(addItem, true)) {
							if (!addItem.isVirtual()) {
								chest.add(ItemUtil.getMiniItemData(addItem.itemDb.code, addItem.itemDb.groupCount, Const.ForceType.getE(addItem.getBind())).build());
							}
							wnBag.addEntityItem(addItem, Const.GOODS_CHANGE_TYPE.use, null, false, false);
						} else {
							notAddItems.add(addItem.itemDb);
						}
					}
					if (notAddItems.size() > 0) {
						MailData mailData = new MailSysData(SysMailConst.BAG_FULL_OPENBOX);
						mailData.entityItems = notAddItems;
						MailUtil.getInstance().sendMailToOnePlayer(player.getId(), mailData, GOODS_CHANGE_TYPE.use);
						player.sendSysTip(LangService.getValue("BAG_FULL_SMELTING_CHEST"), Const.TipsType.BLACK);
					}

					if (needDiamond > 0) {
						player.moneyManager.costDiamond(needDiamond, Const.GOODS_CHANGE_TYPE.use);
					}

					res.setS2CCode(PomeloRequest.OK);
					res.addAllS2CChest(chest);
					body.writeBytes(res.build().toByteArray());
				} else if (item.prop.itemType == Const.ItemType.Potion.getValue()) {
					// 药剂使用
					if (num != 1) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("BAG_ONLY_ONE_CAN_BE_USED_AT_A_TIME"));
						body.writeBytes(res.build().toByteArray());
						return;
					}
					// cd 判断
					if (item.isCD()) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("BAG_USE_IN_CD"));
						body.writeBytes(res.build().toByteArray());
						return;
					}

					Area area = player.getArea();
					MapBase areaProp = AreaUtil.getAreaProp(area.areaId);
					if (areaProp.useAgent == 0) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("BAG_THIS_SENCE_CANNOT_USE_MEDICAMENT"));
						body.writeBytes(res.build().toByteArray());
						return;
					}

					player.bag.setCD(item.prop.itemSecondType, index);
					player.bag.discardItemByPos(index, 1, GOODS_CHANGE_TYPE.use);
					GetItemChanagePropertyResult data = ItemUtil.getItemChanageProperty(item);

					if (data.bPet) {
						player.refreshPlayerPetPropertyChange(data.itemData);
					} else {

						player.bufferManager.add(data.itemData);
						player.refreshPlayerPropertyChange(data.itemData);
					}

					player.sendSysTip(LangService.getValue("GAIN_POTION"), Const.TipsType.NO_BG);
					res.setS2CCode(OK);
					res.setIsGain(1); // 1表示药剂
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (item.prop.itemType == Const.ItemType.Mate.getValue() && item.prop.itemSecondType == Const.ItemSecondType.rideItem.getValue()) {
					// 坐骑皮肤使用
					if (num != 1) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("BAG_ONLY_ONE_CAN_BE_USED_AT_A_TIME"));
						body.writeBytes(res.build().toByteArray());
						return;
					}
					int ret = player.mountManager.addNewSkin(template.min);
					if (ret == 1) {
						player.bag.discardItemByPos(index, 1, GOODS_CHANGE_TYPE.use);
						res.setS2CCode(OK);
						body.writeBytes(res.build().toByteArray());
						return;
					} else if (ret == -1) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("BAG_HAD_THIS_SKIN"));
						body.writeBytes(res.build().toByteArray());
						return;
					} else if (ret == -2) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("BAG_SKIN_NULL"));
						body.writeBytes(res.build().toByteArray());
						return;
					} else if (ret == -3) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("MOUNT_NOT_HAVE"));
						body.writeBytes(res.build().toByteArray());
						return;
					}
				} else if (item.prop.itemType == Const.ItemType.Mate.getValue() && item.prop.itemSecondType == Const.ItemSecondType.petItem.getValue() && ((DItemBase) item.prop).prop.equals("pet")) {
					DItemBase prop = (DItemBase) item.prop;
					int petId = prop.min;
					String msg = player.petNewManager.summonPetByItem(petId);
					if (msg != null) {//宠物已经拥有，就是分解碎片
						BaseDataExt baseDataExt = GameData.BaseDatas.get(petId);
						for(int i=0; i<num; i++) {
							player.bag.addCodeItemMail(baseDataExt.petItemCode, baseDataExt.itemCount, Const.ForceType.DEFAULT, Const.GOODS_CHANGE_TYPE.pet, SysMailConst.BAG_FULL_COMMON);
						}
						player.bag.discardItemByPos(index, num, GOODS_CHANGE_TYPE.use);
					}else {
						player.bag.discardItemByPos(index, 1, GOODS_CHANGE_TYPE.use);//第一次召唤最多只使用消耗一个
					}
					res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (item.prop.itemType == Const.ItemType.Misc.getValue() && item.prop.itemSecondType == Const.ItemSecondType.petItem.getValue()) {
					if (num != 1) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("BAG_ONLY_ONE_CAN_BE_USED_AT_A_TIME"));
						body.writeBytes(res.build().toByteArray());
						return;
					}
					SummonPetResponse result = player.petNewManager.summonPet(template.min).build();
					boolean ret = result.getS2CCode() == OK ? true : false;
					if (ret) {
						player.bag.discardItemByPos(index, 1, false, Const.GOODS_CHANGE_TYPE.petCost);
						res.setS2CCode(OK);
						body.writeBytes(res.build().toByteArray());
						return;
					} else {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("GAIN_PET_SAME"));
						body.writeBytes(res.build().toByteArray());
						return;
					}

				} else if (item.prop.itemType == Const.ItemType.Misc.getValue() && item.prop.itemSecondType == Const.ItemSecondType.misc.getValue() && template.prop.equals("ExpRatio")) {
					// 经验卡使用
					if (num != 1) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("BAG_ONLY_ONE_CAN_BE_USED_AT_A_TIME"));
						body.writeBytes(res.build().toByteArray());
						return;
					}
					player.bag.discardItemByPos(index, 1, GOODS_CHANGE_TYPE.use);
					// item.Par 单位分钟 Min：万分比
					player.bufferManager.addLocalBuff("ExdExp", template.par * Const.Time.Minute.getValue(), template.min);
					player.sendSysTip(LangService.getValue("ITEM_USE_NOTICE").replace("{itemName}", item.prop.name), Const.TipsType.NO_BG);
					res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());
				} else if (item.prop.itemType == Const.ItemType.Misc.getValue() && template.prop.equals("rank")) {
					// 称号
					if (num != 1) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("BAG_ONLY_ONE_CAN_BE_USED_AT_A_TIME"));
						body.writeBytes(res.build().toByteArray());
						return;
					}
					player.titleManager.onAwardRank(template.min);
					player.bag.discardItemByPos(index, 1, GOODS_CHANGE_TYPE.use);
					res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());
				} else if (item.prop.itemType == Const.ItemType.Mate.getValue() && item.prop.itemSecondType == ItemSecondType.fashionItem.getValue()) {
					if (num != 1) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("BAG_ONLY_ONE_CAN_BE_USED_AT_A_TIME"));
						body.writeBytes(res.build().toByteArray());
						return;
					}
					FashionItemCO fashionItemCO = GameData.FashionItems.get(item.itemCode());
					boolean activiateResult = player.fashionManager.activiateFashion(fashionItemCO.prop);
					if (!activiateResult) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("FASHION_CANNOT_ACTIVIATE"));
						body.writeBytes(res.build().toByteArray());
						return;
					}
					player.bag.discardItemByPos(index, 1, GOODS_CHANGE_TYPE.use);
					res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());

					FashionGetPush.Builder fBuilder = FashionGetPush.newBuilder();
					fBuilder.setS2CCode(OK);
					fBuilder.setCode(fashionItemCO.prop);
					player.receive("area.playerPush.onSuperScriptPush", fBuilder.build());

				} else if (item.prop.itemType == Const.ItemType.Misc.getValue() && template.prop.equals("ReducePK")) {
					int pkValue = player.pkRuleManager.getPkValue();
					if (pkValue <= 0) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("PK_VALUE_ZERO_NOT_USE"));
						body.writeBytes(res.build().toByteArray());
						return;
					}
					int maxCount = (pkValue + template.min - 1) / template.min;
					int realCount = num <= maxCount ? num : maxCount;
					player.changePlayerPkValue(-template.min * realCount);
					player.bag.discardItemByPos(index, realCount, GOODS_CHANGE_TYPE.use);
					
					player.sendSysTip(LangService.getValue("GAIN_POTION"), Const.TipsType.NO_BG);
					
					res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());
				} else if (item.prop.itemType == Const.ItemType.Misc.getValue()) {
					if (template.prop.equals("gold")) {
						player.moneyManager.addGold(template.min * num, Const.GOODS_CHANGE_TYPE.use);
					} else if (template.prop.equals("prestige")) {
						player.addPrestige(template.min * num, Const.GOODS_CHANGE_TYPE.use);
						player.pushDynamicData("prestige", player.player.prestige);
					} else if (template.prop.equals("ticket")) {
						player.moneyManager.addTicket(template.min * num, Const.GOODS_CHANGE_TYPE.use);
					} else if (template.prop.equals("exp")) {
						player.addExp(template.min * num, Const.GOODS_CHANGE_TYPE.use);
					} else if (template.prop.equals("upexp")) {
						player.baseDataManager.addClassExp(template.min * num, Const.GOODS_CHANGE_TYPE.use);
					} else if (template.prop.equals("guildPoints")) {
						int totalPoints = template.min * num;
						player.guildManager.addContribution(totalPoints, Const.GOODS_CHANGE_TYPE.use);
						String guildId = player.guildManager.getGuildId();
						player.guildManager.addTotalContribution(guildId, totalPoints);
						player.guildManager.pushRedPoint();
					} else if (template.prop.equals("solopoint")) {
						player.soloManager.addSolopoint(template.min * num, Const.GOODS_CHANGE_TYPE.use);
					} else if (template.prop.equalsIgnoreCase("CbtQuest")) {
						// 宝藏任务
						int templateId = template.par;
						if (player.taskManager.treasureTasks.size() > 0) {
							res.setS2CCode(FAIL);
							res.setS2CMsg(LangService.getValue("TASK_IS_ACCPETED"));
							body.writeBytes(res.build().toByteArray());
							return;
						}

						// 随机获取一条日常任务
						TaskBase prop = TaskUtils.getTaskProp(templateId);
						AcceptTaskResult result = player.getPlayerTasks().acceptTask(prop.iD, TaskKind.TREASURE);
						if (result.task != null) {
							player.getPlayerTasks().pushTaskUpdate(result.task);

							// 推送场景和路点
							TaskUtils.treasurePush(player, Integer.parseInt(result.task.prop.targets.get(0)), templateId);

							// 扣除道具
							player.bag.discardItemByPos(index, 1, GOODS_CHANGE_TYPE.use);

							res.setS2CCode(OK);
							body.writeBytes(res.build().toByteArray());
							return;
						} else {
							res.setS2CCode(FAIL);
							res.setS2CMsg(result.msg);
							body.writeBytes(res.build().toByteArray());
							return;
						}
					}

					player.bag.discardItemByPos(index, num, GOODS_CHANGE_TYPE.use);
					res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());
				}
			}
		};
	}

	public static class GetItemChanagePropertyResult {
		public RefreshPlayerPropertyChange itemData;
		public boolean bPet;

		public GetItemChanagePropertyResult(RefreshPlayerPropertyChange itemData, boolean bPet) {
			this.itemData = itemData;
			this.bPet = bPet;
		}
	}
}
