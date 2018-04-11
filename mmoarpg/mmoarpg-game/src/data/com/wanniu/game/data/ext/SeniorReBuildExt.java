package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.game.data.SeniorReBuildCO;

/**
 * 高级重铸
 * @author Liyue
 *
 */
public class SeniorReBuildExt extends SeniorReBuildCO {

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
		materials.put(mateCode3, mateCount3);
	}

	
}
