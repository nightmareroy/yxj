package com.wanniu.game.request.fightLevel;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.data.DungeonMapCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.fightLevel.FightLevel;
import com.wanniu.game.fightLevel.FightLevelManager;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FightLevelHandler.FubenInfo;
import pomelo.area.FightLevelHandler.FubenListRequest;
import pomelo.area.FightLevelHandler.FubenListResponse;

/**
 * 获取副本列表
 * 
 * @author agui
 *
 */
@GClientEvent("area.fightLevelHandler.fubenListRequest")
public class FubenListHandler extends FightLevelLine {

	public PomeloResponse request(WNPlayer player) throws Exception {
		FubenListRequest req = FubenListRequest.parseFrom(pak.getRemaingBytes());
		int mapId = req.getMapId();
		int mapType = req.getType();
		DungeonMapCO map = GameData.DungeonMaps.get(mapId);
		if (map == null) {
			return new ErrorResponse(LangService.getValue("DUNGEON_NULL"));
		}

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				FubenListResponse.Builder res = FubenListResponse.newBuilder();

				FightLevelManager fightLevelManager = player.fightLevelManager;
				List<DungeonMapCO> dungeonMaps = GameData.findDungeonMaps(t -> {
					return t.templateID == map.templateID && t.dungeonShow == 1 && t.type == mapType;
				});
				Collections.sort(dungeonMaps, new Comparator<DungeonMapCO>() {
					@Override
					public int compare(DungeonMapCO o1, DungeonMapCO o2) {
						return o1.hardModel - o2.hardModel;
					}
				});
				dungeonMaps.forEach((t) -> {
					FubenInfo.Builder dungeonInfo = FubenInfo.newBuilder();
					dungeonInfo.setMapId(t.mapID);
					dungeonInfo.setRemainTimes(player.getLevel() >= t.reqLevel + FightLevel.NEED_PRODUCE_LEVEL ? 0 : Math.max(0, Const.FB_PRODUCE_COUNT + fightLevelManager.getTodayBuy(t.templateID) - fightLevelManager.getTodayFinish(t.templateID)));
					dungeonInfo.addAllAwardItems(fightLevelManager.getDropItems(t.bonusViewTC));
					res.addS2CList(dungeonInfo);
				});
				// res.setS2CHard(Math.min(dungeonMaps.size(),
				// fightLevelManager.getCurrHard(map.templateID)));
				// res.setS2CAcross(1);
				// 难度等级设计取消，统一改为单人、组队2种情况 modify by wfy
				res.setS2CHard(0);
				res.setS2CAcross(0);
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}