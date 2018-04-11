package cn.qeng.common.gm.vo;

public class GmGuildMemberVO {
	private String roleName;
	private int level;
	private int power;
	private String duty;// 职务
	private long contribute;
	private String state;
	private String pro;
	private int job;// 职务ID

	public GmGuildMemberVO() {

	}

	public GmGuildMemberVO(String roleName, int level, int power, String duty, long contribute, String state, String pro, int job) {
		super();
		this.roleName = roleName;
		this.level = level;
		this.power = power;
		this.duty = duty;
		this.contribute = contribute;
		this.state = state;
		this.pro = pro;
		this.job = job;
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

	public String getDuty() {
		return duty;
	}

	public void setDuty(String duty) {
		this.duty = duty;
	}

	public long getContribute() {
		return contribute;
	}

	public void setContribute(long contribute) {
		this.contribute = contribute;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPro() {
		return pro;
	}

	public void setPro(String pro) {
		this.pro = pro;
	}

	public int getJob() {
		return job;
	}

	public void setJob(int job) {
		this.job = job;
	}
}