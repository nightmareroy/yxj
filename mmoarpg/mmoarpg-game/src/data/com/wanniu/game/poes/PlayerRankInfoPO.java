package com.wanniu.game.poes;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Table;
import com.wanniu.game.guild.GuildServiceCenter;

/**
 * 玩家排行展示信息.
 * <p>
 * 这个PO都是冗余值
 *
 * @author 小流氓(176543888@qq.com)
 */
@DBTable(Table.player_rank_info)
public class PlayerRankInfoPO extends GEntity {

	private String id;
	private String name;
	/** 职业ID */
	private int pro;
	private int level;
	private int fightPower;
	/**
	 * 境界编号
	 */
	private int upOrder;

	private int hp;
	private int phy;
	private int mag;
	// 仙缘值
	private int xianyuan;
	// 镇妖塔层数
	private int demonTower;

	// 坐骑战斗力
	private int mountFightPower;
	// 坐骑皮肤
	private int mountSkinId;

	// 宠物模板ID
	private int petId;
	// 宠物名称
	private String petName;
	// 宠物战力
	private int petFightPower;


	public PlayerRankInfoPO() {}

	public void setId(String id) {
		this.id = id;
	}

	public String getGuildName() {
		GuildPO guildPO=GuildServiceCenter.getInstance().getGuildByMemberId(id);
		if(guildPO!=null) {
			return guildPO.name;
		}
		else {
			return "";
		}
		
	}



	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPro() {
		return pro;
	}

	public void setPro(int pro) {
		this.pro = pro;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getFightPower() {
		return fightPower;
	}

	public void setFightPower(int fightPower) {
		this.fightPower = fightPower;
	}

	public int getUpOrder() {
		return upOrder;
	}

	public void setUpOrder(int upOrder) {
		this.upOrder = upOrder;
	}

	public String getId() {
		return id;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getPhy() {
		return phy;
	}

	public void setPhy(int phy) {
		this.phy = phy;
	}

	public int getMag() {
		return mag;
	}

	public void setMag(int mag) {
		this.mag = mag;
	}

	public int getXianyuan() {
		return xianyuan;
	}

	public void setXianyuan(int xianyuan) {
		this.xianyuan = xianyuan;
	}

	public int getDemonTower() {
		return demonTower;
	}

	public void setDemonTower(int demonTower) {
		this.demonTower = demonTower;
	}

	public int getMountFightPower() {
		return mountFightPower;
	}

	public void setMountFightPower(int mountFightPower) {
		this.mountFightPower = mountFightPower;
	}

	public int getMountSkinId() {
		return mountSkinId;
	}

	public void setMountSkinId(int mountSkinId) {
		this.mountSkinId = mountSkinId;
	}

	public int getPetId() {
		return petId;
	}

	public void setPetId(int petId) {
		this.petId = petId;
	}

	public String getPetName() {
		return petName;
	}

	public void setPetName(String petName) {
		this.petName = petName;
	}

	public int getPetFightPower() {
		return petFightPower;
	}

	public void setPetFightPower(int petFightPower) {
		this.petFightPower = petFightPower;
	}
}