package com.wanniu.game.data.ext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanniu.game.data.EquipSockCO;

/**
 * 装备宝石镶嵌
 * @author Yangzz
 *
 */
public class EquipSockExt extends EquipSockCO {

	public Map<Integer, Integer> sockOpenLevel;
	
	public List<String> typeList;

	@Override
	public void initProperty() {
		sockOpenLevel = new HashMap<>();
		sockOpenLevel.put(1, sock1OpenLvl);
		sockOpenLevel.put(2, sock2OpenLvl);
		sockOpenLevel.put(3, sock3OpenLvl);
		sockOpenLevel.put(4, sock4OpenLvl);
		sockOpenLevel.put(5, sock5OpenLvl);
		
		typeList = Arrays.asList(gemTypeList.split(","));
	}
	
	
}
