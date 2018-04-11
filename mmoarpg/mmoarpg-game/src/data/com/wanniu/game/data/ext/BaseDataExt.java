package com.wanniu.game.data.ext;

import java.util.ArrayList;
import java.util.List;

import com.wanniu.core.common.IntIntPair;
import com.wanniu.game.data.BaseDataCO;

public class BaseDataExt extends BaseDataCO {
	
	
	private IntIntPair[] _initSkills;
	public List<String> list_ExpCode=new ArrayList<>();
	@Override
	public void initProperty() {
		List<IntIntPair> sl = new ArrayList<>();
		String[] skillsInfo = this.initSkill.split("\\|");
		for (String skillInfo : skillsInfo) {
			String[] params = skillInfo.split(":");
			if (params.length > 1) {
				IntIntPair pair = new IntIntPair();
				pair.first = Integer.parseInt(params[0]);
				pair.second = Integer.parseInt(params[1]);
				sl.add(pair);
			}
		}
		_initSkills = new IntIntPair[sl.size()];
		_initSkills = sl.toArray(_initSkills);
		
		String[] _expCode = expCode.split(",");
		for(String par:_expCode){
			list_ExpCode.add(par);
		}
		
	}
	
	public IntIntPair[] getInitSkills() {
		return _initSkills;
	}

	public String getModelStar(int level) {
//		switch (level) {
//		case 1:
//			return modelStar1;
//		case 2:
//			return modelStar2;
//		case 3:
//			return modelStar3;
//		case 4:
//			return modelStar4;
//		case 5:
//			return modelStar5;
//		}
		return null;
	}

	public int getModelStarPercent(int level) {
//		switch (level) {
//		case 1:
//			return modelStar1Percent;
//		case 2:
//			return modelStar2Percent;
//		case 3:
//			return modelStar3Percent;
//		case 4:
//			return modelStar4Percent;
//		case 5:
//			return modelStar5Percent;
//		}
		return 0;
	}
	
	public int getModelStarScenePercent(int level) {
//		switch (level) {
//		case 1:
//			return modelStar1ScenePercent;
//		case 2:
//			return modelStar2ScenePercent;
//		case 3:
//			return modelStar3ScenePercent;
//		case 4:
//			return modelStar4ScenePercent;
//		case 5:
//			return modelStar5ScenePercent;
//		}
		return 0;
	}
}
