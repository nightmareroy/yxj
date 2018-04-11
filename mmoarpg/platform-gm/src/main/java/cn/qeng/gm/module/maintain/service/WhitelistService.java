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
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.AuthServer;
import com.wanniu.util.DateUtil;

import cn.qeng.common.gm.RedisKeyConst;

/**
 * 白名单服务类.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
@Service
public class WhitelistService {

	public List<JSONObject> getAll() {
		List<JSONObject> result = new ArrayList<>();
		loadWhiteList(result, RedisKeyConst.REDIS_KEY_WHITELIST_IP, 1, 1);
		loadWhiteList(result, RedisKeyConst.REDIS_KEY_WHITELIST_UID, 2, 1);
		loadWhiteList(result, RedisKeyConst.REDIS_KEY_BLACKLIST_IP, 1, 0);
		loadWhiteList(result, RedisKeyConst.REDIS_KEY_BLACKLIST_UID, 2, 0);
		return result;
	}

	// 加载白名单列表
	private void loadWhiteList(List<JSONObject> result, String key, int type, int white) {
		Map<String, String> list = AuthServer.hgetAll(key);
		for (Map.Entry<String, String> entry : list.entrySet()) {
			JSONObject json = new JSONObject();
			json.put("type", type);
			String ip = entry.getKey();
			json.put("ip", ip);
			long time = Long.valueOf(entry.getValue());
			json.put("time", time == 0 ? null : DateUtil.format(new Date(time)));
			json.put("white", white);
			json.put("desc", AuthServer.get(key + ip));
			result.add(json);
		}
	}

	public JSONObject getWhitelist(int whitelisttype, int type, String ip) {
		String key = getKeyByType(whitelisttype, type);
		String hour = AuthServer.hget(key, ip);
		if (StringUtils.isEmpty(hour)) {
			return null;
		}

		long time = Long.parseLong(hour);
		JSONObject json = new JSONObject();
		json.put("type", type);
		json.put("ip", ip);
		json.put("time", time == 0 ? 0 : null);
		json.put("white", whitelisttype);
		json.put("desc", AuthServer.get(key + ip));
		return json;
	}

	public void delete(int whitelisttype, int type, String ip) {
		String key = getKeyByType(whitelisttype, type);
		// 删除
		AuthServer.hdel(key, ip);
		// 把描述也删了.
		AuthServer.del(key + ip);

		publishClearAll();
	}

	public void edit(int whitelisttype, int type, String ip, int hour, String describe) {
		String key = getKeyByType(whitelisttype, type);
		// 保存
		String time = hour == 0 ? "0" : String.valueOf(System.currentTimeMillis() + 1L * hour * 60 * 1000);
		AuthServer.hset(key, ip, time);

		// 描述
		if (!StringUtils.isEmpty(describe)) {
			AuthServer.set(key + ip, describe);
		}

		publishClearAll();
	}

	public void publishClearAll() {
		AuthServer.publish("/clear/whitelist", new JSONObject());
	}

	private String getKeyByType(int whitelisttype, int type) {
		String key = "";
		// 白名单
		if (whitelisttype == 1) {
			// IP
			if (type == 1) {
				key = RedisKeyConst.REDIS_KEY_WHITELIST_IP;
			}
			// UID
			else {
				key = RedisKeyConst.REDIS_KEY_WHITELIST_UID;
			}
		}
		// 黑名单
		else {
			// IP
			if (type == 1) {
				key = RedisKeyConst.REDIS_KEY_BLACKLIST_IP;
			}
			// UID
			else {
				key = RedisKeyConst.REDIS_KEY_BLACKLIST_UID;
			}
		}
		return key;
	}
}