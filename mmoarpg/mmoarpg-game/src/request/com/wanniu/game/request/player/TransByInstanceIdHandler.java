package com.wanniu.game.request.player;

import java.io.IOException;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.GConfig;
import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.proxy.ProxyClient;
import com.wanniu.core.proxy.ProxyType;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.area.Area;
import com.wanniu.game.area.AreaData;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.common.Const.SCENE_TYPE;
import com.wanniu.game.common.Const.TipsType;
import com.wanniu.game.common.Utils;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.team.TeamData;

import pomelo.area.PlayerHandler.TransByInstanceIdRequest;
import pomelo.area.PlayerHandler.TransByInstanceIdResponse;

/**
 * 切线请求
 * 
 * @author agui
 *
 */
@GClientEvent("area.playerHandler.transByInstanceIdRequest")
public class TransByInstanceIdHandler extends ChangeAreaFilter {

	public PomeloResponse request(WNPlayer player) throws Exception {

		TransByInstanceIdRequest req = TransByInstanceIdRequest.parseFrom(pak.getRemaingBytes());
		String instanceId = req.getC2SInstanceId();
		if (StringUtil.isEmpty(instanceId)) {
			return new ErrorResponse(LangService.getValue("DATA_ERR"));
		}
		if (instanceId.equals(player.getInstanceId())) {
			return new ErrorResponse(LangService.getValue("LINE_CHANGE_UNECESSARY"));
		}

		Area area = player.getArea();
		if (!area.isNormal() 
				&& area.sceneType != SCENE_TYPE.CROSS_SERVER.getValue()
				&& area.sceneType != SCENE_TYPE.WORLD_BOSS.getValue()) {
			return new ErrorResponse(LangService.getValue("LINE_CHANGE_FAILED"));
		}

		boolean newCrossArea = false;
		Area targetArea = AreaUtil.getArea(instanceId);
		if (targetArea == null && area.sceneType == SCENE_TYPE.CROSS_SERVER.getValue() && GConfig.getInstance().isEnableProxy()) {
			TeamData team = player.getTeamManager().getTeam();
			int count = team == null ? 1 : team.memberCount();
			JSONObject json = Utils.toJSON("instanceId", instanceId, "count", count);
			json = ProxyClient.getInstance().request(ProxyType.ProxyMethod.M_TRANS_LINE, json);
			if (json.containsKey("csNode")) {
				AreaData areaData = new AreaData(area.areaId, instanceId);
				JSONObject pos = PlayerUtil.getPlayerPosition(player);
				areaData.targetX = pos.getIntValue("x");
				areaData.targetY = pos.getIntValue("y");
				targetArea = AreaUtil.bindCrossServerArea(player, json, (crossArea) -> {
					AreaUtil.dispatchByInstanceId(player, areaData);
					player.sendSysTip(LangService.getValue("LINE_CHANGE_SUCESS"), TipsType.BLACK);
				});
				newCrossArea = true;
			}
		}

		if (targetArea == null || targetArea.logicServerId != area.logicServerId
				|| targetArea.areaId != area.areaId
				|| targetArea.isFull()) {
			return new ErrorResponse(LangService.getValue("LINE_CHANGE_FAILED"));
		}

		if (!newCrossArea) {
			AreaData areaData = new AreaData(area.areaId, instanceId);
			JSONObject pos = PlayerUtil.getPlayerPosition(player);
			areaData.targetX = pos.getIntValue("x");
			areaData.targetY = pos.getIntValue("y");

			targetArea = AreaUtil.dispatchByInstanceId(player, areaData);
			if (targetArea != null) {
				player.sendSysTip(LangService.getValue("LINE_CHANGE_SUCESS"), TipsType.BLACK);
			} else {
				return new ErrorResponse(LangService.getValue("LINE_CHANGE_FAILED"));
			}
		}
	
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				TransByInstanceIdResponse.Builder res = TransByInstanceIdResponse.newBuilder();
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}