package com.wanniu.game.request.farm;

import java.io.IOException;
import java.util.Date;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.farm.FarmMgr;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.FarmPO;

import pomelo.area.FriendHandler;
import pomelo.farm.Farm;
import pomelo.farm.FarmHandler.FriendFarmInfoRequest;
import pomelo.farm.FarmHandler.FriendFarmInfoResponse;

@GClientEvent("farm.farmHandler.friendFarmInfoRequest")
public class FriendFarmInfoHandler extends PomeloRequest {
	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		FriendFarmInfoRequest msg = FriendFarmInfoRequest.parseFrom(pak.getRemaingBytes());
		String friendId=msg.getPlayerId();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				FriendFarmInfoResponse.Builder res = FriendFarmInfoResponse.newBuilder();
				FarmMgr farmMgr = player.getFarmMgr();
				FarmPO farmPO=farmMgr.myPO;
				
				
				//好友列表里的信息
				FriendHandler.PlayerInfo playerFriendInfo=null;
				for (FriendHandler.PlayerInfo tempInfo : player.friendManager.getAllFriends()) {
					if(tempInfo.getId().equals(friendId))
						playerFriendInfo=tempInfo;
				}
				if(playerFriendInfo==null)
				{
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("FARM_NOT_MY_FRIEND"));
				}
				
				
				//player info
				{
					Farm.PlayerInfo.Builder playerInfoBuilder=Farm.PlayerInfo.newBuilder();
					
					//id
					playerInfoBuilder.setPlayerId(playerFriendInfo.getId());
					
					//summary
					Farm.PlayerSummary.Builder playerSummaryBuilder=Farm.PlayerSummary.newBuilder();
					playerSummaryBuilder.setPlayerId(playerFriendInfo.getId());
					playerSummaryBuilder.setRoleName(playerFriendInfo.getName());
					playerSummaryBuilder.setLv(playerFriendInfo.getLevel());
					playerSummaryBuilder.setFarmLv(farmPO.lv);
					playerSummaryBuilder.setCanSow(farmMgr.getPlayerCultivatable(playerFriendInfo.getId()));
					playerSummaryBuilder.setCanSteal(farmMgr.getPlayerStealable(playerFriendInfo.getId()));
					playerInfoBuilder.setPlayerSummary(playerSummaryBuilder.build());
					
					//block ls
					for (FarmMgr.Block farmMgrBlock : farmPO.blockMap.values()) {
						Farm.Block.Builder blockBuilder=Farm.Block.newBuilder();
						blockBuilder.setBlockId(farmMgrBlock.blockId);
						blockBuilder.setBlockState(farmMgrBlock.blockState.value);
						blockBuilder.setSeedCode(farmMgrBlock.seedCode);
						blockBuilder.setSeedState(farmMgrBlock.seedState.value);
						blockBuilder.setCultivateType(farmMgrBlock.cultivateType.value);
						blockBuilder.setHarvestTime(FarmMgr.evaluateHarvestTime(farmPO.playerId, farmMgrBlock.blockId).getTime());
						Date protectionEndTime=FarmMgr.getProtectEndTime(farmPO.playerId, farmMgrBlock.blockId);
						if(protectionEndTime!=null)
							blockBuilder.setProtectEndTime(protectionEndTime.getTime());
						playerInfoBuilder.addBlockLs(blockBuilder.build());
					}
					
					Farm.PlayerInfo handlerPlayerInfo=playerInfoBuilder.build();
					res.setPlayerInfo(handlerPlayerInfo);
				
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