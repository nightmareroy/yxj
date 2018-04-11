package com.wanniu.game.request.fightLevel.resourcedungeon;

import java.io.IOException;

import com.wanniu.core.game.LangService;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.area.Area;
import com.wanniu.game.area.ResourceDungeon;
import com.wanniu.game.common.Const;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.request.fightLevel.FightLevelLine;

import pomelo.area.ResourceDungeonHandler.ResourceCountDownResponse;

/**
 * 资源副本倒计时请求
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.resourceDungeonHandler.resourceCountDownRequest")
public class ResourceCountDownHandler extends FightLevelLine {

	public PomeloResponse request(WNPlayer player) throws Exception {

//		ResourceCountDownRequest req = ResourceCountDownRequest.parseFrom(pak.getRemaingBytes());

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				ResourceCountDownResponse.Builder res = ResourceCountDownResponse.newBuilder();
				
				Area area = player.getArea();
				if (!(area.getSceneType() == Const.SCENE_TYPE.RESOURCE_DUNGEON.getValue())) {
					res.setS2CCode(FAIL);
					res.setS2CMsg(LangService.getValue("PARAM_ERROR"));
					body.writeBytes(res.build().toByteArray());
					return;
				}
				
				// 额外多加3秒，进入副本，3秒之后开始刷怪
				res.setCountDown(Const.RESOURCE_COUNTDOWN + Const.Time.Second.getValue() * 15 + ((ResourceDungeon) area).createTime - System.currentTimeMillis());
				
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}