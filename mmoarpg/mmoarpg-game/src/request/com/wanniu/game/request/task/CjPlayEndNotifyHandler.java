package com.wanniu.game.request.task;

import com.alibaba.fastjson.JSON;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.TaskHandler.CjPlayEndNotify;

/**
 * 演绎播放完毕, 通知服务器,转发给战斗服
 * 
 * @author Yangzz
 *
 */
@GClientEvent("area.taskHandler.cjPlayEndNotify")
public class CjPlayEndNotifyHandler extends PomeloRequest {

	public PomeloResponse request() throws Exception {
		CjPlayEndNotify req = CjPlayEndNotify.parseFrom(pak.getRemaingBytes());
		WNPlayer player = (WNPlayer) pak.getPlayer();
		String msg = req.getS2CMsg();
		
		CJBattleServerNotify notify = new CJBattleServerNotify();
		notify.playerId = player.getId();
		notify.msg = msg;
		
		player.getXmdsManager().notifyBattleServer(player.getInstanceId(), "CjPlayEndNotify", JSON.toJSONString(notify));
		return null;
	}

	/**
	 * 游戏服通知战斗服的协议数据
	 */
	public static final class CJBattleServerNotify {
		public String playerId;
		public String msg;
	}
}
