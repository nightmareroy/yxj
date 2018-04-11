package com.wanniu.game.common;

import java.util.List;

import com.wanniu.game.common.Const.ManagerType;
import com.wanniu.game.common.Const.PlayerEventType;

import pomelo.area.PlayerHandler.SuperScriptType;

public abstract class ModuleManager {

	public abstract void onPlayerEvent(PlayerEventType eventType);

	/**
	 * 红点信息
	 * 
	 * @return
	 */
	public List<SuperScriptType> getSuperScript() {
		return null;
	}

	public abstract ManagerType getManagerType();
}
