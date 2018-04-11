package com.wanniu.game.request.player;

import java.io.IOException;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.GWorld;
import com.wanniu.game.area.Area;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.data.base.MapBase;
import com.wanniu.game.player.PathService;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerTempPO;

import pomelo.area.PlayerHandler.ChangeAreaRequest;
import pomelo.area.PlayerHandler.ChangeAreaResponse;

/**
 * 传送门传送接口
 * 
 * @author agui
 *
 */
@GClientEvent("area.playerHandler.changeAreaRequest")
public class ChangeAreaHandler extends ChangeAreaFilter {

	public PomeloResponse request(WNPlayer player) throws Exception {

		ChangeAreaRequest req = ChangeAreaRequest.parseFrom(pak.getRemaingBytes());
		String pointId = req.getC2SPointId();
		int areaId = 0;
		float targetX = 0;
		float targetY = 0;

		Area area = player.getArea();
		PlayerTempPO tempData = player.getPlayerTempData();
		// 离开副本进野外的时候发0
		if ("0".equals(pointId)) {
			areaId = tempData.historyAreaId;
			targetX = tempData.historyX;
			targetY = tempData.historyY;
			float[] xy = PathService.findToAreaXYByPointId(areaId, pointId);
			if (xy != null) {
				targetX = xy[0];
				targetY = xy[1];
			}
		} else {
			areaId = PathService.findToAreaByPointId(player.getAreaId(), pointId);
			if (areaId == tempData.historyAreaId) {
				targetX = tempData.historyX;
				targetY = tempData.historyY;
			}
		}
		if (areaId == 0) {
			return new ErrorResponse(player.getAreaId() + " - " + pointId + " : " + LangService.getValue("AREA_ID_NULL"));
		}
		float[] xy = PathService.findToAreaXYByAreaId(area.areaId, areaId);
		if (xy == null) {
			JSONObject json = area.getBornPlace(areaId);
			xy = new float[2];
			xy[0] = json.getFloatValue("x");
			xy[1] = json.getFloatValue("y");
			area.prop.toAreaXY.put(areaId, xy);
		}
		if (xy != null && xy[0] != 0 && xy[1] != 0) {
			targetX = xy[0];
			targetY = xy[1];
		}
		
		ChangeAreaResponse.Builder res = ChangeAreaResponse.newBuilder();
		
		if(area.sceneType == Const.SCENE_TYPE.GUILD_FORT_PVE.getValue() || area.sceneType == Const.SCENE_TYPE.GUILD_FORT_PVP.getValue()) {//仙盟据点战
			String result = player.guildFortManager.handleChangeArea(areaId);
			if (result != null) {
				return new ErrorResponse(result);
			}			
			res.setS2CCode(OK);
			return new PomeloResponse() {
				@Override
				protected void write() throws IOException {
					body.writeBytes(res.build().toByteArray());
				}
			};		
		}
		
		
		MapBase sceneProp = AreaUtil.getAreaProp(areaId);
		String result = AreaUtil.canEnterArea(sceneProp, player);
		if (result != null) {
			return new ErrorResponse(result);
		}
		
		Out.debug(sceneProp.mapID, " ===changeArea=== x:", targetX, ", y:", targetY);
		res.setS2CCode(OK);
		if ((area.sceneType == Const.SCENE_TYPE.FIGHT_LEVEL.getValue() || area.sceneType == Const.SCENE_TYPE.LOOP.getValue()) && area.hasHighQualityItem()) {
			player.puchFuncGoToPickItem();
		}else {
			// 添加进副本需弹框提示
			if (req.getC2SType() == 0 && //
					(sceneProp.type == Const.SCENE_TYPE.FIGHT_LEVEL.getValue() // 副本
							|| area.sceneType == Const.SCENE_TYPE.LOOP.getValue() // 皓月镜
							|| sceneProp.type == Const.SCENE_TYPE.ILLUSION_2.getValue())// 幻境2
			) {
				res.setS2CEnterTips(String.valueOf(sceneProp.mapID));
			} else {
				int dstId = areaId;
				float dstX = targetX, dstY = targetY;
				GWorld.getInstance().ansycExec(() -> {
					String instanceId = player.getInstanceId();
					int oldAreaId = player.getAreaId();
					AreaUtil.enterArea(player, dstId, dstX, dstY);
					if (AreaUtil.needCreateArea(oldAreaId)) {
						AreaUtil.closeAreaNoPlayer(instanceId);
					}
				});
			}
		}

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}