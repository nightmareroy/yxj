package com.wanniu.game.playerSkill.po;

public class SkillDB {

	public int lv;
	public int id;
	/**
	 * 技能是否解锁
	 */
	public int flag;
	public long skillTime;
	public int extLv;

	public int pos;
	public int talentLv;
	public boolean isTalent;
	public int replaceSkillId;

	// private SkillDataExt prop;

	public SkillDB() {

	}

	public SkillDB(int lv, int id, int flag, int skillTime, int extLv, int pos) {
		this.lv = lv;
		this.id = id;
		this.flag = flag;
		this.skillTime = skillTime;
		this.extLv = extLv;
		this.pos = pos;
		// prop = GameData.findSkillDatas((t)->t.skillID == id).get(0);
	}

	// public SkillDataExt prop(){
	// return prop;
	// }
}
