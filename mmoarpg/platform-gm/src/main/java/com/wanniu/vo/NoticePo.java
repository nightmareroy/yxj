package com.wanniu.vo;

/**
 * 滚动公告
 */
public class NoticePo {
	private String id;
	private String name;
	private String content;
	private String beginDate;
	private String endDate;
	private int intervalMinute;// 间隔分钟
	private int sendedNum;// 已发次数
	private String servers;// 要发送的服务器id。用,分割
	private String createDate;

	public NoticePo() {

	}

	public NoticePo(String id, String name, String content, String beginDate, String endDate, int intervalMinute,
			int sendedNum, String servers, String createDate) {
		super();
		this.id = id;
		this.name = name;
		this.content = content;
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.intervalMinute = intervalMinute;
		this.sendedNum = sendedNum;
		this.servers = servers;
		this.createDate = createDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public String getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public int getIntervalMinute() {
		return intervalMinute;
	}

	public void setIntervalMinute(int intervalMinute) {
		this.intervalMinute = intervalMinute;
	}

	public int getSendedNum() {
		return sendedNum;
	}

	public void setSendedNum(int sendedNum) {
		this.sendedNum = sendedNum;
	}

	public String getServers() {
		return servers;
	}

	public void setServers(String servers) {
		this.servers = servers;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

}
