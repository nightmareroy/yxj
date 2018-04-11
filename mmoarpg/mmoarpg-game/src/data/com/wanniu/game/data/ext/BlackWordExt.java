package com.wanniu.game.data.ext;

import com.wanniu.core.util.StringUtil;
import com.wanniu.game.data.BlackWordCO;

public class BlackWordExt extends BlackWordCO{

	@Override
	public void initProperty() {
		if (StringUtil.isNotEmpty(this.word)) {
			this.word = this.word.toLowerCase();
		}
	}
}
