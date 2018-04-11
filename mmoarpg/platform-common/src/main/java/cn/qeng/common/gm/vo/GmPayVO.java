package cn.qeng.common.gm.vo;

/**
 * 游戏区与GM工具交互的VO
 * 
 * @author 小流氓(176543888@qq.com)
 */
public class GmPayVO {
	private String account;
	private String name;
	private String payDate;
	private int money;
	private String isCard;

	public GmPayVO() {

	}

	public GmPayVO(String account, String name, String payDate, int money, String isCard) {
		super();
		this.account = account;
		this.name = name;
		this.payDate = payDate;
		this.money = money;
		this.isCard = isCard;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPayDate() {
		return payDate;
	}

	public void setPayDate(String payDate) {
		this.payDate = payDate;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public String getIsCard() {
		return isCard;
	}

	public void setIsCard(String isCard) {
		this.isCard = isCard;
	}
}