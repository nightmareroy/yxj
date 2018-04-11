package com.wanniu.game.data.ext;

import com.wanniu.game.data.RecoveryCO;

public class RecoveryExt extends RecoveryCO {
	public int minLevel;
	public int maxLevel;

	@Override
	public void initProperty() {
		String[] ls = this.level.split(":");
		this.minLevel = Integer.parseInt(ls[0]);
		this.maxLevel = Integer.parseInt(ls[1]);
	}
}