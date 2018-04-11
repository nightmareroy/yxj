package com.wanniu.game.request.team;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.area.Area;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;

/**
 * 组队功能过滤
 * @author agui
 */
public abstract class TeamRequestFilter extends PomeloRequest {

	
	protected PomeloResponse checkRemote(WNPlayer player) {
		if (player.isRomote()) {
			return new ErrorResponse(LangService.getValue("TEAM_ACROSS"));
		}
		return null;
	}
	
	protected PomeloResponse checkOpen(WNPlayer player) {
		Area area = player.getArea();
		if (area == null || !area.isOpenJoinTeam()) {
			return new ErrorResponse(LangService.getValue("TEAM_BATTLE_ERR"));
		}
		return null;
	}
	
	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		if (player.getLevel() < GlobalConfig.Team_Min_Level) {
			return new ErrorResponse(LangService.format("TEAM_LV_LESS", GlobalConfig.Team_Min_Level));
		}
		PomeloResponse res = checkRemote(player);
		if (res != null) {
			return res;
		}
		res = checkOpen(player);
		if (res != null) {
			return res;
		}
		return request(player);
	}

	public abstract PomeloResponse request(WNPlayer player) throws Exception;

	public short getType() {
		return 0x302;
	}
	
}
