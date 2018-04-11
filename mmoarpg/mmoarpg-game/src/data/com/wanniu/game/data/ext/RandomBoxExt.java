package com.wanniu.game.data.ext;

import java.util.ArrayList;
import java.util.List;

import com.wanniu.core.util.RandomUtil;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.data.RandomBoxCO;

/**
 * 扩展随机宝箱
 * 
 * @author agui
 *
 */
public class RandomBoxExt extends RandomBoxCO {

	public static class Point {
		public float x;
		public float y;

		Point(float x, float y) {
			this.x = x;
			this.y = y;
		}

		public String toString() {
			return x + "," + y;
		}

		public double distance(Point p) {
			return Math.sqrt((p.x - x) * (p.x - x) + (p.y - y) * (p.y - y));
		}

	}

	public final List<Point> points = new ArrayList<>();

	@Override
	public void initProperty() {
		if (StringUtil.isNotEmpty(this.startPoint)) {
			String[] points = this.startPoint.split(":");
			for (String point : points) {
				String[] pos = point.split(",");
				this.points.add(new Point(Float.valueOf(pos[0]), Float.valueOf(pos[1])));
			}
		}
	}

	public Point randomPoint() {
		return points.get(RandomUtil.getIndex(points.size()));
	}

}
