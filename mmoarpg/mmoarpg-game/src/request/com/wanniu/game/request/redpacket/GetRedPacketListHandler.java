package com.wanniu.game.request.redpacket;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.redpacket.RedPacketService;

import pomelo.redpacket.RedPacketHandler.GetRedPacketListResponse;

/**
 * 获取红包详情
 * 
 * @author liyue
 */
@GClientEvent("redpacket.redPacketHandler.getRedPacketListRequest")
public class GetRedPacketListHandler extends PomeloRequest {


	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		GetRedPacketListResponse.Builder res = RedPacketService.getInstance().getAllRedPackets(player.getId());
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				body.writeBytes(res.build().toByteArray());

			}
		};
	}
}