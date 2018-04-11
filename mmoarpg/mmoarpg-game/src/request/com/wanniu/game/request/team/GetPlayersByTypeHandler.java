package com.wanniu.game.request.team;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.area.Area;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.daoyou.DaoYouService;
import com.wanniu.game.guild.GuildUtil;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.team.TeamUtil;

import pomelo.area.TeamHandler.GetPlayersByTypeRequest;
import pomelo.area.TeamHandler.GetPlayersByTypeResponse;
import pomelo.area.TeamHandler.Player;

/**
 * @author agui
 */
@GClientEvent("area.teamHandler.getPlayersByTypeRequest")
public class GetPlayersByTypeHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		GetPlayersByTypeRequest msg = GetPlayersByTypeRequest.parseFrom(pak.getRemaingBytes());
		int c2s_type = msg.getC2SType();
		Out.debug("getPlayersByTypeRequest c2s_type::", c2s_type);
		// 参数检测(1.好友 2.盟友 3.公会成员4.附近)
		if (c2s_type < 1 || c2s_type > 4) {
			return new ErrorResponse(LangService.getValue("PARAM_ERROR"));
		}

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetPlayersByTypeResponse.Builder res = GetPlayersByTypeResponse.newBuilder();

				Collection<String> playerIds = null;

				if (c2s_type == 1) {
					playerIds = player.getFriendManager().getAllFriendId();
				} else if (c2s_type == 2) {
					playerIds = DaoYouService.getInstance().getDaoYouMemPlayerId(player.getId());
				} else if (c2s_type == 3) {
					playerIds = GuildUtil.getGuildMemberIdList(player.guildManager.getGuildId());
				} else {
					Area area = player.getArea();
					playerIds = area.actors.keySet();
				}

				if (playerIds == null) {
					playerIds = new ArrayList<>(0);
				}

				List<String> finalPlayerIds = new ArrayList<>(playerIds.size());
				for (String rid : playerIds) {
					if (!TeamUtil.isInTeam(rid) && PlayerUtil.isOnline(rid) && !player.getId().equals(rid)) {
						finalPlayerIds.add(rid);
					}
				}

				int maxViewCount = GlobalConfig.TeamViewMAX;
				if (finalPlayerIds.size() > maxViewCount) {
					Collections.shuffle(finalPlayerIds);
				} else {
					maxViewCount = finalPlayerIds.size();
				}
				
				for (int i = 0; i < maxViewCount; i++) {
					String playerId = finalPlayerIds.get(i);
					Player.Builder playerTeamData = PlayerUtil.transToJson4TeamMemberSimple(PlayerUtil.findPlayer(playerId));
					if (TeamUtil.isHasInvitedPlayer(player, playerId)) {
						playerTeamData.setIsInvited(1);
					} else {
						playerTeamData.setIsInvited(0);
					}
					res.addS2CPlayers(playerTeamData.build());
				}

				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}

	public short getType() {
		return 0x306;
	}
	
}
