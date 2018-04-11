package com.wanniu.game.request.daily;

import java.io.IOException;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.attendance.PlayerAttendance.GiftState;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.daily.DailyActivityMgr;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.VitBonusExt;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.DailyActivityHandler.GetDegreeRewardRequest;
import pomelo.area.DailyActivityHandler.GetDegreeRewardResponse;

/**
 * 日常活跃
 * 
 * @author jjr
 *
 */
@GClientEvent("area.dailyActivityHandler.getDegreeRewardRequest")
public class GetDegreeRewardHandler extends PomeloRequest {
	@Override
	public PomeloResponse request() throws Exception {
		return new PomeloResponse() {
			WNPlayer player = (WNPlayer) pak.getPlayer();
			GetDegreeRewardRequest req = GetDegreeRewardRequest.parseFrom(pak.getRemaingBytes());

			@Override
			protected void write() throws IOException {
				GetDegreeRewardResponse.Builder res = GetDegreeRewardResponse.newBuilder();
				try {
					if (null == player) {
						res.setS2CCode(FAIL);
						res.setS2CMsg(LangService.getValue("SOMETHING_ERR"));
						body.writeBytes(res.build().toByteArray());
						return;
					}

					DailyActivityMgr mgr = player.dailyActivityMgr;
					if (null == mgr) {
						return;
					}

					JSONObject ret = mgr.getReward(req.getId());
					if (0 == ret.getIntValue("result")) {
						VitBonusExt prop = GameData.VitBonuss.get(req.getId());
						List<NormalItem> list_items = ItemUtil.createItemsByItemCode(prop.rewards);
						player.bag.addCodeItemMail(list_items, null, GOODS_CHANGE_TYPE.daily_activity, SysMailConst.BAG_FULL_COMMON);
						res.setS2CCode(OK);
						res.setId(req.getId());
						res.setS2CState(GiftState.RECEIVED.getValue());
						mgr.updateRewardState();
						mgr.updateSuperScript();
						body.writeBytes(res.build().toByteArray());
					} else {
						res.setS2CCode(FAIL);
						res.setS2CMsg(ret.getString("des"));
						body.writeBytes(res.build().toByteArray());
						return;
					}

				} catch (Exception err) {
					Out.error(err);
					res.setS2CCode(FAIL);
					body.writeBytes(res.build().toByteArray());
				}
			}
		};
	}

}
