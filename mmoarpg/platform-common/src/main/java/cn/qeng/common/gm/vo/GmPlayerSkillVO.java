package cn.qeng.common.gm.vo;

public class GmPlayerSkillVO {
	private String skillName;
	private int skillLevel;

	public GmPlayerSkillVO() {}

	public GmPlayerSkillVO(String skillName, int skillLevel) {
		this.skillName = skillName;
		this.skillLevel = skillLevel;
	}

	public String getSkillName() {
		return skillName;
	}

	public void setSkillName(String skillName) {
		this.skillName = skillName;
	}

	public int getSkillLevel() {
		return skillLevel;
	}

	public void setSkillLevel(int skillLevel) {
		this.skillLevel = skillLevel;
	}
}