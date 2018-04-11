package com.wanniu.game.request.activity;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.activity.DemonTowerManager;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.DemonTowerHandler.StartToSweepDemonTowerRequest;
import pomelo.area.DemonTowerHandler.StartToSweepDemonTowerResponse;
//import pomelo.area.DemonTowerHandler.DemontTowerRewardItem;

@GClientEvent("area.demonTowerHandler.startToSweepDemonTowerRequest")
public class StartToSweepDemonTowerHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		StartToSweepDemonTowerRequest req = StartToSweepDemonTowerRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			protected void write() throws IOException {
				WNPlayer player = (WNPlayer) pak.getPlayer();
				
				DemonTowerManager manager=player.demonTowerManager;
				
				StartToSweepDemonTowerResponse.Builder res=StartToSweepDemonTowerResponse.newBuilder();
				
				if(manager.po.maxFloor==1)
				{
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("DEMON_TOWER_CANNOT_SWEEP"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				
				if(manager.po.sweepCountLeft==0)
				{
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("DEMON_TOWER_NOT_ENOUGH_SWEEP_COUNT"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				
				if(manager.po.sweepEndTime!=null)
				{
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("DEMON_TOWER_IS_SWEEPING"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				
				
				
				
				
				
				//扫荡秒数
				int sweepTime=GlobalConfig.SweepTime*(manager.po.maxFloor-1);
				
				res.setEndTimeStamp(System.currentTimeMillis()+sweepTime);
				
				//开始扫荡
				player.demonTowerManager.StartToSweep(sweepTime);
				
//				HashMap<String, Integer> rewardMap=player.demonTowerManager.ComputeSweepRewards();
//				
//				for(String code:rewardMap.keySet())
//				{
//					DemontTowerRewardItem.Builder itemBuilder=DemontTowerRewardItem.newBuilder();
//					itemBuilder.setCode(code);
//					itemBuilder.setValue(rewardMap.get(code));
//					res.addItemView(itemBuilder.build());
//				}
				
				
				
				
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
				return;

			}
		};
	}

}
