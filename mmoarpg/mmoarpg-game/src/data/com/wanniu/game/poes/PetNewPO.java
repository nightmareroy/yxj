package com.wanniu.game.poes;

import java.util.HashMap;

import com.wanniu.game.DBField;
import com.wanniu.game.petNew.PetSkill;

public class PetNewPO {

	@DBField(isPKey=true,fieldType="int")
	public int id;
	public String name;
	public int level;
	public long exp;
	public int upLevel;
	public int fightPower;
	
	public HashMap<Integer, PetSkill> skills;
	
	public HashMap<Integer, PetSkill> passiveSkills;
}
