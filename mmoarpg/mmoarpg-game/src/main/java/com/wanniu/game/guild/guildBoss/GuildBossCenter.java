package com.wanniu.game.guild.guildBoss;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wanniu.game.poes.GuildPO;

public class GuildBossCenter {
	private Map<String, String> hasEnterGuildIds = new ConcurrentHashMap<>();
	private boolean isOpen = false;
	private long beginTime;

	public boolean isOpen() {
		return isOpen;
	}

	public void onBegin() {
		isOpen = true;
		beginTime = System.currentTimeMillis();
	}

	public long getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(long beginTime) {
		this.beginTime = beginTime;
	}

	public void onOver() {
		isOpen = false;
	}

	public void addOneGuildId(String guildId, String instanceId, GuildPO po) {
		synchronized (po) {
			hasEnterGuildIds.put(guildId, instanceId);
		}
	}

	public void removeOneGuildId(String guildId, GuildPO po) {
		synchronized (po) {
			hasEnterGuildIds.remove(guildId);
		}
	}

	public String getGuildBossScenceIdByGuildId(String guildId, GuildPO po) {
		synchronized (po) {
			return hasEnterGuildIds.get(guildId);
		}
	}

	public String getGuildBossScenceIdByGuildIdNoLock(String guildId) {
		return hasEnterGuildIds.get(guildId);
	}

	public Map<String, String> getHasEnterGuildIds() {
		return hasEnterGuildIds;
	}

	public void clear() {
		hasEnterGuildIds.clear();
	}

	private GuildBossCenter() {}

	private static class GuildBossCenterHolder {
		public final static GuildBossCenter INSTANCE = new GuildBossCenter();
	}

	public static GuildBossCenter getInstance() {
		return GuildBossCenterHolder.INSTANCE;
	}
}
