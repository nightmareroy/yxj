package cn.qeng.common.gm.vo;

/**
 * GM查询玩家信息VO.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class GmPlayerInfoVO {
	private String id;
	private String username;
	private String name;
	private int level;
	private String exp;
	private int diamond;
	private int gold;
	private String vip;
	private String loginTime;
	private String pro;
	private String isOnline;
	private String logoutTime;
	private String roleState;
	private String classExp;
	private int ticket;
	private String guildName;
	private String daoyouName;
	private int fightPower;

	public GmPlayerInfoVO() {

	}

	public GmPlayerInfoVO(String id, String username, String name, int level, String exp, int diamond, int gold, String vip, String loginTime, String pro, String isOnline, String logoutTime, String roleState, String classExp, int ticket, String guildName, String daoyouName, int fightPower) {
		super();
		this.id = id;
		this.username = username;
		this.name = name;
		this.level = level;
		this.exp = exp;
		this.diamond = diamond;
		this.gold = gold;
		this.vip = vip;
		this.loginTime = loginTime;
		this.pro = pro;
		this.isOnline = isOnline;
		this.logoutTime = logoutTime;
		this.roleState = roleState;
		this.classExp = classExp;
		this.ticket = ticket;
		this.guildName = guildName;
		this.daoyouName = daoyouName;
		this.fightPower = fightPower;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getExp() {
		return exp;
	}

	public void setExp(String exp) {
		this.exp = exp;
	}

	public int getDiamond() {
		return diamond;
	}

	public void setDiamond(int diamond) {
		this.diamond = diamond;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public String getVip() {
		return vip;
	}

	public void setVip(String vip) {
		this.vip = vip;
	}

	public String getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
	}

	public String getPro() {
		return pro;
	}

	public void setPro(String pro) {
		this.pro = pro;
	}

	public String getIsOnline() {
		return isOnline;
	}

	public void setIsOnline(String isOnline) {
		this.isOnline = isOnline;
	}

	public String getLogoutTime() {
		return logoutTime;
	}

	public void setLogoutTime(String logoutTime) {
		this.logoutTime = logoutTime;
	}

	public String getRoleState() {
		return roleState;
	}

	public void setRoleState(String roleState) {
		this.roleState = roleState;
	}

	public String getClassExp() {
		return classExp;
	}

	public void setClassExp(String classExp) {
		this.classExp = classExp;
	}

	public int getTicket() {
		return ticket;
	}

	public void setTicket(int ticket) {
		this.ticket = ticket;
	}

	public String getGuildName() {
		return guildName;
	}

	public void setGuildName(String guildName) {
		this.guildName = guildName;
	}

	public String getDaoyouName() {
		return daoyouName;
	}

	public void setDaoyouName(String daoyouName) {
		this.daoyouName = daoyouName;
	}

	public int getFightPower() {
		return fightPower;
	}

	public void setFightPower(int fightPower) {
		this.fightPower = fightPower;
	}
}