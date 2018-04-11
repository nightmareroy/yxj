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
package com.wanniu.game.monster;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.wanniu.game.data.GameData;
import com.wanniu.game.data.GuildBossRatioCO;
import com.wanniu.game.data.ext.InspireLevelExt;

/**
 * 
 *
 * @author Feiling(feiling@qeng.cn)
 */
public class GuildBossRatioConfig {
	public static Map<Integer, GuildBossRatioCO> monsterProps = new HashMap<>();
	public static Map<String, InspireLevelExt> inspires = new HashMap<>();

	public static void loadScript() {
		monsterProps.putAll(GameData.GuildBossRatios);
		loadInspire();
	}

	private static void loadInspire() {
		Map<String, InspireLevelExt> tpInspires = new HashMap<>();
		Map<Integer, InspireLevelExt> inspireLevels = GameData.InspireLevels;
		Set<Entry<Integer, InspireLevelExt>> sets = inspireLevels.entrySet();
		for (Entry<Integer, InspireLevelExt> e : sets) {
			InspireLevelExt co = e.getValue();
			String key = co.inspireType + "_" + co.inspireNum;
			tpInspires.put(key, co);
		}

		for (Entry<Integer, InspireLevelExt> e : sets) {
			InspireLevelExt co = e.getValue();
			int num = co.inspireNum;
			int lastnum = num - 1;
			String key = co.inspireType + "_" + co.inspireNum;
			InspireLevelExt currentExt = tpInspires.get(key);
			currentExt.totalInspirePlus = currentExt.inspirePlus * 100;
			if (lastnum > 0) {
				String lastKey = co.inspireType + "_" + lastnum;
				InspireLevelExt lastExt = tpInspires.get(lastKey);
				currentExt.totalInspirePlus += lastExt.totalInspirePlus;
			}
		}
		inspires = tpInspires;
	}

	public static GuildBossRatioCO getGuildBossRatioCO(int level) {
		return monsterProps.get(level);
	}

	public static InspireLevelExt getInspireLevelCO(int type, int num) {
		String key = type + "_" + num;
		return inspires.get(key);
	}
}
