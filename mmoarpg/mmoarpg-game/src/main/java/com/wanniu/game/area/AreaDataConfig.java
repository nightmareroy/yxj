package com.wanniu.game.area;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import com.wanniu.game.data.GameData;
import com.wanniu.game.data.base.MapBase;

public class AreaDataConfig {

	private static AreaDataConfig instance;

	public static AreaDataConfig getInstance() {
		if (instance == null) {
			instance = new AreaDataConfig();
		}
		return instance;
	}

	private AreaDataConfig() {
		allMaps.putAll(GameData.GameMaps);
		allMaps.putAll(GameData.DungeonMaps);
		allMaps.putAll(GameData.NormalMaps);
	}

	public Map<Integer, MapBase> allMaps = new HashMap<>();

	public final MapBase get(int mapId) {
		return allMaps.get(mapId);
	}

	public final List<MapBase> find(Predicate<MapBase> pre) {
		List<MapBase> list = new ArrayList<>();
		for (MapBase map : allMaps.values()) {
			if (pre.test(map)) {
				list.add(map);
			}
		}
		return list;
	}

	public final void foreach(Predicate<MapBase> pre) {
		for (MapBase map : allMaps.values()) {
			pre.test(map);
		}
	}

}
