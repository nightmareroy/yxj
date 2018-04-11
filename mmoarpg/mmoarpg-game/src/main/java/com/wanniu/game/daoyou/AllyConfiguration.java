package com.wanniu.game.daoyou;

import com.wanniu.game.data.GameData;

/**
 * 盟友
 * 
 * @author Yangzz
 *
 */
public class AllyConfiguration {

	public static AllyConfiguration getInstance() {
		return Inner.instance;
	}

	private static class Inner {
		private static AllyConfiguration instance = new AllyConfiguration();
	}

	private AllyConfiguration() {

	}

	/**
	 * 返回Int类型配置
	 */
	public int getConfigI(String key) {
		return Integer.valueOf(getConfigS(key));
	}

	/**
	 * 返回String类型配置
	 */
	public String getConfigS(String key) {
		return GameData.AllyConfigs.get(key).paramValue;
	}
}
