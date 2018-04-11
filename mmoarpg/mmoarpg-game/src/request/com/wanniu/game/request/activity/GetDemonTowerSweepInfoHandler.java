package com.wanniu.game.request.activity;

import java.io.IOException;
import java.util.HashMap;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.activity.DemonTowerManager;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.DemonTowerHandler.DemontTowerRewardItem;
//import pomelo.area.DemonTowerHandler.GetDemonTowerSweepInfoRequest;
import pomelo.area.DemonTowerHandler.GetDemonTowerSweepInfoResponse;

@GClientEvent("area.demonTowerHandler.getDemonTowerSweepInfoRequest")
public class GetDemonTowerSweepInfoHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
//		GetDemonTowerInfoRequest req = GetDemonTowerInfoRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			protected void write() throws IOException {
				WNPlayer player = (WNPlayer) pak.getPlayer();
				
				DemonTowerManager manager=player.demonTowerManager;
				
				GetDemonTowerSweepInfoResponse.Builder res=GetDemonTowerSweepInfoResponse.newBuilder();
				
				
				//扫荡毫秒数
//				long sweepTime=(long)GlobalConfig.SweepTime*(player.demonTowerLevel-1)*1000;
				
				HashMap<String, Integer> rewardMap=player.demonTowerManager.ComputeSweepRewards();
				if(rewardMap!=null)
				{
					for(String code:rewardMap.keySet())
					{
						DemontTowerRewardItem.Builder itemBuilder=DemontTowerRewardItem.newBuilder();
						itemBuilder.setCode(code);
						itemBuilder.setValue(rewardMap.get(code));
						res.addItemView(itemBuilder.build());
					}
				}
				
				
				boolean isSweeping=manager.po.sweepEndTime==null?false:true;
				if(!isSweeping)
					res.setSweepTime(GlobalConfig.SweepTime*(manager.po.maxFloor-1));
				else
					res.setSweepTime(manager.GetSecondToEndTime());
				res.setDiamondCost(GlobalConfig.SweepPrice);
				res.setFloor(manager.po.maxFloor);
				res.setIsSweeping(isSweeping);
				
//				GameData.DropLists.
				
				
				
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
				return;

			}
		};
	}

}
