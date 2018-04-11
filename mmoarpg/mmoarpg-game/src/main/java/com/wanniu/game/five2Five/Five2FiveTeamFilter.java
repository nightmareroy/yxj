package com.wanniu.game.five2Five;

import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.team.TeamData;
import com.wanniu.game.team.TeamFilter;

/**
 * @author wanghaitao
 *
 */
public class Five2FiveTeamFilter implements TeamFilter {
	private int teamMaxNumber;// 队伍里最多人数

	private int targetId = GlobalConfig.Group_Team;// 目标

	public Five2FiveTeamFilter(int filterLogicServerId, int teamMaxNumber) {
		this.teamMaxNumber = teamMaxNumber;
	}

	@Override
	public boolean filter(TeamData team) {
		return team.isAutoTeam && team.targetId == targetId && team.memberCount() < teamMaxNumber;
	}
}
