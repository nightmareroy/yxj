package com.wanniu.game.area;

import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.GWorld;
import com.wanniu.game.player.WNPlayer;

public class ProxyArea extends Area {

	public ProxyArea(JSONObject opts) {
		super(opts);
	}

	@Override
	public void bindBattleServer(WNPlayer player) {
		Out.warn("proxy bindBattleServer!!!");
	}

	public void setBattleServerId(String battleServerId) {
		serverId = battleServerId;
		Out.debug("bindBattleServer ", serverId);
	}

	protected void init() {
		this.hasPlayerEntered = false;
		this.actors = new ConcurrentHashMap<>();
		this.emptyTime = GWorld.APP_TIME;
		this.sceneType = this.prop.type;
		this.lifeTime = this.prop.lifeTime * 1000;
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void addPlayer(WNPlayer player) {
		super.addPlayer(player);
	}

	@Override
	public void removePlayer(WNPlayer player, boolean keepObject) {
		super.removePlayer(player, keepObject);
	}

	public boolean hasHighQualityItem() {
		return false;
	}

}
