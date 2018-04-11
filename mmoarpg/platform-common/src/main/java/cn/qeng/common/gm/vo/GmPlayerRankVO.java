package cn.qeng.common.gm.vo;

/**
 * GM个人排行榜
 * 
 * @author lxm
 */
public class GmPlayerRankVO {
	private String type;
	private String name;
	private String rank;

	public GmPlayerRankVO() {}

	public GmPlayerRankVO(String type, String name, String rank) {
		this.type = type;
		this.name = name;
		this.rank = rank;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}
}