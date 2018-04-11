package com.wanniu.game.common;

/**
 * redis表对应的key
 * 
 * key: 表名/唯一ID/对象
 * 
 * @author Yangzz
 *
 */
public enum ConstsTR implements Table {

	/** player */
	playerTR(player), playerBaseTR(player_base), playerAttachTR(player_attach), playerTempTR(player_temp_data), playerTitleTR(player_title), shopMallTR(player_shop_mall), intergalMallTR(player_intergal_mall), // 积分商城
	taskTR(player_tasks), skillTR(player_skill), activityTR(player_activity), mountTR(player_mount), achievementTR(player_achieves), playerPetTR(player_pets), pkRuleTR(player_pk_data), bagTR(player_bags), player_signTR(player_sign), player_consignmentTR(player_consignment), // 玩家相关
	consignment_itemsTR("consignment_items"), player_vipTR(player_vip), player_solo_dataTR(player_solo_data), player_arena_dataTR(player_arena_data), player_func_openTR(player_func_open), player_fightlevelTR(player_fightlevel), monster_drop_infoTR(monster_drop_info), player_friendsTR(player_friends), player_chourenTR(player_chouren), player_leaderboardTR(player_leaderboard), // 世界等级/膜拜
	player_blood("player_blood"),

	// 日常活动
	player_dailyTR(player_daily),
	// 果园
	player_farmTR(player_farm),
	// 挂机设置
	hookSetTR(player_hookset), onlineGiftTR(player_online_data), playerRecentChatTR(player_recent_chat),

	playerBtlData("player_btl_data"), playerIdsTR("playerIdsTR"), intergalMallGlobalTR("global_intergal_mall"), // 积分商城-全服限购数据

	// 交互
	player_interactTR("player_interact"), player_interact_numTR("player_interact_num"),

	playerRankTR(player_rank_info),
	// 充值活动
	playerRechargeActivityTR("player_recharge_activity"),

	// 大乱斗（五岳一战）系统配置
	arenaSystemTR("arenaSystemTR"),
	// solo问道大会系统配置
	soloSystemTR("soloSystemTR"),
	// 大逃杀系统配置
	fleeSystemTR("fleeSystemTR"),
	// 排行榜
	rankTR("rank"),
	// 好友榜
	friendRankTR("friendRank"),
	// 赛季排行榜
	seasonRankTR("seasonRank"),

	// guild
	playerGuildTR(player_guild), guildTR("guild"), guildMemberTR("guild_member"), guildApplyTR("guild_apply"), //
	guildNewsTR("guild_news"), // 仙盟操作日志
	guildAuctionLogTR("guild_auction_log"), // 仙盟竞拍日志
	guildImpeachTR("guild_impeach"), guildDepotTR("guild_depot"), guildBlessTR("guild_bless"), guildDungeonTR("guild_dungeon"), playerGuildTechTR("player_guild_tech"), guildBossHurtTR("guild_boss_rank"), guildBossTR("guild_boss"), redpointTR("red_point"), guildFortTR("guild_fort"), guildFortReportTR("guild_fort_report"),
	// mail
	mailTR(player_mails), serverMailsTR("servermails"),

	// 成长基金
	fundsTR("funds"),

	feeOrder("feeOrder"), prepaidNewTR("prepaidNewTR"),

	// 道友
	daoYouTR("dao_you"), daoYouMemberTR("dao_you_member"),

	// 仙缘
	xianYuanTR(player_xianyuan),

	// 5v5
	five2FiveTR(player_five2five), five2FiveBtlTR("five2FiveBtl"), five2FiveShardBtlReportTR("five2FiveShardBtlReport"), fivie2FiveSystemTR("five2FiveSystem"),

	chat_item_tr("chat_item_tr"),

	// 全局CDK
	CDK("cdk"),
	// 全局CDK分布式锁
	CDK_LOCK("cdk_lock"),
	// 滚动公告
	notice("notice"),
	// DAILY_DEMON_TOWER_COUNT("DAILY_DEMON_TOWER_COUNT"),
	PLAYER_DEMON_TOWER("PLAYER_DEMON_TOWER"), DAILY_RELIVE("DAILY_RELIVE"),
	// 大逃杀
	player_fleeTR(player_flee), NAME_MODULE("NAME_MODULE"),
	// 首次登录记录（全局Redis）
	FIRST_LOGIN("first_login"),
	// 竟拍存在Key
	auction_itemsTR("auction_itemsTR"),
	// 红包
	RedPacket("RedPacket"),
	// 大富翁
	Rich("Rich"),
	// 七日目标
	SevenGoal("SevenGoal"),
	// 镇妖塔
	DemonTower("DemonTower"),
	// 仙盟BossPO
	GuildBossTR("guild_boss");

	public String value;

	private ConstsTR(String tr) {
		this.value = tr;
	}
}
