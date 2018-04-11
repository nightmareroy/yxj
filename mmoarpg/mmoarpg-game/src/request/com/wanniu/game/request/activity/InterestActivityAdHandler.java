package com.wanniu.game.request.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.data.ActivityCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.ActivityExt;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.ActivityHandler.ActivityListInfo;
import pomelo.area.ActivityHandler.InterestActivityAdResponse;

@GClientEvent("area.activityHandler.interestActivityAdRequest")
public class InterestActivityAdHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		// InterestActivityAdRequest req =
		// InterestActivityAdRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			protected void write() throws IOException {
				WNPlayer player = (WNPlayer) pak.getPlayer();
				InterestActivityAdResponse.Builder res = InterestActivityAdResponse.newBuilder();

				long now = System.currentTimeMillis();
				Map<Integer, ActivityExt> props = GameData.Activitys;

				ArrayList<ActivityListInfo> list_info = new ArrayList<>();
				for (ActivityCO p : props.values()) {
					ActivityExt prop = (ActivityExt) p;
					if (prop.beginTime < now && now < prop.endTime) {
						if (prop.activityTab == Const.ActivityRewardType.FIRST_PAY.getValue()) {
							if (player.activityManager.hasFirstPayReward()) {
								continue;
							}
						} else if (prop.activityTab == Const.ActivityRewardType.SECOND_PAY.getValue()) {
							if (!player.activityManager.hasFirstPayReward()) {
								continue;
							}
							if (!player.activityManager.isSecondPayVaild()) {
								continue;
							}
						} else if (prop.activityTab == Const.ActivityRewardType.OPEN_SEVEN_DAY.getValue()) {
							continue;
						}
						ActivityListInfo.Builder info = ActivityListInfo.newBuilder();
						info.setActivityId(prop.activityID);
						info.setHudNum(player.activityManager.getActivityHud(prop.activityID, prop.activityTab));
						list_info.add(info.build());
						// activityList.activityId = prop.ActivityID;
						// activityList.hudNum =
						// player.activityManager.getActivityHud(prop.ActivityID,prop.ActivityTab);
						// result.s2c_activityList.push({activityId:
						// activityList.activityId, hudNum:
						// activityList.hudNum});
					}
				}
				res.addAllS2CActivityList(list_info);
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

}
