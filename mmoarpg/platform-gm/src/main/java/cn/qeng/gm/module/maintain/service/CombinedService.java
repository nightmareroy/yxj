/*
 * Copyright © 2017 qeng.cn All Rights Reserved.
 * 
 * 感谢您加入清源科技，不用多久，您就会升职加薪、当上总经理、出任CEO、迎娶白富美、从此走上人生巅峰
 * 除非符合本公司的商业许可协议，否则不得使用或传播此源码，您可以下载许可协议文件：
 * 
 * 		http://www.noark.xyz/qeng/LICENSE
 *
 * 1、未经许可，任何公司及个人不得以任何方式或理由来修改、使用或传播此源码;
 * 2、禁止在本源码或其他相关源码的基础上发展任何派生版本、修改版本或第三方版本;
 * 3、无论你对源代码做出任何修改和优化，版权都归清源科技所有，我们将保留所有权利;
 * 4、凡侵犯清源科技相关版权或著作权等知识产权者，必依法追究其法律责任，特此郑重法律声明！
 */
package cn.qeng.gm.module.maintain.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.AuthServer;
import com.wanniu.GServer;
import com.wanniu.gm.message.GameInfoMessage;

import cn.qeng.gm.api.KickAllAPI;
import cn.qeng.gm.api.combined.BackupRedisAPI;
import cn.qeng.gm.api.combined.DeletePlayerAPI;
import cn.qeng.gm.core.Redis;
import cn.qeng.gm.module.maintain.domain.Server;
import cn.qeng.gm.module.maintain.domain.ServerConfig;
import io.netty.channel.Channel;
import redis.clients.jedis.Tuple;

/**
 * 
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@Service
public class CombinedService {
	private final static Logger logger = LogManager.getLogger(CombinedService.class);
	@Autowired
	private ServerPackageService serverPackageService;
	@Autowired
	private ServerConfigService serverConfigService;

	public void start(Server fs, Server ts) {
		new BackupRedisAPI().request(fs.getId());
	}

	public void locking(int[] fs, int t) {
		Server to = ServerService.getServer(t);
		for (int f : fs) {
			Server form = ServerService.getServer(f);
			serverPackageService.logInfo("开始合服，由【" + form.getServerName() + "(" + form.getId() + ")】合并到" + "【" + to.getServerName() + "(" + to.getId() + ")】");
		}
	}

	public void internal(Server server) {
		server.setShowState(1);// 1=对内
		ServerService.saveServer(server);
		// 同步信息到游戏服...
		Channel channel = GServer.getInstance().getChannel(server.getId());
		if (channel != null) {
			channel.writeAndFlush(new GameInfoMessage(server));
			// 走Redis订阅同步登录服.
			JSONObject json = new JSONObject();
			json.put("type", 0x109);
			json.put("appId", server.getAppId());
			json.put("logicServerId", server.getId());
			json.put("show", server.getShowState());
			AuthServer.publish(json);
		}
		serverPackageService.logInfo("服务器设计对内状态,【" + server.getServerName() + "(" + server.getId() + ")】");
	}

	public void kickall(Server server) {
		new KickAllAPI().request(server.getId());
		serverPackageService.logInfo("服务器开始踢人,【" + server.getServerName() + "(" + server.getId() + ")】");
	}

	public void restart(Server server) {
		serverPackageService.restart(new int[] { server.getId() });
	}

	public void backup(Server server) {
		serverPackageService.logInfo("服务器开始备份,【" + server.getServerName() + "(" + server.getId() + ")】");
		new BackupRedisAPI().request(server.getId());
		serverPackageService.logInfo("服务器备份完成,【" + server.getServerName() + "(" + server.getId() + ")】");
	}

	public void deletePlayer(Server server, Date openDate) {
		serverPackageService.logInfo("服务器开始删小号,【" + server.getServerName() + "(" + server.getId() + ")】");
		new DeletePlayerAPI(openDate).request(server.getId());
		serverPackageService.logInfo("服务器删小号完成,【" + server.getServerName() + "(" + server.getId() + ")】");
	}

	public void merge(String form, ServerConfig to) {
		Redis toRedis = new Redis(to.getGameHost(), to.getRedisPort(), to.getRedisPassword(), to.getRedisIndex());
		if ("PONG".equals(toRedis.ping())) {
			serverPackageService.logInfo("To库链接正常...");
		} else {
			serverPackageService.logError("To库链接失败...");
			return;
		}

		// 预处理...
		serverPackageService.logInfo("TO预处理开始...");
		preprocessing(toRedis, to);
		serverPackageService.logInfo("TO预处理完成...");

		for (String f : form.split(",")) {
			ServerConfig fc = serverConfigService.getServerConfig(Integer.parseInt(f));
			Redis formRedis = new Redis(fc.getGameHost(), fc.getRedisPort(), fc.getRedisPassword(), fc.getRedisIndex());
			if ("PONG".equals(formRedis.ping())) {
				serverPackageService.logInfo("Form库链接正常...");
			} else {
				serverPackageService.logError("Form库链接失败...");
				return;
			}

			// 预处理...
			serverPackageService.logInfo("Form预处理开始...");
			preprocessing(formRedis, fc);
			serverPackageService.logInfo("Form预处理完成...");

			this.merge(fc, formRedis, to, toRedis);
		}
	}

	public void merge(ServerConfig form, Redis formRedis, ServerConfig to, Redis toRedis) {
		// 合并名称库
		serverPackageService.logInfo("合并名称库...");
		Map<String, String> names = formRedis.hgetAll("NAME_MODULE");
		toRedis.hmset("NAME_MODULE", names);
		serverPackageService.logInfo("合并完成：" + names.size());

		{// 玩家
			serverPackageService.logInfo("迁移玩家...");
			for (String id : names.values()) {
				logger.info("迁移玩家{}", id);
				toRedis.hmset(id, formRedis.hgetAll(id));
			}
			serverPackageService.logInfo("合并完成：" + names.size());
		}

		{// 公会
			serverPackageService.logInfo("迁移仙盟...");
			for (String key : new String[] { "guild", "guild_auction_log", "guild_bless", "guild_boss_rank", "guild_depot", "guild_impeach", "guild_member", "guild_news" }) {
				try {
					Map<String, String> data = formRedis.hgetAll(key);
					if (!data.isEmpty()) {
						toRedis.hmset(key, data);
						serverPackageService.logInfo("迁移仙盟的" + key);
					} else {
						serverPackageService.logInfo("仙盟无数据：" + key);
					}
				} catch (Exception e) {
					serverPackageService.logError("迁移仙盟的" + key + "出错了。。。");
				}
			}
		}

		{// 道友
			serverPackageService.logInfo("迁移道友...");
			for (String key : new String[] { "dao_you", "dao_you_member" }) {
				Map<String, String> data = formRedis.hgetAll(key);
				if (!data.isEmpty()) {
					toRedis.hmset(key, data);
					serverPackageService.logInfo("迁移道友的" + key);
				} else {
					serverPackageService.logInfo("道友无数据：" + key);
				}
			}
		}

		{// 合并排行榜.
			serverPackageService.logInfo("迁移排行榜...");
			for (String key : new String[] { "LEVEL", "FIGHTPOWER", "FIGHTPOWER_1", "FIGHTPOWER_3", "FIGHTPOWER_5", "GUILD_LEVEL", "Mount", "PET", "XIANYUAN", "HP", "PHY", "MAGIC", "DEMON_TOWER" }) {
				try {
					Set<Tuple> ts = formRedis.zrangeWithScores("rank/" + form.getId() + "/" + key);
					Map<String, Double> rank = new HashMap<>();
					for (Tuple t : ts) {
						rank.put(t.getElement(), t.getScore());
					}
					toRedis.zadd("rank/" + to.getId() + "/" + key, rank);
					serverPackageService.logInfo("迁移排行榜的" + key);
				} catch (Exception e) {
					serverPackageService.logError("迁移排行榜的" + key + "出错了。。。");
				}
			}
		}

		{// 复活
			serverPackageService.logInfo("复活...");
			for (String key : new String[] { "DAILY_RELIVE" }) {
				Map<String, String> data = formRedis.hgetAll(key);
				if (!data.isEmpty()) {
					toRedis.hmset(key, data);
					serverPackageService.logInfo("迁移复活的" + key);
				} else {
					serverPackageService.logInfo("复活无数据：" + key);
				}
			}
		}

		{// 成长基金,两个服加起来...
			String funds = formRedis.hget(String.valueOf(form.getId()), "funds");
			if (!StringUtils.isEmpty(funds)) {
				long sum = toRedis.hincrBy(String.valueOf(to.getId()), "funds", Integer.parseInt(funds));
				serverPackageService.logInfo("成长基金和为:" + sum);
			}
		}

		{// 拍卖
			serverPackageService.logInfo("拍卖...");
			for (String key : new String[] { "consignment_items" }) {
				Map<String, String> data = formRedis.hgetAll(key);
				if (!data.isEmpty()) {
					toRedis.hmset(key, data);
					serverPackageService.logInfo("迁移拍卖的" + key);
				} else {
					serverPackageService.logInfo("拍卖无数据：" + key);
				}
			}
		}

		{// 竞拍
			serverPackageService.logInfo("竞拍...");
			for (String key : new String[] { "auction_itemsTR" }) {
				Map<String, String> data = formRedis.hgetAll(key);
				if (!data.isEmpty()) {
					toRedis.hmset(key, data);
					serverPackageService.logInfo("迁移竞拍的" + key);
				} else {
					serverPackageService.logInfo("竞拍无数据：" + key);
				}
			}
		}

		serverPackageService.logInfo(form.getServerName() + "搞定啦...");
	}

	private void preprocessing(Redis redis, ServerConfig config) {
		// 删除竞技类的系统配置
		redis.hdel(String.valueOf(config.getId()), "fleeSystemTR");
		redis.hdel(String.valueOf(config.getId()), "servermails");
		redis.hdel(String.valueOf(config.getId()), "arenaSystemTR");
		redis.hdel(String.valueOf(config.getId()), "five2FiveSystem");
		redis.hdel(String.valueOf(config.getId()), "soloSystemTR");

		// 删除几个排行榜...
		redis.del("rank/" + config.getId() + "/GUILD_BOSS_GUILD/preday");
		redis.del("rank/" + config.getId() + "/GUILD_BOSS_GUILD/today");
		redis.del("rank/" + config.getId() + "/GUILD_BOSS_SINGLE/preday");
		redis.del("rank/" + config.getId() + "/GUILD_BOSS_SINGLE/today");
		redis.del("rank/" + config.getId() + "/FLEE");
		redis.del("rank/" + config.getId() + "/PVP_5V5");
		redis.del("rank/" + config.getId() + "/ARENA_SCORE");

		for (int i = 0; i < 100; i++) {
			redis.del("rank/" + config.getId() + "/ARENA_SCOREALL-" + i);
			redis.del("rank/" + config.getId() + "/SOLO_SCORE-" + i);
		}

		// 未合服才更名...
		if (ServerService.getSidList(config.getId()).size() == 1) {
			{// 修正公会名称
				Map<String, String> data = redis.hgetAll("guild");
				for (String k : data.keySet()) {
					JSONObject object = JSON.parseObject(data.get(k));
					object.put("name", "S" + (config.getId() - 10000) + "." + object.getString("name"));
					data.put(k, object.toJSONString());
					logger.info("修正公会名称：{}", object.getString("name"));
				}
				redis.hmset("guild", data);
			}

			{// 修正道友名称
				Map<String, String> data = redis.hgetAll("dao_you");
				for (String k : data.keySet()) {
					JSONObject object = JSON.parseObject(data.get(k));
					object.put("name", "S" + (config.getId() - 10000) + "." + object.getString("name"));
					data.put(k, object.toJSONString());
					logger.info("修正道友名称：{}", object.getString("name"));
				}
				redis.hmset("dao_you", data);
			}
		}
	}

	public void stop(String form, int to) {
		String[] fs = form.split(",");
		int[] sidList = new int[fs.length + 1];
		for (int i = 0; i < fs.length; i++) {
			sidList[i] = Integer.parseInt(fs[i]);
		}
		sidList[fs.length] = to;
		serverPackageService.stop(sidList);
	}

	public void gameover(String form, int to) {
		// 修正区服配置...
		for (String f : form.split(",")) {
			Server server = ServerService.getServer(Integer.parseInt(f));
			server.setMaster(to);
			ServerService.saveServer(server);
			// 删除配置
			serverConfigService.delete(Integer.parseInt(f));
		}
		ServerService.resetCalServerByGroupCaches();
	}
}