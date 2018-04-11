package com.wanniu.vo;

/**
 * 全局邮件记录
 * 
 * @author lxm
 *
 */
public class GlobalMailPo {
	private String id;
	private String createDate;
	private String servers;
	private String content;

	public GlobalMailPo() {

	}

	public GlobalMailPo(String id, String createDate, String servers, String content) {
		super();
		this.id = id;
		this.createDate = createDate;
		this.servers = servers;
		this.content = content;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getServers() {
		return servers;
	}

	public void setServers(String servers) {
		this.servers = servers;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

}
