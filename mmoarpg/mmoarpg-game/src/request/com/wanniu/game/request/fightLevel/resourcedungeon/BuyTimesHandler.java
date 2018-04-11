package com.wanniu.game.request.fightLevel.resourcedungeon;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.RechargeCostCO;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.FightLevelsPO;
import com.wanniu.game.poes.FightLevelsPO.ResourceDungeonPO;
import com.wanniu.game.request.fightLevel.FightLevelLine;

import pomelo.area.ResourceDungeonHandler.BuyTimesRequest;
import pomelo.area.ResourceDungeonHandler.BuyTimesResponse;

/**
 * 购买资源副本
 * 
 * @author 周明凯
 */
@GClientEvent("area.resourceDungeonHandler.buyTimesRequest")
public class BuyTimesHandler extends FightLevelLine {

	public PomeloResponse request(WNPlayer player) throws Exception {

		BuyTimesRequest req = BuyTimesRequest.parseFrom(pak.getRemaingBytes());

		final int dungeonId = req.getDungeonId();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				BuyTimesResponse.Builder res = BuyTimesResponse.newBuilder();
				FightLevelsPO fightLevelsPO = player.fightLevelManager.getFightLevelsPo();
				ResourceDungeonPO resourceDungeon = fightLevelsPO.resourceDungeon.get(dungeonId);
				if (resourceDungeon == null) {
					resourceDungeon = new ResourceDungeonPO();
					fightLevelsPO.resourceDungeon.put(dungeonId, resourceDungeon);
				}

				// 购买所需配置
				RechargeCostCO config = GameData.RechargeCosts.get(resourceDungeon.buyTimes + 1);
				if (config == null) { // 购买次数已达上限
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOLO_BUY_TIMES_LIMIT"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				// 扣除元宝
				if (!player.moneyManager.costTicketAndDiamond(config.costNum, GOODS_CHANGE_TYPE.resource_dungeon_buy_times).isSuccess()) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("TICKET_NOT_ENOUGH"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				// 更新购买次数
				resourceDungeon.buyTimes += 1;

				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}