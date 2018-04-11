package com.wanniu.game.request.guild.guildFort;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.GuildFortHandler.ApplyGuildFundResponse;


/**
 * 请求工会资金协议
 * @author fangyue
 *
 */
@GClientEvent("area.guildFortHandler.applyGuildFundRequest")
public class ApplyGuildFundHandler extends PomeloRequest {
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				ApplyGuildFundResponse.Builder res = ApplyGuildFundResponse.newBuilder();
				String msg = handleApplyGuildFund(res,player);		
				
				if (msg != null) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(msg);
				} else {
					res.setS2CCode(Const.CODE.OK);
				}
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
	
	private String handleApplyGuildFund(ApplyGuildFundResponse.Builder res,WNPlayer player) {
		if(!player.guildManager.isInGuild()) {
			res.setGuildFund(0);
			return LangService.getValue("GUILD_NOT_JOIN");
		}
		
		int fund = (int)player.guildManager.getGuildInfo().fund;
		res.setGuildFund(fund);
		
		return null;
	}
}
