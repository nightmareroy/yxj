package com.wanniu.game.request.leaderboard;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.data.GuildLevelCO;
import com.wanniu.game.guild.GuildServiceCenter;
import com.wanniu.game.guild.GuildUtil;
import com.wanniu.game.poes.GuildPO;

import pomelo.area.LeaderBoardHandler.GuildInfoRequest;
import pomelo.area.LeaderBoardHandler.GuildInfoResponse;
import pomelo.area.LeaderBoardHandler.RankGuildInfo;

/**
 * 排行榜获取公会信息
 * 
 * @author jjr
 *
 */
@GClientEvent("area.leaderBoardHandler.guildInfoRequest")
public class GuildInfoHandler extends PomeloRequest {
	public PomeloResponse request() throws Exception {
		GuildInfoRequest req = GuildInfoRequest.parseFrom(pak.getRemaingBytes());

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GuildInfoResponse.Builder res = GuildInfoResponse.newBuilder();
				try {
					String guildId = req.getC2SGuildId();
					GuildPO guild = GuildServiceCenter.getInstance().getGuild(guildId);
					if (null != guild) {
						GuildLevelCO prop = GuildUtil.getGuildLevelPropByLevel(guild.level);
						int maxMember = null != prop ? prop.member : 1;
						RankGuildInfo.Builder data = RankGuildInfo.newBuilder();
						data.setGuildId(guild.id);
						data.setGuildIcon(guild.icon);
						data.setGuildMaster(guild.presidentName);
						data.setGuildMasterPro(guild.presidentPro);
						data.setGuildLevel(guild.level);
						data.setCurMember(GuildUtil.getGuildMemberCount(guild.id));
						data.setMaxMember(maxMember);
						data.setFund((int) guild.sumFund); // 公会改成累计基金
						data.setNotice(guild.notice);

						res.setS2CCode(OK);
						res.setS2CData(data.build());
						body.writeBytes(res.build().toByteArray());
					} else {
						res.setS2CCode(OK);
						res.setS2CMsg(LangService.getValue("GUILD_NOT_EXIST"));
						body.writeBytes(res.build().toByteArray());
					}
				} catch (Exception err) {
					Out.error(err);
					res.setS2CCode(FAIL);
					body.writeBytes(res.build().toByteArray());
				}

			}
		};
	}
}
