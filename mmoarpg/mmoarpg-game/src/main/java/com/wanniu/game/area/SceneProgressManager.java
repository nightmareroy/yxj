package com.wanniu.game.area;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wanniu.game.common.Const.EventType;
import com.wanniu.game.task.TaskEvent;

/**
 * 场景进度保存
 * 
 * @author Yangzz
 *
 */
public class SceneProgressManager {

	public Map<Integer, Object> progress = null;

	public SceneProgressManager(Map<Integer, Object> data) {
		this.progress = data;
	};

	public void onEvent(TaskEvent event) {
		if (event.type == EventType.changeSceneProgress.getValue()) {
			int key = (int) event.params[0];
			Object value = event.params;
			if (value == null) {
				this.progress.remove(key); // delete this.progress[key];
			} else {
				this.progress.put(key, value);
			}
		}
	};

	//
	// public void toJson4Serialize () {
	// return this.progress;
	// };

	public List<Object[]> toJson4BattleServer() {
		List<Object[]> data = new ArrayList<>();
		for (int key : this.progress.keySet()) {
			Object value = this.progress.get(key);
			data.add(new Object[] { key, value });
		}
		return data;
	};
}
