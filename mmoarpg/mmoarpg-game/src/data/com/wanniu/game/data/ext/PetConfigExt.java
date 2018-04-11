package com.wanniu.game.data.ext;

import com.wanniu.game.data.PetConfigCO;

public class PetConfigExt extends PetConfigCO {

	public int intValue = 0;
	@Override
	public void initProperty() {
		if (this.paramType.trim().equals("NUMBER")) {// paramType为NUMBER才需要转，是STRING就不能转
			this.intValue = Integer.parseInt(paramValue);
		}
	}
	

}
