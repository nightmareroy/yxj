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

import pomelo.redpacket.RedPacketHandler.DispatchRedPacketRequest;
import pomelo.redpacket.RedPacketHandler.DispatchRedPacketResponse;

/**
 * 发红包
 * 
 * @author liyue
 */
@GClientEvent("redpacket.redPacketHandler.dispatchRedPacketRequest")
public class DispatchRedPacketlHandler extends PomeloRequest {


	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		DispatchRedPacketRequest req = DispatchRedPacketRequest.parseFrom(pak.getRemaingBytes());
		int count=req.getCount();
		int totalNum=req.getTotalNum();
		int channelType=req.getChannelType();
		int fetchType=req.getFetchType();
		int benifitType=req.getBenifitType();
		String msg=req.getMessage();
		int result = RedPacketService.getInstance().dispatchRedPacket(player.getId(), totalNum, count, channelType, fetchType, benifitType, msg);
		switch (result) {
		case 0:
			return new PomeloResponse() {
				@Override
				protected void write() throws IOException {
					DispatchRedPacketResponse.Builder res=DispatchRedPacketResponse.newBuilder();
					res.setS2CCode(OK);	
					body.writeBytes(res.build().toByteArray());

				}
			};
		case 1:
			return new ErrorResponse(LangService.getValue("RED_PACKET_TOTAL_NUM_BIGGER_OR_SMALLER"));
		case 2:
			return new ErrorResponse(LangService.getValue("RED_PACKET_COUNT_BIGGER_OR_SMALLER"));
		case 3:
			return new ErrorResponse(String.format(LangService.getValue("RED_PACKET_LEVEL_NEED"), GlobalConfig.Red_SendLevel));
		case 4:
			return new ErrorResponse(LangService.getValue("RED_PACKET_NOT_ENOUGH_DIAMOND"));
		case 5:
			return new ErrorResponse(LangService.getValue("RED_PACKET_NOT_IN_GUILD"));
		default:
			return new ErrorResponse();
		}

		
	}
}