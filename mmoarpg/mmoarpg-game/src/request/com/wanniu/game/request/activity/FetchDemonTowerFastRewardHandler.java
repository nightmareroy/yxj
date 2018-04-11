//package com.wanniu.game.request.activity;
//
//import java.io.IOException;
//import java.util.Date;
//
//import com.wanniu.core.game.entity.GClientEvent;
//import com.wanniu.core.game.protocol.PomeloRequest;
//import com.wanniu.core.game.protocol.PomeloResponse;
//import com.wanniu.core.logfs.Out;
//import com.wanniu.game.activity.DemonTowerManager;
//import com.wanniu.game.activity.DemonTowerService;
//import com.wanniu.game.common.Const.PlayerBtlData;
//import com.wanniu.game.common.ConstsTR;
//import com.wanniu.game.common.Table;
//import com.wanniu.game.common.msg.ErrorResponse;
//import com.wanniu.game.data.GameData;
//import com.wanniu.game.data.ext.DropListExt;
//import com.wanniu.game.player.GlobalConfig;
//import com.wanniu.game.player.WNPlayer;
//import com.wanniu.game.poes.PlayerBasePO;
//import com.wanniu.game.poes.PlayerPO;
//import com.wanniu.redis.PlayerPOManager;
//
//import pomelo.area.DemonTowerHandler.DemonTowerFloorInfo;
//import pomelo.area.DemonTowerHandler.DemontTowerRewardItem;
//import pomelo.area.DemonTowerHandler.FetchDemonTowerFastRewardRequest;
//import pomelo.area.DemonTowerHandler.FetchDemonTowerFastRewardResponse;
//
//@GClientEvent("area.demonTowerHandler.fetchDemonTowerFastRewardRequest")
//public class FetchDemonTowerFastRewardHandler extends PomeloRequest {
//
//	@Override
//	public PomeloResponse request() throws Exception {
//		FetchDemonTowerFastRewardRequest req = FetchDemonTowerFastRewardRequest.parseFrom(pak.getRemaingBytes());
//		
//		WNPlayer player = (WNPlayer) pak.getPlayer();
//		int floorId=req.getFloorId();
////		DemonTowerManager manager=player.demonTowerManager;
//		
//		if(DemonTowerService.getInstance().getGotable(floorId, player.getId())) {
//			return new ErrorResponse();
//		}
//		
//		return new PomeloResponse() {
//			protected void write() throws IOException {
//				
//				
//				FetchDemonTowerFastRewardResponse.Builder res=FetchDemonTowerFastRewardResponse.newBuilder();
//				
//				
//				DemonTowerService.getInstance().fetchFastReward(floorId, player);
//				
//				
//				res.setS2CCode(OK);
//				body.writeBytes(res.build().toByteArray());
//				
//				Out.error(res.build());
//				return;
//
//			}
//		};
//	}
//
//}
