package com.wanniu.game.rank.handler;

import java.util.Collections;
import java.util.List;

import com.wanniu.game.GWorld;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.BaseDataExt;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerRankInfoPO;
import com.wanniu.game.rank.AbstractPlayerRankHandler;
import com.wanniu.game.rank.RankType;

import pomelo.Common.Avatar;

/**
 * 宠物排行榜.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class PetRankHandler extends AbstractPlayerRankHandler {

	@Override
	protected String getRedisKey(int logicServerId, int season) {
		return RankType.PET.getRedisKey(logicServerId, season);
	}

	@Override
	public void handle(WNPlayer player, Object... value) {
		// 修正个人信息
		PlayerRankInfoPO info = player.rankManager.getRankPO();
		int oldFightPower = info.getPetFightPower();
		info.setPetId((int) value[0]);
		info.setPetName((String) value[1]);
		info.setPetFightPower((int) value[2]);

		// 更新排名
		if (oldFightPower != info.getPetFightPower()) {
			String key = this.getRedisKey(GWorld.__SERVER_ID);
			this.updateRank(key, info.getPetFightPower(), player.getId());
		}
	}

	@Override
	protected void buildRankInfo(List<String> contents, PlayerRankInfoPO player, long score) {
		contents.add(player.getPetName());// 4：宠物名称
		contents.add(String.valueOf(score));// 5：宠物战斗力
		BaseDataExt prop = GameData.BaseDatas.get(player.getPetId());
		if (null == prop) {
			return;
		}
		// 6:petModel
		contents.add(prop.model);
		// 7:modePercent
		contents.add(String.valueOf(prop.modelPercent));
		// 8:petIcon
		contents.add(prop.icon);
		// 9:petQColor
		contents.add(String.valueOf(prop.qcolor));
		// 10:playerLevel
		contents.add(String.valueOf(player.getLevel()));
		// 11:modelY
		contents.add(String.valueOf(prop.modelY));
		
		contents.add(player.getGuildName());//12：公会名
	}

	@Override
	protected List<Avatar> buildAvatarsInfo(PlayerRankInfoPO player) {
		return Collections.emptyList();
	}
}