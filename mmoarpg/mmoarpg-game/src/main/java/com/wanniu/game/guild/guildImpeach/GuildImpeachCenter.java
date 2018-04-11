package com.wanniu.game.guild.guildImpeach;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wanniu.core.game.JobFactory;
import com.wanniu.core.util.DateUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.data.ext.GuildSettingExt;
import com.wanniu.game.guild.GuildCommonUtil;
import com.wanniu.game.guild.GuildMsg;
import com.wanniu.game.guild.GuildMsg.RefreshGuildMsg;
import com.wanniu.game.guild.GuildService;
import com.wanniu.game.guild.GuildServiceCenter;
import com.wanniu.game.guild.GuildUtil;
import com.wanniu.game.guild.RoleInfo;
import com.wanniu.game.guild.dao.GuildImpeachDao;
import com.wanniu.game.guild.guidDepot.GuildRecordData;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.poes.GuildMemberPO;
import com.wanniu.game.poes.GuildPO;

public class GuildImpeachCenter {
	private static GuildImpeachCenter instance;
	Map<String, GuildImpeachData> impeachMap;

	private GuildImpeachCenter() {
		impeachMap = new HashMap<String, GuildImpeachData>();
		init();
	}

	public static GuildImpeachCenter getInstance() {
		if (instance == null) {
			instance = new GuildImpeachCenter();
		}
		return instance;
	}

	public void init() {
		initFromRedis();
		// 定时器
		JobFactory.addDelayJob(new Runnable() {
			@Override
			public void run() {
				refreshAllImpeach();
			}
		}, Const.Time.Minute.getValue());
	}

	public void initFromRedis() {
		ArrayList<GuildImpeachData> impeachList = GuildImpeachDao.getImpeachList();
		for (int i = 0; i < impeachList.size(); ++i) {
			GuildImpeachData impeach = impeachList.get(i);
			impeachMap.put(impeach.id, impeach);
		}
	}

	public void refreshAllImpeach() {
		GuildSettingExt settingProp = GuildUtil.getGuildSettingExtProp();
		if (null == impeachMap || settingProp==null)
			return;
		List<GuildImpeachData> copyList = new ArrayList<>();
		for(GuildImpeachData data : impeachMap.values()) {//copy到临时队列里，后面的迭代内部有remove操作
			copyList.add(data);
		}	
		
		for (GuildImpeachData impeach : copyList) {
			refreshOneImpeachByData(impeach, settingProp);
		}
	}

	public void refreshImpeash(String guildId) {
		GuildImpeachData impeach = getImpeach(guildId);
		GuildSettingExt settingProp = GuildUtil.getGuildSettingExtProp();
		refreshOneImpeachByData(impeach, settingProp);
	}

	public void refreshOneImpeachByData(GuildImpeachData impeach, GuildSettingExt settingProp) {
		if (null == settingProp || null == impeach) {
			return;
		}
		GuildServiceCenter guildManager = GuildServiceCenter.getInstance();
		if (null != impeach) {
			long createTime = impeach.createTime.getTime();
			long nowTime = System.currentTimeMillis();
			if ((nowTime - createTime) > settingProp.impeachTimeMs) {
				int impeachNum = impeach.playerIds.size();
				if (impeachNum >= settingProp.impeachNo) {
					autoTransferPresident(impeach.id);// 自动转移会长
					removeGuildImpeach(impeach.id);// 过期必清
				} else {
					// 过期记录
					GuildRecordData record = new GuildRecordData();
					record.type = Const.GuildRecord.IMPEACH_TIMEOUT.getValue();
					RoleInfo role1 = new RoleInfo();
					role1.pro = impeach.sponsor.pro;
					role1.name = impeach.sponsor.name;
					record.role1 = role1;
					record.result.v2 = Integer.toString(impeach.playerIds.size());
					guildManager.addGuildRecord(impeach.id, record);
					removeGuildImpeach(impeach.id);// 过期必清
				}

			}
		}
	}

	public GuildImpeachData getImpeach(String guildId) {
		return impeachMap.get(guildId);
	}


	public void addImpeachAndSave(GuildImpeachData impeach) {
		impeachMap.put(impeach.id, impeach);
		updateGuildImpeach(impeach.id);
	}

	public void updateGuildImpeach(String guildId) {
		GuildImpeachData impeach = getImpeach(guildId);
		if (null == impeach) {
			return;
		}
		updateGuildImpeachByData(impeach);
	}

	public void removeGuildImpeach(String guildId) {
		GuildImpeachData impeach = getImpeach(guildId);
		if (null == impeach) {
			return;
		}
		removeGuildImpeachByData(impeach);
	}

	public void updateGuildImpeachByData(GuildImpeachData impeach) {
		GuildImpeachDao.updateGuildImpeach(impeach);
	}

	public void removeGuildImpeachByData(GuildImpeachData impeach) {
		GuildImpeachDao.removeGuildImpeachByData(impeach);
		impeachMap.remove(impeach.id);
	}

	public void sortGuildMember(List<GuildMemberPO> list) {
		list.sort((memberA, memberB) -> {
			boolean isTodayA = !DateUtil.canRefreshData(Const.REFRSH_NEW_DAY_TIME, memberA.lastContributeTime);
			boolean isTodayB = !DateUtil.canRefreshData(Const.REFRSH_NEW_DAY_TIME, memberB.lastContributeTime);
			if (isTodayA && isTodayB) {
				if (memberA.lastContributeValue != memberB.lastContributeValue) {
					return memberA.lastContributeValue < memberB.lastContributeValue ? 1 : -1;
				} else {
					return memberA.createTime.getTime() < memberB.createTime.getTime() ? -1 : 1;
				}
			} else if (isTodayA) {
				return -1;
			} else if (isTodayB) {
				return 1;
			} else {
				return memberA.createTime.getTime() < memberB.createTime.getTime() ? -1 : 1;
			}
		});
	}

	// 自动转移会长（注：需要判断是否满足弹劾条件)
	public boolean autoTransferPresident(String guildId) {
		GuildServiceCenter guildManager = GuildServiceCenter.getInstance();
		GuildPO guild = guildManager.getGuild(guildId);
		if (null == guild) {
			return false;
		}

		ArrayList<GuildMemberPO> memberList = guildManager.getGuildMemberList(guildId);
		GuildSettingExt settingProp = GuildUtil.getGuildSettingExtProp();
		if (null == settingProp) {
			return false;
		}
		// 分组

		GuildMemberPO oldPresident = null;// 会长
		List<GuildMemberPO> viceGroup = new ArrayList<GuildMemberPO>(); // 副会长
		List<GuildMemberPO> memberGroup = new ArrayList<GuildMemberPO>(); // 会员，不包括会长，副会长
		for (int i = 0; i < memberList.size(); ++i) {
			GuildMemberPO member = memberList.get(i);
			if (member.job == Const.GuildJob.VICE_PRESIDENT.getValue()) {
				viceGroup.add(member);
//				memberGroup.add(member);// 如果副会长为捐献，则跟其他成员相同
			} else if (member.job == Const.GuildJob.PRESIDENT.getValue()) {
				oldPresident = member;
			} else {
				memberGroup.add(member);
			}
		}

		GuildMemberPO newPresident = null;//new GuildMemberPO();
		if (viceGroup.size() > 0) {
			sortGuildMember(viceGroup);
			if (!DateUtil.canRefreshData(Const.REFRSH_NEW_DAY_TIME, viceGroup.get(0).lastContributeTime)) {
				newPresident = viceGroup.get(0);
			}
		}
		if (null == newPresident && memberGroup.size() > 0) {
			sortGuildMember(memberGroup);
			newPresident = memberGroup.get(0);
		}
		if (null == newPresident) {
			return false;
		}
		oldPresident.job = Const.GuildJob.MEMBER.getValue();
		newPresident.job = Const.GuildJob.PRESIDENT.getValue();
		guild.presidentId = newPresident.playerId;
		guild.presidentPro = newPresident.pro;
		guild.presidentName = newPresident.name;

		guildManager.saveMember(newPresident);
		guildManager.saveMember(oldPresident);
		guildManager.saveGuild(guild);

		// 发送职位变更邮件
		Map<String, String> map = new HashMap<String, String>();
		map.put("guildposition", guild.officeNames.get(oldPresident.job));
		GuildCommonUtil.sendMailSystenType(oldPresident.playerId, SysMailConst.GUILD_POSITION, map);

		Map<String, String> newMap = new HashMap<String, String>();
		newMap.put("guildposition", guild.officeNames.get(newPresident.job));
		GuildCommonUtil.sendMailSystenType(oldPresident.playerId, SysMailConst.GUILD_POSITION, newMap);

		// 同步场景
		RefreshGuildMsg msgData = new RefreshGuildMsg();
		msgData.job = oldPresident.job;
		msgData.jobName = guild.officeNames.get(oldPresident.job);
		GuildMsg msg = new GuildMsg(Const.NotifyType.GUILD_JOB_CHANGE.getValue(), msgData);
		msg.data = msgData;
		Set<String> ids1 = new HashSet<String>();
		ids1.add(oldPresident.playerId);
		GuildService.notifySomePlayerRefreshGuild(ids1, msg, null);

		RefreshGuildMsg msgData2 = new RefreshGuildMsg();
		msgData2.job = newPresident.job;
		msgData2.jobName = guild.officeNames.get(newPresident.job);
		GuildMsg msg2 = new GuildMsg(Const.NotifyType.GUILD_JOB_CHANGE.getValue(), msgData2);
		msg2.data = msgData2;
		Set<String> ids2 = new HashSet<String>();
		ids1.add(newPresident.playerId);
		GuildService.notifySomePlayerRefreshGuild(ids2, msg, null);

		return true;
	}

}
