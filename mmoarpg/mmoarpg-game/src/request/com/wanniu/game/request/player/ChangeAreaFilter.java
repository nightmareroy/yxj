package com.wanniu.game.request.player;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.team.TeamData.TeamMemberData;

/**
 * 切场景过滤
 * @author agui
 *
 */
public abstract class ChangeAreaFilter extends PomeloRequest {

	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();
		if (player.inPvP()) {
			return new ErrorResponse(LangService.getValue("MAP_IN_FIGHT"));
		}

		TeamMemberData teamMember = player.getTeamManager().getTeamMember();
		if (teamMember != null && !teamMember.isLeader && teamMember.follow) {
			return new ErrorResponse(LangService.getValue("TEAM_FOLLOW_CHANGE_AREA"));
		}

		return request(player);
	}
	
	public abstract PomeloResponse request(WNPlayer player) throws Exception;
	

	public short getType() {
		return 0x501;
	}

}