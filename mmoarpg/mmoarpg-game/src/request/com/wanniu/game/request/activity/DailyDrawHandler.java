package com.wanniu.game.request.activity;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.activity.ActivityManager;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.data.AdventureItemAddCO;
import com.wanniu.game.data.AdventureItemCO;
import com.wanniu.game.data.ext.ActivityConfigExt;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.player.bi.LogReportService;
import com.wanniu.game.poes.ActivityDataPO;

import pomelo.area.ActivityFavorHandler.DailyDrawAwardInfo;
import pomelo.area.ActivityFavorHandler.DailyDrawRequest;
import pomelo.area.ActivityFavorHandler.DailyDrawResponse;

/**
 * 幸运抽奖.
 *
 * @author 小流氓(176543888@qq.com)
 */
@GClientEvent("area.activityFavorHandler.dailyDrawRequest")
public class DailyDrawHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		DailyDrawRequest req = DailyDrawRequest.parseFrom(pak.getRemaingBytes());
		final int id = req.getId();// 0=普通抽奖，1=新春抽奖
		final int timeType = req.getTimeType();// 0=单抽，1=10连抽
		int costType = req.getCostType();// 0=免费，1=使用道具，2=元宝抽

		WNPlayer player = (WNPlayer) pak.getPlayer();

		ActivityManager activityManager = player.activityManager;
		ActivityDataPO activityDataPO = activityManager.toJson4Serialize();

		// 单抽 十连抽 的消耗
		List<ActivityConfigExt> activityConfigExt = activityManager.findActivitieConfigsByRewardType(Const.ActivityRewardType.LUCK_DRAW);
		int proportion = -1;
		for (ActivityConfigExt activityConfigExt_temp : activityConfigExt) {
			if (activityConfigExt_temp.notes1.equals("Activity_Adventure_Proportion")) {
				proportion = activityConfigExt_temp.parameter1;
			}
		}
		if (proportion == -1) {
			Out.error("数据表记录缺失");
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}

		// 背包里总的抽奖卷数量
		int raffleticketsItemCount = player.bag.findItemNumByCode(Const.raffletickets);
		int needConsumeCount = 0;// 需要消耗多少个材料
		int needConsumeMoney = 0;// 需要消耗多少个元宝

		// 消耗来源
		Const.GOODS_CHANGE_TYPE costDes = Const.GOODS_CHANGE_TYPE.ActivityDraw;
		if (id == 1) {
			costDes = Const.GOODS_CHANGE_TYPE.ActivityDrawSpring;
		}

		switch (costType) {
		case 0:// 免费
			switch (id) {
			case 0:
				if (activityDataPO.daily_draw_free_time < 1) {
					return new ErrorResponse(LangService.getValue("ACTIVITY_DAILY_DRAW_NO_FREE_TIME"));
				}
				activityDataPO.daily_draw_free_time--;
				break;
			case 1:
				if (activityDataPO.daily_draw_free_time_add < 1) {
					return new ErrorResponse(LangService.getValue("ACTIVITY_DAILY_DRAW_NO_FREE_TIME"));
				}
				activityDataPO.daily_draw_free_time_add--;
				break;
			default:
				return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
			}
			break;
		case 1:// 使用道具

			// 单抽
			if (timeType == 0) {
				needConsumeCount = 1;
			}
			// 1=10连抽
			else if (timeType == 1) {
				needConsumeCount = 10;
			} else {
				return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
			}
			// 直接扣，扣不成功提示材料不够.
			if (!player.bag.discardItem(Const.raffletickets, needConsumeCount, costDes)) {
				return new ErrorResponse(LangService.getValue("ACTIVITY_DAILY_DRAW_NO_EXPLORE_TICKETS"));
			}
			break;
		case 2:// 使用元宝补

			// 单抽
			if (timeType == 0) {
				if (raffleticketsItemCount >= 1) {
					needConsumeCount = 1;
				} else {
					needConsumeMoney = proportion * 1;
				}
			}
			// 1=10连抽
			else if (timeType == 1) {
				if (raffleticketsItemCount >= 10) {
					needConsumeCount = 10;
				} else {
					needConsumeCount = raffleticketsItemCount;
					needConsumeMoney = proportion * (10 - raffleticketsItemCount);
				}
			} else {
				return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
			}

			// 先扣钱
			if (needConsumeMoney > 0) {
				if (!player.moneyManager.costDiamond(needConsumeMoney, costDes)) {
					return new ErrorResponse(LangService.getValue("ACTIVITY_DAILY_DRAW_NO_DIAMOND"));
				}
			}
			// 再扣材料
			if (needConsumeCount > 0) {
				player.bag.discardItem(Const.raffletickets, needConsumeCount, costDes);
			}
			break;
		default:
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}

		DailyDrawResponse.Builder res = DailyDrawResponse.newBuilder();
		res.setExploredTicketCountLeft(raffleticketsItemCount - needConsumeCount);

		switch (id) {
		case 0:
			List<AdventureItemCO> adventureItemCOList = null;
			if (timeType == 0)
				adventureItemCOList = activityManager.DailyDraw_Draw(1);
			else
				adventureItemCOList = activityManager.DailyDraw_Draw(10);

			for (AdventureItemCO adventureItemCO : adventureItemCOList) {
				DailyDrawAwardInfo.Builder awardInfoBuilder = DailyDrawAwardInfo.newBuilder();
				awardInfoBuilder.setId(adventureItemCO.id);
				awardInfoBuilder.setCode(adventureItemCO.item);
				awardInfoBuilder.setNum(adventureItemCO.itemNum);
				res.addAwards(awardInfoBuilder.build());
			}

			res.setFreeCountLeft(activityDataPO.daily_draw_free_time);

			Out.info(player.getId(), ":每日抽奖成功,抽奖类型:", timeType == 0 ? "单抽" : "十连抽", "常规抽奖");
			break;
		case 1:
			List<AdventureItemAddCO> adventureItemCOAddList = null;
			if (timeType == 0)
				adventureItemCOAddList = activityManager.DailyDraw_Draw_Add(1);
			else
				adventureItemCOAddList = activityManager.DailyDraw_Draw_Add(10);

			for (AdventureItemAddCO adventureItemAddCO : adventureItemCOAddList) {
				DailyDrawAwardInfo.Builder awardInfoBuilder = DailyDrawAwardInfo.newBuilder();
				awardInfoBuilder.setId(adventureItemAddCO.id);
				awardInfoBuilder.setCode(adventureItemAddCO.item);
				awardInfoBuilder.setNum(adventureItemAddCO.itemNum);
				res.addAwards(awardInfoBuilder.build());
			}

			res.setFreeCountLeft(activityDataPO.daily_draw_free_time_add);

			Out.info(player.getId(), ":每日抽奖成功,抽奖类型:", timeType == 0 ? "单抽" : "十连抽", "新春抽奖");
			break;
		default:
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}

		// id 0=普通抽奖，1=新春抽奖
		int count = timeType == 0 ? 1 : 10;// 0=单抽，1=10连抽
		LogReportService.getInstance().ansycReportLuckDraw(player, id, count, needConsumeMoney, needConsumeCount);

		return new PomeloResponse() {
			protected void write() throws IOException {
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}