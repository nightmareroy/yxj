package com.wanniu.game.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanniu.game.data.GameData;
import com.wanniu.game.data.PrefixCO;
import com.wanniu.game.data.SuffixCO;
import com.wanniu.game.data.ext.UpLevelExpExt;

/**
 * 玩家配置管理
 * 
 * @author Yangzz
 *
 */
public class PlayerConfig {

	/** 随机名字配置 0:女 1:男 */
	public List<PrefixCO> randomname_prefix = null;
	public Map<Integer, List<SuffixCO>> randomname_suffix;

	private static PlayerConfig instance;

	public static PlayerConfig getInstance() {
		if (instance == null) {
			instance = new PlayerConfig();
		}
		return instance;
	}

	private PlayerConfig() {
		// 加载随机名字配置
		randomname_prefix = new ArrayList<PrefixCO>(GameData.Prefixs.values());
		randomname_suffix = new HashMap<>();
		for (SuffixCO suffix : GameData.Suffixs.values()) {
			// 性别为2有男女通用...
			if (suffix.sex == 2) {
				randomname_suffix.computeIfAbsent(0, key -> new ArrayList<>()).add(suffix);
				randomname_suffix.computeIfAbsent(1, key -> new ArrayList<>()).add(suffix);
			} else {
				randomname_suffix.computeIfAbsent(suffix.sex, key -> new ArrayList<>()).add(suffix);
			}
		}
	}

	/**
	 * 根据进阶等级和职业获取进阶配置
	 */
	public UpLevelExpExt findupLevelExpPropsByUpLevelAndPro(int upLevel, int pro) {
		// for (UpLevelExpExt cfg : GameData.UpLevelExps.values()) {
		// if (cfg.upLevel == upLevel && getCharactorByName(cfg.pro).pro == pro) {
		// return cfg;
		// }
		// }
		GameData.findUpLevelExps(o -> {
			return o.classUPLevel == upLevel && o.Pro == pro;
		});
		return null;
	}

}
