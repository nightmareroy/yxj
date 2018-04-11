package com.wanniu.game.request.activity;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.RandomUtil;
import com.wanniu.game.activity.DemonTowerManager;
import com.wanniu.game.area.AreaData;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.area.DemonTower;
import com.wanniu.game.common.Utils;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.DropListExt;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.DemonTowerHandler.StartDemonTowerResponse;
import pomelo.area.DemonTowerHandler.StartDemonTowerRequest;

//import pomelo.area.DemonTowerHandler.DemontTowerRewardItem;

@GClientEvent("area.demonTowerHandler.startDemonTowerRequest")
public class StartDemonTowerHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		StartDemonTowerRequest req = StartDemonTowerRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			protected void write() throws IOException {
				WNPlayer player = (WNPlayer) pak.getPlayer();
				int floorId=req.getFloorId();
				DemonTowerManager manager=player.demonTowerManager;
				Out.error(floorId);
				if(floorId==0) {
					floorId=Math.min(manager.po.maxFloor, GameData.DropLists.size());
				}
				StartDemonTowerResponse.Builder res=StartDemonTowerResponse.newBuilder();
				
				if(!GameData.DropLists.containsKey(floorId)) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				
				if(floorId>player.demonTowerManager.po.maxFloor) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("DEMON_TOWER_NOT_OPEN"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				
				if(manager.po.sweepEndTime!=null){
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("DEMON_TOWER_IS_SWEEPING"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				
				if(manager.po.maxFloor>= GameData.DropLists.size()){
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("DEMON_TOWER_IN_TOP"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				
//				int mapId = manager.po.failedMapId;//default set last failed mapId
//				if(mapId<=0){
//					mapId = GlobalConfig.DemonTowerMapIds[RandomUtil.getIndex(GlobalConfig.DemonTowerMapIds.length)];
//				}
				DropListExt dropListExt = GameData.DropLists.get(floorId);
				if(dropListExt==null) {
					Out.error("参数错误");
					return;
				}
				int mapId = dropListExt.mapId;
				DemonTower area = (DemonTower) AreaUtil.createArea(player, Utils.toJSON("logicServerId",
						player.getLogicServerId(), "areaId", mapId, "lv", floorId));
				AreaData areaData = new AreaData(area.areaId, area.instanceId);
				AreaUtil.changeArea(player, areaData);
				
				
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
				return;

			}
		};
	}

}
