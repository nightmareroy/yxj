package com.wanniu.game.poes;

import java.util.Map;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBTable;
import com.wanniu.game.playerSkill.po.SkillDB;

@DBTable("player_skill")
public final class SkillsPO extends GEntity {

	public Map<Integer, SkillDB> skills;

	public Map<Integer, Integer> skillKeys;

	public int openCount;
	
	public Map<Integer, SkillDB> talentSkills;

	public SkillsPO () {
		
	}

	/**
	 * 可用天赋点数
	 */
	public int talentPoint=0;
}
