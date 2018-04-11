package com.wanniu.game.request.guild.guildManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.guild.GuildCommonUtil;
import com.wanniu.game.guild.RecordInfo;
import com.wanniu.game.guild.guidDepot.GuildDepotCenter;
import com.wanniu.game.player.WNPlayer;

import pomelo.guild.GuildManagerHandler;
import pomelo.guild.GuildManagerHandler.GetDepotRecordRequest;
import pomelo.guild.GuildManagerHandler.GetDepotRecordResponse;

@GClientEvent("guild.guildManagerHandler.getDepotRecordRequest")
public class GetDepotRecordHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		GetDepotRecordRequest req = GetDepotRecordRequest.parseFrom(pak.getRemaingBytes());
		int page = req.getPage();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetDepotRecordResponse.Builder res = GetDepotRecordResponse.newBuilder();

				GuildDepotCenter depotManager = GuildDepotCenter.getInstance();
				if (null == player.getId() || null == depotManager) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				ArrayList<RecordInfo> recordList = depotManager.getDepotRecordByPlayerId(player.getId(), page);

				List<GuildManagerHandler.RecordInfo> retList = new ArrayList<GuildManagerHandler.RecordInfo>();
				for (int i = 0; i < recordList.size(); i++) {
					RecordInfo tmp = recordList.get(i);
					if (null == tmp) {
						continue;
					}

					GuildManagerHandler.RecordInfo.Builder guildMgrInfo = GuildManagerHandler.RecordInfo.newBuilder();
					guildMgrInfo.setRole1(GuildCommonUtil.toMgrRoleInfo(tmp.role1.pro, tmp.role1.name));
					guildMgrInfo.setRole2(GuildCommonUtil.toMgrRoleInfo(tmp.role2.pro, tmp.role2.name));
					guildMgrInfo.setResultNum(tmp.resultNum);
					guildMgrInfo.setResultStr(tmp.resultStr);
					guildMgrInfo.setTime(tmp.time);
					guildMgrInfo.setRecordType(tmp.recordType);
					guildMgrInfo.setItem(GuildCommonUtil.toMgrItemRecordInfo(tmp.item.qColor, tmp.item.name));
					retList.add(guildMgrInfo.build());
				}

				res.setS2CCode(OK);
				res.addAllS2CRecordList(retList);
				res.setS2CPage(page);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
