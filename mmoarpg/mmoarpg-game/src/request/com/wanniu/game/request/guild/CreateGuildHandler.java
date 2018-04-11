package com.wanniu.game.request.guild;

import java.io.IOException;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.data.ext.GuildSettingExt;
import com.wanniu.game.guild.GuildResult;
import com.wanniu.game.guild.GuildUtil;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildHandler.CreateGuildRequest;
import pomelo.area.GuildHandler.CreateGuildResponse;
import pomelo.area.GuildHandler.MyGuildInfo;

/**
 * 创建公会
 * 
 * @author jjr
 *
 */
@GClientEvent("area.guildHandler.createGuildRequest")
public class CreateGuildHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		CreateGuildRequest req = CreateGuildRequest.parseFrom(pak.getRemaingBytes());
		JSONObject params = new JSONObject();
		params.put("icon", req.getC2SIcon());
		params.put("name", req.getC2SName());
		params.put("qqGroup", req.getC2SQqGroup());

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				CreateGuildResponse.Builder res = CreateGuildResponse.newBuilder();
				if (null == player) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				GuildResult ret = player.guildManager.createGuild(params);
				int result = ret.result;
				if (0 == result) {
					MyGuildInfo guildInfo = player.guildManager.getMyGuildInfo();
					if (null == guildInfo) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("GUILD_NAME_EXIST"));
						body.writeBytes(res.build().toByteArray());
						return;
					}
					MessageUtil.sendRollChat(GWorld.__SERVER_ID, String.format(LangService.getValue("GUILD_CREATE"), player.getName(),req.getC2SName()), Const.CHAT_SCOPE.SYSTEM);
					res.setS2CCode(OK);
					res.setS2CGuildInfo(guildInfo);
					body.writeBytes(res.build().toByteArray());
					return;
				} else {
					GuildSettingExt prop = GuildUtil.getGuildSettingExtProp();
					if (-1 == result) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
						body.writeBytes(res.build().toByteArray());
						return;
					} else if (-2 == result) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("GUILD_CREATE_ERROR"));
						body.writeBytes(res.build().toByteArray());
						return;
					} else if (-3 == result) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("GUILD_CREATE_LEVEL").replace("{roleLevel}", String.valueOf(prop.joinLv)));
						body.writeBytes(res.build().toByteArray());
						return;
					} else if (-4 == result) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("GUILD_CREATE_COST").replace("{guildPay}", String.valueOf(prop.cost)));
						body.writeBytes(res.build().toByteArray());
						return;
					} else if (-5 == result) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
						body.writeBytes(res.build().toByteArray());
						return;
					} else if (-6 == result) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("GUILD_NAME_TOO_SHORT"));
						body.writeBytes(res.build().toByteArray());
						return;
					} else if (-7 == result) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("GUILD_NAME_TOO_LONG"));
						body.writeBytes(res.build().toByteArray());
						return;
					} else if (-8 == result) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("GUILD_NAME_SPECIAL_CHAR"));
						body.writeBytes(res.build().toByteArray());
						return;
					} else if (-9 == result) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("GUILD_NAME_BLACK_STRING"));
						body.writeBytes(res.build().toByteArray());
						return;
					} else if (-10 == result) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("GUILD_CREATE_CD").replace("{time}", String.valueOf(ret.cdInfo)));
						body.writeBytes(res.build().toByteArray());
						return;
					} else if (1 == result) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("GUILD_NAME_EXIST"));
						body.writeBytes(res.build().toByteArray());
						return;
					} else if (-11 == result) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("GUILD_NAME_EMPTY"));
						body.writeBytes(res.build().toByteArray());
						return;
					}
				}
			}
		};
	}
}
