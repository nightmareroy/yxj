package com.wanniu.game.request.farm;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.farm.FarmMgr;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.FarmPO;

import pomelo.area.FriendHandler;
import pomelo.farm.Farm;
import pomelo.farm.FarmHandler.FriendLsRequest;
import pomelo.farm.FarmHandler.FriendLsResponse;

@GClientEvent("farm.farmHandler.friendLsRequest")
public class FriendLsHandler extends PomeloRequest {
	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		FriendLsRequest msg = FriendLsRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				FriendLsResponse.Builder res = FriendLsResponse.newBuilder();
				FarmMgr farmMgr = player.getFarmMgr();
				FarmPO farmPO=farmMgr.myPO;
				
				
				List<FriendHandler.PlayerInfo> playerFriendInfos = player.friendManager.getAllFriends();
				
				for (FriendHandler.PlayerInfo friendInfo : playerFriendInfos) {
//					FarmMgr.PlayerInfo friendFarmInfo=farmMgr.getPlayerInfo(friendInfo.getId());
					
					Farm.PlayerSummary.Builder playerSummaryBuilder=Farm.PlayerSummary.newBuilder();
					playerSummaryBuilder.setPlayerId(friendInfo.getId());
					playerSummaryBuilder.setRoleName(friendInfo.getName());
					playerSummaryBuilder.setLv(friendInfo.getLevel());
					playerSummaryBuilder.setFarmLv(farmPO.lv);
					playerSummaryBuilder.setCanSow(farmMgr.getPlayerCultivatable(friendInfo.getId()));
					playerSummaryBuilder.setCanSteal(farmMgr.getPlayerStealable(friendInfo.getId()));
					
					res.addPlayerSummary(playerSummaryBuilder.build());
				}
				
				
				
				
				
				
//				for (FarmMgr.RecordInfo recordInfo : myInfo.recrodLs) {
//					Farm.RecordInfo.Builder recordInfoBuilder=Farm.RecordInfo.newBuilder();
//					recordInfoBuilder.setRecordType(recordInfo.recordType);
//					for (String recordParam : recordInfo.recordParams) {
//						recordInfoBuilder.addRecordParams(recordParam);
//					}
//					res.addRecordInfoLs(recordInfoBuilder.build());
//				}
				
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}