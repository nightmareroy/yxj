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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.qeng.gm.module.maintain.domain.ServerConfig;
import cn.qeng.gm.module.maintain.domain.ServerConfigRepository;

/**
 * 
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@Service
public class ServerConfigService {

	@Autowired
	private ServerConfigRepository serverConfigRepository;

	public List<ServerConfig> getAllServerConfig() {
		return serverConfigRepository.findAll();
	}

	/**
	 * 重新生成分组缓存.
	 */
	public LinkedHashMap<String, List<ServerConfig>> calServerByGroupCaches() {
		List<ServerConfig> configs = this.getAllServerConfig();
		LinkedHashMap<String, List<ServerConfig>> result = new LinkedHashMap<>();
		final int size = 500;
		configs.sort((s1, s2) -> s1.getId() - s2.getId());
		configs.stream().filter(v -> v.getAreaId() > 0).forEach((v) -> {
			int x = v.getId() / size;
			String key = Math.max(x * size, 1) + "-" + ((x + 1) * size - 1);
			result.computeIfAbsent(key, k -> new ArrayList<>()).add(v);
		});
		return result;
	}

	public void edit(int id, String name, String template, int appId, int areaId, String pubhost, int port, String gameHost, // 基本配置
			String redisHost, int redisPort, String redisPassword, int redisIndex, // Redis配置
			String battleHost, int battleFastPort, int battleIcePort) {
		ServerConfig config = new ServerConfig();
		config.setId(id);
		config.setServerName(name);
		config.setAppId(appId);
		config.setAreaId(areaId);
		config.setGameHost(gameHost);
		config.setTemplate(template);

		config.setPubhost(pubhost);
		config.setPort(port);

		config.setRedisHost(redisHost);
		config.setRedisPort(redisPort);
		config.setRedisPassword(redisPassword);
		config.setRedisIndex(redisIndex);

		config.setBattleHost(battleHost);
		config.setBattleFastPort(battleFastPort);
		config.setBattleIcePort(battleIcePort);

		serverConfigRepository.save(config);
	}

	public ServerConfig getServerConfig(int id) {
		return serverConfigRepository.findOne(id);
	}

	public void delete(int id) {
		serverConfigRepository.delete(id);
	}
}