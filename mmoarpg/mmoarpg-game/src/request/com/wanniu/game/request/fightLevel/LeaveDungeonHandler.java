package com.wanniu.game.request.fightLevel;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.area.Area;
import com.wanniu.game.area.AreaData;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.FightLevelHandler.LeaveDungeonResponse;

/**
 * 离开副本
 * @author agui
 *
 */
@GClientEvent("area.fightLevelHandler.leaveDungeonRequest")
public class LeaveDungeonHandler extends FightLevelLine {

	public PomeloResponse request(WNPlayer player) throws Exception {
		Area area = player.getArea();

		if (area == null) {
			AreaUtil.dispatchByAreaId(player, new AreaData(
					player.playerTempData.historyAreaId, 
					player.playerTempData.historyX, 
					player.playerTempData.historyY),null);
			Out.warn("chuxianle1!!!playerId=",player.getId(),"area null");
			return new ErrorResponse(LangService.getValue("DATA_ERR"));
		}
		if (area.sceneType == Const.SCENE_TYPE.NORMAL.getValue()) {    
			Out.warn("chuxianle2!!!playerId=",player.getId(),"areaId=",area.areaId,",instanceId=",area.instanceId);
			return new ErrorResponse(LangService.getValue("DATA_ERR"));
		}

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				LeaveDungeonResponse.Builder res = LeaveDungeonResponse.newBuilder();
				String data = null;
				if (area.sceneType == Const.SCENE_TYPE.GUILD_FORT_PVE.getValue()) {
					player.guildFortManager.handleLeaveArea(area.areaId);
				}
				
				if (area.hasHighQualityItem()) {
					player.puchFuncGoToPickItem();
					res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());
					return;
				}
				data = player.fightLevelManager.leaveDungeon(player, area);			
				
				if (data == null) {
					res.setS2CCode(OK);
				} else {
					res.setS2CCode(FAIL);
					res.setS2CMsg(data);
				}
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}