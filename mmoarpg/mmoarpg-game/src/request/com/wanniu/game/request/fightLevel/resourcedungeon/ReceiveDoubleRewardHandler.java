package com.wanniu.game.request.fightLevel.resourcedungeon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.data.DungeonMapCostCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.item.po.PlayerItemPO;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.FightLevelsPO;
import com.wanniu.game.request.fightLevel.FightLevelLine;

import pomelo.Common.KeyValueStruct;
import pomelo.area.ResourceDungeonHandler.ReceiveDoubleRewardRequest;
import pomelo.area.ResourceDungeonHandler.ReceiveDoubleRewardResponse;

/**
 * 领取双倍奖励
 * 
 * @author Yangzz
 */
@GClientEvent("area.resourceDungeonHandler.receiveDoubleRewardRequest")
public class ReceiveDoubleRewardHandler extends FightLevelLine {

	public PomeloResponse request(WNPlayer player) throws Exception {

		ReceiveDoubleRewardRequest req = ReceiveDoubleRewardRequest.parseFrom(pak.getRemaingBytes());

		final int dungeonId = req.getDungeonId();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				ReceiveDoubleRewardResponse.Builder res = ReceiveDoubleRewardResponse.newBuilder();

				// MapBase mapProp =
				// AreaDataConfig.getInstance().get(dungeonId);
				DungeonMapCostCO config = GameData.DungeonMapCosts.get(dungeonId);
				FightLevelsPO fightLevelsPO = player.fightLevelManager.getFightLevelsPo();
				// ResourceDungeonPO resourceDungeon =
				// fightLevelsPO.resourceDungeon.get(dungeonId);

				// 当前副本没有双倍奖励
				if (config.isDoubleBonus == 0) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				// 没有奖励可领取
				if (fightLevelsPO.doubleReward == null) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("DUNGEON_NOT_DOUBLE_REWARD"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				// 元宝不足
				if (player.player.diamond < config.bounsCostDiamond) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("DIAMAND_NOT_ENOUGH"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				// 变化道具
				List<KeyValueStruct> changeItems = new ArrayList<>();

				// 领取奖励
				if (fightLevelsPO.doubleReward.doubleVirtualItems != null) {
					for (String key : fightLevelsPO.doubleReward.doubleVirtualItems.keySet()) {
						int value = fightLevelsPO.doubleReward.doubleVirtualItems.get(key);
						KeyValueStruct.Builder items = KeyValueStruct.newBuilder();
						items.setKey(key);
						items.setValue(String.valueOf(value));
						changeItems.add(items.build());

						if (key.equals("exp")) {
							player.addExp(value, GOODS_CHANGE_TYPE.resource_dungeon_award);
						} else if (key.equals("gold")) {
							player.moneyManager.addGold(value, GOODS_CHANGE_TYPE.resource_dungeon_award);
						}
					}
				}
				if (fightLevelsPO.doubleReward.doubleItems != null
						&& fightLevelsPO.doubleReward.doubleItems.size() > 0) {
					for (PlayerItemPO it : fightLevelsPO.doubleReward.doubleItems) {
						KeyValueStruct.Builder items = KeyValueStruct.newBuilder();
						items.setKey(it.code);
						items.setValue(String.valueOf(it.groupCount));
						changeItems.add(items.build());
					}
					player.bag.addEntityItemsPO(fightLevelsPO.doubleReward.doubleItems,
							GOODS_CHANGE_TYPE.resource_dungeon_award);
				}

				// 扣除元宝
				player.moneyManager.costDiamond(config.bounsCostDiamond, GOODS_CHANGE_TYPE.resource_dungeon_cost_diamond,
						changeItems);

				// 清除双倍奖励
				fightLevelsPO.doubleReward = null;

				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}