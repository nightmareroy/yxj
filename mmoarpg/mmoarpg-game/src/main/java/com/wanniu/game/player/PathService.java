package com.wanniu.game.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wanniu.game.area.AreaDataConfig;
import com.wanniu.game.data.base.MapBase;

/**
 * 跨场景寻路广度优先遍历
 * 
 * @author Yangzz
 *
 */
public class PathService {

	private static PathService instance;

	private Map<Integer, Map<Integer, Integer>> adj = null;
	private Map<Integer, Map<Integer, Integer>> pathMap = null;

	private PathService() {
		Map<Integer, MapBase> allMaps = AreaDataConfig.getInstance().allMaps;
		Set<Integer> sceneIds = allMaps.keySet();
		this.adj = new HashMap<>();
		this.pathMap = new HashMap<>();
		for (int id : sceneIds) {
			this.adj.put(id, new HashMap<>());
		}

		allMaps.values().forEach((prop) -> {
			Map<String, Integer> toPath = prop.toPath;
			for (String pointId : toPath.keySet()) {
				addEdge(prop.mapID, toPath.get(pointId), Integer.parseInt(pointId));
			}
		});

		traverse();
	}

	public static PathService getInstance() {
		if (instance == null) {
			instance = new PathService();
		}
		return instance;
	}

	private void addEdge(int fromId, int toId, int pointId) {
		this.adj.get(fromId).put(toId, pointId);// v的邻接表添加顶点w
	};

	private void traverse() {
		for (int id : this.adj.keySet()) {
			Map<Integer, Integer> data = this._bfs(id);
			this.pathMap.put(id, data);
		}
	};

	private Map<Integer, Integer> _bfs(int fromId) {
		Map<Integer, Boolean> marked = new HashMap<>();
		for (int areaId : this.adj.keySet()) {
			marked.put(areaId, false);
		}
		Map<Integer, Integer> data = new HashMap<>();
		data.put(fromId, -1);
		marked.put(fromId, true);
		this._bfsUtil(data, marked, fromId);
		return data;
	};

	private void _bfsUtil(Map<Integer, Integer> data, Map<Integer, Boolean> marked, int id) {
		List<Integer> que = new ArrayList<>();
		que.add(id);
		while (que.size() != 0) {
			id = que.remove(0); // que.shift();
			if (this.adj.get(id) != null) {
				for (int childId : this.adj.get(id).keySet()) {
					if (marked.get(childId) == null || !marked.get(childId)) {
						data.put(childId, id);
						marked.put(childId, true);
						que.add(childId);
					}
				}
			}
		}
	};

	public int findPath(int fromId, int toId) {
		List<Integer> result = new ArrayList<>();

		if (pathMap.get(fromId) != null && pathMap.get(toId) != null) {
			Map<Integer, Integer> data = pathMap.get(fromId);
			result.add(toId);
			int pid = data.get(toId) == null ? -1 : data.get(toId);
			while (pid != -1) {
				result.add(pid);
				pid = data.get(pid);
			}
		}

		int pointId = 0;
		if (result.size() >= 2) {
			int secondAreaId = result.get(result.size() - 2);
			pointId = this.adj.get(fromId).get(secondAreaId);
		}
		return pointId;
	};

	/**
	 * 通过路点找寻目的地场景
	 */
	public static int findToAreaByPointId(int areaId, String pointId) {
		MapBase prop = AreaDataConfig.getInstance().get(areaId);
		if (prop != null) {
			Map<String, Integer> toPathData = prop.toPath;
			if (toPathData.containsKey(pointId)) {
				return toPathData.get(pointId);
			}
		}
		return 0;
	};

	/**
	 * 通过路点找寻目的地场景坐标
	 */
	public static float[] findToAreaXYByPointId(int areaId, String pointId) {
		MapBase prop = AreaDataConfig.getInstance().get(areaId);
		if (prop != null) {
			Map<String, float[]> toPathXY = prop.toPathXY;
			if (toPathXY.containsKey(pointId)) {
				return toPathXY.get(pointId);
			}
		}
		return null;
	};

	/**
	 * 通过场景Id找寻目的地场景坐标
	 */
	public static float[] findToAreaXYByAreaId(int areaId, int toAreaId) {
		MapBase prop = AreaDataConfig.getInstance().get(areaId);
		if (prop != null) {
			Map<Integer, float[]> toPathXY = prop.toAreaXY;
			if (toPathXY.containsKey(toAreaId)) {
				return toPathXY.get(toAreaId);
			}
		}
		return null;
	};

}
