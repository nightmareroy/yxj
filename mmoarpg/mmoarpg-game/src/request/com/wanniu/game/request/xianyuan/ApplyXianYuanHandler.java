package com.wanniu.game.request.xianyuan;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.xianyuan.XianYuanService;

import pomelo.xianyuan.XianYuanHandler.XianYuanResponse;

/**
 * @author wanghaitao
 *
 */
@GClientEvent("xianyuan.xianYuanHandler.applyXianYuanRequest")
public class ApplyXianYuanHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		if (!player.functionOpenManager.isOpen(Const.FunctionType.XIAN_YUAN.getValue())) {
			return new ErrorResponse(LangService.getValue("XIAN_YUAN_NOT_OPEN"));
		}

		return new PomeloResponse() {

			@Override
			protected void write() throws IOException {
				XianYuanResponse.Builder res = XianYuanResponse.newBuilder();

				XianYuanService.getInstance().applyXianYuanGetInfo(player, res);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}