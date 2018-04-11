package com.wanniu.game.request.redpacket;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.redpacket.RedPacketService;

import pomelo.redpacket.RedPacketHandler.FetchRedPacketRequest;
import pomelo.redpacket.RedPacketHandler.FetchRedPacketResponse;

/**
 * 抢红包
 * 
 * @author liyue
 */
@GClientEvent("redpacket.redPacketHandler.fetchRedPacketRequest")
public class FetchRedPacketlHandler extends PomeloRequest {


	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		FetchRedPacketRequest req = FetchRedPacketRequest.parseFrom(pak.getRemaingBytes());

		String id=req.getId();
		int result = RedPacketService.getInstance().fetchRedPacket(player.getId(), id);
		
		if(result>0) {
			return new PomeloResponse() {
				@Override
				protected void write() throws IOException {
					FetchRedPacketResponse.Builder res=FetchRedPacketResponse.newBuilder();
					res.setS2CCode(OK);	
					res.setValue(result);
					body.writeBytes(res.build().toByteArray());

				}
			};
		}
		
		switch (result) {

		case -1:
			return new ErrorResponse(LangService.getValue("RED_PACKET_WRONG_ID"));
		case -2:
			return new ErrorResponse(LangService.getValue("RED_PACKET_FETCHED"));
		case -3:
			return new ErrorResponse(LangService.getValue("RED_PACKET_FETCHED_OUT"));
		case -4:
			return new ErrorResponse(LangService.getValue("RED_PACKET_CANNOT_FETCH"));
		case -5:
			return new ErrorResponse(String.format(LangService.getValue("RED_PACKET_LEVEL_NEED"), GlobalConfig.Red_LootLevel));
		case -6:
			return new ErrorResponse(LangService.getValue("RED_PACKET_NOT_IN_GUILD"));
		case -7:
			return new ErrorResponse(LangService.getValue("RED_PACKET_NOT_IN_THIS_GUILD"));
		default:
			return new ErrorResponse();
		}

		
	}
}