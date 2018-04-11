package com.wanniu.game.request.leaderboard;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.GWorld;
import com.wanniu.game.leaderBoard.LeaderBoardDetail;
import com.wanniu.game.leaderBoard.LeaderBoardProto;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.rank.RankType;

import io.netty.util.internal.StringUtil;
import pomelo.area.LeaderBoardHandler.LeaderBoardData;
import pomelo.area.LeaderBoardHandler.LeaderBoardRequest;
import pomelo.area.LeaderBoardHandler.LeaderBoardResponse;

/**
 * 拉取排行榜信息.
 * 
 * @author jjr
 */
@GClientEvent("area.leaderBoardHandler.leaderBoardRequest")
public class LeaderBoardHandler extends PomeloRequest {

	public short getType() {
		return 0x311;
	}

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		LeaderBoardRequest req = LeaderBoardRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				LeaderBoardResponse.Builder res = LeaderBoardResponse.newBuilder();
				RankType type = RankType.valueOf(req.getC2SKind());
				if (type == null || type.getHandler() == null) {
					Out.warn("未实现的排行榜:", req.getC2SKind());
					// 无数据返回
					res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());
					return;
				}

				LeaderBoardProto result = type.getHandler().getRankData(player, req.getC2SSeason());
				if (null == result) {
					// 无数据返回
					res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());
					return;
				}

				res.setS2CCode(OK);
				res.addAllS2CLists(result.s2c_lists);
				String rank = "0";
				if (result.s2c_myData.getContentsCount() > 0) {
					rank = result.s2c_myData.getContents(0);
				}

				// 榜单中没有找到自己的排名
				if (null == result.s2c_myData || StringUtil.isNullOrEmpty(rank) || Integer.parseInt(rank) <= 0) {
					boolean isNeedSelfData = true;
					if (type == RankType.FIGHTPOWER_1 || type == RankType.FIGHTPOWER_3 || type == RankType.FIGHTPOWER_5) {
						int kindPro = (type.getValue() - RankType.FIGHTPOWER_1.getValue()) + 1;
						if (player.getPro() != kindPro) {
							isNeedSelfData = false;
						}
					}
					if (isNeedSelfData && (type == RankType.GUILD_BOSS_SINGLE || type == RankType.GUILD_BOSS_GUILD || type == RankType.GUILD_BOSS_PRE_SINGLE || type == RankType.GUILD_BOSS_PRE_GUILD)) {
						isNeedSelfData = false;
					}

					if (isNeedSelfData) {
						LeaderBoardDetail detail = new LeaderBoardDetail();
						detail.memberId = type.getHandler().getSelfId(player);
						detail.rank = type.getHandler().getSeasonRank(GWorld.__SERVER_ID, req.getC2SSeason(), detail.memberId);
						if (detail.rank > 0) {
							detail.score = type.getHandler().getSeasonScore(GWorld.__SERVER_ID, req.getC2SSeason(), detail.memberId);

							LeaderBoardData myData = type.getHandler().genBuilderInfo(detail.memberId, (int) detail.score, (int) detail.rank);
							if (null != myData && myData.getContentsCount() > 1) {
								result.s2c_myData = myData;
							}
						}
					}
				}

				if (result.s2c_lists.size() > 0 && null != result.s2c_myData && result.s2c_myData.getContentsCount() > 0) {
					res.setS2CMyData(result.s2c_myData);
				}
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
