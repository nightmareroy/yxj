package com.wanniu.game.request.hookSet;

import java.io.IOException;
import java.util.List;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.HookSetHandler.ChangeHookSetRequest;
import pomelo.area.HookSetHandler.ChangeHookSetResponse;
import pomelo.area.HookSetHandler.HookSetData;

@GClientEvent("area.hookSetHandler.changeHookSetRequest")
public class ChangeHookSetHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {

		WNPlayer player = (WNPlayer) pak.getPlayer();
		ChangeHookSetRequest msg = ChangeHookSetRequest.parseFrom(pak.getRemaingBytes());

		ChangeHookSetResponse.Builder res = ChangeHookSetResponse.newBuilder();
		if (player != null) {
			HookSetData hsd = msg.getC2SHookSetData();
			List<Integer> colors = hsd.getMeltQcolorList();
			// 设置自动熔炼蓝色或紫色装备需要VIP等级
//			if (colors.indexOf(Const.ItemQuality.BLUE.getValue()) != -1
//					|| colors.indexOf(Const.ItemQuality.PURPLE.getValue()) != -1) {
//				if(player.baseDataManager.getVip() < GlobalConfig.Transfer_VIP_Min){
//					res.setS2CCode(FAIL);
//				}
//			} else {
				res.setS2CCode(OK);
				player.getHookSet().changeHookSet(hsd);
//			}
		} else {
			res.setS2CCode(FAIL);
		}
		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
