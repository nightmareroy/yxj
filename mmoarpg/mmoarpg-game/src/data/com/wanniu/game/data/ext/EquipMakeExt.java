package com.wanniu.game.data.ext;

import java.util.ArrayList;
import java.util.List;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.ClassUtil;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.bag.WNBag.SimpleItemInfo;
import com.wanniu.game.data.EquipMakeCO;

public class EquipMakeExt extends EquipMakeCO {

	public List<SimpleItemInfo> reqMate;
//	public Map<Integer, String> targetCodeMap;
	
	@Override
	public void initProperty() {
		this.reqMate = new ArrayList<>();
//	    this.targetCodeMap = new HashMap<>();
	    for(int i = 1; i <= 3; ++i){
	    	String codeKey = "reqMateCode" + i;
	    	String countKey = "reqMateCount" + i;
			try {
				if (StringUtil.isNotEmpty((String) ClassUtil.getProperty(this, codeKey))) {
					SimpleItemInfo mateItem = new SimpleItemInfo();
					mateItem.itemCode = (String) ClassUtil.getProperty(this, codeKey);
					mateItem.itemNum = (int) ClassUtil.getProperty(this, countKey);
					this.reqMate.add(mateItem);
				}
			} catch(Exception e) {
	        	Out.error(e);
	        }
	        
	    }
//	    String[] codeArr = this.targetCode.split(";");
//	    if(codeArr.length < 5){
//	        Out.error("EquipMakeProp error, TargetCode is not match all profession");
//	    }
//	    for(int i = 0; i < codeArr.length; ++i){
//	        this.targetCodeMap.put(i + 1, codeArr[i]);
//	    }
	}

	
}
