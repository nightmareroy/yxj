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
package com.wanniu.gm.handler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.alibaba.fastjson.JSONArray;
import com.wanniu.core.GConfig;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.redis.Redis;
import com.wanniu.game.GWorld;
import com.wanniu.gm.GMEvent;
import com.wanniu.gm.GMResponse;
import com.wanniu.gm.GMStateResponse;

import cn.qeng.common.gm.RpcOpcode;

/**
 * Redis命令的实现.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@GMEvent
public class BackupRedisHandler extends GMBaseHandler {

	@Override
	public GMResponse execute(JSONArray arr) {
		String redisHost = GConfig.getInstance().get("server.redis.host", "127.0.0.1");
		int redisPort = GConfig.getInstance().getInt("server.redis.port", 6379);
		String pwd = GConfig.getInstance().get("server.redis.password");
		int db = GConfig.getInstance().getInt("server.redis.db", 0);
		Redis redis = new Redis(redisHost, redisPort, 10 * 60_0000, pwd != null ? pwd.trim() : null, db);
		redis.save();// 数据落地
		redis.close();

		this.makeDir();

		String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		File source = new File("/data/redis/dump_" + redisPort + ".rdb");
		File dest = new File("/data/backup/temp/dump_" + GWorld.__SERVER_ID + ".rdb." + date);
		try {
			Files.copy(source.toPath(), dest.toPath());
			return new GMStateResponse(1);
		} catch (IOException e) {
			Out.error(e);
			return new GMStateResponse(0);
		}
	}

	private void makeDir() {
		File dest = new File("/data/backup/temp/");
		if (!dest.exists()) {
			dest.mkdirs();
		}
	}

	@Override
	public short getType() {
		return RpcOpcode.OPCODE_BACKUP_REDIS;
	}
}