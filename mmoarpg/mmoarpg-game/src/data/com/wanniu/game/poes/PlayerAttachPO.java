package com.wanniu.game.poes;

import java.util.List;
import java.util.Map;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Table;
import com.wanniu.game.guild.guildFort.dao.GuildFortAwardPO;
import com.wanniu.game.item.po.MedalPO;
import com.wanniu.game.player.po.MiscData;

@DBTable(Table.player_attach)
public class PlayerAttachPO extends GEntity {

	/** 系统设置 */
	public int sysSet;

	/*********************************************************************
	 * miscData
	 */
	public MiscData miscData = new MiscData(); // 套装属性 Map<Integer, Integer>

	public int guildBlessToday;

	public int guildSkillUpToday;

	public int guildDonateToday;

	/** 爵位 声望 勋章 */
	public MedalPO medal;

	/** drama_guide_move等配置 */
	public Map<String, String> config;

	public List<Integer> firstKillMonsterIds;

	public Map<Integer, Object> sceneProgress;

	public VipPO vipData;
	
	public int KillBossCount;
	
//	public int fetchRedPacketCount;
	
	/**存储据点战每日奖励的领取状况 */
	public GuildFortAwardPO guildFortDailyAwards;

	public PlayerAttachPO() {
		KillBossCount=0;
//		fetchRedPacketCount=0;
	}
	
	public void addFirstMonsterId(int monsterId) {
		firstKillMonsterIds.add(monsterId);
	}

}
