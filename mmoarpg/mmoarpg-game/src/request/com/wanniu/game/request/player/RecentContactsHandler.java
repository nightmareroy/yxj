package com.wanniu.game.request.player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.ProtocolStringList;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.PlayerHandler.RecentContactsRequest;
import pomelo.area.PlayerHandler.RecentContactsResponse;
import pomelo.area.PlayerHandler.recentContactInfo;

/**
 * 获取最近接触
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.playerHandler.recentContactsRequest")
public class RecentContactsHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {

//		WNPlayer player = (WNPlayer) pak.getPlayer();

		RecentContactsRequest req = RecentContactsRequest.parseFrom(pak.getRemaingBytes());
		ProtocolStringList ids = req.getC2SIdsList();

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				RecentContactsResponse.Builder res = RecentContactsResponse.newBuilder();

				List<recentContactInfo> data = new ArrayList<>();
				if (ids == null) {
					res.setS2CCode(FAIL);
					res.addAllS2CData(data);
					body.writeBytes(res.build().toByteArray());
					return;
				}
				ids.forEach(v -> {
					WNPlayer p = PlayerUtil.findPlayer(v); // playerDao.getPlayerById(v);
					recentContactInfo.Builder info = recentContactInfo.newBuilder();
					info.setId(v);
					info.setName(p.getName());
					info.setLevel(p.getPlayer().level);
					info.setPro(p.getPlayer().pro);
					info.setIsFriend(1);
					data.add(info.build());
				});

				data.forEach(v -> {
					// TODO
					// if(!player.friendManager.isFriend(v.id)){
					// v.isFriend = 0;
					// }
				});

				res.setS2CCode(OK);
				res.addAllS2CData(data);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}