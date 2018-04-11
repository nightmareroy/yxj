package com.wanniu.game.request.fightLevel.illusion;

import java.io.IOException;
import java.util.Map;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.area.Illusion2Area;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.request.fightLevel.FightLevelLine;
import com.wanniu.game.team.TeamData.TeamMemberData;

import pomelo.area.FightLevelHandler.EnterLllsion2Response;

/**
 * 进入幻境2
 */
@GClientEvent("area.fightLevelHandler.enterLllsion2Request")
public class EnterIllsion2Handler extends FightLevelLine {

	public PomeloResponse request(WNPlayer player) throws Exception {

		if (!isInOpenTime(player)) {
			return new ErrorResponse(LangService.getValue("FIVE_2_FIVE_NOT_IN_TIME"));
		}

		Map<String, TeamMemberData> members = player.getTeamManager().getTeamMembers();
		if (members != null && !members.isEmpty()) {
			if (!player.getTeamManager().isTeamLeader()) {
				return new ErrorResponse(LangService.getValue("DUNGEON_GUILDBOSS_NOT_TEAM_LEADER"));
			}
		}

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				EnterLllsion2Response.Builder res = EnterLllsion2Response.newBuilder();

				AreaUtil.enterArea(player, Illusion2Area.DEFAULT_ID);

				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

	/**
	 * 当前时间是否开启
	 */
	public boolean isInOpenTime(WNPlayer player) {
		return player.dailyActivityMgr.calIllusion2ScriptNum() == 2;
	}
}