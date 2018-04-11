package com.wanniu.game.request.player;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.area.AreaData;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.common.msg.ErrorResponse;
import com.wanniu.game.data.base.MapBase;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.PlayerHandler.ChangeAreaXYRequest;
import pomelo.area.PlayerHandler.ChangeAreaXYResponse;

/**
 * 切换到指定的场景和坐标
 * @author agui
 *
 */
@GClientEvent("area.playerHandler.changeAreaXYRequest")
public class ChangeAreaXYHandler extends ChangeAreaFilter {

	public PomeloResponse request(WNPlayer player) throws Exception {

		ChangeAreaXYRequest req = ChangeAreaXYRequest.parseFrom(pak.getRemaingBytes());

		String instanceId = req.getInstanceId();
		if (StringUtil.isEmpty(instanceId)) {
			return new ErrorResponse(LangService.getValue("DATA_ERR"));
		}
		if (instanceId.equals(player.getInstanceId())) {
			return new ErrorResponse(LangService.getValue("MAP_NOT_SWITCH"));
		}

		int areaId = req.getMapId();
		int posx = req.getPosx();
		int posy = req.getPosy();
		Out.debug("changeAreaXYRequest player instanceId:", instanceId, "AreaId:", areaId, " x:", posx, " y:", posy);

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				ChangeAreaXYResponse.Builder res = ChangeAreaXYResponse.newBuilder();

				// 各类限制性条件判断
				MapBase sceneProp = AreaUtil.getAreaProp(areaId);
				String result = AreaUtil.canTransArea(sceneProp, player);
				if (result != null) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(result);
					body.writeBytes(res.build().toByteArray());
					return;
				}
				AreaUtil.disCardItemByTransArea(sceneProp, player);
				AreaUtil.changeArea(player, new AreaData(areaId, posx, posy, instanceId));
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}