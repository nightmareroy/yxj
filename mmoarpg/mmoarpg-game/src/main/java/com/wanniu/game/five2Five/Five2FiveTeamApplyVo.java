package com.wanniu.game.five2Five;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.wanniu.game.team.TeamData.TeamMemberData;

/**
 * @author wanghaitao
 *
 */
public class Five2FiveTeamApplyVo {
	public Date joinTime;

	public Map<String, TeamMemberData> teamMembers;

	public String teamId;

	public Five2FiveTeamApplyVo(Map<String, TeamMemberData> teamMembers, String teamId) {
		this.teamMembers = new HashMap<>();
		this.teamMembers.putAll(teamMembers);
		this.teamId = teamId;
	}

}
