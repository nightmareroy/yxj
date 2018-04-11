package cn.qeng.common.gm.po;

import java.util.Date;

public class CdkCode {
	private String code;
	private int useNum;
	private Date useDate;
	private String usePlayerId;
	private int useChannel;

	public CdkCode() {

	}

	public CdkCode(String code, int useNum) {
		super();
		this.code = code;
		this.useNum = useNum;
	}

	public void useCdk(Date useDate, String usePlayerId, int useChannel) {
		this.useDate = useDate;
		this.usePlayerId = usePlayerId;
		this.useChannel = useChannel;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getUseNum() {
		return useNum;
	}

	public void setUseNum(int useNum) {
		this.useNum = useNum;
	}

	public Date getUseDate() {
		return useDate;
	}

	public void setUseDate(Date useDate) {
		this.useDate = useDate;
	}

	public String getUsePlayerId() {
		return usePlayerId;
	}

	public void setUsePlayerId(String usePlayerId) {
		this.usePlayerId = usePlayerId;
	}

	public int getUseChannel() {
		return useChannel;
	}

	public void setUseChannel(int useChannel) {
		this.useChannel = useChannel;
	}
}
