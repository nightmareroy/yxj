package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import com.wanniu.game.data.BloodSuitListCO;


public class BloodSuitListExt extends BloodSuitListCO {

	
	
	public List<Integer> partIdList;
	public List<Integer> occupationProIds;

	@Override
	public void initProperty() {
		partIdList=new LinkedList<>();
		occupationProIds=new LinkedList<>();
		
		String[] partCodeListStrs=partCodeList.split(",");
		for (String partCodeListItem : partCodeListStrs) {
			partIdList.add(Integer.parseInt(partCodeListItem));
		}
		
		String[] occupationStrs=occupation.split(",");
		for (String occupationItem : occupationStrs) {
			occupationProIds.add(Integer.parseInt(occupationItem));
		}
	}


	
	
}
