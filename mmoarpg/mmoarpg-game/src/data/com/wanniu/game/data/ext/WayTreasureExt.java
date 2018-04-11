package com.wanniu.game.data.ext;

import java.util.ArrayList;
import java.util.List;

import com.wanniu.game.data.WayTreasureCO;

public class WayTreasureExt extends WayTreasureCO {

	public List<Integer> doScenes;

	public List<List<Integer>> doPoints;

	public List<Integer> monsterIds;

	@Override
	public void initProperty() {
		doScenes = new ArrayList<>();
		doPoints = new ArrayList<>();
		monsterIds = new ArrayList<>();

		String[] temp = this.doScene.split("\\|");
		for (String scene : temp) {
			doScenes.add(Integer.parseInt(scene));
		}

		temp = this.doPoint.split("\\|");
		for (String points : temp) {
			String[] ps = points.split(":");
			List<Integer> list_points = new ArrayList<>();
			for (String p : ps) {
				list_points.add(Integer.parseInt(p));
			}
			this.doPoints.add(list_points);
		}

		temp = this.monsterID.split(":");
		for (String m : temp) {
			this.monsterIds.add(Integer.parseInt(m));
		}

	}

}
