package com.wanniu.game.request.vip;

import java.io.IOException;

import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.util.DateUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.VipHandler.VipGiftInfo;
import pomelo.area.VipHandler.VipResponse;

@GClientEvent("area.vipHandler.vipInfoRequest")
public class VipInfoHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {

		return new PomeloResponse() {

			@Override
			protected void write() throws IOException {
				WNPlayer player = (WNPlayer) pak.getPlayer();
				VipResponse.Builder res = VipResponse.newBuilder();
				res.setS2CCode(OK);
				VipGiftInfo.Builder info_month = VipGiftInfo.newBuilder();
				info_month.setType(Const.VipType.month.value);
				boolean flag = false;
				if (player.vipManager.po.lastMonthRewardDate != null
						&& DateUtil.isToday(player.vipManager.po.lastMonthRewardDate)) {
					flag = true;
				}
				info_month.setFlag(flag ? 1 : 0);
				res.addDatas(info_month);
				
				VipGiftInfo.Builder info_forever = VipGiftInfo.newBuilder();
				info_forever.setType(Const.VipType.forever.value);
				flag = false;
				if (player.vipManager.po.lastForeverRewardDate != null
						&& DateUtil.isToday(player.vipManager.po.lastForeverRewardDate)) {
					flag = true;
				}
				info_forever.setFlag(flag ? 1 : 0);
				res.addDatas(info_forever);
				res.setS2CRemainTime(player.vipManager.getVipRemainTime());
				body.writeBytes(res.build().toByteArray());
			}

		};
	}
}
