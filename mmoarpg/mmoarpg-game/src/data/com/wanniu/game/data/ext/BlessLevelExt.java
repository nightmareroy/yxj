package com.wanniu.game.data.ext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.data.BlessBuffCO;
import com.wanniu.game.data.BlessLevelCO;
import com.wanniu.game.data.GameData;

public class BlessLevelExt extends BlessLevelCO{
	public Map<PlayerBtlData, Integer> bufsAttr = new HashMap<>();
	public List<Integer> buffList;
	public void initProperty(){
		super.initProperty();
		buffList = new ArrayList<Integer>();
	    String[] strArr = super.blessBuff.split(",");
	    for(int i = 0; i < strArr.length; ++i){
	    	int id = Integer.parseInt(strArr[i]);
	        buffList.add(id);
	        BlessBuffCO prop = GameData.BlessBuffs.get(id);
	        if(prop==null){
	        	Out.error("找不到工会buffId");
	        }
	        else{
	        	PlayerBtlData pbd = PlayerBtlData.getE(prop.buffAttribute1);
	        	if(pbd==null){
	        		Out.error("找不到工会buff类型");
	        	}
	        	else{
	        		bufsAttr.put(pbd, prop.buffValue1);
	        	}
	        }
	    }
	    
	    
	    
	};
}
