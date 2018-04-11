package cn.qeng.common.gm.vo;

public class GmManagerVO {
	private String id;
	private String roleName;
	private String freezeTime;// 冻结恢复时间
	private String forbidTime;// 禁言恢复时间
	private String forbidTalkReason;// 禁言原因
	private String freezeReason;// 冻结原因

	public GmManagerVO() {}

	public GmManagerVO(String id, String roleName, String freezeTime, String forbidTime, String freezeReason, String forbidTalkReason) {
		this.id = id;
		this.roleName = roleName;
		this.freezeTime = freezeTime;
		this.forbidTime = forbidTime;
		this.freezeReason = freezeReason;
		this.forbidTalkReason = forbidTalkReason;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getFreezeTime() {
		return freezeTime;
	}

	public void setFreezeTime(String freezeTime) {
		this.freezeTime = freezeTime;
	}

	public String getForbidTime() {
		return forbidTime;
	}

	public void setForbidTime(String forbidTime) {
		this.forbidTime = forbidTime;
	}

	public String getForbidTalkReason() {
		return forbidTalkReason;
	}

	public void setForbidTalkReason(String forbidTalkReason) {
		this.forbidTalkReason = forbidTalkReason;
	}

	public String getFreezeReason() {
		return freezeReason;
	}

	public void setFreezeReason(String freezeReason) {
		this.freezeReason = freezeReason;
	}
}