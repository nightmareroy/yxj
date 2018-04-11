package com.wanniu.game.area;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.game.protocol.PomeloPush;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.SCENE_TYPE;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.player.BILogService;
import com.wanniu.game.player.PlayerDao;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.player.po.AllBlobPO;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.redis.GameDao;
import com.wanniu.redis.PlayerPOManager;

import io.netty.channel.Channel;
import pomelo.area.PlayerHandler.BattleClearPush;
import pomelo.area.TaskHandler.TaskAutoPush;
import pomelo.player.PlayerOuterClass.PlayerBasic;

/**
 * 全局角色访问接口
 * 
 * @author Yangzz
 *
 */
public class PlayerRemote {

	/**
	 * 判定是否为唯一名字
	 * 
	 * @param checkName
	 * @returns {boolean}
	 */
	public static boolean isValidName(String randomName) {
		if (StringUtil.isEmpty(randomName) || randomName.length() > GWorld.__SERVER_LANG.getNameLimit()) {
			return false;
		}
		return !PlayerDao.existsName(randomName); // FindPlayerDao.getPlayerByName(randomName);//
	};

	/**
	 * 生成随机名字
	 * 
	 * @type {*|exports}
	 */
	public static String getRandomName(int pro, int logicServerId) {
		try {
			// 生成姓名
			String randomName = PlayerUtil.getRandomName(pro);
			for (int i = 0; i < 20; i++) {
				boolean isValid = isValidName(randomName);
				if (isValid) {
					break;
				}
				randomName = PlayerUtil.getRandomName(pro);
				Out.debug(PlayerRemote.class, "重新随机名字   count : ", i, " ---name: ", randomName);
			}
			return randomName;
		} catch (Exception e) {
			Out.error(PlayerRemote.class, "生成随机名字出错", e.getMessage());
			return PlayerUtil.getRandomName(pro);
		}

	};

	/**
	 * 创建角色
	 * 
	 * @type {*|exports}
	 */
	public static WNPlayer createPlayer(String uid, String name, int pro, int logicServerId) {
		String playerId = UUID.randomUUID().toString();
		boolean isPutSuccess = PlayerDao.putName(name, playerId);
		if (!isPutSuccess) {
			Out.warn("发现有玩家重名,创角失败!", name);
			return null;
		}

		// 建议本服名称与角色ID对应的关系，也叫本服玩家列表吧...
		if (!GameDao.putName(name, playerId)) {
			Out.warn("建立本服角色列表时异常啦!", name);
		}

		AllBlobPO playerData = PlayerUtil.createPlayer(playerId, uid, name, pro, logicServerId);
		PlayerDao.insertPlayerId(playerData);
		// WNPlayer player = PlayerManager.createNewPlayer(uid, name, pro,
		// logicServerId, serverData.logicServerId, serverData.acrossServerId);

		return new WNPlayer(playerData);
	};

	/**
	 * 进入Area服务器
	 * 
	 * @param player
	 */
	public static void playerEnterAreaServerInner(WNPlayer player, Area area) {
		try {
			// 初始化角色出生坐标数据
			player.initBornData();
			// 获取Player对象并且初始化设置
			player.write(new PomeloPush() {
				@Override
				protected void write() throws IOException {
					BattleClearPush.Builder push = BattleClearPush.newBuilder();
					push.setS2CName("");
					body.writeBytes(push.build().toByteArray());
				}

				@Override
				public String getRoute() {
					return "area.playerPush.battleClearPush";
				}
			});

			if (player.getState() == 1) {
				AreaUtil.changeAreaPush(player, area.areaId, area.instanceId);
			} else {
				Out.warn("change scene err!!!playerId=", player.getId(), ",areaId=", area.areaId, ",instanceId=", area.instanceId);
			}

			area.addPlayer(player);

			// 中断自动寻路
			if (area.sceneType != SCENE_TYPE.NORMAL.getValue() && area.sceneType != SCENE_TYPE.FIGHT_LEVEL.getValue() && area.sceneType != SCENE_TYPE.LOOP.getValue()) {
				TaskAutoPush.Builder autoPush = TaskAutoPush.newBuilder();
				autoPush.setAuto(0);
				player.receive("area.taskPush.taskAutoPush", autoPush.build());
			}
		} catch (Exception error) {
			Out.error(error);
		}
	};

	public static void playerEnterAreaServer(WNPlayer player, String instanceId) {
		Area oldArea = player.getArea();
		if (oldArea != null) {
			oldArea.removePlayer(player, false);
		}
		Area area = AreaUtil.getArea(instanceId);
		// 判断area是否为空
		playerEnterAreaServerInner(player, area);
	}

	/**
	 * 角色下线同步相关数据
	 */
	public static void syncPlayerDataOffline(WNPlayer player, Area area) {
		// 离线相关逻辑处理
		player.setBornType(Const.BORN_TYPE.NORMAL);
		player.setEnterState(Const.ENTER_STATE.online.value);

		if (area.prop.disConnToMapID != 0) {
			String result = player.getXmdsManager().getBornPlace(area.instanceId, AreaUtil.getAreaProp(area.prop.disConnToMapID).templateID);
			JSONObject res = JSON.parseObject(result);
			player.syncBornData(res.getIntValue("x"), res.getIntValue("y"), area.prop.disConnToMapID);
		}

		// 退出单挑王报名
		player.soloManager.onPlayerOffline();
		Out.debug(PlayerRemote.class, "player.soloManager.quitMatching()");
		player.friendManager.onPlayerOffline();
		player.onlineGiftManager.onPlayerOffline();
		player.five2FiveManager.onPlayerOffline();
		// player.payGiftManager.update();
		player.guildManager.onLogout();
	}

	/**
	 * 通过logicServerId和uid获取玩家列表
	 * 
	 * @param ip
	 */
	public static List<PlayerBasic> getPlayersByUidAndLogicServerId(Channel channel, String uid, int logicServerId, String ip) {

		List<String> list_ids = PlayerDao.getPlayerIdsByUid(uid, logicServerId);
		if (list_ids.isEmpty()) {
			BILogService.getInstance().ansycReportRegister(channel, uid, ip);
		}

		List<PlayerPO> playerDatas = new ArrayList<>(list_ids.size());
		for (String playerId : list_ids) {
			PlayerPO baseData = PlayerPOManager.findPO(ConstsTR.playerTR, playerId, PlayerPO.class);
			if (baseData != null && baseData.isDelete == 0) {
				playerDatas.add(baseData);
			}
		}

		playerDatas.sort(new Comparator<PlayerPO>() {
			@Override
			public int compare(PlayerPO p0, PlayerPO p1) {
				Date loginTime0 = p0.loginTime;
				Date loginTime1 = p1.loginTime;
				if (loginTime0 == null) {
					return -1;
				} else if (loginTime1 == null) {
					return 1;
				} else {
					if (loginTime1.getTime() == loginTime0.getTime()) {
						return 0;
					} else {
						return loginTime1.getTime() > loginTime0.getTime() ? -1 : 1;
					}
				}

			}
		});

		List<PlayerBasic> playerBasics = new ArrayList<>();
		// 如果缓存里面有,从缓存获取数据
		for (PlayerPO playerData : playerDatas) {
			AllBlobPO allblobData = PlayerDao.getAllBlobData(playerData.id);
			playerBasics.add(PlayerUtil.transToJson4BasicByBlob(allblobData));
		}
		return playerBasics;
	};

}
