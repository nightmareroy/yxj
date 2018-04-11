package com.wanniu.game.poes;

import java.util.Date;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBField;

/**
 * 大逃杀系统信息
 * 
 * @author lxm
 *
 */
public class FleeSystemPO extends GEntity {
	@DBField(isPKey = true, fieldType = "varchar", size = 50)
	public String id;

	/** 赛季结束时间 */
	public Date seasonEndTime = new Date();

	public FleeSystemPO(String logicServerId) {
		this.id = logicServerId;
	}

	public FleeSystemPO() {

	}
}
