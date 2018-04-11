package com.wanniu.game.request.farm;

import java.io.IOException;
import java.util.Date;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.data.MiscCO;
import com.wanniu.game.farm.FarmMgr;
import com.wanniu.game.item.ItemConfig;
import com.wanniu.game.item.NormalItem;
//import com.wanniu.game.farm.FarmMgr.MyInfo;
//import com.wanniu.game.farm.FarmMgr.PlayerInfo;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.FarmPO;

import pomelo.farm.Farm;
import pomelo.farm.FarmHandler.MyFarmInfoRequest;
import pomelo.farm.FarmHandler.MyFarmInfoResponse;

@GClientEvent("farm.farmHandler.myFarmInfoRequest")
public class MyFarmInfoHandler extends PomeloRequest {
	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		MyFarmInfoRequest msg = MyFarmInfoRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				MyFarmInfoResponse.Builder res = MyFarmInfoResponse.newBuilder();
				FarmMgr farmMgr = player.getFarmMgr();
				FarmPO farmPO=farmMgr.myPO;
				
				
				
				//player info
				{
					Farm.PlayerInfo.Builder playerInfoBuilder=Farm.PlayerInfo.newBuilder();
					
					//player id
					playerInfoBuilder.setPlayerId(player.getId());
					
					
					//player summary
					Farm.PlayerSummary.Builder playerSummaryBuilder=Farm.PlayerSummary.newBuilder();
					playerSummaryBuilder.setPlayerId(player.getId());
					playerSummaryBuilder.setRoleName(player.getName());
					playerSummaryBuilder.setLv(player.getLevel());
					playerSummaryBuilder.setFarmLv(farmPO.lv);
					playerSummaryBuilder.setCanSow(farmMgr.getPlayerCultivatable(player.getId()));
					playerSummaryBuilder.setCanSteal(farmMgr.getPlayerStealable(player.getId()));
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
					
					
					Farm.PlayerInfo playerInfo=playerInfoBuilder.build();
					res.setPlayerInfo(playerInfo);
				}
				
				//seed
				for (MiscCO miscCO : ItemConfig.seedMiscMap.values()) {
				//	NormalItem normalItem = player.bag.findItemByCode(miscCO.code);
					int seedNum = player.bag.findItemNumByCode(miscCO.code);
					Farm.Seed.Builder seedBuilder=Farm.Seed.newBuilder();
					seedBuilder.setSeedCode(miscCO.code);
					seedBuilder.setNum(seedNum);
					
					Farm.Seed handlerSeed=seedBuilder.build();
					
					res.addSeedLs(handlerSeed);
				}

				
				//fruit
				for (MiscCO miscCO : ItemConfig.productMiscMap.values()) {
					int seedNum = player.bag.findItemNumByCode(miscCO.code);
					
					Farm.Product.Builder productBuilder=Farm.Product.newBuilder();
					productBuilder.setProductCode(miscCO.code);
					productBuilder.setNum(seedNum);
					
					Farm.Product handlerProduct=productBuilder.build();
					
					res.addProductLs(handlerProduct);
				}

				
				//exp
				res.setExp(farmPO.exp);
				
				//record
				for (FarmMgr.RecordInfo recordInfo : farmPO.recordLs) {
					Farm.RecordInfo.Builder recordInfoBuilder=Farm.RecordInfo.newBuilder();
					recordInfoBuilder.setRecordTimeStamp(recordInfo.recordTime.getTime());
					recordInfoBuilder.setRecordType(recordInfo.recordType.value);
					for (String recordParam : recordInfo.recordParams) {
						recordInfoBuilder.addRecordParams(recordParam);
					}
					res.addRecordInfoLs(recordInfoBuilder.build());
				}
				
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}

