package com.wanniu.game.request.fightLevel.resourcedungeon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.area.AreaDataConfig;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.data.DungeonMapCostCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ResRewardCO;
import com.wanniu.game.data.base.MapBase;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.FightLevelsPO;
import com.wanniu.game.poes.FightLevelsPO.ResourceDungeonPO;
import com.wanniu.game.request.fightLevel.FightLevelLine;

import pomelo.area.ResourceDungeonHandler.ResourceSweepRequest;
import pomelo.area.ResourceDungeonHandler.ResourceSweepResponse;
import pomelo.item.ItemOuterClass.MiniItem;

/**
 * 资源副本扫荡
 * 
 * @author 周明凯
 */
@GClientEvent("area.resourceDungeonHandler.resourceSweepRequest")
public class ResourceSweepHandler extends FightLevelLine {

	public PomeloResponse request(WNPlayer player) throws Exception {
		ResourceSweepRequest req = ResourceSweepRequest.parseFrom(pak.getRemaingBytes());
		final int dungeonId = req.getDungeonId();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				ResourceSweepResponse.Builder res = ResourceSweepResponse.newBuilder();
				
				if (true) { // 暂时先屏蔽扫荡功能
					res.setS2CCode(FAIL);
					body.writeBytes(res.build().toByteArray());
					return;
				}
				
				
				MapBase mapProp = AreaDataConfig.getInstance().get(dungeonId);
				FightLevelsPO fightLevelsPO = player.fightLevelManager.getFightLevelsPo();
				ResourceDungeonPO resourceDungeon = fightLevelsPO.resourceDungeon.get(dungeonId);
				if (resourceDungeon == null) {
					resourceDungeon = new ResourceDungeonPO();
					fightLevelsPO.resourceDungeon.put(dungeonId, resourceDungeon);
				}

				// 只少先打一次...
				if (resourceDungeon.usedTimes < 1) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOLO_USE_SWEEP_BY_ONE"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				if (resourceDungeon.usedTimes >= mapProp.defaultTimes + resourceDungeon.buyTimes) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOLO_ENTER_TIMES_LIMIT"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				// 奖励配置
				ResRewardCO config = GameData.ResRewards.get(player.getLevel());
				if (config == null) { // 购买次数已达上限
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOLO_REWARD_NOT_EXIST"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				// 使用次数+1
				resourceDungeon.usedTimes += 1;

				// 发奖励...
				DungeonMapCostCO dungeonMapCostCO=GameData.DungeonMapCosts.get(dungeonId);
				int difficult=mapProp.hardModel;
				String saodangReward=null;
				try {
					Object saodangRewardObj = ResRewardCO.class.getField("saodangReward"+String.valueOf(dungeonMapCostCO.playType)+String.valueOf(difficult)).get(config);
					saodangReward=String.valueOf(saodangRewardObj);
				}
				catch(Exception e){
					e.printStackTrace();
					return;
				}
				
//				switch (dungeonMapCostCO.playType) {
//				case 1:// 极限挑战
//					saodangReward = config.saodangReward1;
//					break;
//				case 2:// 守护神宠
//					saodangReward = config.saodangReward2;
//					break;
//				case 3:// 幻妖农场
//					saodangReward = config.saodangReward3;
//					break;
//				default:
//					break;
//				}

//				Map<String, Integer> rewards = ItemUtil.parseString2Map(saodangReward);
				if (saodangReward.isEmpty()) {
					Out.error("资源副本扫荡奖励未配置 playerId=", player.getId(), ",Name=", player.getName(), ",dungeonId=",
							dungeonId);
				} else {
					List<NormalItem> list_items = ItemUtil.createItemsByTcCode(saodangReward);//ItemUtil.createItemsByItemCode(rewards);
					player.bag.addCodeItemMail(list_items, null, GOODS_CHANGE_TYPE.resource_dungeon_sweep,
							SysMailConst.BAG_FULL_COMMON);
					
					// 奖励直接发给客户端
					List<MiniItem> list_award = new ArrayList<>();
					for (NormalItem item : list_items) {
						list_award.add(item.toJSON4MiniItem());
					}
					res.addAllAwardItems(list_award);
				}
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}