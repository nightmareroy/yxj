package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.game.data.ReBuildCO;

/**
 * 重铸
 * @author Yangzz
 *
 */
public class ReBuildExt extends ReBuildCO {

	public Map<String, Integer> materials;

	@Override
	public int getKey() {
		return iD;
	}

	@Override
	public void initProperty() {
		materials = new HashMap<>();
		materials.put(mateCode1, mateCount1);
		materials.put(mateCode2, mateCount2);
		materials.put(mateCode3, mateCount3);
	}

	
}
