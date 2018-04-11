package com.wanniu.game.five2Five;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.wanniu.core.GConfig;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.game.team.TeamData;
import com.wanniu.game.team.TeamData.TeamMemberData;
import com.wanniu.game.team.TeamService;

/**
 * @author wanghaitao
 *
 */
public class Five2FiveMatchPool {
	/** 可以开始比赛的每一方队伍人数 */
	private final static int TEAM_NUMBER = GConfig.getInstance().getInt("five_2_five_match_number", 1);

	/** 报名的玩家池<playerId,Five2FiveSingleMatchVo> */
	private final static CopyOnWriteArrayList<Five2FiveSingleApplyVo> playerApplysPool = new CopyOnWriteArrayList<>();

	/** 报名的队伍池(少于5个人的队伍优先把队伍组满,等于5个人的队伍直接放入匹配池)<TeamId,Five2FiveTeamMatchVo> */
	private final static CopyOnWriteArrayList<Five2FiveTeamApplyVo> teamApplysPool = new CopyOnWriteArrayList<>();

	/** 匹配的队伍池(真正进行队伍匹配的匹配池)<TeamId,Five2FiveTeamMatchVo> */
	private final static CopyOnWriteArrayList<Five2FiveMatchTeamVo> teamMatchPool = new CopyOnWriteArrayList<>();

	/** 匹配成功的队伍 */
	private final static Map<String, Five2FiveMatchTeamVo> matchedPool = new ConcurrentHashMap<>();

	/** 并发锁 */
	private final static ReentrantReadWriteLock poolLock = new ReentrantReadWriteLock();
	/** 用户行为最大等写锁的毫秒时间 */
	private final static long MAX_WAIT_LOCK_MILL = 100;

	public Five2FiveMatchPool() {}

	/**
	 * 获取开始比赛的总人数
	 * 
	 * @return
	 */
	public final static int getBeginNeedCount() {

		return TEAM_NUMBER;
	}

	/**
	 * 清理匹配池
	 */
	public static void clearMatchPool() {
		playerApplysPool.clear();
		teamApplysPool.clear();
		teamMatchPool.clear();
	}

	/**
	 * 获取当前匹配池中玩家
	 * 
	 * @return
	 */
	public final static List<String> getApplyPlayer() {
		List<String> playerIds = new ArrayList<>();
		for (int i = 0; i < playerApplysPool.size(); i++) {
			WNPlayer tempPlayer = playerApplysPool.get(i).player;
			if (tempPlayer != null) {
				playerIds.add(tempPlayer.getId());
			}
		}
		for (int i = 0; i < teamApplysPool.size(); i++) {
			Five2FiveTeamApplyVo temp = teamApplysPool.get(i);
			if (temp == null) {
				continue;
			}
			Set<String> memPlayerIds = temp.teamMembers.keySet();
			playerIds.addAll(memPlayerIds);
		}
		for (int i = 0; i < teamMatchPool.size(); i++) {
			Five2FiveMatchTeamVo temp = teamMatchPool.get(i);
			if (temp == null) {
				continue;
			}
			if (temp.tempTeamMember == null) {
				continue;
			}
			List<Five2FiveTempTeamMember> tempTeamMember = temp.tempTeamMember;
			for (int j = 0; j < tempTeamMember.size(); j++) {
				Five2FiveTempTeamMember tempMem = tempTeamMember.get(j);
				String tempMemPlayerId = tempMem.playerId;
				playerIds.add(tempMemPlayerId);
			}
		}
		return playerIds;
	}

	/**
	 * 移除已经匹配成功队伍
	 * 
	 * @param tempTeamId
	 */
	public final static Five2FiveMatchTeamVo removeMatchedTeam(String tempTeamId) {
		return matchedPool.remove(tempTeamId);
	}

	/**
	 * 单人放入报名池
	 * 
	 * @param player
	 */
	public final static void singlePutInApplyPool(WNPlayer player) {
		Five2FiveSingleApplyVo singleMatchVo = new Five2FiveSingleApplyVo();
		singleMatchVo.joinTime = new Date();
		singleMatchVo.player = player;
		try {
			if (!poolLock.writeLock().tryLock(MAX_WAIT_LOCK_MILL, TimeUnit.MILLISECONDS)) {
				Out.warn("Try writeLock timeout");
			}
			playerApplysPool.add(singleMatchVo);
		} catch (Exception e) {
			Out.error(e);
		} finally {
			poolLock.writeLock().unlock();
		}
	}

	/**
	 * 队伍放入报名池
	 * 
	 * @param teamMembers
	 * @param teamId
	 */
	public final static void teamPutInApplyPool(Map<String, TeamMemberData> teamMembers, String teamId) {
		Five2FiveTeamApplyVo teamMatchVo = new Five2FiveTeamApplyVo(teamMembers, teamId);
		teamMatchVo.joinTime = new Date();
		try {
			if (!poolLock.writeLock().tryLock(MAX_WAIT_LOCK_MILL, TimeUnit.MILLISECONDS)) {
				Out.warn("Try writeLock timeout");
			}

			if (teamMatchVo.teamMembers.size() == TEAM_NUMBER) {
				Five2FiveMatchTeamVo tempTeam = createTempTeam(null, Arrays.asList(teamMatchVo));
				if (tempTeam == null) {
					teamApplysPool.add(teamMatchVo);
				} else {
					teamMatchPool.add(tempTeam);
				}
			} else {
				teamApplysPool.add(teamMatchVo);
			}
		} catch (Exception e) {
			Out.error(e);
		} finally {
			poolLock.writeLock().unlock();
		}
	}

	/**
	 * 单人移除报名池
	 * 
	 * @param playerId
	 * @return
	 */
	public final static boolean singleRemoveApplyPool(String playerId) {
		try {
			if (!poolLock.writeLock().tryLock(MAX_WAIT_LOCK_MILL, TimeUnit.MILLISECONDS)) {
				Out.warn("Try writeLock timeout");
			}

			for (int i = 0; i < playerApplysPool.size(); i++) {
				Five2FiveSingleApplyVo singleMatchVo = playerApplysPool.get(i);
				WNPlayer player = singleMatchVo.player;
				if (player == null) {
					playerApplysPool.remove(singleMatchVo);
					continue;
				}
				if (player.getId().equals(playerId)) {
					playerApplysPool.remove(singleMatchVo);
					Five2FiveService.getInstance().pushMatchPool();
					return true;
				}
			}

			for (int i = 0; i < teamApplysPool.size(); i++) {
				Five2FiveTeamApplyVo teamMatchVo = teamApplysPool.get(i);
				TeamData teamData = TeamService.getTeam(teamMatchVo.teamId);
				if (teamData == null) {
					teamApplysPool.remove(teamMatchVo);
					continue;
				}
				Map<String, TeamMemberData> teamMembers = teamMatchVo.teamMembers;
				if (teamMembers.size() == 0) {
					teamApplysPool.remove(teamMatchVo);
					continue;
				}
				if (teamMembers.containsKey(playerId)) {
					teamApplysPool.remove(teamMatchVo);
					// WNPlayer player = PlayerUtil.getOnlinePlayer(playerId);
					// if (!player.teamManager.isInTeam()) {
					for (TeamMemberData td : teamMatchVo.teamMembers.values()) {
						if (td.id.equals(playerId)) {
							continue;
						}
						Five2FiveService.getInstance().pushCancelMatch(td.id);
					}
					// }
					Five2FiveService.getInstance().pushMatchPool();
					return true;
				}
			}

			for (int i = 0; i < teamMatchPool.size(); i++) {
				Five2FiveMatchTeamVo matchTeamVo = teamMatchPool.get(i);
				CopyOnWriteArrayList<Five2FiveTempTeamMember> tempTeamMember = matchTeamVo.tempTeamMember;
				for (int j = 0; j < tempTeamMember.size(); j++) {
					Five2FiveTempTeamMember ttm = tempTeamMember.get(j);
					String tempPlayerId = ttm.playerId;
					if (tempPlayerId.equals(playerId)) {
						if (matchTeamVo.useNumber.getAndAdd(1) == 0) {
							teamMatchPool.remove(matchTeamVo);
							if (matchTeamVo.singleMatchVos != null) {
								List<Five2FiveSingleApplyVo> noContainThisPlayerApplys = new ArrayList<>();
								for (int k = 0; k < matchTeamVo.singleMatchVos.size(); k++) {
									Five2FiveSingleApplyVo temp = matchTeamVo.singleMatchVos.get(k);
									if (temp.player == null) {
										continue;
									}
									String tempId = temp.player.getId();
									if (!tempId.equals(playerId)) {
										noContainThisPlayerApplys.add(temp);
									}
								}
								if (!noContainThisPlayerApplys.isEmpty()) {
									playerApplysPool.addAll(noContainThisPlayerApplys);
								}
							}
							if (matchTeamVo.teamMatchVos != null) {
								for (int k = 0; k < matchTeamVo.teamMatchVos.size(); k++) {
									Five2FiveTeamApplyVo tempTeam = matchTeamVo.teamMatchVos.get(k);
									Map<String, TeamMemberData> teamMembers = tempTeam.teamMembers;
									if (teamMembers.size() == 0 || teamMembers.containsKey(playerId)) {
										// 通知队伍另外的人取消
										for (TeamMemberData td : teamMembers.values()) {
											if (td.id.equals(playerId)) {
												continue;
											}
											Five2FiveService.getInstance().pushCancelMatch(td.id);
										}
										continue;
									}
									// 其他队伍重新加入匹配队列
									teamApplysPool.add(tempTeam);
								}
							}
							Five2FiveService.getInstance().pushMatchPool();
							return true;
						} else {
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			Out.error(e);
		} finally {
			poolLock.writeLock().unlock();
		}

		return false;
	}

	/**
	 * 队伍移除报名池
	 * 
	 * @param teamId
	 * @return
	 */
	@Deprecated
	public final static boolean teamRemoveApplyPool(String teamId) {
		for (int i = 0; i < teamApplysPool.size(); i++) {
			Five2FiveTeamApplyVo teamMatchVo = teamApplysPool.get(i);
			if (teamMatchVo.teamId.equals(teamId)) {
				teamApplysPool.remove(teamMatchVo);
				return true;
			}
		}

		for (int i = 0; i < teamMatchPool.size(); i++) {
			Five2FiveMatchTeamVo matchTeamVo = teamMatchPool.get(i);
			List<Five2FiveTeamApplyVo> teamMatchVos = matchTeamVo.teamMatchVos;
			if (teamMatchVos == null) {
				continue;
			}
			for (int j = 0; j < teamMatchVos.size(); j++) {
				Five2FiveTeamApplyVo teamMatchVo = teamMatchVos.get(j);
				if (teamMatchVo.teamMembers.size() == 0) {
					teamMatchPool.remove(matchTeamVo);
				} else {
					if (teamMatchVo.teamId.equals(teamId)) {
						if (matchTeamVo.useNumber.getAndAdd(1) == 0) {
							teamMatchPool.remove(matchTeamVo);
							if (matchTeamVo.singleMatchVos != null) {
								playerApplysPool.addAll(matchTeamVo.singleMatchVos);
							}
							for (int k = 0; k < teamMatchVos.size(); k++) {
								Five2FiveTeamApplyVo teamMatchVo_1 = teamMatchVos.get(k);
								if (!teamMatchVo_1.teamId.equals(teamId)) {
									teamApplysPool.add(teamMatchVo_1);
								}
							}
							return true;
						} else {
							break;
						}
					}
				}
			}
		}
		return false;

	}

	/**
	 * 队伍是否已经匹配成功
	 * 
	 * @param teamId
	 * @return
	 */
	@Deprecated
	public final static boolean teamIsMakeWithOthers(String teamId) {
		for (Entry<String, Five2FiveMatchTeamVo> matchedVo : matchedPool.entrySet()) {
			Five2FiveMatchTeamVo matchTeamVo = matchedVo.getValue();
			List<Five2FiveTeamApplyVo> teamMatchVos = matchTeamVo.teamMatchVos;
			if (teamMatchVos != null) {
				for (int j = 0; j < teamMatchVos.size(); j++) {
					Five2FiveTeamApplyVo teamMatchVo = teamMatchVos.get(j);
					String teamMatchId = teamMatchVo.teamId;
					if (teamMatchId.equals(teamId)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 玩家报名时间(是否在报名队列中)
	 * 
	 * @param playerId
	 * @return
	 */
	public final static Date applyMatchTime(String playerId) {
		for (int i = 0; i < playerApplysPool.size(); i++) {
			Five2FiveSingleApplyVo singleMatchVo = playerApplysPool.get(i);
			if (singleMatchVo.player.getId().equals(playerId)) {
				return singleMatchVo.joinTime;
			}
		}

		for (int i = 0; i < teamApplysPool.size(); i++) {
			Five2FiveTeamApplyVo teamMatchVo = teamApplysPool.get(i);
			Map<String, TeamMemberData> teamMembers = teamMatchVo.teamMembers;
			if (teamMembers.containsKey(playerId)) {
				return teamMatchVo.joinTime;
			}
		}

		for (int i = 0; i < teamMatchPool.size(); i++) {
			Five2FiveMatchTeamVo matchTeamVo = teamMatchPool.get(i);
			List<Five2FiveTeamApplyVo> teamMatchVos = matchTeamVo.teamMatchVos;
			if (teamMatchVos != null) {
				for (int j = 0; j < teamMatchVos.size(); j++) {
					Five2FiveTeamApplyVo tempTeamApplyVo = teamMatchVos.get(j);
					Map<String, TeamMemberData> teamMembers = tempTeamApplyVo.teamMembers;
					if (teamMembers.containsKey(playerId)) {
						return tempTeamApplyVo.joinTime;
					}
				}
			}
			List<Five2FiveSingleApplyVo> singleMatchVos = matchTeamVo.singleMatchVos;
			if (singleMatchVos != null) {
				for (int j = 0; j < singleMatchVos.size(); j++) {
					WNPlayer single = singleMatchVos.get(j).player;
					if (single != null && single.getId().equals(playerId)) {
						return singleMatchVos.get(j).joinTime;
					}
				}
			}
		}

		// 匹配成功等待进入场景
		for (Five2FiveMatchTeamVo vo : matchedPool.values()) {
			for (Five2FiveTempTeamMember m : vo.tempTeamMember) {
				if (m.playerId.equals(playerId)) {
					return new Date();
				}
			}
		}
		return null;
	}

	/**
	 * 单人报名池的大小
	 * 
	 * @return
	 */
	public final static int getSingleApplyPoolSize() {
		return playerApplysPool.size();
	}

	/**
	 * 队伍报名池的大小
	 * 
	 * @return
	 */
	public final static int getTeamApplyPoolSize() {
		return teamApplysPool.size();
	}

	/**
	 * 队伍匹配池的大小
	 * 
	 * @return
	 */
	public final static int getTeamMatchPoolSize() {
		return teamMatchPool.size();
	}

	/**
	 * 根据临时队伍ID获取临时队伍
	 * 
	 * @param tempTeamId
	 * @return
	 */
	public final static Five2FiveMatchTeamVo getMatchingTeam(String tempTeamId) {
		return matchedPool.get(tempTeamId);
	}

	/**
	 * 5个人的组匹配5个人的组
	 */
	private final static void teamMatchTeam() {
		for (int i = 0; i < teamMatchPool.size(); i++) {
			Five2FiveMatchTeamVo matchTeam = teamMatchPool.get(i);
			int totalSize = 0;
			List<Five2FiveSingleApplyVo> singleMatchVos = matchTeam.singleMatchVos;
			if (singleMatchVos != null) {
				totalSize += singleMatchVos.size();
			}
			List<Five2FiveTeamApplyVo> teamMatchVos = matchTeam.teamMatchVos;
			if (teamMatchVos != null) {
				for (int j = 0; j < teamMatchVos.size(); j++) {
					Five2FiveTeamApplyVo teamApply = teamMatchVos.get(j);
					Map<String, TeamMemberData> teamMembers = teamApply.teamMembers;
					totalSize += teamMembers.size();
				}
			}
			if (totalSize != TEAM_NUMBER) {
				teamMatchPool.remove(matchTeam);
				if (matchTeam.singleMatchVos != null) {
					playerApplysPool.addAll(matchTeam.singleMatchVos);
				}
				if (matchTeam.teamMatchVos != null) {
					teamApplysPool.addAll(matchTeam.teamMatchVos);
				}
				continue;
			}
			String tempTeamId = matchTeam.tempTeamId;
			Date joinTime = matchTeam.joinTime;
			Date now = new Date();
			long diff = (now.getTime() - joinTime.getTime()) / 1000;
			int addNumber = (int) (diff / GlobalConfig.Group_MatchingTime == 0 ? 1 : diff / GlobalConfig.Group_MatchingTime);
			int teamScore = matchTeam.teamScore;
			int scoreChange = GlobalConfig.Group_PlusPoint * addNumber;
			int matchMinScore = getMinMatchScore(teamScore, scoreChange);
			int matchMaxScore = getMaxMatchScore(teamScore, scoreChange);
			Five2FiveMatchTeamVo conformTeam = getConformToRulesTeamsEqual5(tempTeamId, matchMinScore, matchMaxScore);
			if (conformTeam != null) {
				matchSuccess(matchTeam, conformTeam);
			}
		}
	}

	/**
	 * 执行匹配任务，先分成5人一组的队伍，再根据匹配规则匹配合适的2组队伍
	 */
	final static void doMatchJob() {
		if (!Five2FiveService.getInstance().isInOpenTime()) {
			return;
		}
		try {
			poolLock.writeLock().lock();
			long begin = System.currentTimeMillis();
			Five2FiveMatchPool.makeFiveTeam();
			Five2FiveMatchPool.teamMatchTeam();
			long spendTimeMill = begin - System.currentTimeMillis();
			if (spendTimeMill > MAX_WAIT_LOCK_MILL) {
				Out.warn("5v5 doMatchJob spend too much time mills : " + spendTimeMill);
			}
		} finally {
			poolLock.writeLock().unlock();
		}
	}

	/**
	 * 组成5人组队伍
	 */
	private final static void makeFiveTeam() {
		teamFindTeam();
		playerFindPlayer();
	}

	/**
	 * 创建临时队伍(临时组队)
	 * 
	 * @param singleMatchVos
	 * @param teamMatchVos
	 */
	private static Five2FiveMatchTeamVo createTempTeam(List<Five2FiveSingleApplyVo> singleMatchVos, List<Five2FiveTeamApplyVo> teamMatchVos) {
		Out.debug("5v5 createTempTeam");
		Five2FiveMatchTeamVo matchVo = new Five2FiveMatchTeamVo();
		CopyOnWriteArrayList<Five2FiveTempTeamMember> tempTeamMember = new CopyOnWriteArrayList<>();
		int totalScore = 0;
		if (singleMatchVos != null) {
			for (int i = 0; i < singleMatchVos.size(); i++) {
				Five2FiveSingleApplyVo singleMatchVo = singleMatchVos.get(i);
				WNPlayer player = singleMatchVo.player;
				if (player == null) {
					playerApplysPool.remove(singleMatchVo);
					Out.debug("5v5 createTempTeam failed:player is null");
					return null;
				}
				Five2FiveTempTeamMember temp = new Five2FiveTempTeamMember();
				temp.playerId = player.getId();
				temp.playerPro = player.getPro();
				temp.playerLvl = player.getLevel();
				temp.playerName = player.getName();
				tempTeamMember.add(temp);
				totalScore += Five2FiveService.getInstance().getFive2FiveScore(player.getId());
			}
		}
		if (teamMatchVos != null) {
			for (int i = 0; i < teamMatchVos.size(); i++) {
				Five2FiveTeamApplyVo teamMatchVo = teamMatchVos.get(i);
				if (teamMatchVo == null) {
					Out.debug("5v5 createTempTeam failed:teamData is null");
					return null;
				}
				Map<String, TeamMemberData> teamMembers = teamMatchVo.teamMembers;
				if (teamMembers.size() == 0) {
					teamApplysPool.remove(teamMatchVo);
					Out.debug("5v5 createTempTeam failed:teamMembers is null");
					return null;
				}
				for (Entry<String, TeamMemberData> tempMemEntry : teamMembers.entrySet()) {
					String playerId = tempMemEntry.getKey();
					PlayerPO player = PlayerUtil.getPlayerBaseData(playerId);
					Five2FiveTempTeamMember temp = new Five2FiveTempTeamMember();
					temp.playerId = player.id;
					temp.playerPro = player.pro;
					temp.playerLvl = player.level;
					temp.playerName = player.name;
					tempTeamMember.add(temp);
					totalScore += Five2FiveService.getInstance().getFive2FiveScore(player.id);
				}
			}
		}
		if (tempTeamMember.size() != TEAM_NUMBER) {
			Out.debug("5v5 createTempTeam failed: tempTeam size not full");
			return null;
		} else {
			Out.debug("5v5 createTempTeam success");
			matchVo.tempTeamId = UUID.randomUUID().toString();
			matchVo.joinTime = new Date();
			matchVo.tempTeamMember = tempTeamMember;
			totalScore /= TEAM_NUMBER;
			matchVo.teamScore = totalScore;
			matchVo.singleMatchVos = singleMatchVos;
			matchVo.teamMatchVos = teamMatchVos;
			Out.info("组建临时队伍:tempTeamId=", matchVo.tempTeamId);
			for (Five2FiveTempTeamMember v : matchVo.tempTeamMember) {
				Out.info("成员：", v.playerId);
			}
		}
		return matchVo;
	}

	/**
	 * 少于5个人的队伍找少于5个人的队伍(优先队伍找队伍组满5个人如:4+1;3+2;2+3;1+4)
	 */
	private static void teamFindTeam() {
		for (int i = 0; i < teamApplysPool.size(); i++) {
			Five2FiveTeamApplyVo matchTeam = teamApplysPool.get(i);
			Date joinTime = matchTeam.joinTime;
			Date now = new Date();
			long diff = (now.getTime() - joinTime.getTime()) / 1000;
			int addNumber = (int) (diff / GlobalConfig.Group_MatchingTime == 0 ? 1 : diff / GlobalConfig.Group_MatchingTime);
			Map<String, TeamMemberData> teamMembers = matchTeam.teamMembers;
			if (teamMembers == null || teamMembers.size() == 0) {
				teamApplysPool.remove(matchTeam);
				continue;
			}
			if (teamMembers.size() == TEAM_NUMBER) {
				Five2FiveMatchTeamVo tempTeam = createTempTeam(null, Arrays.asList(matchTeam));
				if (tempTeam == null) {
					teamApplysPool.add(matchTeam);
				} else {
					teamMatchPool.add(tempTeam);
					teamApplysPool.remove(matchTeam);
					continue;
				}
			}
			int teamScore = getTeamScore(teamMembers);
			int scoreChange = GlobalConfig.Group_PlusPoint * addNumber;
			int matchMinScore = getMinMatchScore(teamScore, scoreChange);
			int matchMaxScore = getMaxMatchScore(teamScore, scoreChange);
			int memSizeDiff = TEAM_NUMBER - teamMembers.size();
			String teamId = matchTeam.teamId;
			Five2FiveTeamApplyVo conformTeam = getConformToRulesTeamsLess5(teamId, matchMinScore, matchMaxScore, memSizeDiff);
			if (conformTeam != null) {// 有人数匹配的队伍则优先组成5人队伍
				removeTeamAndAddTeam(matchTeam, conformTeam);
				break;
			} else {// 没有则去找单个玩家
				List<Five2FiveSingleApplyVo> conformSingle = getConformToRulesPlayer(null, matchMinScore, matchMaxScore);
				if (conformSingle.size() >= memSizeDiff) {
					for (int j = 0; j < memSizeDiff; j++) {
						Five2FiveSingleApplyVo singleMatchVo = conformSingle.get(j);
						removeSingleAndAddTeam(singleMatchVo, matchTeam);
					}
				}
			}
		}

	}

	/**
	 * 玩家找队伍
	 */
	private static void playerFindPlayer() {
		for (int i = 0; i < playerApplysPool.size(); i++) {
			Five2FiveSingleApplyVo matchPlayer = playerApplysPool.get(i);
			WNPlayer player = matchPlayer.player;
			if (player == null) {
				playerApplysPool.remove(matchPlayer);
				continue;
			}
			String playerId = player.getId();
			Date joinTime = matchPlayer.joinTime;
			Date now = new Date();
			long diff = (now.getTime() - joinTime.getTime()) / 1000;
			int addNumber = (int) (diff / GlobalConfig.Group_MatchingTime == 0 ? 1 : diff / GlobalConfig.Group_MatchingTime);
			int scoreChange = GlobalConfig.Group_PlusPoint * addNumber;
			int score = Five2FiveService.getInstance().getFive2FiveScore(playerId);
			int matchMinScore = getMinMatchScore(score, scoreChange);
			int matchMaxScore = getMaxMatchScore(score, scoreChange);
			List<Five2FiveSingleApplyVo> conformSingle = getConformToRulesPlayer(playerId, matchMinScore, matchMaxScore);
			if (conformSingle.size() >= TEAM_NUMBER - 1) {// 如果符合积分的玩家大于等于4个则可以将这几个玩家组成新的队伍放入报名池
				removeSingleAndCreateTeam(matchPlayer, conformSingle);
			}
		}
	}

	/**
	 * 处理匹配成功后相关
	 * 
	 * @param teamMatchVoA
	 * @param teamMatchVoB
	 */
	private static void matchSuccess(Five2FiveMatchTeamVo teamMatchVoA, Five2FiveMatchTeamVo teamMatchVoB) {
		// 将2组队伍都从匹配池中移除
		teamMatchPool.remove(teamMatchVoA);
		teamMatchPool.remove(teamMatchVoB);
		matchedPool.put(teamMatchVoA.tempTeamId, teamMatchVoA);
		matchedPool.put(teamMatchVoB.tempTeamId, teamMatchVoB);

		Out.info("匹配成功 A队:", teamMatchVoA.tempTeamId, "，B队:", teamMatchVoB.tempTeamId);
		int index = 1;
		for (int i = 0; i < teamMatchVoA.tempTeamMember.size(); i++) {
			Five2FiveTempTeamMember tempTeamMember = teamMatchVoA.tempTeamMember.get(i);
			tempTeamMember.force = Const.AreaForce.FORCEA.value;
			tempTeamMember.index = index;
			index++;
			Out.info("匹配A队成员:", tempTeamMember.playerId);
		}
		index = 1;
		for (int i = 0; i < teamMatchVoB.tempTeamMember.size(); i++) {
			Five2FiveTempTeamMember tempTeamMember = teamMatchVoB.tempTeamMember.get(i);
			tempTeamMember.force = Const.AreaForce.FORCEB.value;
			tempTeamMember.index = index;
			index++;
			Out.info("匹配B队成员:", tempTeamMember.playerId);
		}
		teamMatchVoA.oppoTempTeamId = teamMatchVoB.tempTeamId;
		teamMatchVoB.oppoTempTeamId = teamMatchVoA.tempTeamId;

		Five2FiveService.getInstance().five2FiveAfterMatchSucess(teamMatchVoA, teamMatchVoB);
	}

	/**
	 * 创建临时队伍将玩家从单人报名队列中移除,并将队伍加入到匹配的队伍池中
	 * 
	 * @param matchPlayer
	 * @param conformSingle
	 */
	private static void removeSingleAndCreateTeam(Five2FiveSingleApplyVo matchPlayer, List<Five2FiveSingleApplyVo> conformSingle) {
		List<Five2FiveSingleApplyVo> singleApplyVos = new ArrayList<>();
		singleApplyVos.add(matchPlayer);
		for (int i = 0; i < TEAM_NUMBER - 1; i++) {
			Five2FiveSingleApplyVo tempMatchTeamVo = conformSingle.get(i);
			singleApplyVos.add(tempMatchTeamVo);
		}
		Five2FiveMatchTeamVo matchTeamVo = createTempTeam(singleApplyVos, null);
		if (matchTeamVo != null) {
			playerApplysPool.removeAll(singleApplyVos);
			teamMatchPool.add(matchTeamVo);
		}
	}

	/**
	 * 创建临时队伍将玩家从单人报名队列中移除,并原来的队伍加入到新队伍中,且将原来的队伍从报名的队伍池中将新队伍移除加入到匹配池中
	 * 
	 * @param singleMatchVo
	 * @param matchTeam
	 */
	private static void removeSingleAndAddTeam(Five2FiveSingleApplyVo singleMatchVo, Five2FiveTeamApplyVo matchTeam) {
		List<Five2FiveSingleApplyVo> singleApplyVos = new ArrayList<>();
		singleApplyVos.add(singleMatchVo);
		Five2FiveMatchTeamVo matchTeamVo = createTempTeam(singleApplyVos, Arrays.asList(matchTeam));
		if (matchTeamVo != null) {
			playerApplysPool.remove(singleMatchVo);
			teamApplysPool.remove(matchTeam);
			teamMatchPool.add(matchTeamVo);
		}
	}

	/**
	 * 移除报名队伍加入匹配队伍
	 * 
	 * @param teamMemA
	 * @param teamMemB
	 */
	private static void removeTeamAndAddTeam(Five2FiveTeamApplyVo teamMemA, Five2FiveTeamApplyVo teamMemB) {
		// 将队伍A和队伍B临时组成一个新的队伍teamMemNew且将队伍A和B从报名的队伍池中移除加入到匹配池
		Five2FiveMatchTeamVo matchTeamVo = createTempTeam(null, Arrays.asList(teamMemA, teamMemB));
		if (matchTeamVo != null) {
			teamApplysPool.remove(teamMemA);
			teamApplysPool.remove(teamMemB);
			teamMatchPool.add(matchTeamVo);
		}
	}

	/**
	 * 获取匹配的队伍的积分
	 * 
	 * @param teamMembers
	 * @return
	 */
	private static int getTeamScore(Map<String, TeamMemberData> teamMembers) {
		int teamScore = 0;
		for (Entry<String, TeamMemberData> tempMemEntry : teamMembers.entrySet()) {
			String teamMemPlayerId = tempMemEntry.getKey();
			int teamMemScore = Five2FiveService.getInstance().getFive2FiveScore(teamMemPlayerId);
			teamScore += teamMemScore;
		}
		int size = teamMembers.size();
		if (size != 0) {
			return teamScore / size;
		}
		return 0;
	}

	/**
	 * 根据积分和加入时间获取匹配的匹配积分的下限
	 * 
	 * @param score
	 * @param scoreChange
	 * @return
	 */
	private static int getMinMatchScore(int score, int scoreChange) {
		int minScore = -1;
		if (scoreChange < GlobalConfig.Group_MaxPoint) {
			minScore = score - scoreChange < 0 ? 0 : score - scoreChange;
		}
		return minScore;
	}

	/**
	 * 根据积分和加入时间获取匹配的匹配积分的上限
	 * 
	 * @param score
	 * @param scoreChange
	 * @return
	 */
	private static int getMaxMatchScore(int score, int scoreChange) {
		int maxScore = -1;
		if (scoreChange < GlobalConfig.Group_MaxPoint) {
			maxScore = score + scoreChange;
		}
		return maxScore;
	}

	/**
	 * 根据积分和人数获取符合规则的不满5个人的最早加入队伍
	 * 
	 * @param teamId
	 * @param minTeamScore
	 * @param maxTeamScore
	 * @param memNumber
	 * @return
	 */
	private static Five2FiveTeamApplyVo getConformToRulesTeamsLess5(String teamId, int minTeamScore, int maxTeamScore, int memNumber) {
		for (int i = 0; i < teamApplysPool.size(); i++) {
			Five2FiveTeamApplyVo matchTeam = teamApplysPool.get(i);
			Map<String, TeamMemberData> teamMembers = matchTeam.teamMembers;
			if (teamMembers == null || teamMembers.size() == 0) {
				teamApplysPool.remove(matchTeam);
				continue;
			}
			if (teamId != null && teamId.equals(matchTeam.teamId)) {
				continue;
			}
			if (teamMembers.size() == memNumber) {
				int currentTeamScore = getTeamScore(teamMembers);
				if (minTeamScore == -1 || maxTeamScore == -1 || (currentTeamScore >= minTeamScore && currentTeamScore <= maxTeamScore)) {
					return matchTeam;
				}
			}
		}
		return null;
	}

	/**
	 * 根据积分和人数获取符合规则的满足5个人的最早加入队伍
	 * 
	 * @param tempTeamId
	 * @param minTeamScore
	 * @param maxTeamScore
	 * @return
	 */
	private static Five2FiveMatchTeamVo getConformToRulesTeamsEqual5(String tempTeamId, int minTeamScore, int maxTeamScore) {
		for (int i = 0; i < teamMatchPool.size(); i++) {
			Five2FiveMatchTeamVo matchTeam = teamMatchPool.get(i);
			if (matchTeam.tempTeamMember.size() != TEAM_NUMBER) {
				teamMatchPool.remove(matchTeam);
				if (matchTeam.singleMatchVos != null) {
					playerApplysPool.addAll(matchTeam.singleMatchVos);
				}
				if (matchTeam.teamMatchVos != null) {
					teamApplysPool.addAll(matchTeam.teamMatchVos);
				}
				continue;
			}
			int currentTeamScore = matchTeam.teamScore;
			if (tempTeamId != null && tempTeamId.equals(matchTeam.tempTeamId)) {
				continue;
			}
			if (minTeamScore == -1 || maxTeamScore == -1 || (currentTeamScore >= minTeamScore && currentTeamScore <= maxTeamScore) && matchTeam.useNumber.getAndAdd(1) == 0) {
				return matchTeam;
			}
		}
		return null;
	}

	/**
	 * 根据积分和人数获取符合规则的单个匹配玩家
	 * 
	 * @param playerId
	 * @param minTeamScore
	 * @param maxTeamScore
	 * @return
	 */
	private static List<Five2FiveSingleApplyVo> getConformToRulesPlayer(String playerId, int minTeamScore, int maxTeamScore) {
		List<Five2FiveSingleApplyVo> conformToRulesSingles = new ArrayList<Five2FiveSingleApplyVo>();
		for (int i = 0; i < playerApplysPool.size(); i++) {
			Five2FiveSingleApplyVo matchSingle = playerApplysPool.get(i);
			WNPlayer player = matchSingle.player;
			if (player == null) {
				playerApplysPool.remove(matchSingle);
				continue;
			}
			String matchPlayerId = player.getId();
			if (playerId != null && matchPlayerId.equals(playerId)) {
				continue;
			}
			int currentTeamScore = Five2FiveService.getInstance().getFive2FiveScore(matchPlayerId);
			if (minTeamScore == -1 || maxTeamScore == -1 || (currentTeamScore >= minTeamScore && currentTeamScore <= maxTeamScore)) {
				conformToRulesSingles.add(matchSingle);
			}
		}
		return conformToRulesSingles;
	}
}