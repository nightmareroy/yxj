package cn.qeng.common.gm.vo;

public class GmPetVO {
	private int id;
	private String name;
	private int level;
	private int upLevel;
	private int fightPower;
	private String quality;
	private String isOut;
	private int skillNum;

	public GmPetVO() {}

	public GmPetVO(int id, String name, int level, int upLevel, int fightPower, String quality, String isOut, int skillNum) {
		this.id = id;
		this.name = name;
		this.level = level;
		this.upLevel = upLevel;
		this.fightPower = fightPower;
		this.quality = quality;
		this.isOut = isOut;
		this.skillNum = skillNum;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getUpLevel() {
		return upLevel;
	}

	public void setUpLevel(int upLevel) {
		this.upLevel = upLevel;
	}

	public int getFightPower() {
		return fightPower;
	}

	public void setFightPower(int fightPower) {
		this.fightPower = fightPower;
	}

	public String getQuality() {
		return quality;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

	public String getIsOut() {
		return isOut;
	}

	public void setIsOut(String isOut) {
		this.isOut = isOut;
	}

	public int getSkillNum() {
		return skillNum;
	}

	public void setSkillNum(int skillNum) {
		this.skillNum = skillNum;
	}
}