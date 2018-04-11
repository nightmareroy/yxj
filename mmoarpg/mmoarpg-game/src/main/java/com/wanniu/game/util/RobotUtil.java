package com.wanniu.game.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.fastjson.JSON;
import com.wanniu.core.db.GCache;
import com.wanniu.core.game.JobFactory;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.RandomUtil;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.area.Area;
import com.wanniu.game.area.Area.Actor;
import com.wanniu.game.area.Area.ReliveType;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.common.Utils;
import com.wanniu.game.data.base.DItemBase;
import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.player.WNRobot;
import com.wanniu.game.player.po.AllBlobPO;
import com.wanniu.game.poes.MountPO;
import com.wanniu.game.poes.PlayerBasePO;
import com.wanniu.game.poes.PlayerPetsNewPO;
import com.wanniu.game.poes.SkillsPO;
import com.wanniu.game.request.bag.UseItemHandler.GetItemChanagePropertyResult;
import com.wanniu.game.team.TeamData;
import com.wanniu.game.team.TeamData.TeamMemberData;
import com.wanniu.game.team.TeamUtil;
import com.wanniu.redis.GameDao;
import com.wanniu.redis.PlayerPOManager;

public final class RobotUtil {

	private static final String BASE = "base/";
	private static final String SKILL = "skill/";
	private static final String MOUNT = "mount/";
	private static final String PET = "pet/";

	// 等级 - 职业
	public static Map<Integer, Map<Integer, String>> SkillEntities = new ConcurrentHashMap<>();

	public static Map<Integer, Map<Integer, String>> BaseEntities = new ConcurrentHashMap<>();

	public static Map<Integer, Map<Integer, String>> MountEntities = new ConcurrentHashMap<>();

	public static Map<Integer, Map<Integer, String>> PetEntities = new ConcurrentHashMap<>();

	public static int[] PROS = new int[] { 1, 3, 5 };

	public static AtomicInteger UUID = new AtomicInteger(0);

	public static void freeRobot(WNRobot robot) {
		try {
			Area area = robot.getArea();
			if (area != null) {
				area.removePlayer(robot, false);
				Out.debug("robot ", robot.getName(), " removed from area ", area.getSceneName());
			}
		} finally {
			TeamUtil.removeAcrossMatch(robot);
			GWorld.Robots.remove(robot.getId());
			PlayerPOManager.pos.remove(robot.getId());
			GameDao.freeName(robot.getName());

			// 机器人的数据设计一个超时时间...
			GCache.expire(robot.getId(), 5 * 60);
		}
	}

	public static void newRobot(WNRobot robot) {
		GWorld.Robots.put(robot.getId(), robot);
	}

	private static final String MATCH_ROBOT = "MATCH_ROBOT";

	public static void cloneRobot(WNPlayer player) {
		int lv = player.getLevel();
		if (lv < GlobalConfig.Robot_Level) {
			return;
		}
		int pro = player.getPro();
		String field = lv + "/" + pro;

		Map<Integer, String> pro_skills = SkillEntities.get(lv);
		if (pro_skills == null) {
			pro_skills = new ConcurrentHashMap<>();
			SkillEntities.put(lv, pro_skills);
		}
		if (!pro_skills.containsKey(pro)) {
			String skill = Utils.serialize(player.skillManager.player_skills);
			pro_skills.put(pro, skill);
			GCache.hset(MATCH_ROBOT, SKILL + field, skill);
		}

		Map<Integer, String> pro_bases = BaseEntities.get(lv);
		if (pro_bases == null) {
			pro_bases = new ConcurrentHashMap<>();
			BaseEntities.put(lv, pro_bases);
		}
		if (!pro_bases.containsKey(pro)) {
			String base = Utils.serialize(player.playerBasePO);
			pro_bases.put(pro, base);
			GCache.hset(MATCH_ROBOT, BASE + field, base);
		}

		if (player.mountManager.mount != null) {
			Map<Integer, String> pro_mount = MountEntities.get(lv);
			if (pro_mount == null) {
				pro_mount = new ConcurrentHashMap<>();
				MountEntities.put(lv, pro_mount);
			}
			if (!pro_mount.containsKey(pro)) {
				String mount = Utils.serialize(player.mountManager.mount);
				pro_mount.put(pro, mount);
				GCache.hset(MATCH_ROBOT, MOUNT + field, mount);
			}
		}

		if (player.petNewManager.petsPO != null) {
			Map<Integer, String> pro_pet = PetEntities.get(lv);
			if (pro_pet == null) {
				pro_pet = new ConcurrentHashMap<>();
				PetEntities.put(lv, pro_pet);
			}
			if (!pro_pet.containsKey(pro)) {
				String pet = Utils.serialize(player.petNewManager.petsPO);
				pro_pet.put(pro, pet);
				GCache.hset(MATCH_ROBOT, PET + field, pet);
			}
		}

	}

	public static String getTemplate(String key, int lv, int pro) {
		Map<Integer, Map<Integer, String>> entities = null;
		switch (key) {
		case BASE: {
			entities = BaseEntities;
			break;
		}
		case SKILL: {
			entities = SkillEntities;
			break;
		}
		case MOUNT: {
			entities = MountEntities;
			break;
		}
		case PET: {
			entities = PetEntities;
			break;
		}
		default: {
			return null;
		}
		}
		Map<Integer, String> entity = entities.get(lv);
		if (entity == null) {
			entity = new ConcurrentHashMap<>();
			entities.put(lv, entity);
		}
		if (entity.containsKey(pro)) {
			return entity.get(pro);
		}

		String field = key + lv + "/" + pro;
		String template = GCache.hget(MATCH_ROBOT, field);
		if (StringUtil.isNotEmpty(template)) {
			entity.put(pro, template);
			return template;
		}
		return null;
	}

	public static void initMathData(int lv) {
		for (int pro : PROS) {
			if (getTemplate(BASE, lv, pro) == null) {
				continue;
			}
			getTemplate(SKILL, lv, pro);
			getTemplate(MOUNT, lv, pro);
			getTemplate(PET, lv, pro);
		}
	}

	public static AllBlobPO matchRobot(WNPlayer fromPlayer, int minLevel) {
		int maxLevel = fromPlayer.getLevel();
		if (minLevel > maxLevel) {
			return null;
		}
		WNPlayer mirror = null, tmpPlayer = null;
		for (GPlayer player : GWorld.getInstance().getOnlinePlayers().values()) {
			tmpPlayer = (WNPlayer) player;
			if (tmpPlayer.getLevel() >= minLevel && tmpPlayer.getLevel() <= maxLevel && (tmpPlayer != fromPlayer || tmpPlayer.getPro() != fromPlayer.getPro())) {
				TeamData team = fromPlayer.teamManager.getTeam();
				if (team == null)
					return null;
				boolean noPro = true;
				for (String playerId : team.teamMembers.keySet()) {
					WNPlayer actor = team.getPlayer(playerId);
					if (actor != null && actor.getPro() == fromPlayer.getPro()) {
						noPro = false;
						break;
					}
				}
				if (noPro) {
					mirror = tmpPlayer;
				}
			}
		}

		int pro = fromPlayer.getPro();
		int level = fromPlayer.getLevel();
		SkillsPO player_skill = null;
		PlayerBasePO player_base = null;
		MountPO player_mount = null;
		PlayerPetsNewPO player_pet = null;

		if (mirror != null) {
			pro = mirror.getPro();
			level = mirror.getLevel();
		} else {
			int matchLv = RandomUtil.getInt(minLevel, maxLevel);
			initMathData(matchLv);
			if (SkillEntities.containsKey(matchLv) && BaseEntities.containsKey(matchLv)) {
				Map<Integer, String> skills = SkillEntities.get(matchLv);
				Map<Integer, String> bases = BaseEntities.get(matchLv);
				Map<Integer, String> mounts = MountEntities.get(matchLv);
				Map<Integer, String> pets = PetEntities.get(matchLv);
				int matchPro = PROS[RandomUtil.getIndex(PROS.length)];
				if (!skills.containsKey(matchPro) || !bases.containsKey(matchPro)) {
					for (int tmpPro : skills.keySet()) {
						if (tmpPro != fromPlayer.getPro() && bases.containsKey(tmpPro)) {
							matchPro = tmpPro;
							break;
						}
					}
				}
				if (skills.containsKey(matchPro) && bases.containsKey(matchPro)) {
					pro = matchPro;
					level = matchLv;
					player_skill = Utils.deserialize(skills.get(matchPro), SkillsPO.class);
					player_base = Utils.deserialize(bases.get(matchPro), PlayerBasePO.class);
					if (mounts.containsKey(matchPro)) {
						player_mount = Utils.deserialize(mounts.get(matchPro), MountPO.class);
					}
					if (pets.containsKey(matchPro)) {
						player_pet = Utils.deserialize(pets.get(matchPro), PlayerPetsNewPO.class);
					}
				}
			}
		}

		String name = PlayerUtil.getRandomName(pro);
		String playerId = java.util.UUID.randomUUID().toString();
		boolean isPutSuccess = GameDao.putName(name, playerId);
		if (!isPutSuccess) {
			Out.warn("发现有机器人重名,创角失败!", name);
			return null;
		}

		if (mirror == null) {
			mirror = fromPlayer;
		}

		if (player_skill == null) {
			player_skill = mirror.skillManager.player_skills;
			player_skill = Utils.deserialize(Utils.serialize(player_skill), SkillsPO.class);
		}

		if (player_base == null) {
			player_base = Utils.deserialize(Utils.serialize(mirror.playerBasePO), PlayerBasePO.class);
			if (mirror.mountManager.mount != null) {
				player_mount = Utils.deserialize(Utils.serialize(mirror.mountManager.mount), MountPO.class);
			}
			if (mirror.petNewManager.petsPO != null) {
				player_pet = Utils.deserialize(Utils.serialize(mirror.petNewManager.petsPO), PlayerPetsNewPO.class);
			}
		}
		String robotId = String.valueOf("RO-" + UUID.incrementAndGet());
		AllBlobPO allBlob = PlayerUtil.createPlayer(playerId, robotId, name, pro, fromPlayer.getLogicServerId());
		allBlob.player.uid = robotId;
		allBlob.player.level = level;
		allBlob.player.exp = allBlob.player.exp;
		PlayerPOManager.put(ConstsTR.skillTR, playerId, player_skill);

		allBlob.playerBase = player_base;
		PlayerPOManager.put(ConstsTR.playerBaseTR, playerId, player_base);

		if (player_mount != null) {
			allBlob.player.openMount = true;
			PlayerPOManager.put(ConstsTR.mountTR, playerId, player_mount);
		}

		if (player_pet != null) {
			PlayerPOManager.put(ConstsTR.playerPetTR, playerId, player_pet);
		}

		return allBlob;
	}

	public static void onRobotDie(Area area, WNPlayer robot) {
		if (robot.isRobot()) {
			Actor actor = area.getActor(robot.getId());
			if (actor == null)
				return;
			if (actor.rebornNum < 99 || area.isNormal()) {
				long deleyTime = actor.reliveCoolTime - System.currentTimeMillis();
				JobFactory.addDelayJob(() -> {
					if (!area.isClose()) {
						area.relive(robot.getId(), RandomUtil.getInt(100) < 70 ? area.getReliveType() : ReliveType.NOW);
					}
				}, Math.max(deleyTime, Utils.getSecMills(1, 5)));
			} else {
				JobFactory.addDelayJob(() -> {
					if (robot.getArea() == area) {
						robot.fightLevelManager.leaveDungeon(robot, area);
					}
				}, Utils.getSecMills(5, 20));
			}
		}
	}

	public static void onRobotLeaderQuit(Area area, WNPlayer leader) {
		if (area.isNormal() || area.isClose() || leader.isRobot())
			return;
		TeamData team = leader.getTeamManager().getTeam();
		if (team != null && team.isRobotJoin) {
			for (TeamMemberData member : team.teamMembers.values()) {
				if (member.robot && area.hasPlayer(member.id)) {
					JobFactory.addDelayJob(() -> {
						if (area.isAllRobot() && area.hasPlayer(member.id)) {
							WNPlayer robot = member.getPlayer();
							robot.fightLevelManager.leaveDungeon(robot, area);
						}
					}, Utils.getSecMills(3, 10));
				}
			}
		}
	}

	public static void onRobotReplyHP(WNPlayer player) {
		if (!player.isRobot() || !(player instanceof WNRobot))
			return;
		WNRobot robot = (WNRobot) player;
		if (robot.hpFuture != null)
			return;
		DItemBase item = ItemUtil.getUnEquipPropByCode("hp1");
		GetItemChanagePropertyResult data = ItemUtil.getItemChanageProperty(item);
		robot.hpFuture = JobFactory.addScheduleJob(() -> {
			Area area = player.getArea();
			if (area == null || area.prop.useAgent == 0) {
				return;
			}
			data.itemData.timestamp = GWorld.APP_TIME + item.par;
			String hp = JSON.toJSONString(data.itemData);
			player.getXmdsManager().refreshPlayerPropertyChange(player.getId(), hp);
		}, Utils.random(30, 60), item.useCD + 1000);
	}

}
