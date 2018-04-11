package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.game.data.RefineCO;

/**
 * 精炼
 * @author Yangzz
 *
 */
public class RefineExt extends RefineCO {
	public Map<String, Integer> materials;

	@Override
	public int getKey() {
		return level;
	}

	@Override
	public void initProperty() {
		materials = new HashMap<>();
		materials.put(mateCode1, mateCount1);
		materials.put(mateCode2, mateCount2);
	}
}
