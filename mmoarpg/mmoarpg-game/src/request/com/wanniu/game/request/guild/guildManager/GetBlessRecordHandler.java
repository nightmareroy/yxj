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
import com.wanniu.game.guild.guildBless.GuildBlessCenter;
import com.wanniu.game.player.WNPlayer;

import io.netty.util.internal.StringUtil;
import pomelo.guild.GuildManagerHandler;
import pomelo.guild.GuildManagerHandler.GetBlessRecordRequest;
import pomelo.guild.GuildManagerHandler.GetBlessRecordResponse;

@GClientEvent("guild.guildManagerHandler.getBlessRecordRequest")
public class GetBlessRecordHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		GetBlessRecordRequest req = GetBlessRecordRequest.parseFrom(pak.getRemaingBytes());
		int page = req.getPage();
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				GetBlessRecordResponse.Builder res = GetBlessRecordResponse.newBuilder();

				GuildBlessCenter blessManager = GuildBlessCenter.getInstance();
				if (StringUtil.isNullOrEmpty(player.getId()) || null == blessManager) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}

				List<RecordInfo> recordList = blessManager.getBlessRecordByPlayerId(player.getId(), page);
				List<GuildManagerHandler.RecordInfo> managerRecordList = new ArrayList<GuildManagerHandler.RecordInfo>();
				for (int i = 0; i < recordList.size(); i++) {
					RecordInfo tmpInfo = recordList.get(i);
					GuildManagerHandler.RecordInfo.Builder rInfo = GuildManagerHandler.RecordInfo.newBuilder();
					GuildManagerHandler.RoleInfo role1 = GuildCommonUtil.toGuildMgrHandler(recordList.get(i).role1);
					GuildManagerHandler.RoleInfo role2 = GuildCommonUtil.toGuildMgrHandler(recordList.get(i).role2);
					if (null != role1) {
						rInfo.setRole1(role1);
					}

					if (null != role2) {
						rInfo.setRole2(role2);
					}

					rInfo.setResultNum(tmpInfo.resultNum);
					rInfo.setResultStr(tmpInfo.resultStr);
					rInfo.setTime(tmpInfo.time);
					rInfo.setRecordType(tmpInfo.recordType);
					GuildManagerHandler.ItemRecordInfo.Builder itemRecord = GuildManagerHandler.ItemRecordInfo.newBuilder();
					itemRecord.setQColor(tmpInfo.item.qColor);
					itemRecord.setName(tmpInfo.item.name);
					rInfo.setItem(itemRecord.build());
					managerRecordList.add(rInfo.build());
				}
				res.setS2CCode(OK);
				res.addAllS2CRecordList(managerRecordList);
				res.setS2CPage(page);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}