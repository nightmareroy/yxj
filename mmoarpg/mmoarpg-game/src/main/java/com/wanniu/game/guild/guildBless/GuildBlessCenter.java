package com.wanniu.game.guild.guildBless;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanniu.core.game.JobFactory;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.guild.GuildServiceCenter;
import com.wanniu.game.guild.GuildUtil;
import com.wanniu.game.guild.RecordInfo;
import com.wanniu.game.guild.po.GuildBlessPO;
import com.wanniu.game.poes.GuildMemberPO;

import pomelo.guild.GuildManagerHandler.GuildBlessInfo;

public class GuildBlessCenter {
	private static GuildBlessCenter instance;
	public Map<String, GuildBless> blessMap;

	public static GuildBlessCenter getInstance() {
		if (instance == null) {
			instance = new GuildBlessCenter();
		}

		return instance;
	}

	private GuildBlessCenter() {
		init();
	}

	public void init() {
		blessMap = new HashMap<String, GuildBless>();
		initFromRedis();

		// 定时器 性能待评估
		JobFactory.addDelayJob(new Runnable() {
			@Override
			public void run() {
				saveAllBless();
			}
		}, Const.Time.Minute.getValue());
	};

	public GuildBlessPO getBlessData(String guildId) {
		if (StringUtil.isEmpty(guildId)) {
			return null;
		}

		GuildBless bless = blessMap.get(guildId);
		if (null == bless) {
			return null;
		}

		return bless.blessData;
	}

	public GuildBless getBless(String guildId) {
		return blessMap.get(guildId);
	};

	public void initFromRedis() {
		ArrayList<GuildBlessPO> blessList = GuildUtil.getGuildBlessList();
		for (GuildBlessPO blessData : blessList) {
			GuildBless bless = new GuildBless(blessData, blessData.logicServerId);
			blessMap.put(bless.id, bless);
		}
	}

	public void createBless(String guildId, int logicServerId) {
		GuildBless bless = getBless(guildId);
		if (null == bless) {
			bless = new GuildBless(guildId, logicServerId);
			blessMap.put(bless.id, bless);
			// 存储
			saveBless(guildId);
		}
	};

	public void saveAllBless() {
		for (String key : blessMap.keySet()) {
			saveBless(key);
		}
	};

	public void saveBless(String id) {
		GuildBless bless = getBless(id);
		if (null == bless) {
			return;
		}
		bless.saveToMysql();
	};

	public GuildBless getBlessByPlayerId(String playerId) {
		GuildServiceCenter guildManager = GuildServiceCenter.getInstance();
		GuildMemberPO myInfo = guildManager.getGuildMember(playerId);
		if (null == myInfo) {
			return null;
		}
		GuildBless bless = getBless(myInfo.guildId);
		if (null == bless) {
			return null;
		}
		return bless;
	};

	public GuildBlessInfo getBlessInfoByPlayerId(String playerId) {
		GuildBlessInfo.Builder data = GuildBlessInfo.newBuilder();
		GuildBless bless = getBlessByPlayerId(playerId);
		if (null == bless) {
			return data.build();
		}

		return bless.toJson4PayLoad();
	};

	public List<RecordInfo> getBlessRecordByPlayerId(String playerId, int page) {
		List<RecordInfo> data = new ArrayList<RecordInfo>();
		GuildBless bless = getBlessByPlayerId(playerId);
		if (null == bless) {
			return data;
		}
		return bless.getRecordList(page);
	}

	public void refreshNewDay() {
		for (GuildBless bless : blessMap.values()) {
			if (null == bless)
				continue;

			bless.checkRefreshNewDay(true);
		}
	}

}
