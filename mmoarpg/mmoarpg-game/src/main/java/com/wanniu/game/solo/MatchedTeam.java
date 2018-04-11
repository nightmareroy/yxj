package com.wanniu.game.solo;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wanniu.game.area.Area;
import com.wanniu.game.player.WNPlayer;

public class MatchedTeam {
	private Map<String, WNPlayer> members = new ConcurrentHashMap<>(2);
	private Area battleArea = null;

	public MatchedTeam(WNPlayer a, WNPlayer b) {
		members.put("A", (a));
		members.put("B", (b));
	}

	public Area getBattleArea() {
		return battleArea;
	}

	public void setBattleArea(Area battleArea) {
		this.battleArea = battleArea;
	}

	public Collection<WNPlayer> getAllPlayers() {
		return members.values();
	}

	public WNPlayer getPlayerA() {
		return members.get("A");
	}

	public WNPlayer getPlayerB() {
		return members.get("B");
	}

	public WNPlayer getPlayer(String playerId) {
		return find(playerId);
	}

	public boolean isIn(WNPlayer player) {
		return find(player.getId()) != null;
	}

	public void remove(String playerId) {
		Iterator<WNPlayer> iter = members.values().iterator();
		while (iter.hasNext()) {
			WNPlayer p = iter.next();
			if (p.getId().equals(playerId)) {
				iter.remove();
				return;
			}
		}
	}

	private WNPlayer find(String playerId) {
		for (WNPlayer p : members.values()) {
			if (playerId.equals(p.getId())) {
				return p;
			}
		}

		return null;
	}

	public int memberSize() {
		return members.size();
	}

	public void clear() {
		members.clear();
	}
}
