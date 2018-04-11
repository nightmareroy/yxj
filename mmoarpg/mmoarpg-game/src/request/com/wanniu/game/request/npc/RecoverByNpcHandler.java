package com.wanniu.game.request.npc;

import java.io.IOException;

import com.alibaba.fastjson.JSON;
import com.wanniu.core.game.entity.GClientEvent;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.game.protocol.PomeloResponse;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.Const;
import com.wanniu.game.player.WNPlayer;

import Xmds.CanTalkWithNpc;
import Xmds.RefreshPlayerPropertyChange;
import pomelo.area.NpcHandler.RecoverByNpcRequest;
import pomelo.area.NpcHandler.RecoverByNpcResponse;

@GClientEvent("area.npcHandler.recoverByNpcRequest")
public class RecoverByNpcHandler extends PomeloRequest {

	@Override
	public PomeloResponse request() throws Exception {
		WNPlayer player = (WNPlayer) pak.getPlayer();
		RecoverByNpcRequest msg = RecoverByNpcRequest.parseFrom(pak.getRemaingBytes());
		GWorld.getInstance().ansycExec(() -> {
			int npcObjId = Integer.parseInt(msg.getNpcObjId());
			String res_str = player.getXmdsManager().canTalkWithNpc(player.getId(), npcObjId);
			CanTalkWithNpc npcResult = null;
			if (StringUtil.isNotEmpty(res_str)) {
				npcResult = JSON.parseObject(res_str, CanTalkWithNpc.class);

				Out.debug("npcResult:", npcResult);
				if (npcResult.canTalk) {
					RefreshPlayerPropertyChange refreshChange = new RefreshPlayerPropertyChange();
					refreshChange.changeType = Const.PropertyChangeType.NPC.value;
					refreshChange.valueType = 0;
					refreshChange.value = 0;
					refreshChange.duration = 0;
					refreshChange.timestamp = 0;
					player.refreshPlayerPropertyChange(refreshChange);
				}
			}
		});

		return new PomeloResponse() {
			@Override
			protected void write() throws IOException {
				RecoverByNpcResponse.Builder res = RecoverByNpcResponse.newBuilder();
				res.setS2CCode(OK);
				body.writeBytes(res.build().toByteArray());
			}
		};
	}
}
