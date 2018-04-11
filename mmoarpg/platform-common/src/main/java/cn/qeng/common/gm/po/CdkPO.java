package cn.qeng.common.gm.po;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CdkPO {

	private String code;
	private Date createDate = new Date();
	private String name;
	private int num;
	private Date beginDate;
	private Date endDate;
	private int channel;
	private int maxUseCount;
	private List<CdkItem> items;
	private List<CdkCode> cdkCodes;
	private int minLevel;
	private List<Integer> serverIds = new ArrayList<>();
	/**
	 * 0-普通，一个只能用一次。1-单个通用次数无限
	 */
	private int cdkType;

	public CdkPO() {}

	public CdkPO(String code, String name, int num, Date beginDate, Date endDate, int channel, int maxUseCount, List<CdkItem> items, List<CdkCode> cdkCodes, int minLevel, int cdkType) {
		this.code = code;
		this.name = name;
		this.num = num;
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.channel = channel;
		this.maxUseCount = maxUseCount;
		this.items = items;
		this.cdkCodes = cdkCodes;
		this.minLevel = minLevel;
		this.cdkType = cdkType;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public int getMaxUseCount() {
		return maxUseCount;
	}

	public void setMaxUseCount(int maxUseCount) {
		this.maxUseCount = maxUseCount;
	}

	public List<CdkItem> getItems() {
		return items;
	}

	public void setItems(List<CdkItem> items) {
		this.items = items;
	}

	public List<CdkCode> getCdkCodes() {
		return cdkCodes;
	}

	public void setCdkCodes(List<CdkCode> cdkCodes) {
		this.cdkCodes = cdkCodes;
	}

	public int getMinLevel() {
		return minLevel;
	}

	public void setMinLevel(int minLevel) {
		this.minLevel = minLevel;
	}

	public List<Integer> getServerIds() {
		return serverIds;
	}

	public void setServerIds(List<Integer> serverIds) {
		this.serverIds = serverIds;
	}

	public int getCdkType() {
		return cdkType;
	}

	public void setCdkType(int cdkType) {
		this.cdkType = cdkType;
	}
}