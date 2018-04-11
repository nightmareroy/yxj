package com.wanniu.game.rank;

import java.util.ArrayList;
import java.util.List;

import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerRankInfoPO;
import com.wanniu.redis.PlayerPOManager;

import pomelo.Common.Avatar;
import pomelo.area.LeaderBoardHandler.LeaderBoardData;
import pomelo.revelry.ActivityRevelryHandler.RevelryRankInfo;

public abstract class AbstractPlayerRankHandler extends AbstractRankHandler {

	@Override
	public String getSelfId(WNPlayer player) {
		return player.getId();
	}

	@Override
	public String getSelfName(WNPlayer player) {
		return player.getName();
	}

	/**
	 * 生成客户端协议字段
	 * 
	 * @param rank
	 */
	@Override
	public LeaderBoardData genBuilderInfo(String memberId, long score, int rank) {
		LeaderBoardData.Builder builder = LeaderBoardData.newBuilder();

		PlayerRankInfoPO player = RankCenter.getInstance().findRankPO(memberId);
		if (player == null) {
			return null;
		}

		List<String> contents = new ArrayList<String>();
		// 前面4位一样
		contents.add(String.valueOf(rank));// 0：排名
		contents.add(player.getId());// 1：玩家ID
		contents.add(String.valueOf(player.getPro()));// 2：职业
		contents.add(player.getName());// 3：玩家名称

		// 后面就不一样了...
		this.buildRankInfo(contents, player, score);// 4-6

		builder.addAllContents(contents);

		// 角色Avatar信息,不需要展示玩家模型，可以不传
		List<Avatar> avatars = buildAvatarsInfo(player);
		if (!avatars.isEmpty()) {
			builder.addAllAvatars(avatars);
		}
		return builder.build();
	}

	/**
	 * 构建第4位以后的参数，等级，战斗力等
	 */
	protected abstract void buildRankInfo(List<String> contents, PlayerRankInfoPO player, long score);

	/**
	 * 宠物，坐骑，公会，需要重写此方法返回空.
	 */
	protected List<Avatar> buildAvatarsInfo(PlayerRankInfoPO player) {
		return PlayerUtil.getBattlerServerAvatar(player.getId());
	}

	@Override
	public void buildRevelryRankInfo(RevelryRankInfo.Builder info, SimpleRankData rankData) {
		// 0:排名
		info.addContents(String.valueOf(rankData.getRank()));
		// 1:排行分数
		info.addContents(String.valueOf(rankData.getScore()));

		String playerId = rankData.getId();
		PlayerRankInfoPO rankinfo = PlayerPOManager.findPO(ConstsTR.playerRankTR, playerId, PlayerRankInfoPO.class);
		if (rankinfo == null) {
			// 2:角色名称
			info.addContents("-");
			// 3:角色职业
			info.addContents("1");
		} else {
			// 2:角色名称
			info.addContents(rankinfo.getName());
			// 3:角色职业
			info.addContents(String.valueOf(rankinfo.getPro()));
			// 4:角色等级
			info.addContents(String.valueOf(rankinfo.getLevel()));
		}

		// 第一名，要有3D模型
		if (rankData.getRank() == 1) {
			List<Avatar> avatars = PlayerUtil.getBattlerServerAvatar(playerId);
			if (!avatars.isEmpty()) {
				info.addAllAvatars(avatars);
			}
		}
	}
}