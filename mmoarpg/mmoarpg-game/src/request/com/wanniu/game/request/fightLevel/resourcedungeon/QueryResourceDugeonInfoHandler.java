package com.wanniu.game.request.fightLevel.resourcedungeon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.area.AreaDataConfig;
import com.wanniu.game.data.DungeonMapCostCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.base.MapBase;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.FightLevelsPO;
import com.wanniu.game.poes.FightLevelsPO.ResourceDungeonPO;
import com.wanniu.game.request.fightLevel.FightLevelLine;

import pomelo.area.ResourceDungeonHandler.QueryResourceDugeonInfoResponse;
import pomelo.area.ResourceDungeonHandler.ResourceDungeonInfo;
import pomelo.item.ItemOuterClass.MiniItem;

/**
 * 获取资源副本界面信息
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.resourceDungeonHandler.queryResourceDugeonInfoRequest")
public class QueryResourceDugeonInfoHandler extends FightLevelLine {

	public PomeloResponse request(WNPlayer player) throws Exception {

		// QueryResourceDugeonInfoRequest req = QueryResourceDugeonInfoRequest.parseFrom(pak.getRemaingBytes());

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				QueryResourceDugeonInfoResponse.Builder res = QueryResourceDugeonInfoResponse.newBuilder();

				FightLevelsPO fightLevelsPO = player.fightLevelManager.getFightLevelsPo();
				List<ResourceDungeonInfo> list = new ArrayList<>();
				for (DungeonMapCostCO config : GameData.DungeonMapCosts.values()) {
					MapBase mapProp = AreaDataConfig.getInstance().get(config.mapID);

					ResourceDungeonPO resourceDungeon = fightLevelsPO.resourceDungeon.get(config.mapID);
					// 已使用次数和购买次数
					if (resourceDungeon == null) {
						resourceDungeon = new ResourceDungeonPO();
						fightLevelsPO.resourceDungeon.put(config.mapID, resourceDungeon);
					}
					// 掉落预览
//					List<NormalItem> list_tc = ItemUtil.createItemsByTcCode(mapProp.bonusViewTC);
//					List<MiniItem> list_award = new ArrayList<>();
//					for (NormalItem item : list_tc) {
//						list_award.add(item.toJSON4MiniItem());
//					}
					ResourceDungeonInfo.Builder info = ResourceDungeonInfo.newBuilder();
					info.setDungeonId(config.mapID);
					info.setLastTimes(mapProp.defaultTimes - resourceDungeon.usedTimes + resourceDungeon.buyTimes);
					info.setBuyTimes(resourceDungeon.buyTimes);
					info.setCanBuyTimes(GameData.RechargeCosts.size() - resourceDungeon.buyTimes);
					info.setCanSweep(resourceDungeon.usedTimes > 0);
					info.setAwardItems(config.showReward);
					info.setPlayType(config.playType);
					info.setEnterLevel(config.enterLevel);
					list.add(info.build());
				}
				
				res.addAllDungeons(list);
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}