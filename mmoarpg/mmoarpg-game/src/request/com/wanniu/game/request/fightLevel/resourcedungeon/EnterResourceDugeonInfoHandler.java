package com.wanniu.game.request.fightLevel.resourcedungeon;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.GWorld;
import com.wanniu.game.area.AreaDataConfig;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.data.DungeonMapCostCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.base.MapBase;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.FightLevelsPO;
import com.wanniu.game.poes.FightLevelsPO.ResourceDungeonPO;
import com.wanniu.game.request.fightLevel.FightLevelLine;

import pomelo.area.ResourceDungeonHandler.EnterResourceDugeonInfoRequest;
import pomelo.area.ResourceDungeonHandler.EnterResourceDugeonInfoResponse;

/**
 * 进入资源副本
 * 
 * @author Yangzz
 */
@GClientEvent("area.resourceDungeonHandler.enterResourceDugeonInfoRequest")
public class EnterResourceDugeonInfoHandler extends FightLevelLine {

	public PomeloResponse request(WNPlayer player) throws Exception {

		EnterResourceDugeonInfoRequest req = EnterResourceDugeonInfoRequest.parseFrom(pak.getRemaingBytes());

		final int dungeonId = req.getDungeonId();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				EnterResourceDugeonInfoResponse.Builder res = EnterResourceDugeonInfoResponse.newBuilder();

				FightLevelsPO fightLevelsPO = player.fightLevelManager.getFightLevelsPo();
				// 清除双倍奖励
				fightLevelsPO.doubleReward = null;

				MapBase mapProp = AreaDataConfig.getInstance().get(dungeonId);

				ResourceDungeonPO resourceDungeon = fightLevelsPO.resourceDungeon.get(dungeonId);
				// 已使用次数和购买次数
				if (resourceDungeon == null) {
					resourceDungeon = new ResourceDungeonPO();
					fightLevelsPO.resourceDungeon.put(dungeonId, resourceDungeon);
				}

				if (resourceDungeon.usedTimes >= mapProp.defaultTimes + resourceDungeon.buyTimes) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOLO_ENTER_TIMES_LIMIT"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				
				DungeonMapCostCO dungeonMapCostCO = GameData.DungeonMapCosts.get(dungeonId);
				if (player.getLevel()<dungeonMapCostCO.enterLevel) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("RESOURCE_DUNGEON_LEVEL_NEED"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				resourceDungeon.entering = true;

				GWorld.getInstance().ansycExec(() -> {
					AreaUtil.enterArea(player, dungeonId);
				});

				// 更新已挑战次数
				// 在进入场景事件里面做

				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}