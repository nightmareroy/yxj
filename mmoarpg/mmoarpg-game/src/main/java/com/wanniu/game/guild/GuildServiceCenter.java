package com.wanniu.game.guild;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.CommonUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.GuildJob;
import com.wanniu.game.data.GuildPositionCO;
import com.wanniu.game.data.ext.GuildSettingExt;
import com.wanniu.game.guild.dao.GuildApplyDao;
import com.wanniu.game.guild.dao.GuildDao;
import com.wanniu.game.guild.dao.GuildMemberDao;
import com.wanniu.game.guild.guidDepot.GuildRecordData;
import com.wanniu.game.poes.GuildApplyPO;
import com.wanniu.game.poes.GuildDungeonPO;
import com.wanniu.game.poes.GuildMemberPO;
import com.wanniu.game.poes.GuildPO;
import com.wanniu.game.rank.RankType;

public class GuildServiceCenter {
	private static GuildServiceCenter instance;

	public static GuildServiceCenter getInstance() {
		if (instance == null) {
			instance = new GuildServiceCenter();
		}
		return instance;
	}

	public Map<String, GuildDungeonPO> guildDungeon;

	private GuildServiceCenter() {
		guildDungeon = new HashMap<>();
	}

	public void init() {

		GuildDao.init();

		GuildApplyDao.init();

		GuildMemberDao.init();

		if (GWorld.DEBUG) {
			for (String guildId : GuildDao.GuildMap.keySet()) {
				if (GuildMemberDao.getGuildMemberCount(guildId) == 0) {
					Out.warn("remove empty guild : ", guildId);
					GuildDao.removeGuild(GuildDao.GuildMap.get(guildId));
				}
			}
		}

		ArrayList<GuildDungeonPO> guildDungeonList = GuildUtil.getAllGuildDungeon();
		for (GuildDungeonPO dungeon : guildDungeonList) {
			guildDungeon.put(dungeon.id, dungeon);
		}
	}

	public void delGuild(GuildPO guildPo) {
		GuildUtil.removeGuild(guildPo);
	}

	/**
	 * 通过玩家id 获取公会信息
	 * 
	 * @return 返回公会信息
	 */
	public GuildPO getGuildByMemberId(String playerId) {
		GuildMemberPO memberPo = getGuildMember(playerId);

		// 不是公会成员
		if (null == memberPo) {
			return null;
		}

		return getGuild(memberPo.guildId);
	}

	public GuildPO getGuild(String guildId) {
		return GuildDao.getGuild(guildId);
	}

	public GuildApplyPO getApply(String applyId) {
		return GuildUtil.getGuildApply(applyId);
	}

	public GuildMemberPO getGuildMember(String playerId) {
		return GuildMemberDao.getGuildMember(playerId);
	}

	public GuildApplyPO getGuildApply(String applyId) {
		return this.getApply(applyId);
	}

	public ArrayList<GuildMemberPO> getGuildMemberList(String guildId) {
		ArrayList<GuildMemberPO> data = new ArrayList<GuildMemberPO>();
		Set<String> memberIds = this.getGuildMemberIdList(guildId);
		for (String id : memberIds) {
			GuildMemberPO member = this.getGuildMember(id);
			if (member != null) {
				data.add(member);
			}
		}
		return data;
	}

	/**
	 * Find all guild member id which member's job in jobs 
	 * @param guildId
	 * @param jobs
	 * @return
	 */
	public List<String> getGuildMemberIdList(String guildId, GuildJob... jobs){
		List<String> data = new ArrayList<String>();
		Set<String> memberIds = this.getGuildMemberIdList(guildId);
		for (String id : memberIds) {
			GuildMemberPO member = this.getGuildMember(id);
			if (member != null) {
				for(GuildJob job: jobs) {
					if(member.job == job.getValue()) {
						data.add(id);
					}
				}
			}
		}
	
		return data;
	}
	
	public List<GuildApplyPO> getPlayerApplyList(String playerId) {
		return GuildUtil.getPlayerApplyIdList(playerId);
	}

	public List<GuildApplyPO> getGuildApplyList(String guildId) {
		return GuildUtil.getGuildApplyList(guildId);
	}

	public Set<String> getGuildMemberIdList(String guildId) {
		return GuildMemberDao.GuildMemberMap.get(guildId);
	}

	public int getGuildMemberCount(String guildId) {
		Set<String> list = this.getGuildMemberIdList(guildId);
		if (null != list) {
			return list.size();
		}
		return 0;
	}

	public void removeGuildNameIndex(int logicServerId, String name) {
		GuildUtil.removeGuildNameIndex(logicServerId, name);
	}

	public void _addArrayIndex(Map<String, Set<String>> arrayMap, String key, String value) {
		if (null == arrayMap.get(key)) {
			arrayMap.put(key, new HashSet<String>());
		}
		arrayMap.get(key).add(value);
	}

	public void addGuild(GuildPO data) {
		this.saveGuild(data);
	}

	public void addMember(GuildMemberPO data) {
		this.saveMember(data);
	}

	public void addApply(GuildApplyPO data) {
		GuildUtil.addGuildApply(data);
		List<GuildMemberPO> ls = GuildUtil.getGuildMemberList(data.guildId);
		for (GuildMemberPO po : ls) {
			GuildPositionCO co = GuildUtil.getGuildJobPropByJobId(po.pro);
			if (null == co || 0 == co.right3) {
				// 没有权限
				continue;
			}
			// 向客户端推送公会申请提示图标
			CommonUtil.sendIconMsgType(Const.MESSAGE_TYPE.guild_apply, po.playerId);
		}
	}

	/**
	 * 删除公会成员
	 * 
	 * @param playerId
	 */
	public void removeMember(String playerId) {
		GuildUtil.removeGuildMember(playerId);
	}

	public void removeApply(String applyId) {
		GuildUtil.removeGuildApply(applyId);
	}

	public void saveGuild(GuildPO guildData) {
		GuildUtil.updateGuild(guildData);
	}

	public void saveMember(GuildMemberPO memberData) {
		GuildUtil.updateGuildMember(memberData);
	}

	public boolean isInGuild(String playerId) {
		return this.getGuildMember(playerId) != null;
	}

	public String getGuildId(String playerId) {
		GuildMemberPO po = this.getGuildMember(playerId);
		return po == null ? null : po.guildId;
	}

	public boolean existGuildName(int logicServerId, String name) {
		return StringUtil.isNotEmpty(GuildDao.getGuildIdByName(name));
	}

	public void removeAllPlayerApplyAsync(String playerId) {
		List<GuildApplyPO> applies = GuildApplyDao.getPlayerApplyIdList(playerId);
		for (int i = applies.size() - 1; i >= 0; i--) {
			this.removeApply(applies.get(i).id);
		}
	}

	public void addGuildRecord(String guildId, GuildRecordData record) {
		GuildPO guild = this.getGuild(guildId);
		if (null == guild)
			return;
		GuildSettingExt settingProp = GuildUtil.getGuildSettingExtProp();
		record.time = new Date();
		if (null == guild.allBlobData) {
			guild.allBlobData = new GuildAllBlob();
		}

		List<GuildRecordData> news = GuildDao.getGuildNews(guildId);
		news.add(0, record);

		while (news.size() > settingProp.recording) {
			news.remove(news.size() - 1);
		}

		GuildDao.saveGuildNews(guildId);
	}

	public double convert2Double(long sumFund) {
		double sum = sumFund;
		while (sum > 1) {
			sum = sum * 0.1;
		}
		return sum;
	}

	public void refreshGuildTopInfo(String guildId) {
		GuildPO guild = this.getGuild(guildId);
		if (null == guild) {
			return;
		}
		// 更新公会排行
		RankType.GUILD_LEVEL.getHandler().handle(guild);
	}

	public GuildDungeonPO getGuildDungeon(String guildId) {

		GuildDungeonPO dungeonInfo = this.guildDungeon.get(guildId);

		if (dungeonInfo != null) {

			return dungeonInfo;
		}

		dungeonInfo = new GuildDungeonPO();
		this.guildDungeon.put(guildId, dungeonInfo);
		return this.guildDungeon.get(guildId);
	}

}
