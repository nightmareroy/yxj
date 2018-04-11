package cn.qeng.common.gm.po;

/**
 * 登录公告
 */
public class AnnouncementPO {
	private String name;
	private String content;
	private boolean isUse;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean getIsUse() {
		return isUse;
	}

	public void setIsUse(boolean isUse) {
		this.isUse = isUse;
	}
}