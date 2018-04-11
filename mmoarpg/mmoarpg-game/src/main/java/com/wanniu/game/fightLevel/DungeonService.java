package com.wanniu.game.fightLevel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.wanniu.core.game.JobFactory;
import com.wanniu.core.game.LangService;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DateUtil;
import com.wanniu.core.util.RandomUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.area.Area;
import com.wanniu.game.area.AreaManager;
import com.wanniu.game.area.AreaUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.HandsUpState;
import com.wanniu.game.common.Const.SCENE_TYPE;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.base.MapBase;
import com.wanniu.game.data.base.MonsterBase;
import com.wanniu.game.data.ext.MonsterRefreshExt;
import com.wanniu.game.monster.MonsterConfig;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.game.team.TeamData;
import com.wanniu.game.team.TeamData.TeamMemberData;

import pomelo.area.FightLevelHandler.MemberData;
import pomelo.area.FightLevelHandler.OnConfirmEnterFubenPush;
import pomelo.area.FightLevelHandler.OnMemberEnterFubenStateChangePush;

/**
 * 副本
 * 
 * @author Yangzz
 *
 */
public class DungeonService {

	private static DungeonService instance;

	public static DungeonService getInstance() {
		if (instance == null) {
			instance = new DungeonService();
		}
		return instance;
	}

	private DungeonService() {
		for (MonsterRefreshExt refreshExt : GameData.MonsterRefreshs.values()) {
			Date bornBeginTime = AreaUtil.formatToday(refreshExt.rebornBeginTime);
			Date bornEndTime = AreaUtil.formatToday(refreshExt.rebornEndTime);
			long now = System.currentTimeMillis();

			Long nextRefreshTime = 0L;
			if (now < bornBeginTime.getTime()) {
				nextRefreshTime = bornBeginTime.getTime() - now;
			} else if (now < bornEndTime.getTime()) {
				nextRefreshTime = refreshExt.coolDownTime * Const.Time.Minute.getValue() - (now - bornBeginTime.getTime()) % (refreshExt.coolDownTime * Const.Time.Minute.getValue());
			} else {
				nextRefreshTime = DateUtil.getDateAfter(bornBeginTime, 1).getTimeInMillis() - now;
			}

			JobFactory.addFixedRateJob(() -> {
				long nowTime = System.currentTimeMillis();
				Date bornBeginTime_everyday = AreaUtil.formatToday(refreshExt.rebornBeginTime);
				Date bornEndTime_everyday = AreaUtil.formatToday(refreshExt.rebornEndTime);
				Out.info("begin refresh boss next refresh date:", bornBeginTime_everyday, ",end time:", bornEndTime_everyday,",bossId=",refreshExt.monsterID);

				if (nowTime >= bornBeginTime_everyday.getTime() && nowTime < bornEndTime_everyday.getTime()) {
					// 怪物没刷出来也公告
					if (refreshExt.msgSend > 0) {
						String msg = LangService.getValue("MONSTER_BOSS_REBORN_MESSAGE");

						MonsterBase monsterProp = MonsterConfig.getInstance().get(refreshExt.monsterID);
						String monsterName = MessageUtil.getMonsterName(monsterProp.name, monsterProp.qcolor);
						MapBase areaProp = AreaUtil.getAreaProp(refreshExt.mapID);
						msg = msg.replace("{name}", monsterName);
						msg = msg.replace("{site}", areaProp.name);

						MessageUtil.sendRollChat(GWorld.__SERVER_ID, msg, Const.CHAT_SCOPE.SYSTEM);
					}

					for (Area area : AreaManager.getInstance().getAreaMap().values()) {
						if (area.areaId != refreshExt.mapID) {
							continue;
						}

						triggerMonster(GWorld.__SERVER_ID, refreshExt.iD, refreshExt.mapID, refreshExt.msgSend, refreshExt.monsterID);
						Out.debug("triggerMonster::", refreshExt.iD, ",", refreshExt.mapID, ",", refreshExt.msgSend, ",", refreshExt.monsterID);
					}
				}
			}, nextRefreshTime, refreshExt.coolDownTime * Const.Time.Minute.getValue());
		}

	};

	public String enterDungeonInTeam(TeamData team, MapBase prop, int dungeonId) {

		Map<String, TeamMemberData> teamMembers = team.teamMembers;
		String data = this.canEnterDungeon(teamMembers, prop);
		if (data != null) {
			// 玩家XXXX（玩家名称）可能不在当前场景或者已下线
			return LangService.format("TEAM_MEMBER_BATTLE_ING", data);
		}

		List<MemberData> memberData = new ArrayList<>();

		for (TeamMemberData teamMember : teamMembers.values()) {
			PlayerPO member = teamMember.getPlayerData();
			MemberData.Builder md = MemberData.newBuilder();
			md.setId(teamMember.id);
			md.setName(member.name);
			md.setPro(member.pro);
			md.setLevel(member.level);

			memberData.add(md.build());
		}

		int overTime = GlobalConfig.TeamGoMapLeftTime;

		OnConfirmEnterFubenPush enterFuben = OnConfirmEnterFubenPush.newBuilder().setS2CMsg(LangService.getValue("CONFIRM_ENTRY")).setS2CFubenId(prop.mapID).setS2COverTime(overTime).setS2CLeaderId(team.leaderId).addAllS2CMemberData(memberData).build();

		team.lock(overTime);
		// 一条龙特殊处理
		if (prop.type == SCENE_TYPE.LOOP.getValue()) {
			// 有队员未跟随则发送确认提示
			for (TeamMemberData teamMember : teamMembers.values()) {
				teamMember.handup = teamMember.isFollow() ? HandsUpState.ACCEPT.value : HandsUpState.WAITING.value;
				if (teamMember.isBusy()) {
					teamMember.handup = HandsUpState.REFUSE.value;
				} else {
					if (teamMember.isLeader || !teamMember.follow) {
						MessageUtil.sendMessage(teamMember.id, "area.fightLevelPush.onConfirmEnterFubenPush", enterFuben);
					}
				}
			}
		} else {
			for (TeamMemberData teamMember : teamMembers.values()) {
				teamMember.handup = teamMember.isLeader ? HandsUpState.ACCEPT.value : HandsUpState.WAITING.value;
				if (teamMember.isBusy()) {
					teamMember.handup = HandsUpState.REFUSE.value;
				} else {
					MessageUtil.sendMessage(teamMember.id, "area.fightLevelPush.onConfirmEnterFubenPush", enterFuben);
				}
			}
		}

		if (prop.type == SCENE_TYPE.LOOP.getValue()) {
			for (TeamMemberData teamMember : teamMembers.values()) {
				if (teamMember.isFollow()) {
					OnMemberEnterFubenStateChangePush msgData = OnMemberEnterFubenStateChangePush.newBuilder().setS2CPlayerId(teamMember.id).setS2CIsReady(1).build();
					for (TeamMemberData member : team.teamMembers.values()) {
						if (member.handup != HandsUpState.REFUSE.value) {
							MessageUtil.sendMessage(member.id, "area.fightLevelPush.onMemberEnterFubenStateChangePush", msgData);
						}
					}
				}
			}
		} else {
			for (TeamMemberData teamMember : teamMembers.values()) {
				if (teamMember.handup == HandsUpState.REFUSE.value) {
					OnMemberEnterFubenStateChangePush msgData = OnMemberEnterFubenStateChangePush.newBuilder().setS2CPlayerId(teamMember.id).setS2CIsReady(0).build();
					for (TeamMemberData member : team.teamMembers.values()) {
						if (member.handup != HandsUpState.REFUSE.value) {
							MessageUtil.sendMessage(member.id, "area.fightLevelPush.onMemberEnterFubenStateChangePush", msgData);
						}
					}
				}
			}
		}
		return data;
	}

	public String canEnterDungeon(Map<String, TeamMemberData> teamMembers, MapBase prop) {
		StringBuilder data = null;
		for (TeamMemberData teamMember : teamMembers.values()) {
			if (!teamMember.isOnline()) {
				if (data == null) {
					data = new StringBuilder();
				}
				PlayerPO player = PlayerUtil.getPlayerBaseData(teamMember.id);
				if (player != null) {
					data.append(player.name).append("、");
				}
			}
			WNPlayer player = teamMember.getPlayer();
			if (player != null && player.fightLevelManager.canEnterDungeon(player, prop, false) != null) {
				if (data == null) {
					data = new StringBuilder();
				}
				data.append(player.getName()).append("、");
			}
		}
		return data == null ? null : data.substring(0, data.length() - 1);
	}

	private void triggerMonster(int serverId, int propId, int areaId, int send, int monsterId) {
		List<Area> ret = AreaManager.getInstance().getAreaMap().getAreas(areaId, serverId);

		if (ret.size() > 0) {
			int index = RandomUtil.getInt(0, ret.size() - 1);
			Area serverInfo = ret.get(index);

			List<Integer> monsterIds = new ArrayList<>();
			monsterIds.add(propId);
			Area area = AreaUtil.getArea(serverInfo.instanceId);
			if (area != null) {
				area.createMonster(monsterIds, false);
			} else {
				Out.warn("triggerMonster:", propId, ",", areaId, ",", monsterId, " area is not exist!");
			}
		} else {
			Out.warn("triggerMonster:", propId, ",", areaId, ",", monsterId, " area size error!");
		}
	}
}
