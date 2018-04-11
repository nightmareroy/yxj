package com.wanniu.game.request.consignment;

import java.io.IOException;
import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.wanniu.core.GGlobal;
import com.wanniu.core.db.GCache;
import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const.CHAT_SCOPE;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.consignmentShop.ConsignmentLineService;
import com.wanniu.game.data.ChatSettingCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.ConsignmentItemsPO;

import pomelo.area.ConsignmentLineHandler.PublicItemRequest;
import pomelo.area.ConsignmentLineHandler.PublicItemResponse;

@GClientEvent("area.consignmentLineHandler.publicItemRequest")
public class PublicItemHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		PublicItemRequest msg = PublicItemRequest.parseFrom(pak.getRemaingBytes());
		String id = msg.getId();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				PublicItemResponse.Builder res = PublicItemResponse.newBuilder();

				ConsignmentItemsPO item = ConsignmentLineService.getInstance().getById(id);
				if (item == null) {
					Out.error("publicItemRequest ", id, " not found.");
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("ITEM_NULL"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				// 聊天发言间隔限制
				int scope = CHAT_SCOPE.WORLD.getValue();
				ChatSettingCO setting = GameData.ChatSettings.get(scope);
				if (player.chatTime == null) {
					player.chatTime = new HashMap<>();
				}
				Long lasttime = player.chatTime.get(scope);
				long currTime = System.currentTimeMillis();
				long second = 0;
				if (lasttime != null) {
					second = setting.coolDown - (currTime - lasttime) / GGlobal.TIME_SECOND;
				}

				// 验证并扣除消耗
				if (item.publishTimes == 0) {
					if (second > 0) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.format("CHAT_WORLD_COOL", second));
						body.writeBytes(res.build().toByteArray());
						return;
					}
				} else if (item.publishTimes > 0 && item.publishTimes <= GlobalConfig.Consignment_Advertisement_goldNum) {
					// Map<String, String> strMsg = new HashMap<>();
					// strMsg.put("content",
					// LangService.getValue("CONSIGN_PUBLISH_YINLIANG").replace("num",
					// GlobalConfig.Consignment_Advertisement_gold + ""));
					// MessageData_Consignment data = new MessageData_Consignment();
					// data.id = id;
					// data.num1 = GlobalConfig.Consignment_Advertisement_gold;
					// MessageData message =
					// MessageUtil.createMessage(Const.MESSAGE_TYPE.consignment_publish.getValue(),
					// player.getId(), data, strMsg);
					// message.id = player.getId();
					// MessageUtil.sendMessageToPlayer(message, player.getId());
					// res.setS2CCode(OK);
					// body.writeBytes(res.build().toByteArray());
					// return;
					if (player.player.gold < GlobalConfig.Consignment_Advertisement_gold) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("GOLD_NOT_ENOUGH"));
						body.writeBytes(res.build().toByteArray());
						return;
					}

					if (second > 0) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.format("CHAT_WORLD_COOL", second));
						body.writeBytes(res.build().toByteArray());
						return;
					}
					// 扣除金币
					player.moneyManager.costGold(GlobalConfig.Consignment_Advertisement_gold, GOODS_CHANGE_TYPE.CONSIGNMENT_PUBLIC);
				} else if (item.publishTimes > GlobalConfig.Consignment_Advertisement_goldNum) {
					// Map<String, String> strMsg = new HashMap<>();
					// strMsg.put("content",
					// LangService.getValue("CONSIGN_PUBLISH_YINLIANG").replace("num",
					// GlobalConfig.Consignment_Advertisement_gold + ""));
					// MessageData_Consignment data = new MessageData_Consignment();
					// data.id = id;
					// data.num1 = GlobalConfig.Consignment_Advertisement_gold;
					// MessageData message =
					// MessageUtil.createMessage(Const.MESSAGE_TYPE.consignment_publish.getValue(),
					// player.getId(), data, strMsg);
					// message.id = player.getId();
					// MessageUtil.sendMessageToPlayer(message, player.getId());
					// res.setS2CCode(OK);
					// body.writeBytes(res.build().toByteArray());
					// return;
					if (player.player.ticket < GlobalConfig.Consignment_Advertisement_diamond && player.player.diamond < GlobalConfig.Consignment_Advertisement_diamond) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("DIAMAND_NOT_ENOUGH"));
						body.writeBytes(res.build().toByteArray());
						return;
					}

					if (second > 0) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.format("CHAT_WORLD_COOL", second));
						body.writeBytes(res.build().toByteArray());
						return;
					}
					// 扣除元宝
					if (player.player.ticket >= GlobalConfig.Consignment_Advertisement_diamond) {
						player.moneyManager.costTicket(GlobalConfig.Consignment_Advertisement_diamond, GOODS_CHANGE_TYPE.CONSIGNMENT_PUBLIC);
					} else {
						player.moneyManager.costDiamond(GlobalConfig.Consignment_Advertisement_diamond, GOODS_CHANGE_TYPE.CONSIGNMENT_PUBLIC);
					}
				}

				item.publishTimes += 1;

				GCache.put(ConstsTR.chat_item_tr.value + "/" + id, JSON.toJSONString(item.db), 600);

				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
