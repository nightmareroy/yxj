package com.wanniu.game.request.activity;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.activity.DemonTowerManager;
import com.wanniu.game.common.Const;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.DemonTowerHandler.FinishSweepDemonTowerResponse;

//import pomelo.area.DemonTowerHandler.DemontTowerRewardItem;

@GClientEvent("area.demonTowerHandler.finishSweepDemonTowerRequest")
public class FinishSweepDemonTowerHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
//		FinishSweepDemonTowerRequest req = FinishSweepDemonTowerRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			protected void write() throws IOException {
				WNPlayer player = (WNPlayer) pak.getPlayer();
				
				DemonTowerManager manager=player.demonTowerManager;
				
				FinishSweepDemonTowerResponse.Builder res=FinishSweepDemonTowerResponse.newBuilder();
				
				
				
				if(player.moneyManager.getDiamond()<GlobalConfig.SweepPrice)
				{
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("DEMON_TOWER_NOT_ENOUGH_DIAMOND"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				
				
				//不在扫荡中，直接完成扫荡，获取奖励
				if(manager.po.sweepEndTime==null)
				{
					if(manager.getSweepCountLeft()<=0)
					{
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("DEMON_TOWER_NOT_ENOUGH_SWEEP_COUNT"));
						body.writeBytes(res.build().toByteArray());
						return;
					}
					
					player.moneyManager.costDiamond(GlobalConfig.SweepPrice, Const.GOODS_CHANGE_TYPE.DemonTower);
					manager.po.sweepCountLeft--;
					manager.FinishSweep();
				}
				
				//在扫荡中，直接完成扫荡，获取奖励
				else
				{
					player.moneyManager.costDiamond(GlobalConfig.SweepPrice, Const.GOODS_CHANGE_TYPE.DemonTower);
					manager.FinishSweepWhenSpeeping();
				}
				
				
				
				

				
				
				
				
				
				
				
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
				return;

			}
		};
	}

}
