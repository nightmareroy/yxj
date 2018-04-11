package com.wanniu.game.monster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import com.wanniu.game.data.GameData;
import com.wanniu.game.data.base.MonsterBase;

/**
 * 怪物模板数据集合(辉总monster.xls里面所有sheet数据,首先继承自 MonsterBase,然后再由 XXExt 去实现)
 * 
 * @author Yangzz
 *
 */
public class MonsterConfig {

	private static MonsterConfig instance;

	public static MonsterConfig getInstance() {
		if (instance == null) {
			instance = new MonsterConfig();
		}
		return instance;
	}

	public Map<Integer, MonsterBase> monsterProps = new HashMap<>();

	public void loadScript() {
		monsterProps.putAll(GameData.Normal_Worlds);
		monsterProps.putAll(GameData.Dungeons);
		monsterProps.putAll(GameData.DemonTowers);
		monsterProps.putAll(GameData.Dungeon_Normals);
		monsterProps.putAll(GameData.Dungeon_Elites);
		monsterProps.putAll(GameData.Dungeon_Heros);
	}

	public MonsterBase get(int id) {
		return monsterProps.get(id);
	}

	/**
	 * 查找符合条件的怪物模板集合
	 */
	public List<MonsterBase> find(Predicate<MonsterBase> t) {
		List<MonsterBase> list = new ArrayList<>();
		for (MonsterBase m : monsterProps.values()) {
			if (t.test(m)) {
				list.add(m);
			}
		}
		return list;
	}

}
