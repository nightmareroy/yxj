package com.wanniu.game.player;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.GConfig;
import com.wanniu.core.GGame;
import com.wanniu.core.GGlobal;
import com.wanniu.core.game.JobFactory;
import com.wanniu.core.game.PoolFactory;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.http.HttpRequester;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.BiLogType;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.data.base.DItemBase;
import com.wanniu.game.downjoy.HttpClientUtil;
import com.wanniu.game.downjoy.MD5;
import com.wanniu.game.item.ItemConfig;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.redis.GlobalDao;

import cn.qeng.common.login.LoginConst;
import cn.qeng.common.login.TokenInfo;
import io.netty.channel.Channel;

/**
 * 记录BI一段时间内的变更数量
 * 
 * @author Yangzz
 */
public class BILogService {
	// 当乐上报URL
	private String downjoyReportUrl = GConfig.getInstance().get("downjoy.report.url");
	private int downjoyAppId = GConfig.getInstance().getInt("downjoy.report.appId", 0);
	private String downjoyAppKey = GConfig.getInstance().get("downjoy.report.appKey");

	// 阿里上报
	private int aliAppId = GConfig.getInstance().getInt("ali.report.appId", 0);
	private String aliAppKey = GConfig.getInstance().get("ali.report.appKey");
	private String aliReportUrl = GConfig.getInstance().get("ali.report.url");

	private static BILogService instance;
	/** 异步上报线程 */
	private final ExecutorService ansycReportExec;

	public static BILogService getInstance() {
		if (instance == null) {
			synchronized (BILogService.class) {
				if (instance == null) {
					instance = new BILogService();
				}
			}
		}
		return instance;
	}

	private static Map<String, Map<BiLogType, List<BILogItem>>> items = new ConcurrentHashMap<>();

	private BILogService() {
		this.ansycReportExec = Executors.newSingleThreadExecutor(new PoolFactory("ansyc-report"));

		JobFactory.addFixedRateJob(() -> {
			for (Map<BiLogType, List<BILogItem>> pitems : items.values()) {
				for (BiLogType logType : pitems.keySet()) {
					List<BILogItem> list = pitems.get(logType);
					if (list == null) {
						continue;
					}

					for (BILogItem item : list) {
						long now = GWorld.APP_TIME;
						switch (logType) {
						case Gold_Total:
						case Cash_Total:
						case Diamond_Total:
						case Exp_Total:
							// 一分钟内的计算
							if (item.time + Const.Time.Minute.getValue() < now) {
								list.remove(item);
							}
							break;
						case Consignment_Total:
							// 24小时
							if (item.time + Const.Time.Day.getValue() < now) {
								list.remove(item);
							}
							break;
						case Gift_Total:
							// 1小时
							if (item.time + Const.Time.Hour.getValue() < now) {
								list.remove(item);
							}
							break;
						default:
							list.remove(item);
							break;
						}
					}
				}
			}
			if (items.size() > 10000) {
				Out.warn("BILogService size: ", items.size());
				if (items.size() > 20000) {
					items.clear();
				}
			}
		}, 0, 1000 * 10);
	}

	/**
	 * 记录 数值溢出型Log
	 */
	public void recordNum(WNPlayer player, BiLogType logType, long num, GOODS_CHANGE_TYPE from) {
		if (player.isRobot())
			return;
		GWorld.getInstance().ansycExec(() -> {
			List<BILogItem> list = null;
			if (logType == BiLogType.Gold || logType == BiLogType.Cash || logType == BiLogType.Diamond || logType == BiLogType.Exp || logType == BiLogType.FightPower || logType == BiLogType.Consignment || logType == BiLogType.Gift) {
				Map<BiLogType, List<BILogItem>> pitem = items.get(player.getId());
				if (pitem == null) {
					pitem = new ConcurrentHashMap<>();
					items.put(player.getId(), pitem);
				}
				list = pitem.get(logType);
				if (list == null) {
					list = new CopyOnWriteArrayList<>();
					pitem.put(logType, list);
				}

				list.add(new BILogItem(GWorld.APP_TIME, Math.abs(num)));
			}
			long total = 0;
			if (list != null && list.size() > 0) {
				for (BILogItem item : list) {
					total += item.num;
				}
			}

			switch (logType) {
			case Gold:
				if (Math.abs(num) > 2000000) {
					PlayerUtil.bi(this.getClass(), BiLogType.Gold, player, num, from);
				}
				if (total > 10000000) {
					PlayerUtil.bi(this.getClass(), BiLogType.Gold_Total, player, total);
				}
				break;
			case Cash:
				// PlayerUtil.bi(this.getClass(), BiLogType.CashChange, player, num, from);
				if (Math.abs(num) > 5000) {
					PlayerUtil.bi(this.getClass(), BiLogType.Cash, player, num, from);
				}
				if (total > 10000) {
					PlayerUtil.bi(this.getClass(), BiLogType.Cash_Total, player, total);
				}
				break;
			case Diamond:
				// PlayerUtil.bi(this.getClass(), BiLogType.DiamondChange, player, num, from);
				// if (Math.abs(num) > 10000) {//充值元宝钻石，无论多少金额都记录
				PlayerUtil.bi(this.getClass(), BiLogType.Diamond, player, num, from);
				// }
				// if (total > 30000) {
				PlayerUtil.bi(this.getClass(), BiLogType.Diamond_Total, player, total);
				// }
				break;
			case Exp:
				if (Math.abs(num) > 500000) {
					PlayerUtil.bi(this.getClass(), BiLogType.Exp, player, num, from);
				}
				if (total > 2000000) {
					PlayerUtil.bi(this.getClass(), BiLogType.Exp_Total, player, total);
				}
				break;
			case FightPower:
				if (num > 5000) {
					PlayerUtil.bi(this.getClass(), BiLogType.FightPower, player, num, from);
				}
				if (total > 30000) {
					PlayerUtil.bi(this.getClass(), BiLogType.FightPower_Total, player, total);
				}
				break;
			case Consignment:
				if (Math.abs(num) > 3000) {
					PlayerUtil.bi(this.getClass(), BiLogType.Consignment, player, num, from);
				}
				if (list.size() > 50) {
					PlayerUtil.bi(this.getClass(), BiLogType.Consignment_Total, player, list.size());
				}
				break;
			case Gift:
				if (list.size() > 10) {
					PlayerUtil.bi(this.getClass(), BiLogType.Gift_Total, player, list.size());
				}
				break;
			default:
				break;
			}
		});
	}

	private static class BILogItem {
		public long time;
		public long num;

		public BILogItem(long time, long num) {
			this.time = time;
			this.num = num;
		}
	}

	/**
	 * 异步上报在线数据...
	 */
	public void ansycReportOnline(int onlineCount) {
		final String reportUrl = GConfig.getInstance().get("server.bi.reportUrl");
		if (StringUtils.isNotEmpty(reportUrl)) {
			Map<String, Integer> ps = new HashMap<>();
			ps.put("type", 0);
			ps.put("area_id", GWorld.__AREA_ID);
			ps.put("server_id", GGame.__SERVER_ID);
			ps.put("count", onlineCount);

			Map<String, String> params = new HashMap<>();
			params.put("name", "Online");
			params.put("ts", String.valueOf(System.currentTimeMillis() / 1000));
			params.put("data", JSON.toJSONString(ps));

			ansycReportExec.execute(() -> {
				try {
					Out.info("online report result:", new HttpRequester().sendPost(reportUrl, params).getContent());
				} catch (IOException e) {
					Out.warn("online report exception", JSON.toJSONString(params), e);
				}
			});
		}
	}

	/**
	 * 首次登录就算是他注册时间.
	 */
	public void ansycReportRegister(Channel channel, String uid, String ip) {
		final String reportUrl = GConfig.getInstance().get("server.bi.reportUrl");
		if (StringUtils.isNotEmpty(reportUrl) && !GlobalDao.hexists(ConstsTR.FIRST_LOGIN.value, uid)) {
			GlobalDao.hset(ConstsTR.FIRST_LOGIN.value, uid, "1");// 写入值

			Map<String, Object> ps = new HashMap<>();
			ps.put("kingdom", 1);
			ps.put("userid", uid);

			Map<String, String> params = new HashMap<>();
			params.put("name", "Register");
			params.put("ts", String.valueOf(System.currentTimeMillis() / 1000));
			params.put("data", JSON.toJSONString(ps));

			TokenInfo tokenInfo = channel.attr(GGlobal.__KEY_TOKEN_INFO).get();
			ansycReportExec.execute(() -> {
				try {
					if (tokenInfo != null) {
						params.put("source", tokenInfo.getSubchannel());//
						params.put("device_id", tokenInfo.getMac());//
						params.put("platform", String.valueOf(osToPlatform(tokenInfo.getOs())));//
						params.put("ip", ip);//
					}
					Out.info("register report result:", new HttpRequester().sendPost(reportUrl, params).getContent());
				} catch (IOException e) {
					Out.warn("register report exception", JSON.toJSONString(params), e);
					GlobalDao.hremove(ConstsTR.FIRST_LOGIN.value, uid);// 如果上报失败，还是把这个删掉
				}
			});
		}
	}

	/**
	 * 退出时上报BI日志.
	 */
	public void ansycReportLogout(WNPlayer player) {
		final String reportUrl = GConfig.getInstance().get("server.bi.reportUrl");
		if (StringUtils.isNotEmpty(reportUrl)) {
			Map<String, Object> ps = new HashMap<>();
			ps.put("kingdom", 1);
			ps.put("userid", player.getUid());
			ps.put("clientid", GGame.__SERVER_ID);
			ps.put("roleid", player.getId());// 角色ID

			// 渠道来源(游戏包ID)，区服，角色名，角色职业，角色等级，角色突破等级，角色VIP等级，角色战斗力，元宝存量，绑元存量，银两存量
			Map<String, Object> extra = new HashMap<>();
			extra.put("区服", GGame.__SERVER_ID);
			extra.put("角色名", player.getPlayer().name);// 角色名称
			extra.put("角色职业", player.getPlayer().pro);// 职业
			extra.put("角色等级", player.getPlayer().level);
			extra.put("角色突破等级", player.getPlayer().upLevel);// 角色突破等级
			extra.put("角色VIP等级", player.baseDataManager.getVip());
			extra.put("角色战斗力", player.getPlayer().fightPower);// 角色战斗力
			extra.put("元宝存量", player.getPlayer().diamond);// 元宝存量
			extra.put("绑元存量", player.getPlayer().ticket);// 绑元存量
			extra.put("银两存量", player.getPlayer().gold);// 银两存量
			ps.put("extra", extra);

			Map<String, String> params = new HashMap<>();
			params.put("name", "Logout");
			params.put("ts", String.valueOf(System.currentTimeMillis() / 1000));
			params.put("data", JSON.toJSONString(ps));
			params.put("source", String.valueOf(player.getPlayer().subchannel));//
			params.put("device_id", player.getPlayer().mac);//
			params.put("platform", String.valueOf(osToPlatform(player.getPlayer().os)));//
			params.put("ip", player.getPlayer().ip);//

			ansycReportExec.execute(() -> {
				try {
					Out.info("logout report result:", new HttpRequester().sendPost(reportUrl, params).getContent());
				} catch (IOException e) {
					Out.warn("logout report exception", JSON.toJSONString(params), e);
				}
			});
		}
	}

	public void ansycReportRechargeSuccess(PlayerPO player, int chargeDiamond, int payMoneyAmount, String classfield) {
		final String reportUrl = GConfig.getInstance().get("server.bi.reportUrl");
		if (StringUtils.isNotEmpty(reportUrl)) {
			Map<String, Object> ps = new HashMap<>();
			ps.put("type", 1);
			ps.put("userid", player.uid);
			ps.put("clientid", GGame.__SERVER_ID);
			ps.put("user_level", player.level);// 角色等级
			ps.put("kingdom", chargeDiamond);// 获得元宝数量
			ps.put("phylum", payMoneyAmount);// 对应充值项（648,328……）
			ps.put("roleid", player.id);// 角色ID
			ps.put("classfield", classfield);

			Map<String, Object> extra = new HashMap<>();
			extra.put("区服", GGame.__SERVER_ID);
			extra.put("角色名", player.name);// 角色名称
			extra.put("角色职业", player.pro);// 职业
			extra.put("角色等级", player.level);// 职业
			extra.put("角色突破等级", player.upLevel);// 角色突破等级
			extra.put("角色战斗力", player.fightPower);// 角色战斗力
			extra.put("元宝存量", player.diamond);// 元宝存量
			extra.put("绑元存量", player.ticket);// 绑元存量
			extra.put("银两存量", player.gold);// 银两存量

			ps.put("extra", extra);
			Map<String, String> params = new HashMap<>();
			params.put("name", "RechargeSuccess");
			params.put("ts", String.valueOf(System.currentTimeMillis() / 1000));
			params.put("data", JSON.toJSONString(ps));
			params.put("source", String.valueOf(player.subchannel));//
			params.put("device_id", player.mac);//
			params.put("platform", String.valueOf(osToPlatform(player.os)));//
			params.put("ip", player.ip);//

			ansycReportExec.execute(() -> {
				try {
					Out.info("RechargeSuccess report result:", new HttpRequester().sendPost(reportUrl, params).getContent());
				} catch (IOException e) {
					Out.warn("RechargeSuccess report exception", JSON.toJSONString(params), e);
				}
			});
		}
	}

	/**
	 * OS转化为平台
	 */
	public int osToPlatform(String os) {
		// 系统类型(ios是5，安卓是6)
		if ("6".equals(os)) {
			return LoginConst.OS_TYPE_ANDROID;
		} else if ("5".equals(os)) {
			return LoginConst.OS_TYPE_IOS;
		}
		return LoginConst.OS_TYPE_OTHER;
	}

	/**
	 * 异步上报玩家数据 P_360, P_XM, P_ALI, P_HW, P_OPPO, P_VIVO, P_YSDK, P_MEIZU,
	 * P_COOLPAI, P_LENOVO, P_AMIGO, P_DUOKU, P_QY
	 */
	public void ansycReportPlayerData(Channel session, PlayerPO player, boolean upgrade) {
		try {
			if (session == null || player == null) {
				Out.warn("异步上报玩家数据时，Session为空");
				return;
			}

			TokenInfo tokenInfo = session.attr(GGlobal.__KEY_TOKEN_INFO).get();
			if (tokenInfo != null && StringUtils.isNotEmpty(tokenInfo.getSubchannel())) {
				String subchannel = tokenInfo.getSubchannel();
				switch (subchannel) {
				case "P_ALI":// 阿里的U9
					ansycReportPlayerDataBy1001P_Ali(tokenInfo, player, upgrade);
					break;
				case "1003":// 当乐
					if (upgrade) {// 升级才会调
						ansycReportPlayerDataBy1003(tokenInfo, player);
					}
					break;
				default:
					break;
				}
			}
		} catch (Exception e) {
			Out.error("异步上报玩家数据时发生异常啦.", e);
		}
	}

	// 阿里的U9
	private void ansycReportPlayerDataBy1001P_Ali(TokenInfo tokenInfo, PlayerPO player, boolean upgrade) throws UnsupportedEncodingException {
		final long timeStamp = System.currentTimeMillis() / 1000;

		JSONObject object = new JSONObject();
		object.put("id", timeStamp);
		object.put("service", "ucid.game.gameData");
		{// data
			JSONObject data = new JSONObject();
			{// gamedata
				JSONObject gameData = new JSONObject();
				gameData.put("category", "loginGameRole");

				{
					JSONObject content = new JSONObject();
					content.put("zoneId", GWorld.__SERVER_ID);
					content.put("zoneName", GWorld.__SERVER_NAME);
					content.put("roleId", player.id);
					content.put("roleName", player.name);
					content.put("roleCTime", player.createTime.getTime() / 1000);
					content.put("roleLevel", String.valueOf(player.level));
					content.put("os", "6".equals(player.os) ? "android" : "ios");
					if (upgrade) {
						content.put("roleLevelMTime", timeStamp);
					}
					gameData.put("content", content);
				}

				data.put("accountId", player.subchannelUID);
				data.put("gameData", URLEncoder.encode(gameData.toJSONString(), "UTF-8"));
			}

			// 签名参数
			object.put("sign", sign(data, aliAppKey));
			object.put("data", data);
		}

		{// game参数
			JSONObject game = new JSONObject();
			game.put("gameId", aliAppId);
			object.put("game", game);
		}

		String postData = object.toJSONString();
		ansycReportExec.execute(() -> {
			try {
				String result = HttpClientUtil.doPost(aliReportUrl, postData, HttpClientUtil.JSON_CONTENT_TYPE);
				Out.info("AliReport result:", result);
			} catch (Exception e) {
				Out.warn("AliReport report exception", e);
			}
		});
	}

	public static String sign(Map<String, Object> reqMap, String signKey) {
		// 将所有key按照字典顺序排序
		TreeMap<String, Object> signMap = new TreeMap<>(reqMap);
		StringBuilder stringBuilder = new StringBuilder(1024);
		for (Map.Entry<String, Object> entry : signMap.entrySet()) {
			// sign和signType不参与签名
			if ("sign".equals(entry.getKey()) || "signType".equals(entry.getKey())) {
				continue;
			}
			// 值为null的参数不参与签名
			if (entry.getValue() != null) {
				stringBuilder.append(entry.getKey()).append("=").append(entry.getValue());
			}
		}
		// 拼接签名秘钥
		stringBuilder.append(signKey);
		// 剔除参数中含有的'&'符号
		String signSrc = stringBuilder.toString().replaceAll("&", "");
		return MD5.encrypt(signSrc).toLowerCase();
	}

	// （当乐平台）
	private void ansycReportPlayerDataBy1003(TokenInfo tokenInfo, PlayerPO player) {
		if (StringUtils.isNotEmpty(downjoyReportUrl)) {
			final long timeStamp = System.currentTimeMillis();
			ansycReportExec.execute(() -> {
				try {
					Long appId = (long) downjoyAppId;
					String umid = tokenInfo.getSubchannelUid(); // 当乐用户唯一标识
					String token = tokenInfo.getAccessToken(); // 当乐登录凭证token
					String zoneId = String.valueOf(GWorld.__SERVER_ID); // 区服ID，长度不超过50个字符
					String zoneName = GWorld.__SERVER_NAME; // 区服名称，长度不超过50个字符，特殊字符、中文不需要编码
					String roleId = player.id; // 角色ID，长度不超过50个字符
					String roleName = player.name; // 角色名，长度不超过50个字符，特殊字符、中文不需要编码
					Date roleCreateTime = player.createTime; // 角色创建时间
					String roleLevel = String.valueOf(player.level); // 角色等级，长度不超过 20 个字符
					Date roleLevelModifyTime = new Date(timeStamp); // 角色名称或等级变化时间

					JSONObject object = new JSONObject();
					object.put("appId", appId);
					object.put("timestamp", timeStamp);
					JSONObject data = new JSONObject();
					data.put("umid", umid);
					data.put("zoneId", zoneId);
					data.put("zoneName", zoneName);
					data.put("roleId", roleId);
					data.put("roleName", roleName);
					data.put("roleCTime", roleCreateTime.getTime() / 1000);
					data.put("roleLevel", roleLevel);
					data.put("roleLevelMTime", roleLevelModifyTime.getTime() / 1000);
					String encodeData = URLEncoder.encode(data.toJSONString(), "UTF-8");
					object.put("data", encodeData);
					String signParam = new StringBuilder(512).append("appId=").append(appId).append("&data=").append(encodeData).append("&timestamp=").append(timeStamp).append(token).append(downjoyAppKey).toString();
					String sign = MD5.getDigest(signParam);
					object.put("sig", sign);
					object.put("token", token);
					String postData = object.toJSONString();

					String result = HttpClientUtil.doPost(downjoyReportUrl, postData, HttpClientUtil.JSON_CONTENT_TYPE);
					Out.info("DownjoyReport result:", result);
				} catch (Exception e) {
					Out.warn("DownjoyReport report exception", e);
				}
			});
		}
	}

	public static void main(String[] args) {
		String test = "%7bcode=value2name=value3personid=value1202cb962234w4ers2aa";
		System.out.println(MD5.encrypt(test).toLowerCase());
	}

	public void ansycReportHolyArmour(PlayerPO player, Map<Integer, String> data) {
		Map<String, Object> param = new HashMap<>();
		param.put("phylum", JSON.toJSON(data));
		this.ansycReport(player, "ActivateHolyArmour", param);
	}

	/**
	 * 异步上报共享参数...
	 */
	private void ansycReport(PlayerPO player, String name, Map<String, Object> param) {
		final String reportUrl = GConfig.getInstance().get("server.bi.reportUrl");
		if (StringUtils.isNotEmpty(reportUrl)) {
			Map<String, Object> ps = new HashMap<>();
			ps.put("userid", player.uid);
			ps.put("clientid", GGame.__SERVER_ID);
			ps.put("user_level", player.level);// 角色等级
			ps.put("roleid", player.id);// 角色ID
			ps.putAll(param);

			Map<String, Object> extra = new HashMap<>();
			extra.put("角色名", player.name);// 角色名称
			extra.put("角色职业", player.pro);// 职业
			extra.put("角色突破等级", player.upLevel);// 角色突破等级
			extra.put("角色战斗力", player.fightPower);// 角色战斗力
			extra.put("元宝存量", player.diamond);// 元宝存量
			extra.put("绑元存量", player.ticket);// 绑元存量
			extra.put("银两存量", player.gold);// 银两存量

			ps.put("extra", extra);
			Map<String, String> params = new HashMap<>();
			params.put("name", name);
			params.put("ts", String.valueOf(System.currentTimeMillis() / 1000));
			params.put("data", JSON.toJSONString(ps));
			params.put("source", String.valueOf(player.subchannel));//
			params.put("device_id", player.mac);//
			params.put("platform", String.valueOf(osToPlatform(player.os)));//
			params.put("ip", player.ip);//

			ansycReportExec.execute(() -> {
				try {
					Out.info("ansycReport ", name, " report result:", new HttpRequester().sendPost(reportUrl, params).getContent());
				} catch (IOException e) {
					Out.warn("ansycReport", name, " report exception", JSON.toJSONString(params), e);
				}
			});
		}
	}

	public void ansycReportSkillUpgrade(PlayerPO player, int skillId, String skillName, int skillLevel) {
		Map<String, Object> param = new HashMap<>();
		param.put("kingdom", skillId);
		param.put("phylum", skillLevel);
		param.put("classfield", skillName);
		this.ansycReport(player, "UpgradeSkill", param);
	}

	public void ansycReportEquipChange(PlayerPO player, int type, int position, Map<String, Object> data) {
		Map<String, Object> param = new HashMap<>();
		param.put("kingdom", type);
		param.put("phylum", position);
		param.put("classfield", data);
		this.ansycReport(player, "EquipChange", param);
	}

	public void ansycReportPetActivate(PlayerPO player, int id, String petName) {
		Map<String, Object> param = new HashMap<>();
		param.put("kingdom", id);
		param.put("phylum", petName);
		this.ansycReport(player, "PetActivate", param);
	}

	public void ansycReportPetBattle(PlayerPO player, int petId, String petName, int level, int upLevel, int type) {
		Map<String, Object> param = new HashMap<>();
		param.put("kingdom", petId);
		param.put("phylum", petName);
		param.put("classfield", level);
		param.put("family", upLevel);
		param.put("genus", type);
		this.ansycReport(player, "PetBattle", param);
	}

	public void ansycReportMountActivate(PlayerPO player, int id, String skinName) {
		Map<String, Object> param = new HashMap<>();
		param.put("kingdom", id);
		param.put("phylum", skinName);
		this.ansycReport(player, "MountActivate", param);
	}

	public void ansycReportBloodChange(PlayerPO player, int type, int position, Map<String, Object> data) {
		Map<String, Object> param = new HashMap<>();
		param.put("kingdom", type);
		param.put("phylum", position);
		param.put("classfield", data);
		this.ansycReport(player, "BloodChange", param);
	}

	public void ansycReportResourceDungeon(PlayerPO player, int win, int mapID) {
		Map<String, Object> param = new HashMap<>();
		param.put("kingdom", win);
		param.put("phylum", mapID);
		this.ansycReport(player, "ResourceDungeon", param);
	}

	public void ansycReportDemonTower(PlayerPO player, int win, int mapID) {
		Map<String, Object> param = new HashMap<>();
		param.put("kingdom", win);
		param.put("phylum", mapID);
		this.ansycReport(player, "DemonTower", param);
	}

	public void ansycReportFightLevel(PlayerPO player, int win, int mapID, String instanceId) {
		Map<String, Object> param = new HashMap<>();
		param.put("kingdom", win);
		param.put("phylum", mapID);
		param.put("classfield", instanceId);
		this.ansycReport(player, "FightLevel", param);
	}

	public void ansycReportEnteredArea(PlayerPO player, int sceneType, int areaId) {
		Map<String, Object> param = new HashMap<>();
		param.put("kingdom", sceneType);
		param.put("phylum", areaId);
		this.ansycReport(player, "EnteredArea", param);
	}

	public void ansycReportLeaderBoard(PlayerPO player, int type) {
		Map<String, Object> param = new HashMap<>();
		param.put("kingdom", type);
		this.ansycReport(player, "LeaderBoard", param);
	}

	public void ansycReportKillBoss(PlayerPO player, int sceneType, int monsterId) {
		Map<String, Object> param = new HashMap<>();
		param.put("kingdom", sceneType);
		param.put("phylum", monsterId);
		this.ansycReport(player, "KillBoss", param);
	}

	public void ansycReportEconomy(PlayerPO player, String currency, boolean kingdom, int value, int origin) {
		Map<String, Object> param = new HashMap<>();
		param.put("currency", currency);
		param.put("kingdom", kingdom ? "earing" : "expenditure");
		param.put("amount", value);
		param.put("phylum", origin);
		this.ansycReport(player, "Economy", param);
	}

	public void ansycReportItem(PlayerPO player, boolean kingdom, String itemcode, int count, GOODS_CHANGE_TYPE origin, String name) {
		Map<String, Object> param = new HashMap<>();
		param.put("kingdom", kingdom ? "获得" : "消耗");
		param.put("phylum", origin.value);
		param.put("family", name + "(" + itemcode + "):" + count);
		this.ansycReport(player, "Item", param);
	}

	public void ansycReportMeltCultivate(PlayerPO player, List<NormalItem> items) {
		Map<String, Object> param = new HashMap<>();
		StringBuilder sb = new StringBuilder(128);
		for (NormalItem item : items) {
			sb.append(item.getName()).append("(").append(item.itemCode()).append(")").append(item.getNum()).append(",");
		}
		param.put("family", sb.toString());
		this.ansycReport(player, "MeltCultivate", param);
	}

	public void ansycReportMission(PlayerPO player, String phylum, int kind, int id, String name) {
		Map<String, Object> param = new HashMap<>();
		// 1=主线任务main
		// 2=支线任务side
		// 3=日常任务daily
		if (kind == 1) {
			param.put("kingdom", "主线任务main");
		} else if (kind == 2) {
			param.put("kingdom", "支线任务side");
		} else if (kind == 3) {
			param.put("kingdom", "日常任务daily");
		} else {
			return;
		}
		param.put("phylum", phylum);
		param.put("genus", id);
		param.put("family", name);
		this.ansycReport(player, "Mission", param);
	}

	public void ansycReportPetCultivate(PlayerPO player, int upLevel, String itemcode, int count, int id) {
		Map<String, Object> param = new HashMap<>();
		param.put("kingdom", 2);
		param.put("phylum", "");
		param.put("classfield", (upLevel - 1) + "_" + upLevel);
		DItemBase it = ItemConfig.getInstance().getItemTemplates().get(itemcode);
		param.put("family", (it == null ? "" : it.name) + "(" + itemcode + "):" + count);
		param.put("genus", id);
		this.ansycReport(player, "PetCultivate", param);
	}

	public void ansycReportPetCultivate(PlayerPO player, int oldLevel, int level, long oldExp, long exp, int id) {
		Map<String, Object> param = new HashMap<>();
		param.put("kingdom", 1);
		param.put("phylum", oldLevel + "_" + level + "," + oldExp + "_" + exp);
		param.put("classfield", "");
		param.put("family", "");
		param.put("genus", id);
		this.ansycReport(player, "PetCultivate", param);
	}

	public void ansycReportRideTrainBI(PlayerPO player, int kingdom, int level, int starLv, String itemcode, int count) {
		Map<String, Object> param = new HashMap<>();
		param.put("kingdom", kingdom);
		param.put("phylum", level);
		param.put("classfield", starLv);
		DItemBase it = ItemConfig.getInstance().getItemTemplates().get(itemcode);
		param.put("family", (it == null ? "" : it.name) + "(" + itemcode + "):" + count);
		this.ansycReport(player, "RideTrainBI", param);
	}

	public void ansycReportStrengthenCultivate(PlayerPO player, int pos, int oldLevel, int level, JSONObject mates) {
		Map<String, Object> param = new HashMap<>();
		param.put("kingdom", pos);
		param.put("phylum", "强化前等级：" + oldLevel);
		param.put("classfield", "强化后等级:" + level);
		StringBuilder sb = new StringBuilder(128);
		for (String itemcode : mates.keySet()) {
			int needNum = mates.getIntValue(itemcode);
			DItemBase it = ItemConfig.getInstance().getItemTemplates().get(itemcode);
			sb.append((it == null ? "" : it.name)).append("(").append(itemcode).append(")").append(needNum).append(",");
		}
		param.put("family", sb.toString());
		this.ansycReport(player, "StrengthenCultivate", param);
	}
}