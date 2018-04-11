package com.wanniu.game.request.revelry;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.RevelryCO;
import com.wanniu.game.data.RevelryConfigCO;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.rank.RankType;
import com.wanniu.game.rank.SimpleRankData;
import com.wanniu.game.revelry.RevelryManager;

import pomelo.revelry.ActivityRevelryHandler.RevelryAwardInfo;
import pomelo.revelry.ActivityRevelryHandler.RevelryConfigInfo;
import pomelo.revelry.ActivityRevelryHandler.RevelryConfigInfo.Builder;
import pomelo.revelry.ActivityRevelryHandler.RevelryGetRankInfoRequest;
import pomelo.revelry.ActivityRevelryHandler.RevelryGetRankInfoResponse;
import pomelo.revelry.ActivityRevelryHandler.RevelryRankInfo;

/**
 * 冲榜获取排行数据协议.
 *
 * @author 小流氓(176543888@qq.com)
 */
@GClientEvent("revelry.activityRevelryHandler.revelryGetRankInfoRequest")
public class RevelryGetRankInfoHandler extends PomeloRequest {
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();

		RevelryGetRankInfoRequest req = RevelryGetRankInfoRequest.parseFrom(pak.getRemaingBytes());

		RevelryCO template = GameData.Revelrys.get(req.getId());
		if (template == null) {
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}

		List<RevelryConfigCO> configs = GameData.findRevelryConfigs(v -> template.tabID.equals(v.type));
		// 拉取排行信息.
		int maxRank = 1;
		for (RevelryConfigCO co : configs) {
			if (co.parameter1 == co.parameter2 && co.parameter1 > maxRank) {
				maxRank = co.parameter1;
			}
		}

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime endTime = GWorld.OPEN_SERVER_DATE.plusDays(template.endDays1).atTime(0, 0, 0, 0);
		boolean isGameOver = now.isAfter(endTime);

		RankType rankType = RevelryManager.getInstance().toRankType(template.activityKey);
		Map<Integer, SimpleRankData> rankMap = rankType.getHandler().getSimpleRankData(isGameOver, template.tabID, 0, maxRank);

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				RevelryGetRankInfoResponse.Builder res = RevelryGetRankInfoResponse.newBuilder();
				res.setRankKey(template.activityKey);

				RevelryRankInfo.Builder selfInfo = null;
				int selfRank = 0;

				for (RevelryConfigCO co : configs) {
					RevelryConfigInfo.Builder info = RevelryConfigInfo.newBuilder();
					info.setMinRank(co.parameter1);
					info.setMaxRank(co.parameter2);

					this.buildItem(info, co.item1code, co.num1);
					this.buildItem(info, co.item2code, co.num2);
					this.buildItem(info, co.item3code, co.num3);
					this.buildItem(info, co.item4code, co.num4);

					// 玩家信息
					if (co.parameter1 == co.parameter2) {
						SimpleRankData rankData = rankMap.get(co.parameter1);
						if (rankData != null) {
							RevelryRankInfo.Builder playerInfo = RevelryRankInfo.newBuilder();
							// 具体是公会还是个人由排行实现去构建
							rankType.getHandler().buildRevelryRankInfo(playerInfo, rankData);
							info.setPlayer(playerInfo);

							if (rankData.getId().equals(player.getId())) {
								selfInfo = playerInfo;
								selfRank = rankData.getRank();
							}
						}
					}

					res.addConfig(info);
				}

				// 如果不在前三名，还要单独取自己的排行与分数.
				if (selfInfo == null) {
					SimpleRankData rankData = rankType.getHandler().getSelfRankInfo(isGameOver, template.tabID, player);
					RevelryRankInfo.Builder playerInfo = RevelryRankInfo.newBuilder();
					rankType.getHandler().buildRevelryRankInfo(playerInfo, rankData);
					selfInfo = playerInfo;
					selfRank = rankData.getRank();
				}

				res.setSelf(selfInfo);
				res.setSelfRank(selfRank);
				// 第一的称号
				res.setTitle(template.icon);

				if (isGameOver) {
					res.setTimeleft(0);
				} else {
					res.setTimeleft((int) Duration.between(now, endTime).getSeconds());
				}
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}

			private void buildItem(Builder info, String itemcode, int num) {
				if (StringUtils.isNotEmpty(itemcode)) {
					RevelryAwardInfo.Builder item = RevelryAwardInfo.newBuilder();
					item.setItemcode(itemcode);
					item.setItemcount(num);
					info.addAward(item);
				}
			}
		};
	}
}