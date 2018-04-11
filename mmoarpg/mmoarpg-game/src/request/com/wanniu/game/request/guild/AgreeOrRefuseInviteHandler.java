package com.wanniu.game.request.guild;

import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.guild.GuildCommonUtil;
import com.wanniu.game.guild.GuildResult;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildHandler.AgreeOrRefuseInviteRequest;
import pomelo.area.GuildHandler.AgreeOrRefuseInviteResponse;

@GClientEvent("area.guildHandler.agreeOrRefuseInviteRequest")
public class AgreeOrRefuseInviteHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		AgreeOrRefuseInviteRequest req = AgreeOrRefuseInviteRequest.parseFrom(pak.getRemaingBytes());
		int isAgree = req.getC2SIsAgree();
		String playerId = req.getC2SInviteId();
		String guildId = req.getC2SGuildId();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				AgreeOrRefuseInviteResponse.Builder res = AgreeOrRefuseInviteResponse.newBuilder();
				if (null == player) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				if (0 == isAgree) { // 拒绝
					String playerName = PlayerUtil.getColorPlayerNameByPro(player.getPro(), player.getName());
					HashMap<String, String> map = new HashMap<>();
					map.put("rolename", playerName);
					GuildCommonUtil.sendMailSystenType(playerId, SysMailConst.GUILD_INVITE_REFUSED, map);
					res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());
					return;
				}

				GuildResult resData = player.guildManager.joinGuild(guildId);
				int result = resData.result;
				if (result == 0) {
					// 发送反馈邮件
					String playerName = PlayerUtil.getColorPlayerNameByPro(player.getPro(), player.getName());
					TreeMap<String, String> map1 = new TreeMap<>();
					map1.put("rolename", playerName);
					GuildCommonUtil.sendMailSystenType(playerId, SysMailConst.GUILD_INVITE_AGREED, map1);
					res.setS2CCode(OK);
					body.writeBytes(res.build().toByteArray());
					return;
				}

				res.setS2CCode(FAIL);
				String errMsg = GuildCommonUtil.getJoinGuildErrorMsg(resData);
				res.setS2CMsg(errMsg);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
