package cn.qeng.common.gm.vo;

public class GmGuildResult extends GmResult<GmGuildMemberVO> {
	private String id;
	private int level;
	private String notice;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getNotice() {
		return notice;
	}

	public void setNotice(String notice) {
		this.notice = notice;
	}
}