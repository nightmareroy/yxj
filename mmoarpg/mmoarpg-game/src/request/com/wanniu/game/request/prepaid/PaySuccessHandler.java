package com.wanniu.game.request.prepaid;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.pay.PayType;
import com.wanniu.core.pay.request.PayHandler;
import com.wanniu.core.tcp.protocol.Message;
import com.wanniu.core.tcp.protocol.Packet;
import com.wanniu.game.prepaid.PrepaidService;

/**
 * 充值中心充值成功回调
 * 
 * @author lxm
 *
 */
public class PaySuccessHandler extends PayHandler {

	@Override
	public void execute(Packet pak) {
		long key = pak.getLong();
		JSONObject json = JSON.parseObject(pak.getString());
		String orderId = json.getString("orderId");

		// 越南这种特别的方式...
		String playerId = json.getString("playerId");
		if (StringUtils.isNotEmpty(playerId)) {
			int productId = json.getIntValue("productId");
			PrepaidService.getInstance().onSimulationOrder(orderId, playerId, productId);
		}

		PrepaidService.getInstance().onPaySuccess(orderId);

		pak.getSession().writeAndFlush(new Message() {

			@Override
			protected void write() throws IOException {
				body.writeLong(key);
				body.writeString("success");
			}

			@Override
			public short getType() {
				return PayType.PAY_SUCCESS;
			}
		});
	}

	@Override
	public short getType() {
		return PayType.PAY_SUCCESS;
	}
}