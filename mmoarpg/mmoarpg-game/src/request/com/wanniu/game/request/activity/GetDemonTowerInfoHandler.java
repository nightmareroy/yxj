package com.wanniu.game.request.activity;

import java.io.IOException;
import java.util.Date;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.activity.DemonTowerManager;
import com.wanniu.game.activity.DemonTowerService;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.Table;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.DropListExt;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerBasePO;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.redis.PlayerPOManager;

import pomelo.Common.DemonTowerFloorInfo;
import pomelo.area.DemonTowerHandler.DemontTowerRewardItem;
import pomelo.area.DemonTowerHandler.GetDemonTowerInfoRequest;
import pomelo.area.DemonTowerHandler.GetDemonTowerInfoResponse;

@GClientEvent("area.demonTowerHandler.getDemonTowerInfoRequest")
public class GetDemonTowerInfoHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		GetDemonTowerInfoRequest req = GetDemonTowerInfoRequest.parseFrom(pak.getRemaingBytes());
		
		return new PomeloResponse() {
			protected void write() throws IOException {
				WNPlayer player = (WNPlayer) pak.getPlayer();
				int floorId=req.getFloorId();
				DemonTowerManager manager=player.demonTowerManager;
				
				GetDemonTowerInfoResponse.Builder res=GetDemonTowerInfoResponse.newBuilder();
				if(floorId==0) {
					floorId=Math.min(manager.po.maxFloor, GameData.DropLists.size());
				}
				
				
				res.setMaxFloor(Math.min(manager.po.maxFloor, GameData.DropLists.size()));
//				for(int i=0;i<dropListExt.rewardPreview.length;i++)
//				{
//					Reward reward=dropListExt.rewardPreview[i];
//					DemontTowerRewardItem.Builder itemBuilder=DemontTowerRewardItem.newBuilder();
//					itemBuilder.setCode(reward.code);
//					itemBuilder.setValue(reward.count);
//					res.addItemView(itemBuilder.build());
//					
//				}
				
				DemonTowerFloorInfo.Builder floorInfoBuilder=manager.getFloorInfoBuilder(floorId);
				
				res.setDemonTowerFloorInfo(floorInfoBuilder);
				
				DropListExt dropListExt = GameData.DropLists.get(floorId);
				res.setFcValue(dropListExt.fcValue);
				res.setSweepCountLeft(manager.po.sweepCountLeft);
				res.setSweepCountMax(GlobalConfig.ResetNum);

				
				
//				GameData.DropLists.
				
				
				
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());

				return;

			}
		};
	}

}
