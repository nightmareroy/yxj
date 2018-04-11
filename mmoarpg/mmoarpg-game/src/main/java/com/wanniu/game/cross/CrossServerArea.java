package com.wanniu.game.cross;

import java.io.IOException;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.game.protocol.PomeloPush;
import com.wanniu.core.proxy.ProxyClient;
import com.wanniu.core.proxy.ProxyType;
import com.wanniu.core.tcp.protocol.Message;
import com.wanniu.game.player.WNPlayer;

/**
 * 连服场景
 * 
 * @author agui
 *
 */
public class CrossServerArea extends CrossServerLocalArea {

	private boolean isLocal;

	public CrossServerArea(JSONObject opts) {
		super(opts);
		if (opts.containsKey("lineIndex")) {
			this.lineIndex = opts.getIntValue("lineIndex");
		}
		isLocal = !opts.containsKey("exists");
	};

	public void init() {
		super.init();
	}

	@Override
	public void onPlayerEntered(WNPlayer player) {
		super.onPlayerEntered(player);
		ProxyClient.getInstance().add(new Message() {
			@Override
			protected void write() throws IOException {
				body.writeString(instanceId);
				body.writeString(player.getId());
			}

			@Override
			public short getType() {
				return ProxyType.ENTER;
			}
		});
	}

	@Override
	public void onPlayerLeaved(WNPlayer player) {
		ProxyClient.getInstance().add(new Message() {
			@Override
			protected void write() throws IOException {
				body.writeString(instanceId);
				body.writeString(player.getId());
			}

			@Override
			public short getType() {
				return ProxyType.LEAVE;
			}
		});
	}

	@Override
	public void dispose() {
		if (isLocal) {
			super.dispose();
		}
	}

	@Override
	public void receive(PomeloPush push) {
		ProxyClient.getInstance().add(new Message() {
			@Override
			protected void write() throws IOException {
				body.writeString(instanceId);
				body.writeBuffer(push.getContent());
			}

			@Override
			public short getType() {
				return ProxyType.AREA_RECEIVE;
			}
		});
	}

	@Override
	public void onUnitDead(JSONObject msg) {
		int unitType = msg.getIntValue("unitType");
		if (unitType == 1) { // 玩家死亡
			String unitPlayerId = msg.getString("unitPlayerId");
			String hitFinalPlayerId = msg.getString("hitFinal");
			// 被玩家击杀
			WNPlayer deadPlayer = getPlayer(unitPlayerId);
			if (deadPlayer != null) {
				pushRelive(deadPlayer);
			} else {
				ProxyClient.getInstance().add(new Message() {
					@Override
					protected void write() throws IOException {
						body.writeString(instanceId);
						body.writeString(unitPlayerId);
						body.writeString(hitFinalPlayerId);
					}

					@Override
					public short getType() {
						return ProxyType.DIE;
					}
				});
			}
		}
	}

}
