package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.ClassUtil;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.common.Utils;
import com.wanniu.game.data.PetAssociateCO;
import com.wanniu.game.data.PetSkillCO;


public class PetAssociateExt extends PetAssociateCO {

	public Map<Integer,Integer> petIDMap = new HashMap<>();
	
	public Map<Integer,Integer> addProMap = new HashMap<>();

	
	public void initProperty() {

		String[] petIDStrs=petID.split(";");
		for (String petIDStr : petIDStrs) {
			String[] subPetIDStr=petIDStr.split(":");
			petIDMap.put(Integer.parseInt(subPetIDStr[0]) , Integer.parseInt(subPetIDStr[1]));
		}
		
		String[] addProStrs=addPro.split(";");
		for (String addProStr : addProStrs) {
			String[] subAddProStr=addProStr.split(":");
			addProMap.put(Integer.parseInt(subAddProStr[0]) , Integer.parseInt(subAddProStr[1]));
		}
		
	}

}