package com.wanniu.game.request.revelry;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.activity.RechargeActivityService;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.StartSerRechargeCO;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.RevelryRechargePO;

import pomelo.revelry.ActivityRevelryHandler.RevelryRechargeAwardInfo;
import pomelo.revelry.ActivityRevelryHandler.RevelryRechargeGetInfoRequest;
import pomelo.revelry.ActivityRevelryHandler.RevelryRechargeGetInfoResponse;
import pomelo.revelry.ActivityRevelryHandler.RevelryRechargeItem;

/**
 * 冲榜累计充值.
 *
 * @author 小流氓(176543888@qq.com)
 */
@GClientEvent("revelry.activityRevelryHandler.revelryRechargeGetInfoRequest")
public class RevelryRechargeGetInfoHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		RevelryRechargeGetInfoRequest req = RevelryRechargeGetInfoRequest.parseFrom(pak.getRemaingBytes());
		final int day = req.getDay();
		WNPlayer player = (WNPlayer) pak.getPlayer();

		RevelryRechargeGetInfoResponse.Builder result = RevelryRechargeGetInfoResponse.newBuilder();

		final RevelryRechargePO po = RechargeActivityService.getInstance().getRevelryRechargeInfo(player.getId(), day);
		for (StartSerRechargeCO template : GameData.findStartSerRecharges(v -> v.date == day)) {
			RevelryRechargeAwardInfo.Builder awardInfo = RevelryRechargeAwardInfo.newBuilder();
			awardInfo.setId(template.iD);
			awardInfo.setCurrNum(po == null ? 0 : po.getRmb());
			awardInfo.setNeedNum(template.rechargeNumber);
			awardInfo.setState(po == null ? 0 : po.getState().getOrDefault(template.iD, 0));// 0=未领取，1=可领取，2=已领取
			if (StringUtils.isNotEmpty(template.reward1)) {
				RevelryRechargeItem.Builder item = RevelryRechargeItem.newBuilder();
				item.setCode(template.reward1);
				item.setGroupCount(template.num1);
				awardInfo.addItem(item);
			}
			if (StringUtils.isNotEmpty(template.reward2)) {
				RevelryRechargeItem.Builder item = RevelryRechargeItem.newBuilder();
				item.setCode(template.reward2);
				item.setGroupCount(template.num2);
				awardInfo.addItem(item);
			}
			if (StringUtils.isNotEmpty(template.reward3)) {
				RevelryRechargeItem.Builder item = RevelryRechargeItem.newBuilder();
				item.setCode(template.reward3);
				item.setGroupCount(template.num3);
				awardInfo.addItem(item);
			}
			if (StringUtils.isNotEmpty(template.reward4)) {
				RevelryRechargeItem.Builder item = RevelryRechargeItem.newBuilder();
				item.setCode(template.reward4);
				item.setGroupCount(template.num4);
				awardInfo.addItem(item);
			}
			if (StringUtils.isNotEmpty(template.reward5)) {
				RevelryRechargeItem.Builder item = RevelryRechargeItem.newBuilder();
				item.setCode(template.reward5);
				item.setGroupCount(template.num5);
				awardInfo.addItem(item);
			}
			result.addInfo(awardInfo);
		}

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				result.setS2CCode(OK);
				body.writeBytes(result.build().toByteArray());
			}
		};
	}
}