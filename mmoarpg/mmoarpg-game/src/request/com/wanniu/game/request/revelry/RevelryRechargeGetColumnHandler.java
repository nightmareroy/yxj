package com.wanniu.game.request.revelry;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.util.DateUtils;
import com.wanniu.game.GWorld;
import com.wanniu.game.activity.RechargeActivityService;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.data.ext.ActivityExt;
import com.wanniu.game.player.WNPlayer;

import pomelo.revelry.ActivityRevelryHandler.RevelryRechargeColumn;
import pomelo.revelry.ActivityRevelryHandler.RevelryRechargeGetColumnResponse;

/**
 * 冲榜累计充值.
 *
 * @author 小流氓(176543888@qq.com)
 */
@GClientEvent("revelry.activityRevelryHandler.revelryRechargeGetColumnRequest")
public class RevelryRechargeGetColumnHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		RevelryRechargeGetColumnResponse.Builder result = RevelryRechargeGetColumnResponse.newBuilder();

		WNPlayer player = (WNPlayer) pak.getPlayer();
		// 活动时间配置
		ActivityExt activityExt = player.activityManager.findActivityByType(Const.ActivityRewardType.REVELRY_RECHARGE.getValue());
		if (activityExt == null) {
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}
		result.setDescribe(activityExt.activityRule);

		LocalDate openServerDate = GWorld.OPEN_SERVER_DATE;
		result.setBeginTime(openServerDate.atTime(0, 0, 0).format(DateUtils.F_YYYYMMDDHHMMSS));
		result.setEndTime(openServerDate.plusDays(6).atTime(23, 59, 59).format(DateUtils.F_YYYYMMDDHHMMSS));

		int day = RechargeActivityService.getInstance().getOpenServerDay();
		if (day > 0) {
			result.setToday(day);
		}

		Map<Integer, String> columns = RechargeActivityService.getInstance().getAllColumn();
		for (Map.Entry<Integer, String> e : columns.entrySet()) {
			RevelryRechargeColumn.Builder column = RevelryRechargeColumn.newBuilder();
			column.setDay(e.getKey());
			column.setName(e.getValue());
			result.addColumn(column);
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