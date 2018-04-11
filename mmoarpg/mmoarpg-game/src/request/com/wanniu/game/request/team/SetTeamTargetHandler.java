package com.wanniu.game.request.team;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.area.Area;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.TeamTargetCO;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.team.TeamData;
import com.wanniu.game.team.TeamData.TeamMemberData;

import pomelo.area.TeamHandler.SetTeamTargetRequest;
import pomelo.area.TeamHandler.SetTeamTargetResponse;

/**
 * @author agui
 */
@GClientEvent("area.teamHandler.setTeamTargetRequest")
public class SetTeamTargetHandler extends TeamRequestFilter {

	@Override
	public PomeloResponse request(WNPlayer player) throws Exception {
		TeamMemberData selfTeamMember = player.getTeamManager().getTeamMember();
		if (selfTeamMember == null || !selfTeamMember.isLeader) {
			return new ErrorResponse(LangService.getValue("TEAM_NO_AUTHORITY"));
		}
		Area area = player.getArea();
		if (area != null && !area.isNormal()) {
			return new ErrorResponse(LangService.getValue("TEAM_LOCKED"));
		}
		SetTeamTargetRequest msg = SetTeamTargetRequest.parseFrom(pak.getRemaingBytes());
		int targetId = msg.getC2STargetId();
	    int difficulty = msg.getC2SDifficulty();
	    int minLevel = msg.getC2SMinLevel();
	    int maxLevel = msg.getC2SMaxLevel();
	    int isAutoTeam = msg.getC2SIsAutoTeam();
	    int isAutoStart = msg.getC2SIsAutoStart();
		return new PomeloResponse() {
			
			@Override
			protected void write() throws IOException {
				SetTeamTargetResponse.Builder res = SetTeamTargetResponse.newBuilder();
				TeamTargetCO targetProp = GameData.TeamTargets.get(targetId);
			    //参数检测
			    if((targetProp == null) ||
			        (difficulty != 1 && difficulty != 2 && difficulty != 3) ||
			        (minLevel < 0 || minLevel > GlobalConfig.Role_LevelLimit) ||
			        (isAutoStart != 0 && isAutoStart != 1) ||
			        (isAutoTeam != 0 && isAutoTeam != 1)){
			    	res.setS2CCode(FAIL);
			    	res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
			    	body.writeBytes(res.build().toByteArray());
			        return;
			    }


				TeamData team = player.getTeamManager().getTeam();
			    team.targetId = targetId;
			    team.difficulty = difficulty;
			    team.minLevel = minLevel;
			    team.maxLevel = maxLevel;
			    team.isAutoTeam = isAutoTeam == 1;
			    team.isAutoStart = isAutoStart == 1;
			    
				PlayerUtil.sendSysMessageToPlayer(LangService.getValue("TEAM_TARGET_SUCCESS"), player.getId(), Const.TipsType.BLACK);
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
				
				team.onTeamChange();
			}
		};
	}

}
