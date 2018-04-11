package cn.qeng.common.gm.vo;

public class GmDaoYouVO {
	private String roleName;
	private int level;
	private int power;
	private String pro;
	private String isCreator;
	private String todayRebate;
	private String sumRebate;

	public GmDaoYouVO() {}

	public GmDaoYouVO(String roleName, int level, int power, String pro, String isCreator, String todayRebate, String sumRebate) {
		this.roleName = roleName;
		this.level = level;
		this.power = power;
		this.pro = pro;
		this.isCreator = isCreator;
		this.todayRebate = todayRebate;
		this.sumRebate = sumRebate;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public String getPro() {
		return pro;
	}

	public void setPro(String pro) {
		this.pro = pro;
	}

	public String getIsCreator() {
		return isCreator;
	}

	public void setIsCreator(String isCreator) {
		this.isCreator = isCreator;
	}

	public String getTodayRebate() {
		return todayRebate;
	}

	public void setTodayRebate(String todayRebate) {
		this.todayRebate = todayRebate;
	}

	public String getSumRebate() {
		return sumRebate;
	}

	public void setSumRebate(String sumRebate) {
		this.sumRebate = sumRebate;
	}
}