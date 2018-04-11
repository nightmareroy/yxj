package com.wanniu.game.request.player;

import java.io.IOException;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.player.PlayerUtil;

import pomelo.area.PlayerHandler.GetPlayerPositionRequest;
import pomelo.area.PlayerHandler.GetPlayerPositionResponse;

/**
 * 获取角色坐标
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.playerHandler.getPlayerPositionRequest")
public class GetPlayerPositionHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		GetPlayerPositionRequest req = GetPlayerPositionRequest.parseFrom(pak.getRemaingBytes());
		String playerId = req.getS2CPlayerId();

		if (StringUtil.isEmpty(playerId)) {
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}

		if (!PlayerUtil.isOnline(playerId)) {
			return new ErrorResponse(LangService.getValue("PLAYER_NOT_ONLINE"));
		}

		JSONObject playerNowData = PlayerUtil.getPlayerNowPosition(playerId);
		if (playerNowData == null) {
			return new ErrorResponse(LangService.getValue("PLAYER_NOT_ONLINE"));
		}

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetPlayerPositionResponse.Builder res = GetPlayerPositionResponse.newBuilder();

			    res.setS2CCode(OK);
			    res.setS2CAreaId(playerNowData.getIntValue("areaId"));
			    res.setS2CTemplateID(playerNowData.getIntValue("templateID"));
			    res.setS2CInstanceId(playerNowData.getString("instanceId"));
			    res.setS2CTargetX(playerNowData.getIntValue("x"));
			    res.setS2CTargetY(playerNowData.getIntValue("y"));
			    
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}