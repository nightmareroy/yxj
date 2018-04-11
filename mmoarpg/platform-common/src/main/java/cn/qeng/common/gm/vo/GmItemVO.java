package cn.qeng.common.gm.vo;

public class GmItemVO {
	private String id;
	private String itemId;
	private String itemName;
	private int itemNum;
	private String isEquip;

	private String level;
	private String baseAttr;
	private String gemAttr;
	private String extAttr;
	private String legendAttr;

	public GmItemVO() {}

	public GmItemVO(String id, String itemId, String itemName, int itemNum, String isEquip) {
		this.id = id;
		this.itemId = itemId;
		this.itemName = itemName;
		this.itemNum = itemNum;
		this.isEquip = isEquip;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public int getItemNum() {
		return itemNum;
	}

	public void setItemNum(int itemNum) {
		this.itemNum = itemNum;
	}

	public String getIsEquip() {
		return isEquip;
	}

	public void setIsEquip(String isEquip) {
		this.isEquip = isEquip;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getBaseAttr() {
		return baseAttr;
	}

	public void setBaseAttr(String baseAttr) {
		this.baseAttr = baseAttr;
	}

	public String getGemAttr() {
		return gemAttr;
	}

	public void setGemAttr(String gemAttr) {
		this.gemAttr = gemAttr;
	}

	public String getExtAttr() {
		return extAttr;
	}

	public void setExtAttr(String extAttr) {
		this.extAttr = extAttr;
	}

	public String getLegendAttr() {
		return legendAttr;
	}

	public void setLegendAttr(String legendAttr) {
		this.legendAttr = legendAttr;
	}
}