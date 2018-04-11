package com.wanniu.game.request.activity;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.prepaid.PrepaidService;

import pomelo.area.ActivityFavorHandler.SuperPackageBuyRequest;
import pomelo.area.PrepaidHandler.PrepaidOrderIdResponse;

@GClientEvent("area.activityFavorHandler.superPackageBuyRequest")
public class SuperPackageBuyHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		SuperPackageBuyRequest req = SuperPackageBuyRequest.parseFrom(pak.getRemaingBytes());
		int productId = req.getPackageId();
		int channelId = req.getChannelId();
		WNPlayer player = (WNPlayer) pak.getPlayer();
		boolean isCard = false;
		String imei = req.getC2SImei();
		int os = req.getC2SOs();
		
		if(!player.activityManager.SuperPackage_GetBoughtable(productId))
		{
			PrepaidOrderIdResponse.Builder res = PrepaidOrderIdResponse.newBuilder();
			res.setS2CCode(PomeloRequest.FAIL);
			res.setS2CMsg(LangService.getValue("ACTIVITY_SUPERPACKAGE_HAVE_BOUGHT"));
			
			PomeloResponse me = new PomeloResponse() {

				@Override
				protected void write() throws IOException {
					body.writeBytes(res.build().toByteArray());
				}
			};
			return me;
		}
		return PrepaidService.getInstance().createOrderId(productId, channelId, player, isCard, true, imei, os);
	}
}