package com.wanniu.game.request.revelry;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.KingCO;
import com.wanniu.game.revelry.RevelryClass;
import com.wanniu.game.revelry.RevelryColumn;
import com.wanniu.game.revelry.RevelryManager;
import com.wanniu.game.revelry.RevelryToday;

import pomelo.revelry.ActivityRevelryHandler.KingExchange;
import pomelo.revelry.ActivityRevelryHandler.RevelryColumnInfo;
import pomelo.revelry.ActivityRevelryHandler.RevelryGetColumnResponse;
import pomelo.revelry.ActivityRevelryHandler.RevelryTabInfo;
import pomelo.revelry.ActivityRevelryHandler.RevelryTodayInfo;

/**
 * 冲榜获取栏目协议.
 *
 * @author 小流氓(176543888@qq.com)
 */
@GClientEvent("revelry.activityRevelryHandler.revelryGetColumnRequest")
public class RevelryGetColumnHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		RevelryGetColumnResponse.Builder result = RevelryGetColumnResponse.newBuilder();

		List<RevelryClass> revelryClassList = RevelryManager.getInstance().getRevelryClassList();
		if (revelryClassList.isEmpty()) {
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}

		// 大类
		for (RevelryClass revelryClass : revelryClassList) {
			RevelryTabInfo.Builder tabInfo = RevelryTabInfo.newBuilder().setName(revelryClass.getName());

			int defaultSeleteIndex = 0;
			long timeleft = Integer.MAX_VALUE;
			// 每一天
			for (Entry<String, RevelryToday> e : revelryClass.getTodays().entrySet()) {
				RevelryTodayInfo.Builder todayInfo = RevelryTodayInfo.newBuilder().setName(e.getValue().getName());

				// 栏目
				for (RevelryColumn column : e.getValue().getColumns()) {
					RevelryColumnInfo.Builder columnInfo = RevelryColumnInfo.newBuilder();
					columnInfo.setId(column.getId());
					columnInfo.setName(column.getName());
					columnInfo.setLabel(column.getLabel());
					columnInfo.setGoto1(column.getGoto1());
					columnInfo.setGoto2(column.getGoto2());
					columnInfo.setTip(column.getTip());
					todayInfo.addColumn(columnInfo);
				}

				tabInfo.addToday(todayInfo);
				if (e.getValue().getTimeleft() < timeleft) {
					timeleft = e.getValue().getTimeleft();
					defaultSeleteIndex = tabInfo.getTodayCount();
				}
			}

			result.setSelectedIndex(defaultSeleteIndex);
			result.addInfo(tabInfo);
		}

		// 兑换奖励
		for (Entry<Integer, KingCO> e : GameData.Kings.entrySet()) {
			if (e.getValue().isOpen == 0) {
				continue;
			}
			KingCO template = e.getValue();
			KingExchange.Builder exchange = KingExchange.newBuilder();
			exchange.setTabId(template.tabID);
			exchange.setTabName(template.tabName);

			exchange.setItem1Code(template.item1code);
			exchange.setItem1Num(template.num1);

			exchange.setItem2Code(template.item2code);
			exchange.setItem2Num(template.num2);

			exchange.setAvatarId(template.avatarId);
			exchange.setTip(template.activityDesc);
			exchange.setShowType(template.showType);

			result.addExchange(exchange);
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