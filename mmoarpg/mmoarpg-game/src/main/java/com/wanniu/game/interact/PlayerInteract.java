package com.wanniu.game.interact;

import java.util.ArrayList;
import java.util.Collection;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.db.GCache;
import com.wanniu.core.game.protocol.PomeloRequest;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.Utils;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.InteractionCO;
import com.wanniu.game.player.WNPlayer;

import pomelo.area.InteractHandler.InteractConfig;
import pomelo.area.InteractHandler.InteractTimes;
import pomelo.area.InteractHandler.InteractTimesResponse;
import pomelo.area.InteractHandler.ReceiveInteractPush;

/**
 * 交互
 * 
 * @author agui
 */
public class PlayerInteract {

	private WNPlayer player;

	private int friendNum;
	private int commonNum;

	public PlayerInteract(WNPlayer player) {
		this.player = player;
		this.friendNum = 10;
		this.commonNum = 10;
		this.init();
	}

	private final void init() {
		String interact = GCache.hget(ConstsTR.player_interact_numTR.value, player.getId());
		if (interact != null) {
			String[] arr = interact.split("#");
			this.friendNum = Integer.parseInt(arr[0]);
			this.commonNum = Integer.parseInt(arr[1]);
		} else {
			this.friendNum = 10;
			this.commonNum = 10;
		}
	}

	public void sync() {
		GCache.hset(ConstsTR.player_interact_numTR.value, player.getId(), friendNum + "#" + commonNum);
	}

	public void push() {
		String interact = GCache.hget(ConstsTR.player_interactTR.value, player.getId());
		if (interact != null) {
			JSONArray arr = JSON.parseArray(interact);
			for (int i = 0; i < arr.size(); i++) {
				JSONObject data = arr.getJSONObject(i);
				receive(data.getIntValue("id"), data.getString("name"));
			}
			GCache.hremove(ConstsTR.player_interactTR.value, player.getId());
		}
	}

	public boolean send(int id, String playerId, String playerName) {
		InteractionCO prop = getPropById(id);
		if (prop == null) {
			return false;
		}
		WNPlayer player = GWorld.getInstance().getPlayer(playerId);
		if (player != null) {
			player.getInteractManager().receive(id, this.player.getName());
		} else {
			String interact = GCache.hget(ConstsTR.player_interactTR.value, playerId);
			JSONArray arr = interact != null ? JSON.parseArray(interact) : new JSONArray();
			arr.add(Utils.toJSON("id", id, "name", this.player.getName()));
			GCache.hset(ConstsTR.player_interactTR.value, playerId, arr.toJSONString());
		}
		this.subTimes(id);
		this.sendMessage(id, playerName);
		return true;
	}

	public void receive(int id, String name) {
		InteractionCO prop = getPropById(id);
		if (prop == null) {
			return;
		}
		player.player.charm += prop.charm;
		ReceiveInteractPush.Builder data = ReceiveInteractPush.newBuilder();
		data.setS2CCode(PomeloRequest.OK);
		data.setId(id);
		data.setSendPlayerName(name);
		data.setShow(prop.show);
		player.receive("area.interactPush.receiveInteractPush", data.build());
	}

	public final InteractTimesResponse.Builder interactTimes(WNPlayer player) {
		InteractTimesResponse.Builder data = InteractTimesResponse.newBuilder();
		data.setS2CCharm(player.getPlayer().charm);
		ArrayList<InteractTimes> list = new ArrayList<>();
		InteractTimes.Builder time1 = InteractTimes.newBuilder();
		time1.setType(Const.InteractType.FRIEND.getValue());
		time1.setTimes(this.friendNum);
		list.add(time1.build());
		InteractTimes.Builder time2 = InteractTimes.newBuilder();
		time2.setType(Const.InteractType.UNFRIEND.getValue());
		time2.setTimes(this.commonNum);
		list.add(time2.build());
		data.addAllS2CData(list);
		return data;
	}

	public final boolean enoughTimes(int id) {
		InteractionCO prop = getPropById(id);
		if (prop == null) {
			return false;
		}

		if (prop.type == Const.InteractType.FRIEND.getValue()) {
			if (this.friendNum > 0) {
				return true;
			}
		} else {
			if (this.commonNum > 0) {
				return true;
			}
		}

		return false;
	}

	public void subTimes(int id) {
		InteractionCO prop = getPropById(id);
		if (prop == null) {
			return;
		}

		if (prop.type == Const.InteractType.FRIEND.getValue()) {
			if (this.friendNum > 0) {
				this.friendNum--;
			}
		} else {
			if (this.commonNum > 0) {
				this.commonNum--;
			}
		}
	}

	public void sendMessage(int id, String name) {
		InteractionCO prop = getPropById(id);
		if (prop == null) {
			return;
		}

		String message = prop.message;
		message = message.replaceAll("\\|1\\|", this.player.getName());
		message = message.replaceAll("\\|2\\|", name);

		Out.debug(prop.message, "\n", message);

		MessageUtil.sendRollChat(player.getLogicServerId(), message, Const.CHAT_SCOPE.SYSTEM);
	}

	public static InteractionCO getPropById(int id) {
		return GameData.Interactions.get(id);
	}

	public static ArrayList<InteractConfig> getConfig() {
		ArrayList<InteractConfig> data = new ArrayList<>();
		Collection<InteractionCO> props = GameData.Interactions.values();
		for (InteractionCO prop : props) {
			InteractConfig.Builder tmp = InteractConfig.newBuilder();
			tmp.setId(prop.id);
			tmp.setName(prop.name);
			tmp.setType(prop.type);
			tmp.setCharm(prop.charm);
			tmp.setGold(prop.gold);
			tmp.setDiamond(prop.diamond);
			tmp.setShow(prop.show);
			data.add(tmp.build());
		}
		return data;
	}

}
