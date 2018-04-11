package cn.qeng.common.gm.vo;

public class GmMountVO {
	private String name;
	private String quality;
	private int star;
	private String attr;

	public GmMountVO() {}

	public GmMountVO(String name, String quality, int star, String attr) {
		this.name = name;
		this.quality = quality;
		this.star = star;
		this.attr = attr;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQuality() {
		return quality;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

	public int getStar() {
		return star;
	}

	public void setStar(int star) {
		this.star = star;
	}

	public String getAttr() {
		return attr;
	}

	public void setAttr(String attr) {
		this.attr = attr;
	}
}