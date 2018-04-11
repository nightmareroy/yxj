package cn.qeng.common.gm.vo;

public class GmGuildVO {
	private String name;
	private int level;
	private String leader;

	public GmGuildVO() {

	}

	public GmGuildVO(String name, int level, String leader) {
		super();
		this.name = name;
		this.level = level;
		this.leader = leader;
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

	public String getLeader() {
		return leader;
	}

	public void setLeader(String leader) {
		this.leader = leader;
	}
}