package com.wanniu.game.request.guild;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.guild.GuildResult;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildHandler.KickMemberRequest;
import pomelo.area.GuildHandler.KickMemberResponse;

@GClientEvent("area.guildHandler.kickMemberRequest")
public class KickMemberHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		KickMemberRequest req = KickMemberRequest.parseFrom(pak.getRemaingBytes());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				KickMemberResponse.Builder res = KickMemberResponse.newBuilder();
				String kickId = req.getMemberId();
				WNPlayer kickPlayer = PlayerUtil.getOnlinePlayer(kickId);

				if (null != kickPlayer) {
					int areaType = AreaUtil.getAreaType(kickPlayer.getAreaId());
					if (areaType == Const.SCENE_TYPE.GUILD_DUNGEON.getValue()) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("IN_GDUNGEON_KICK"));
						body.writeBytes(res.build().toByteArray());
						return;
					}
				}

				GuildResult resData = player.guildManager.kickMember(kickId);
				int result = resData.result;
				if (result == 0) {
					res.setS2CCode(OK);
					res.setS2CLeftKickNum(resData.leftKickNum);
					body.writeBytes(res.build().toByteArray());
					return;

				} else if (result == -1) {
					res.setS2CCode(FAIL);
					if (StringUtil.isNotEmpty(resData.des)) {
						res.setS2CMsg(resData.des);
					} else {
						res.setS2CMsg(LangService.getValue("GUILD_NOT_JOIN"));
					}
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -2) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_NO_POWER"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -3) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_MEMBER_NOT_EXIST"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -4) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_KICK_JOB_TOO_LOW"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -5) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_JOIN_TIME_TOO_SHORT"));
					body.writeBytes(res.build().toByteArray());
					return;
				} else if (result == -6) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("GUILD_KICK_NUM_NOT_ENOUGH"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				res.setS2CCode(FAIL);
				res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
