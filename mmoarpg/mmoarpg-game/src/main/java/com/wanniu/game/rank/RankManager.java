package com.wanniu.game.rank;

import java.util.Map;

import com.wanniu.game.GWorld;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.MountPO;
import com.wanniu.game.poes.PetNewPO;
import com.wanniu.game.poes.PlayerRankInfoPO;
import com.wanniu.redis.PlayerPOManager;

/**
 * 排行榜.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class RankManager {
	// 玩家信息缓存...
	private final WNPlayer player;
	private PlayerRankInfoPO rankPO;

	public RankManager(WNPlayer player) {
		this.player = player;
		this.rankPO = RankCenter.getInstance().findRankPO(player.getId());
		if (rankPO == null) {
			this.rankPO = this.initRankPO();
			PlayerPOManager.put(ConstsTR.playerRankTR, player.getId(), rankPO);
		}
	}

	public PlayerRankInfoPO getRankPO() {
		return rankPO;
	}

	private PlayerRankInfoPO initRankPO() {
		PlayerRankInfoPO info = new PlayerRankInfoPO();
		info.setId(player.getId());
		info.setName(player.getName());
		info.setPro(player.getPro());
		info.setLevel(player.getLevel());
		info.setUpOrder(player.getUpOrder());
		info.setFightPower(player.getFightPower());

		Map<PlayerBtlData, Integer> attrs = player.btlDataManager.finalInflus;
		info.setHp(attrs.getOrDefault(PlayerBtlData.MaxHP, 0));
		info.setMag(attrs.getOrDefault(PlayerBtlData.Mag, 0));
		info.setPhy(attrs.getOrDefault(PlayerBtlData.Phy, 0));

		info.setXianyuan(player.moneyManager.getXianYuan());
		info.setDemonTower(player.demonTowerManager.getMaxFloor());


		// 坐骑
		MountPO mount = player.mountManager.mount;
		info.setMountFightPower(mount == null ? 0 : mount.fightPower);
		info.setMountSkinId(mount == null ? 0 : mount.usingSkinId);

		// 宠物

		PetNewPO petMax = null;
		int petFightPowerMax = 0;
		for (PetNewPO pet : player.petNewManager.petsPO.pets.values()) {
			int petFightPower = pet.fightPower;
			if (petFightPower > petFightPowerMax) {
				petFightPowerMax = petFightPower;
				petMax = pet;
			}
		}
		if (petMax != null) {
			info.setPetId(petMax.id);
			info.setPetName(petMax.name);
			info.setPetFightPower(petMax.fightPower);
		} else {
			info.setPetId(1);
			info.setPetName("");
			info.setPetFightPower(1);
		}
		return info;
	}

	/**
	 * 处理排行榜事件.
	 */
	public void onEvent(RankType type, Object... value) {
		if (player.isRobot()) {
			return;
		}
		GWorld.getInstance().ansycExec(() -> type.getHandler().handle(player, value));
	}

	/**
	 * 处理改名问题.
	 */
	public void onChangeName() {
		rankPO.setName(player.getName());
	}
}