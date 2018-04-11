package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.RandomUtil;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.data.MonsterRefreshCO;

public class MonsterRefreshExt extends MonsterRefreshCO {

	public String[] monsterRefreshPoints;

	public float pointX = 0f;
	public float pointY = 0f;

	public Map<String, Integer> joinReward;

	@Override
	public void initProperty() {
		super.initProperty();

		monsterRefreshPoints = this.refreshPoint.split(":");

		String[] xy = this.monPoint.split(",");
		if (xy.length == 2) {
			try {
				pointX = Float.parseFloat(xy[0]);
				pointY = Float.parseFloat(xy[1]);
			} catch (NumberFormatException e) {
				Out.error(e);
				pointX = 0f;
				pointY = 0f;
			}
		}
		if (!StringUtil.isEmpty(this.partakeDropPre)) {
			Map<String, Integer> tempJoinReward = new HashMap<>();
			String[] joins = this.partakeDropPre.split(";");
			if (joins != null && joins.length > 0) {
				for (String j : joins) {
					String[] js = j.split(":");
					tempJoinReward.put(js[0], Integer.parseInt(js[1]));
				}
			}
			joinReward = tempJoinReward;
		}

	}

	public String getRefreshPoint() {
		return monsterRefreshPoints[RandomUtil.getIndex(monsterRefreshPoints.length)];
	}

	public boolean containsRefreshPoint(String point) {
		for (String p : monsterRefreshPoints) {
			if (p.equalsIgnoreCase(point)) {
				return true;
			}
		}
		return false;
	}
}
