//package com.wanniu.game.request.redpacket;
//
//import java.io.IOException;
//
//import com.wanniu.core.game.LangService;
//import com.wanniu.core.game.entity.GClientEvent;
//import com.wanniu.core.game.protocol.PomeloRequest;
//import com.wanniu.core.game.protocol.PomeloResponse;
//import com.wanniu.game.common.msg.ErrorResponse;
//import com.wanniu.game.player.WNPlayer;
//import com.wanniu.game.redpacket.RedPacketService;
//
//import pomelo.redpacket.RedPacketHandler.GetRedPacketDetailRequest;
//import pomelo.redpacket.RedPacketHandler.GetRedPacketDetailResponse;
//
///**
// * 获取所有红包摘要
// * 
// * @author liyue
// */
//@GClientEvent("redpacket.redPacketHandler.getRedPacketDetailRequest")
//public class GetRedPacketDetailHandler extends PomeloRequest {
//
//
//	@Override
//	public PomeloResponse request() throws Exception {
//		WNPlayer player = (WNPlayer) pak.getPlayer();
//		GetRedPacketDetailRequest req = GetRedPacketDetailRequest.parseFrom(pak.getRemaingBytes());
//		String id=req.getId();
//		GetRedPacketDetailResponse.Builder res = RedPacketService.getInstance().getRedPacketDetail(id, player.getId());
//		if(res!=null) {
//			return new PomeloResponse() {
//				@Override
//				protected void write() throws IOException {
//					body.writeBytes(res.build().toByteArray());
//
//				}
//			};
//		}
//		else {
//			return new ErrorResponse(LangService.getValue("RED_PACKET_WRONG_ID"));
//		}
//		
//	}
//}