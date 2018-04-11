package com.wanniu.game.common;

import java.util.List;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DateUtil;
import com.wanniu.core.util.StringUtil;

/**
 * 通用属性定义
 * 
 * @author Yangzz
 *
 */
public class Const {

	/** 一条龙 一轮任务数量 */
	public static final int LOOP_TASK_ROUND_COUNT = 5;
	/** 一条龙 一轮任务一轮结束以后机器人等待时间 */
	public static final long LOOP_TASK_ROBOT_WAIT_LEAVE_TIME = 5 * DateUtil.ONE_MINUTE_MILLS;
	/** 一条龙任务队伍最低人数要求 */
	public static final int LOOP_TASK_TEAM_MEMBER_COUNT = 3;
	/** 一条龙可队长可加成的任务次数 */
	public static final int LOOP_LEADER_ADD = 10;
	/** 师门一轮任务数量 */
	public static final int DAILY_TASK_ROUND_COUNT = 10;
	/** 师门当日总数 */
	public static final int DAILY_TASK_DAY_COUNT = 20;
	/** 一条龙副本死亡复活时间 */
	public static final int LOOP_DUNGEON_REVIVE_TIME = 5;
	/** 地图传送的时候用的固定 一条龙场景ID */
	public static final int LOOP_SPECIAL_AREA_ID = 8888;
	/** 资源副本-倒计时事件 */
	public static final long RESOURCE_COUNTDOWN = 5 * 60 * 1000;

	public static final int RELIVE_DIAMOND = 5;
	public static final int RELIVE_NUM = 3;

	public static final int FB_PRODUCE_COUNT = 3;

	/** 每日自动发送邮件上线 */
	public static final int AUTO_PICKUP_LIMIT = 50;

	public static final int REFRSH_NEW_DAY_TIME = 5; // 每天5点刷新

	public static class NUMBER_MAX {
		public static final long INT_BIG = Long.MAX_VALUE;
		public static final int INT = Integer.MAX_VALUE;
		public static final short INT_SMALL = Short.MAX_VALUE;

		public static final int SAME_SCREEN_NUM = 30; // 同屏人数最大值
		public static final int GUILD_FINISHED_MAX = 3; // 公会祈福完成段最大值
	}

	public static class CODE {
		public static final int OK = 200;
		public static final int FAIL = 500;
		public static final int KICK = 400;
	}

	public static enum SUPERSCRIPT_TYPE {
		MAIL(100), // 邮件
		ACTIVITY_CENTER(300), // 活动中心
		FLAG_ACTIVITY_ACIVITY(301), // 活动页签
		FLAG_ACTIVITY_BOSS(302), // BOSS页签
		FLAG_ACTIVITY_FATE(303), // 仙缘页签
		MASTERY_ACTIVE(400), // 专精激活
		MASTERY_RING(401), // 魔戒兑换
		SHOPMALL(500), // 商城
		ATTRIBUTE(600), // 属性
		GUILD(800), // 公会
		GUILD_PRAY(801), // 仙盟祈福
		GUILD_BOSS(802), // 公会boss
		GUILD_BOSS2(803), // 公会boss,用于首页icon推送
		GUILDFORT_INBID(804), // 报名提醒
		GUILDFORT_INBATTLE(805), // 开战提醒
		GUILDFORT_INAWARD(806), // 领取奖励提醒
		GUILD_AUCTION(810), // 工会拍卖
		WORLD_AUCTION(811), // 世界拍卖
		ALLY(900), // 盟友
		ALLY_BLESS(901), // 盟友祝福
		ALLY_REBATE(902), // 盟友返利
		JJC_ENTER(1000), // 竞技场_进入
		JJC_REWARD(1001), // 竞技场_领奖
		SOLO(1100), // 单挑王,
		SOLO_REWARD(1102), // 单挑王段位奖励,
		MEDAL(1200), // 勋章
		MOUNT(1300), // 坐骑),
		ALLY_FIGHT(1400), // 盟战),
		VIP(1500), WING(1600), // 翅膀
		SKILL(1700), // 技能
		CROSS_SERVER(1800), // 连服
		PET(1900), // 宠物
		DUNGEON(2000), // 副本
		DUNGEON_NORMAL(2001), // 普通难度
		DUNGEON_ELITE(2002), // 精英难度
		DUNGEON_HERO(2003), // 英雄难度
		VITALITY(2100), // 活跃度
		// DAILY_TASK(2200), // 日常任务
		FIRSTPAY_GIFT(2300), // 首充奖励
		DAILYPAY_GIFT(2400), // 每日充值奖励
		EQUIP_LEVEL_UP(2500), // 装备升级
		EQUIP_COLOR_UP(2600), // 装备升品
		BOSS_HOME(2800), // 世界首领
		WORLD_LEVEL(2900), // 世界等级
		TREASURE(3000), // 君王宝藏
		FLAG_OFFLINE(3100), // 离线福利

		FLAG_WELFARE(3200), // 福利活动
		FLAG_WELFARE_SIGN(3201), // 福利签到
		FLAG_WELFARE_ONLINE_GIFT(3202), // 福利在线礼包
		FLAG_WELFARE_ROLE_LV_GIFT(3203), // 福利等级礼包
		FLAG_WELFARE_ROLE_FC_GIFT(3204), // 福利战力礼包
		FLAG_WELFARE_EXCHANGE(3205), // 福利礼包兑换
		OPEN_SERVER_DAY(3206), // 开服狂欢
		TOTAL_CONSUME(3207), // 累计消费
		TOTAL_PAY(3208), // 累计充值
		FUNDS(3209), // 开服基金
		GAME_NOTICE(3210), // 游戏内公告
		LUCKY_DRAW(3211), // 幸运抽奖
		SUPER_PACKAGE(3212), // 超值礼包
		RECOVERY(3213), // 资源找回
		DAILY_RECHARGE(3214), // 每日充值
		SPRING_DRAW(3215), // 新春抽奖
		SINGLE_RECHARGE(3216), // 单笔充值
		REVELRY_RECHARGE(3217), // 冲榜累计

		CONTINUOUS_RECHARGE(3251), // 连续充值
		RICH(3252), // 大富翁
		SEVEN_GOAL(3253), // 七日目标

		FIVE_2_FIVE_REWARD(3300), // 5v5奖励
		FIVE_2_FIVE(3301), // 5v5
		GROWUP_TARGET(3400), // 成长目标
		GROWUP_TARGET_1(3401), // 成长目标 第一章节
		GROWUP_TARGET_2(3402), // 成长目标 第二章节
		GROWUP_TARGET_3(3403), GROWUP_TARGET_4(3404), GROWUP_TARGET_5(3405), GROWUP_TARGET_6(3406), GROWUP_TARGET_7(3407), GROWUP_TARGET_8(3408), GROWUP_TARGET_9(3409), GROWUP_TARGET_10(3410), GROWUP_TOTAL(3499), // 特殊，num值用来表示当前已完成的成就总数，不会减少
		WORKING(3500), // 加工
		REBORN(3501), // 洗炼
		// REFINE(3502), // 精炼
		REBUILD(3503), // 重铸
		MAKE(3504), // 打造
		KAIGUANG(3505), // 开光
		UPLEVEL(3506), // 进阶

		// EQUIP_STRENGTH(3600), // 装备
		EQUIP_STRENGTH(3601), // 强化-装备位
		EQUIP_FILL_GEM(3602), // 镶嵌宝石
		EQUIP_EQUIP(3603), // 穿装备
		FLAG_FASHION(3604), // 获取时装
		INTERGAL_MALL(3700), // 积分商店
		DEMON_INVADE_ACTIVED(4001), // 妖族入侵活动开启
		FIVE_MOUNTAIN_ACTIVED(4002), // 五岳一战活动开启
		SOLO_ACTIVED(4003), // 问道大会活动开启
		TRIAL_ACTIVIED(4004), // 试炼大赛活动开启
		ILLUSION2(4005), // 幻境2

		FARM_CULTIVATE(5001), // 果园 可以对自己进行培育操作
		FARM_FRIEND(5002), // 果园 可以对好友进行偷窃或者培育

		ACTIVITY_REVELRY(6001), // 冲榜活动开启

		LIMIT_TIME_GIFT(7001), // 限时礼包

		MIN(0);

		private SUPERSCRIPT_TYPE(int value) {
			this.value = value;
		}

		private final int value;

		public int getValue() {
			return value;
		}

		public static SUPERSCRIPT_TYPE getType(int value) {
			for (SUPERSCRIPT_TYPE superscript_type : SUPERSCRIPT_TYPE.values()) {
				if (superscript_type.value == value) {
					return superscript_type;
				}
			}
			return null;
		}
	}

	public static class PLAYER {
		public static final int initLevel = (1);
		public static final int initExp = (0);
		public static final int initSp = (5000);
		public static final int initGold = 0;// 1000000;
		public static final int initTicket = 0;// 1000000;
		public static final int initPrimaryDiamond = (0);
		public static final int initDiamond = 0;// 1000000;
		public static final int initEnergy = (1000);
		public static final int initFriendly = (0);
		public static final int initSolopoint = (0);
		public static final int initVip = (0);
		public static final int initHp = (999999);
		public static final int initMp = (999999);
		public static final int initPrestige = (0);
		public static final int initCharm = (100);
		public static final float initSpeed = (5.6f);
		public static final int maxNum = (5); // 最多角色数量
	}

	// 功能跳转界面
	public static enum FUNCTION_GOTO_TYPE {
		PREPAID(1), // 充值
		CONSIGNMENT(2), // 寄卖行
		DIAMONDSHOP(6), // 商城传送卷轴
		JIANDINGSHOP(7), // 商城鉴定卷轴,
		MATERIAL(8), // 坐骑，翅膀等培养材料
		LOUD_NOT_ENOUGH(9), // 喇叭不足，前往商城购买
		TICKET_NOT_ENOUGH(10), // 金票不足，是否立即用钻石购买？(比率1:1)
		REWARDPK_NOT_ENOUGH(11), XUESHI_NOT_ENOUGH(13), PICK_ITEM(14), // 离开副本前提示拾取道具
		TREASURE_NOT_ENOUGH(19), // 开启君王宝藏所需物品
		ACHIEVEMENT_GOLD_NOT_ENOUGH(22), // 成就领取项链
		SUMMON_ITEM_NOT_ENOUGH(23); // 召唤卷轴不足

		private final int value;

		private FUNCTION_GOTO_TYPE(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	public static enum TRANSPORT_CONDITION {
		NOTHING(0), ACCEPT_TASK(1), FINISH_TASK(2), HAS_ITEM(3), CONSUME_ITEM(4);

		public final int value;

		private TRANSPORT_CONDITION(int value) {
			this.value = value;
		}
	}

	public static enum BORN_TYPE {
		HISTORY(0), NORMAL(1), BORN(2);

		public final int value;

		private BORN_TYPE(int value) {
			this.value = value;
		}
	}

	public static enum ENTER_STATE {
		changeArea(1), // 切场景
		online(2); // 登陆游戏

		public final int value;

		private ENTER_STATE(int value) {
			this.value = value;
		}
	}

	public static enum PlayerDataType {
		POSITION(1);
		public final int value;

		private PlayerDataType(int value) {
			this.value = value;
		}
	}

	/**
	 * 战斗服过来的事件类型，如果类型是任务，那么枚举值需要和任务TaskType对应的枚举值相同；如果类型为非任务类型，那么枚举值写为>10000,这样跟任务类型分开
	 */
	public static enum EventType {
		killMonster(1), interActiveNpc(2), interActiveItem(3), collect(4), escort(5), guard(6), killBossCount(72), consumeItem(10007), changeSceneProgress(10008), changeArea(10009), summonMount(10010), rebirth(10011), addUnit(10012), // 刷怪任务
		loopTransform(10013), hurtRank(10014), hurtRank_sort(10015), statics_ranks(10016), over_statics_ranks(10017),;

		private final int value;

		private EventType(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	/**
	 * 任务状态
	 */
	public static enum TaskState {
		COMPLETED(2), COMPLETED_NOT_DELIVERY(1), NOT_COMPLETED(0), NOT_START(-1), DELETE(-2), FIAL(-3);

		private final int value;

		private TaskState(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	/**
	 * 任务类型
	 */
	public static class TaskKind {
		public static final int MAIN = (0); // 主线任务
		public static final int BRANCH = (1); // 支线任务
		public static final int DAILY = (2); // 师门任务
		// public static final int FESTIVAL = (3); // 节日任务
		public static final int LOOP = 3; // 一条龙任务
		public static final int TREASURE = 8; // 挖宝任务
	}

	/**
	 * 任务类型
	 */
	public static enum TaskType {
		KILL_MONSTER(1), // 杀怪
		INTERACT_NPC(2), // 与npc交互
		interActiveItem(3), // 与道具互动
		collect(4), // 杀怪获得道具
		escort(5), // 护送
		guard(6), // 守护npc
		reachPos(7), // 到达指定区域(幻境)
		GET_RIDESKIN(8), // 坐骑皮肤
		TRAIN_RIDE(9), // 坐骑培养
		TRAIN_EQUIP(10), // 装备强化
		FILL_GEM(11), // 宝石镶嵌
		FINISH_CLONESCENE(12), // 通关副本
		SINCOM(13), // 单挑王领奖
		EQUIP_INHERIT(14), // 装备传承
		FISH(15), // 钓鱼获得道具
		KILL_PLAYER(16), // 击杀玩家
		COMBINE_GEM(17), // 宝石合成
		TRAIN_WING(18), // 翅膀培养
		GET_ITEM(19), // 获得实体道具
		DISCARD_ITEM(20), // 扣除道具
		GET_RING(21), // 领取魔戒
		GET_WING(22), // 获得翅膀
		ADD_GUILD(23), // 加入工会
		GUILD_DONATE(24), // 公会捐献
		GOT(25), // 单挑类，通过npc传送进入秘境副本来完成任务的玩法
		FIENDS(26), // 炼魔
		STEAL(27), // 偷窃
		CHAIN_SOUL(28), // 炼魂
		GRAD_PET(29), // 抓宠
		TREASURE_HUNT(30), // 寻宝
		GETMEDAL(31), // 获得勋章 ，当角色领取一枚勋章时任务完成
		LEVEL_UP(32), // 升到某个等级，角色达到指定等级时任务完成
		OPEN_SONSIGMENT(33), // 打开寄卖行，角色打开寄卖行界面时任务完成
		SKILL_UP(34), // 升级技能
		ADD_FRIEND(35), // 添加好友
		CONCERN_FRIEND(36), // 关注好友
		EXCHANGE_FRIEND_REWARD(37), // 兑换好友奖励
		JOIN_LEAGUE(38), // 加入盟
		JOIN_LEAGUE_WAR(39), // 参加盟战
		GUILD_TECH_UP(40), // 升级公会科技
		GUILD_CONTRIBUTE(41), // 公会仓库捐献
		GUILD_PRAY(42), // 公会祈福
		JOIN_GUILD_INSTANCE(43), // 参加公会副本
		EQUIP_MELT(44), // 装备熔炼
		JOAN_ARENA(45), // 参加竞技场
		FRIEND_NUM(46), // 好友数量
		PET_TRAIN(47), // 宠物培养(没有升满一级也算)
		USERUP_LEVEL(48), // 人物进阶
		ENCHANT_EQUIP(49), // 附魔
		EARN_NECK(50), // 获得项链
		JOIN_SOLO(51), // 参加单挑王
		FINISH_DAILY_TASK(52), // 完成日常任务
		FUNC_DESK(53), // 功能界面
		TAKE_EQUIP_Qt(54), // 穿装备要求：品质等级|品质色
		MOUNT_UPLEVEL(55), // 坐骑进阶等级
		WING_UPLEVEL(56), // 翅膀进阶等级
		JOIN_DAOYOU(57), // 加入道友
		EQUIP_REBORN(58), // 装备洗练
		EQUIP_REBUILD(59), // 装备重铸
		EQUIP_REFINE(60), // 装备精炼
		ROLE_UPGRADE(61), // 角色进阶 突破
		EQUIP_MAKE(62), // 装备打造
		TRAIN_EQUIP_ALL(63), // 强化装备(所有部位强化到1段0级)
		// CALL_BOSS(64), // 召唤BOSS
		FINISH_LOOP_TASK(65), // 完成师门任务
		ACCEPT_DAILY_LOOP(66), // 每日主动推送 一条龙和师门
		ACTIVITY_NUM(67), // 一个完成活跃度的数量
		ACCEPT_DAILY(68), // 接取师门任务
		// ROLE_UPGRADE_ONE(69), // 角色进阶 突破 一次
		FIND_TREASURE(70), // 挖宝任务
		FINISH_DUNGEONS_COUNT(71), // 通关副本次数 （包含所有副本） Quantity字段对应次数需求
		KILL_BOSS_COUNT(72),// 击杀首领数量（环境和野外boss） Quantity对应需要击杀的boss数量
		;
		private final int value;

		private TaskType(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	/**
	 * tips类型
	 */
	public static enum TipsType {
		NORMAL(0), // 飘字
		BLACK(1), // 带背景的提示
		LEFTDWON(2), // 左下角
		NO_BG(3), // 不带背景的提示
		ROLL(4), // 滚动提示
		NEWTYPE(5), // 艺术字飘动
		BOTTOM(6); // 下方跑马灯

		private final int value;

		private TipsType(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	/**
	 * 通用|狂战士|刺客|魔法师|猎人|牧师
	 */
	public static enum Profession {
		all(0), canglang(1), yujian(2), yixian(3), shenjian(4), linghu(5);

		private final int value;

		private Profession(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum MailType {
		MAIL_PLAYER_TYPE(1), MAIL_SYSTEM_TYPE(2), MAIL_GM_TYPE(3);

		private final int value;

		private MailType(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum MailSysParam {
		MAIL_TIME_LIMIT_1(30000000), MAIL_TIME_LIMIT_2(70000000), MAIL_MAX_NUM(100), MAIL_MAX_WORD(2000);

		private final int value;

		private MailSysParam(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum MailReadDeal {
		MAIL_READ_NULL(1), MAIL_READ_DEL(2);

		private final int value;

		private MailReadDeal(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum MailState {
		MAIL_STATE_NULL(1), MAIL_STATE_READ(2), MAIL_STATE_ATTACH_RECEIVE(3);

		private final int value;

		private MailState(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum MailAttach {
		MAIL_ATTACH_NULL(1), MAIL_ATTACH_CARRY(2);

		private final int value;

		private MailAttach(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum MailStaus {
		MAIL_UNREAD(0), MAIL_UNRECEIVE(1);

		private final int value;

		private MailStaus(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	// public static enum MailID{ //邮件配置id
	// //公会
	// GUILD_KICK(20206), //被踢出公会
	// GUILD_JOIN_SUCCESS(20207), //公会加入成功
	// GUILD_JOIN_REFUSE(20212), //公会申请被拒绝
	// GUILD_JOB_CHANGE(20213), //公会职位变更
	// GUILD_DOUNGEON_REWARD(20216), //公会副本奖励
	// GUILD_INVITE_REFUSED(20226), //邀请入会被拒绝
	// GUILD_INVITE_SUCCESS(20227), //邀请成功
	// //寄卖行
	// CONSIGN_SALE_SUCCESS(20208), //寄卖行出售成功
	// CONSIGN_BUY_SUCCESS(20209), //寄卖行购买成功
	// CONSIGN_OFF_SHELF(20210), //寄卖行物品下架
	// CONSIGN_TIME_OUT(20211), //寄卖行物品寄卖过期
	// //充值
	// PAY_REWARD(20301), //月度首充礼包
	// //活动中心
	// ACTIVITY_REWARD(20302), //活动中心奖励
	// ACTIVITY_LUCK(20304), //活动中心幸运抽奖
	// //兑换码
	// EXCHANGE_WITH_CODE_SUCCESS(20303), //兑换码领取奖励
	// //盟友
	// ALLY_REBATE(20214), //盟友返利
	// ALLY_KICK(20215), //被踢出盟
	// //精灵
	// CHEST_SPIRIT(20217), //宝箱精灵
	// SMELT_SPIRIT(20218), //熔炼精灵
	// //单挑王
	// SOLO_SEASON_REWARD(20219), //问道大会赛季奖励
	// SOLO_RANK_REWARD_LAST_SEASON(20601),//问道大会上赛季段位奖励
	// //悬赏
	// REWARD_SUCCESS(20220), //悬赏成功
	// REWARD_PREY(20221), //被人悬赏
	// REWARD_REVENGE(20222), //大仇得报
	// REWARD_FAIL(20223), //悬赏失败
	// REWARD_FULL(20224), //背包已满
	// GUILD_DOUNGEON_OPEN(20225), //公会副本开启
	// TREASURE_BAG(20229), //提取君王宝藏背包
	// DUNGEON_REWARD(20401), //单人副本掉落
	// DUNGEON_FAST_REWARD(20402),//副本扫荡掉落
	// TEAM_GRAP_ITEM(20403),//组队掷骰子分配道具背包已满提示
	//
	// FIRSTPAY_GIFT_FULL(20230),
	// DAILYPAY_GIFT_FULL(20231),
	// GUILD_DOUNGEON_SCORE(20404),
	// WORLD_LEVEL(20501),
	// VIP_RANK_MAIL(20701),
	// BAG_FULL(20801); // 背包已满
	//
	// private final int value;
	// private MailID(int value){
	// this.value = value;
	// }
	//
	// public int getValue() {
	// return this.value;
	// }
	// }

	/** 邮件附件领取列表 */
	public static class MailAttachments {
		public List<String> mailIds;
		public int code;
	}

	/** 消息类型 */
	public static enum MESSAGE_TYPE {
		mail_receive(1), team_invite(2), team_apply(3), friend_invite(4), daoyou_invite(7), guild_apply(8), // 仙盟申请
		guild_invite(9), // 仙盟邀请
		daily_task_times(10), // 师门任务当日次数
		loop_task_addfriend(11), // 一条龙队员加队长好友
		loop_task_member_leave(12), // 一条龙任务队员离开
		loop_task_times(13), // 一条龙本轮跑环已完成
		upLevel_up(14), // 境阶突破
		redpacket(17), // 红包

		// 200-300是服务器给客户端的二次确认
		consignment_publish(200), // 寄卖行宣传
		;
		private final int value;

		private MESSAGE_TYPE(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum AreaForce {
		MONSTER(1), FORCEA(2), FORCEB(3);

		public final int value;

		private AreaForce(int value) {
			this.value = value;
		}
	}

	public static enum AreaCamp {
		Neutral(1), RongYao(2), TieXue(3);

		public final int value;

		private AreaCamp(int value) {
			this.value = value;
		}
	}

	public static enum FightLevelCond {
		TIME(1), REBORNTIME(2), KILL_MONSTER_NUM(3), GUARD_NPC_HP_PRE(4), GUARD_NPC_NUM(5), COLLECTION_ITEM_NUM(6);

		public final int value;

		private FightLevelCond(int value) {
			this.value = value;
		}
	}

	public static enum FightLevelCondModel {
		MORE(1), LESS(2);

		public final int value;

		private FightLevelCondModel(int value) {
			this.value = value;
		}
	}

	/**
	 * 场景类型
	 */
	public static enum SCENE_TYPE {
		// 主城|野外
		NORMAL(1),
		// 副本
		FIGHT_LEVEL(2),
		/** 单挑 */
		SIN_COM(3), ARENA(4), CROSS_SERVER(5), ALLY_FIGHT(6), // 盟战
		GUILD_DUNGEON(7), WORLD_BOSS(8),
		// 幻境
		ILLUSION(9),
		// 5v5
		FIVE2FIVE(10),
		// 镇妖塔
		DEMON_TOWER(11), RESOURCE_DUNGEON(12), // 资源副本
		LOOP(13), // 一条龙 皓月镜 副本
		GUILD_BOSS(14), // 工会BOSS
		ILLUSION_2(15), // 幻境2
		FIGHT_LEVEL_ULTRA(16), // 极限副本
		GUILD_FORT_PVE(17), // 工会据点采集地图
		GUILD_FORT_PVP(18),// 工会据点战斗地图
		;

		private final int value;

		private SCENE_TYPE(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum EquipPos {
		BODY(1), BAG(2);

		public final int value;

		private EquipPos(int value) {
			this.value = value;
		}
	}

	public static enum ACHIEVEMENT_TYPE {
		ACHIEVEMENT_TYPE_ALL(1), ACHIEVEMENT_TYPE_MAIN(2), ACHIEVEMENT_TYPE_SUB(3), ACHIEVEMENT_TYPE_ELEMENT(4);
		public final int value;

		private ACHIEVEMENT_TYPE(int value) {
			this.value = value;
		}
	};

	public static enum ACHIEVEMENT_AWARD_TYPE {
		ACHIEVEMENT_AWARD_TYPE_ITEM(1), ACHIEVEMENT_AWARD_TYPE_TITLE(2);
		public final int value;

		private ACHIEVEMENT_AWARD_TYPE(int value) {
			this.value = value;
		}
	};

	public static enum ACHIEVEMENT_CONDITION_TYPE {
		DEFAULT(-1), PLAYER_LEVEL(1), PLAYER_RANK(2), PLAYER_POWER_POINT(3), KILL_NPC(4), EQUIPMENT_ENHANCE(5), PLACE_ARRIVED(6), FINISH_TASK(7), FINISH_TASK_NUM(8), // 累计完成主线或支线数量达到
		FINISH_DAILY_TASK(12), GET_GOLD(13), GET_DIAMOND_IN_CONSIGNMENT(15), GET_MAGIC_RING(18), GET_MEDAL(19), GET_NECKLACE(20), GET_EQUIPMENT(21), EQUIPMENT_ENCHANT(25), GET_PET(26), GET_QUALITY_PET(27), PET_LEVEL(28), PET_UPGRADE_LEVEL(29), PET_TRANSFORM_LEVEL(30), RIDE_DEVELOPMENT(31), WING_LEVEL(32), GEM_COMBINE(33),
		// ACHIEVEMENT_CONDITION_FILL_GEM ( 34),
		FISH_ITEM(35), SKILL_LEVEL(36), DUNGEON_PASSED(37), SOLO_WIN(38), SOLO_RANK(39), ARENA_RANK(41), ARENA_KILL_PLAYER(42), ARENA_SCORE(43), ALLY_GOLD(44), ALLY_KILL_PLAYER(45), FRIENDS_NUM(51), RANK(52),

		ILLUSION_TIME(101), // 幻境挂机时间达到指定数值
		KILL_BOSS(102), // 击杀野外BOSS达到指定数量
		PASS_DEMONTOWER(103), // 通关指定层数的镇妖塔
		WORLD_SPEAK_TIME(104), // 世界频道喊话达到指定次数
		GEM_FILL_TOTAL_LEVEL(105), // 镶嵌宝石总等级达到指定数值
		EQUIP_POS_LEVEL(106), // 指定数量的部位强化达到指定等级 用5
		EQUIP_MAKE_TIMES(107), // 装备打造次数达到指定数值
		EQUIP_REBORN_TIMES(108), // 装备洗练次数达到指定数值
		EQUIP_REFINE_TIMES(109), // 装备精炼次数达到指定数值
		EQUIP_REBUILD_TIMES(110), // 装备重铸次数达到指定数值
		FIVE_VS_FIVE_TIMES(111), // 111.累计参加试练大赛的次数达到指定数值 5v5=试练大赛
		AREANA_TIMES(112), // 112.累计参加五岳一战的次数达到指定数值 竞技场=大乱斗=五岳一战
		SOLO_TIMES(113), // 113.累计参加问道大会的次数达到指定数值 单挑王=问道大会
		DEMONTOWER_TIMES(114), // 累计参加镇妖塔次数达到指定数值
		WORLD_LEVEL_TIMES(115), // 膜拜大神达到指定次数
		MOUNT_COUNT(116), // 获得坐骑数量达到指定数值
		XIANYUAN_COUNT(117), // 累计获取仙缘值达到指定数值
		FINISH_LOOP_TASK(118), // 累计完成皓月镜任务达到指定次数

		;
		public final int value;

		private ACHIEVEMENT_CONDITION_TYPE(int value) {
			this.value = value;
		}
	}

	public static enum ACHIEVEMENT_QUALITY_TYPE {
		WHITE(0), GREEN(1), BLUE(2), PURPLE(3), ORANGE(4), RED(5);
		public final int value;

		private ACHIEVEMENT_QUALITY_TYPE(int value) {
			this.value = value;
		}
	}

	/** 背包类型 */
	public static enum BAG_TYPE {
		BAG(1), // 背包
		WAREHOUSE(2), // 仓库
		ACCOUNT_WAREHOUSE(3), // 帐号仓库
		RECYCLE(4); // 出售物品回收站

		private int value;

		private BAG_TYPE(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}

		public static BAG_TYPE getE(int value) {
			for (BAG_TYPE type : values()) {
				if (type.value == value) {
					return type;
				}
			}
			return null;
		}
	}

	public static class Bag {
		/***/
		public static final int BAG_INIT_GRIDCOUNT = 5;
		/***/
		public static final int BAG_MAX_GRIDCOUNT = 100;
		/***/
		public static final int BAG_GRID_OPEN_TIME = 60000;
		/***/
		public static final int BAG_GRID_PACKUP_TIME = 5000;
		/***/
		public static final int BAG_GRID_GOLD = 100;
	}

	public static enum Time {
		Day(24 * 60 * 60 * 1000), Hour(60 * 60 * 1000), Minute(60 * 1000), Second(1000);
		private int value;

		private Time(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	/**
	 * 绑定类型
	 */
	public static enum BindType {
		UN_BIND(0), // 未绑定（商店和身上均显示未绑定)
		BIND(1), // 已绑定
		EQUIP_BIND(2), // 装备后绑定
		BIND_AFTER_GET(3), // 获取后绑定
		EQUIP_BIND_AFTER_GET(4);// 获取后装绑

		private int value;

		private BindType(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	/**
	 * 强制绑定类型
	 */
	public static enum ForceType {
		DEFAULT(0), // 默认走道具配置
		BIND(1), // 强制绑定
		UN_BIND(2); // 强制不绑定

		private int value;

		private ForceType(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}

		public static ForceType getE(int value) {
			for (ForceType e : values()) {
				if (value == e.getValue()) {
					return e;
				}
			}
			return null;
		}
	}

	public static enum ITEM_CODE {
		TICKET("cash"), TREASURE_POINT("treasurespoint"), XIAN_YUAN("xianyuan"), Changename("changename"),
		/**
		 * 副本收益卷
		 */
		DUNGEONPROFIT("dungeonprofit");

		public String value;

		private ITEM_CODE(String value) {
			this.value = value;
		}
	}

	/**
	 * 物品类型
	 */
	public static enum ItemType {
		Weapon(1), Armor(2), Oranament(3), RideEquip(4), Bijou(5), Mate(6), Misc(7), Chest(8), Potion(9), Quest(10), Virtual(11);

		private int value;

		private ItemType(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}

		public static ItemType getE(int value) {
			for (ItemType e : values()) {
				if (value == e.getValue()) {
					return e;
				}
			}
			return null;
		}

		public static int getV(String s) {
			for (ItemType e : values()) {
				if (e.toString().equals(s)) {
					return e.getValue();
				}
			}
			throw new RuntimeException("物品类型没找到:" + s);
		}
	}

	public static enum ItemSecondType {
		ZHU_SHOU(1, "武器"), TOU_BU(2, "头部"), // 帽子
		SHANG_YI(3, "上衣"), TUI_BU(4, "腿部"), YAO_BU(5, "肩部"), SHOU_TAO(6, "护腕"), XIE_ZI(7, "鞋子"), XIANG_LIAN(8, "项链"), JIE_ZI(9, "戒指"), HU_SHENG_FU(10, "护身符"), rideSoul(21, "rideSoul", "坐骑装备"), rideShield(22, "rideShield", "坐骑装备"), rideSpirit(23, "rideSpirit", "坐骑装备"), rideHeart(24, "rideHeart", "坐骑装备"), gem(201, "gem", "宝石"), mate(202, "mate", "材料"), rideItem(203, "rideItem", "材料"), petItem(204, "petItem", "材料"), fashionItem(205, "fashionItem", "材料"), chest(301, "chest", "宝箱"), hpot(401, "hpot",
				"药剂"), rank(402, "rank", "称号"), virtQuest(501, "virtQuest", "任务物品"), realQuest(502, "realQuest", "任务物品"), misc(999, "misc", "杂物"), virtual(1000, "virtual", "虚拟物品"), soul(998, "soul", "魂魄");

		private String key;
		private int value;
		String desc;

		private ItemSecondType(int value, String key) {
			this.value = value;
			this.key = key;
		}

		private ItemSecondType(int value, String key, String desc) {
			this.value = value;
			this.key = key;
			this.desc = desc;
		}

		public String getKey() {
			return this.key;
		}

		public int getValue() {
			return this.value;
		}

		public static int getV(String s) {
			for (ItemSecondType e : values()) {
				if (e.key.equals(s)) {
					return e.getValue();
				}
			}
			throw new RuntimeException("未找到的装备类型:" + s);
		}
	}

	/** 装备类型 */
	/**
	 * 去掉 勋章/副手/ 腰带改为肩部
	 */
	public static enum EquipType {
		MAIN_HAND(1), HEAD(2), CLOTH(3), LEG(4), // 腿部
		SHOULDER(5), // 肩部
		GLOVES(6), // 手套
		SHOES(7), // 鞋子
		NECKLACE(8), // 项链
		RING(9), // 戒指
		CHARM(10);// 护符

		private int value;

		private EquipType(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum ItemQuality {
		WHITE(0), // 白色
		BLUE(1), // 蓝色
		PURPLE(2), // 紫色
		ORANGE(3), // 橙色
		GREEN(4), // 绿色
		RED(5); // 红色

		private int value;

		private ItemQuality(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}

		public static ItemQuality getE(int value) {
			for (ItemQuality e : values()) {
				if (e.value == value) {
					return e;
				}
			}
			return null;
		}
	}

	public static enum GOODS_CHANGE_TYPE {

		random_box(81, "随机宝箱"), // 随机宝箱?????

		// 可以共享的 ....
		def(0, "默认"), //
		gm(1, "GM命令"), //
		shop(9, "商城购买"), // 商城
		use(15, "物品使用"), // 物品使用
		mail(18, "邮件"), // 一个无意思的定义，请不要使用了...
		DAOYOU(73, "道友返利"), // 道友
		CDK(85, "CDK兑换"), // CDK兑换
		ONLINE_GIFT(86, "领取在线奖励"), //
		AUTO_MELT(87, "自动熔炼"), // "自动熔炼"
		AUCTION(110, "仙盟竞拍"), //
		GMT(111, "GM工具（WEB后台）"), // GM工具，区别于GM命令
		REVELRY_EXCHANGE(117, "开服冲榜兑换"), // 冲榜活动

		// 增加来源编号 201 开始....
		AUCTION_BONUS(201, "竞拍分红"), //
		AUCTION_RESTITUTION(202, "竞拍失败归还竞价"), //
		CHARGE(203, "充值"), //
		SUPER_PACKAGE(204, "超值礼包"), // 1元 3元 6元
		FIRST_CHARGE(205, "首充奖励"), //
		CUMULATIVE_CHARGE(206, "累充奖励"), //
		VIPBUY(207, "购买月卡"), //
		PAY_REBATE(208, "封测充值返利"), // 充值返利
		RECEIVE_FUNDS(209, "开服基金奖励"), // 领取开服基金奖励
		REVELRY(210, "开服冲榜奖励"), // 冲榜活动
		GUILD_BOSS(211, "仙盟BOSS奖励"), // 公会BOSS

		ACTIVITY_SEVENDAY(212, "福利-开服狂欢"), // 福利里的开服狂欢
		ACTIVITY_TOTAL_PAY(213, "福利-累计充值"), // 福利里的累计充值
		ACTIVITY_TOTAL_CONSUME(214, "福利-累计消费"), // 福利里的累计消费
		ACTIVITY_LEVEL(215, "福利-等级礼包"), // 福利里的等级礼包
		ACTIVITY_FIGHT_POEWR(216, "福利-战力礼包"), // 福利里的战力礼包

		CONSIGNMENT_TIMEOUT(217, "拍卖超时退还"), // 寄售行
		CONSIGNMENT_SELL(218, "拍卖出售"), // 寄售行
		CONSIGNMENT_REMOVE(219, "拍卖下架"), // 寄售行

		DailyRecharge(220, "每日充值"), // 每日充值
		ActivityDraw(221, "活动抽奖"), // 活动抽奖
		ActivityDrawSpring(222, "新春抽奖"), // 新春抽奖
		LimitTimeGift(223, "限时礼包"), // 限时礼包
		DemonTower(224, "镇妖塔"), // 镇妖塔
		Fashion(226, "时装"), // 时装

		WORLD_LEVEL(227, "世界等级"), // 世界等级
		KILL_PLAYER(228, "击杀玩家"), // 击杀玩家
		smriti_equip(229, "继承装备"), // 继承装备
		task_submit(230, "提交任务"), // 提交任务
		move(231, "转移物品"), // 转移物品

		guild_store(232, "工会仓库"), // 工会仓库
		guild_tech(233, "公会修行"), // 公会修行
		guild_bless(234, "公会祈福"), // 公会祈福
		guild_bless_award(235, "公会祈福奖励"), // 公会祈福奖励
		guild_donate(236, "公会捐献"), // 公会捐献
		guild_mail(237, "仙盟发的邮件"), // 工会发的邮件
		guild_create(238, "创建仙盟"), // 创建仙盟
		guild_upgrade_bless_level(239, "提升仙盟祝福等级"), // 提升仙盟祝福等级
		guild_upgrade_depot_level(240, "提升仙盟存储等级"), // 提升仙盟存储等级
		guild_upgrade_level(241, "提升仙盟等级"), // 提升仙盟等级
		guild_buy_tech_product(242, "购买仙盟科技产品"), // 购买仙盟科技产品
		guild_upgrade_tech_level(243, "提升仙盟科技等级"), // 提升仙盟科技等级
		guildfort_daily_award(244, "仙盟据点战日常奖励"), // 据点战占领方成员日常奖励

		resource_dungeon_sweep(250, "资源副本扫荡"), // 资源副本扫荡
		recovered(253, "资源找回"), // 资源找回

		equip(254, "穿装备"), // 穿装备
		saveRebuid(255, "保存重铸"), // 保存重铸
		saveReborn(256, "保存洗练"), // 保存洗练

		resource_dungeon_buy_times(257, "资源副本购买次数"), // 资源副本购买次数
		resource_dungeon_award(258, "资源副本领取奖励"), // 资源副本领取奖励
		resource_dungeon_cost_diamond(259, "资源副本扣除元宝"), // 资源副本扣除元宝

		FUNCTION_OPEN(260, "领取功能开放奖励"), // 领取功能开放奖励
		relive(261, "复活"), // 复活

		intergalmall(262, "积分商城"), // 积分商城
		five2five(263, "5v5"), // 5v5

		fight_level(264, "副本"), // 副本
		RollPoint(265, "roll点"), // roll点

		Rebuild(266, "重铸"), // 重铸
		SeniorRebuild(267, "高级重铸"), // 高级重铸
		Reborn(268, "洗练"), // 洗练
		Refine(269, "精炼"), // 精炼

		leaderBoard(270, "世界等级膜拜"), // 世界等级膜拜

		interact(271, "交互"), // 交互：如送鲜花

		inherit(272, "继承"), // 继承

		openbag(273, "开背包格子"), // 开背包格子

		guildchangename(274, "公会改名"), // 公会改名

		chat(275, "聊天"), // 聊天

		transport(276, "传送"), // 进入或者传送场景，扣除道具

		consume(277, "消费"), // 消费

		buy(278, "个人商店"), // 个人商店

		skill(279, "升级技能"), // 技能

		pet(280, "宠物培养"), // 宠物培养
		petChangeName(281, "宠物改名"), // 宠物改名
		petCost(282, "使用宠物道具"), // 使用宠物道具

		monsterdrop(283, "怪物掉落"), //
		achieve(284, "成就"), //

		task(285, "任务"), //
		solo(286, "问道"), // 单挑王

		trade(287, "出售"), // 交易

		// online(16, "在线领取"), // 在线领取//FIXME ???
		compound(288, "合成"), //

		arena(289, "竞技场"), // 竞技场//FIXME
		sign(290, "签到"), // 签到
		exchange(291, "兑换"), // 好礼兑换
		melt(292, "熔炼"), //
		clear_when_logout(293, "出售"), // 下线时清除所有回购背包的物品？？
		fish(294, "钓鱼"), // 钓鱼
		hitUser(295, "PK"), // pk 爆装
		daily_activity(296, "活跃度礼包"), // 活跃度礼包

		equipMake(297, "装备制作"), // 装备制作
		equipLevelUp(298, "装备升级（等级）"), // 装备升级（等级）
		equipColorUp(299, "装备升品（品质）"), // 装备升品（品质）
		equipmosaic(300, "装备镶嵌"), //
		equipstrengh(301, "装备强化"), //
		dailypay_gift(302, "月卡奖励"), //

		flee(303, "五岳一战"), // 五岳一战
		BOSS_ASSISTS(304, "野外Boss助攻"), // 野外Boss助攻

		CombineCompensate(305, "合服补偿"), // 合服补偿
		blood(306, "血脉"), // 血脉
		EXCHANGE_PROPARTY(307, "属性兑换"), // 属性兑换
		RedPacket(308, "红包"), // 红包
		Rich(309, "大富翁"), // 大富翁
		SevenGoal(310, "七日目标"), // 七日目标
		ContinuousRecharge(311, "连续充值"), // 连续充值
		SingleRecharge(312, "单笔充值"), // 单笔充值
		RevelryRecharge(313, "冲榜累充"), // 冲榜累充

		// 减少来源编号 401 开始....
		BUY_FUNDS(401, "购买开服基金"), // 福利里的购买基金
		BOSS_GUILD_INSPIRE(402, "仙盟BOSS鼓舞"), // 工会BOSS鼓舞
		CONSIGNMENT_ADD(403, "拍卖上架"), // 寄售行
		CONSIGNMENT_BUY(404, "拍卖购买"), // 寄售行
		CONSIGNMENT_PUBLIC(405, "拍卖宣传"), // 寄售行

		FarmChange(1001, "果园兑换"), // 果园兑换
		FarmHarvest(1002, "果园收获"), // 果园收获
		FarmOpen(1003, "果园开启地块"), // 果园开启地块
		FarmSow(1004, "果园播种"), // 果园播种
		FarmSteal(1005, "果园偷取"), // 果园偷取

		;
		public int value;
		private final String desc;

		private GOODS_CHANGE_TYPE(int value, String desc) {
			this.value = value;
			this.desc = desc;
		}

		public int getValue() {
			return this.value;
		}

		public String getDesc() {
			return desc;
		}

		public static GOODS_CHANGE_TYPE getE(int value) {
			for (GOODS_CHANGE_TYPE e : values()) {
				if (e.value == value) {
					return e;
				}
			}
			return null;
		}
	}

	/**
	 * 货币类型
	 */
	public static enum CurrencyType {
		NONE(0), // 无
		DIAMOND(1), // 钻石
		COIN(2), // 金币
		TICKET(3), // 金票
		OTHER(4);
		private int value;

		private CurrencyType(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum SYS_SET {

		OFF(0), ON(1);

		private int value;

		private SYS_SET(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum FunctionType { // 功能名称枚举
		MAINLIND("Mainline"), // 主线任务
		BRANCH("Branch"), // 支线任务
		DAILY("Daily"), // 日常任务
		MAIL("Mail"), // 邮件
		CHAT("Chat"), // 聊天
		WAREHOUSE("Warehouse"), // 随身仓库
		PICK("Pick"), // 拾取互动
		MAP("Map"), // 地图
		SIGN("Sign"), // 签到
		ONLINE_GIFT("Olgift"), // 在线礼包
		AUTO_FIGHT("HangUP"), // 自动战斗
		AUTO_FIGHT_SET("HangUPSet"), // 挂机设置
		BAG("Bag"), // 背包
		CURRENCY("Currency"), // 货币
		ATTRIBUTE("Character"), // 属性
		TEAM("Team"), // 组队
		DAILY_PLAY("DailyPlay"), // 每日必玩
		EXCHANGE("Change"), // 兑换
		APPRAISAL("Appraisal"), // 装备-鉴定
		ACHIEVEMENT("Achievement"), // 成就
		UP("Up"), // 角色进阶
		MALL("Mall"), // 商城
		ACTIVITY("Activity"), // 活动
		PAY("Pay"), // 充值
		RANK("Rank"), // 排行榜
		FRIEND("Social"), // 好友
		SYSTEM_SETTING("SysSetting"), // 系统设置
		RIDING("Riding"), // 骑乘
		SKILL("Skill"), // 技能天赋
		TITLE2("Title2"), // 称号
		MOUNT("Ride"), // 坐骑
		SOLO("Solo"), // 单挑王
		SetNew("SetNew"), // 宝石镶嵌
		STRENGTHEN("Strengthen"), // 装备强化
		REWORK("Reworking"), // 加工-传承
		SMELTING("Smelting"), // 装备-熔炼
		PET("Pet"), // 宠物
		MAGIC_RING("MagicRing"), // 魔戒
		WING("Wings"), // 翅膀
		MEDAL("Title"), // 爵位，勋章
		GUILD("Guild"), // 公会
		FB("FB"), // 副本
		DaoYou("Ally"), // 道友
		JJC("JJC"), // 竞技场
		CONSIGNMENT("Consignment"), // 寄卖行
		SERVERS("Servers"), // 连服
		ALLY_BATTLE("AllyBattle"), // 盟战
		REWORKING_UP("ReworkingUp"), // 加工-进阶
		Make("Make"), // 加工-制作-打造
		TOWER("Tower"), // 通天塔
		GUILD_BATTLE("GuildBattle"), // 公会战
		FASHION("Fashion"), // 时装
		BOSS_HOME("BossHome"), // 世界首领
		EQUIP_LV_UP("EquipLvUp"), // 加工-升级
		REFINE("Refine"), // 加工-开光
		COMBINE("Combine"), // 加工-合成
		TARGET("Target"), // 今日目标
		FIRST_PAY("FirstPay"), // 首充大礼
		DAILY_PAY("DailyPay"), // 每日充值
		Rebuild("Rebuild"), // 加工-重铸
		Reborn("Reborn"), // 加工-洗炼
		WORLD_EXP("WorldExp"), // 世界等级
		XIAN_YUAN("XianYuan"), // 仙缘
		FIVE_2_FIVE("5v5"), // 5v5
		LoopTask("oneDragon"), // 皓月镜 一条龙
		teacher("teacher"), Inherit("Inherit"), // 传承
		BloodLineage("BloodLineage");// 血脉
		private String value;

		private FunctionType(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	/** 坐骑皮肤状态 */
	public static enum MOUNT_SKIN_STATE {
		notactive(1), notuse(2), useing(3);

		private int value;

		private MOUNT_SKIN_STATE(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	/** 坐骑骑乘状态 */
	public static enum MOUNT_RIDING_STATE {
		off(0), on(1);

		private int value;

		private MOUNT_RIDING_STATE(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}

		public static MOUNT_RIDING_STATE getValue(int value) {
			for (MOUNT_RIDING_STATE mrs : MOUNT_RIDING_STATE.values()) {
				if (mrs.getValue() == value) {
					return mrs;
				}
			}
			return MOUNT_RIDING_STATE.off;
		}
	}

	/**
	 * 积分商城 商店类型
	 */
	public static class IntergalMallType {
		/** 杂货商店 */
		public static final int SundryShop = 1;
		/** 商城积分商店 */
		public static final int MallShop = 2;
		/** 仙缘商店 */
		public static final int FateShop = 3;
		/** 竞技商店 */
		public static final int AthleticShop = 4;
		/** 仙盟商店 */
		public static final int GuildShop = 5;
	}

	public static enum SHOP_MALL_CONSUME_TYPE {
		DIAMOND(1), TICKET(2), ITEMCHANGE(3);
		private int value;

		private SHOP_MALL_CONSUME_TYPE(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}

		public static SHOP_MALL_CONSUME_TYPE getType(int value) {
			for (SHOP_MALL_CONSUME_TYPE shMall_CONSUME_TYPE : SHOP_MALL_CONSUME_TYPE.values()) {
				if (shMall_CONSUME_TYPE.value == value) {
					return shMall_CONSUME_TYPE;
				}
			}
			return null;
		}
	}

	public static enum SHOP_MALL_SERVER_LIMIT {
		GLOBAL(1), SELF(0);
		private int value;

		private SHOP_MALL_SERVER_LIMIT(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum SHOP_MALL_SCOREITEM_POSITION {
		FIRST(1), SECOND(2), THREE(3), FOUR(4), FIVE(5), SIX(6);
		private int value;

		private SHOP_MALL_SCOREITEM_POSITION(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum SHOP_MALL_ITEM_TYPE {
		DIAMOND_LIMIT(101), TICKET_LIMIT(201), ITEM_LIMIT(301);
		private int value;

		private SHOP_MALL_ITEM_TYPE(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}

		public static SHOP_MALL_ITEM_TYPE getType(int value) {
			for (SHOP_MALL_ITEM_TYPE sMall_ITEM_TYPE : SHOP_MALL_ITEM_TYPE.values()) {
				if (sMall_ITEM_TYPE.value == value) {
					return sMall_ITEM_TYPE;
				}
			}
			return null;
		}
	}

	public static enum ONLINE_GIFT {
		INIT_ID(1001), END_ID(0);

		private int value;

		private ONLINE_GIFT(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum ONLINE_GIFT_TYPE {
		NORMAL(0), FIRST_DAY(1);
		private int value;

		private ONLINE_GIFT_TYPE(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum EVENT_GIFT_STATE {
		NOT_RECEIVE(0), CAN_RECEIVE(1), RECEIVED(2);
		private int value;

		private EVENT_GIFT_STATE(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum EVENT_GIFT_REQUIRE_TYPE {
		LV(1), FIGHT_POWER(2);
		private int value;

		private EVENT_GIFT_REQUIRE_TYPE(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum EVENT_GIFT_PROP_TYPE {
		ONE("event_template1"), TWO("event_template2");
		private String value;

		private EVENT_GIFT_PROP_TYPE(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	public static enum PropertyChangeType {
		HP(0), MP(1), HPAndMP(2), NPC(3);

		public final int value;

		private PropertyChangeType(int value) {
			this.value = value;
		}
	}

	public static enum ValueType {
		Value(0), Percent(1);

		public final int value;

		private ValueType(int value) {
			this.value = value;
		}
	}

	public static enum CUSTOMTIPTYPE {
		GOLD(1), TICKET(2), DIAMOND(3), EXP(4), PRESTIGE(5);

		private int value;

		private CUSTOMTIPTYPE(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum CHAT_SCOPE {
		WORLD(1), // 世界
		GUILD(2), // 公会|盟友
		TEAM(3), // 队伍
		PRIVATE(4), // 私聊
		ZONE(5), // 连服(跨服)
		DAOYOU(6), // 道友
		SYSTEM(7), // 系统
		HORM(8), // 喇叭
		TEAM_CALL(9); // 队伍喊话

		private final int value;

		private CHAT_SCOPE(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum SYS_CHAT_TYPE {
		EXP(1), COIN(2), ITEM(3), TRADE(4), TEAM_EXP(5), CLASS_EXP(6);

		private int value;

		private SYS_CHAT_TYPE(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	// 共同属性提升类型
	public static enum COMMONPROPERTYUP {
		WING(1), MOUNT(2);
		private int value;

		private COMMONPROPERTYUP(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum PlayerPro {
		ALL("ALL", 0), COMMON("通用", 0), CANG_LANG("苍狼", 1), YU_JIAN("御剑", 2), YI_XIAN("逸仙", 3), SHEN_JIAN("神箭", 4), LI_NHU("灵狐", 5);

		public final String key;
		public final int value;

		private PlayerPro(String key, int value) {
			this.key = key;
			this.value = value;
		}

		public static PlayerPro Enum(int value) {
			for (PlayerPro e : values()) {
				if (value == e.value) {
					return e;
				}
			}
			Out.error("PlayerPro undefine v : ", value);
			return null;
		}

		public static int Value(String s) {
			if (StringUtil.isEmpty(s)) {
				return 0;
			}
			for (PlayerPro e : values()) {
				if (e.key.equals(s)) {
					return e.value;
				}
			}
			return 0;
		}

	}

	public static void main(String[] args) {
		System.out.println(PlayerPro.Value("灵狐"));
	}

	public static enum TEAM_DISTRIBUTE_TYPE {
		FREEDOM(1), // 自由拾取
		// DISTRIBUTE(2), // 隊長分配
		GRAB(3); // 搖骰子

		public final int value;

		private TEAM_DISTRIBUTE_TYPE(int value) {
			this.value = value;
		}
	}

	public static enum TEAM_DISTRIBUTE_TYPE_DES {
		FREEDOM("自由拾取"), DISTRIBUTE("队长分配"), GRAB("摇色子");

		public final String value;

		private TEAM_DISTRIBUTE_TYPE_DES(String value) {
			this.value = value;
		}
	}

	public static enum EQUIP_QCOLOR {
		WHITE(0), BLUE(1), PURPLE(2), ORANGE(3), GREEN(4);

		public final int value;

		private EQUIP_QCOLOR(int value) {
			this.value = value;
		}
	}

	public static enum TEAM_DISTRIBUTE_ITEM_QCOLOR_DES {
		BLUE("蓝色"), PURPLE("紫色"), ORANGE("橙色");

		public final String value;

		private TEAM_DISTRIBUTE_ITEM_QCOLOR_DES(String value) {
			this.value = value;
		}
	}

	public static enum TEAM_GRAB_ITEM_TYPE {
		GIVE_UP(1), // 放弃
		RANDOM(2), // 随意
		WANT(3); // 需要

		public final int value;

		private TEAM_GRAB_ITEM_TYPE(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static TEAM_GRAB_ITEM_TYPE getE(int value) {
			for (TEAM_GRAB_ITEM_TYPE e : values()) {
				if (value == e.getValue()) {
					return e;
				}
			}
			return null;
		}
	}

	public static enum RankSeasonType {
		NO_SEASON(-1), // 没有赛季
		LAST_SEASON(0), // 上一赛季
		CUR_SEASON(1); // 当前赛季
		public final int value;

		private RankSeasonType(int value) {
			this.value = value;
		}
	}

	public static enum UpdateType {
		INTERVAL_TIME(0), // 整点刷新
		REAL_TIME(1); // 实时刷新

		public final int value;

		private UpdateType(int value) {
			this.value = value;
		}
	}

	public static enum LeaderBoardType {
		// 普通排行榜 必须后缀 _rankTR
		FIGHTPOWER_ALL_rankTR(101), // 战力本服榜
		FIGHTPOWER_1_rankTR(102), // 战力苍狼榜
		// FIGHTPOWER_2_rankTR(103), // 战力御剑榜
		FIGHTPOWER_3_rankTR(104), // 战力逸仙榜
		// FIGHTPOWER_4_rankTR(105), // 战力神箭榜
		FIGHTPOWER_5_rankTR(106), // 战力灵狐榜
		LEVEL_rankTR(200), // 本服等级榜
		GUILD_LEVEL_rankTR(300), // 仙盟等级榜
		GUILD_WAR_rankTR(400), // 仙盟盟战榜
		RIDE_rankTR(500), // 本服坐骑榜
		PET_rankTR(600), // 本服宠物榜
		XIANYUAN_rankTR(700), // 仙缘榜
		HP_rankTR(800), // 生命榜
		PHY_rankTR(801), // 物攻榜
		MAGIC_rankTR(802), // 魔攻榜
		PVP_5V5_rankTR(1003), // 试炼大赛
		DAOYOU_rankTR(2005), // 道友排行榜
		FLEE_rankTR(900), // 大逃杀

		// 好友排行榜 必须后缀 _friendRankTR
		// ARENA_friendRankTR(1002), // 预留好友排行榜字段类型

		// 赛季排行榜 必须后缀 _seasonRankTR
		SOLO_SCORE_seasonRankTR(2006), // 问道大会资历榜
		ARENA_SCORE_rankTR(2007), // 竞技场（五岳一战）每日积分排行
		ARENA_SCOREALL_seasonRankTR(2009), // 五岳一战总积分排行
		DEMON_TOWER_rankTR(2100), // 镇妖塔
		MIN(0);

		public final int value;

		private LeaderBoardType(int value) {
			this.value = value;
		}

		public static LeaderBoardType getE(int value) {
			for (LeaderBoardType e : values()) {
				if (e.value == value) {
					return e;
				}
			}
			return null;
		}
	}

	public static enum PkModel {
		Peace(0), Justice(1), Force(2), Guild(3), Team(4), Server(5), All(6);

		public final int value;

		private PkModel(int value) {
			this.value = value;
		}
	}

	public static enum OrderType {
		A(1), Asc(1), D(2), Desc(2);

		public final int value;

		private OrderType(int value) {
			this.value = value;
		}

		public static OrderType getE(String name) {
			for (OrderType e : values()) {
				if (e.name().equals(name)) {
					return e;
				}
			}
			return null;
		}
	}

	public static enum TaskOpenWay {
		None(0), DailyOpenInTime(1), // 每日定时开放
		WeekOpenInTime(2), // 每周定日（定时）开发
		OpenInTime(3), // 定时开放
		FestivalOpenInTime(4); // 节假日每日开放
		private final int value;

		private TaskOpenWay(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	/** 永久时装 */
	public static final int FASHION_FOREVER = -1;

	/**
	 * 时装获得途径
	 */
	public static enum FASHION_BUY_TYPE {
		DIAMOND(1), COIN(2), ITEM(3);
		private final int value;

		private FASHION_BUY_TYPE(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	/**
	 * 时装部位类型
	 */
	public static enum FASHION_TYPE {
		WEPON(1), CLOTH(2), WING(3);

		public final int value;

		private FASHION_TYPE(int value) {
			this.value = value;
		}

		public static FASHION_TYPE valueOf(int type) {
			for (FASHION_TYPE e : values()) {
				if (type == e.value) {
					return e;
				}
			}
			return null;
		}
	}

	public static class Position {
		public int x;
		public int y;

		public Position(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	public static enum AVATAR_TYPE {
		NODE(0), R_HAND_WEAPON(1), R_HAND_BUFF(2), L_HAND_WEAPON(3), L_HAND_BUFF(4), HEAD_EQUIPMENT(5), HEAD_BUFF(6), CHEST_EQUIPMENT(7), CHEST_BUFF(8), REAR_EQUIPMENT(9), // 翅膀
		REAR_BUFF(10), FOOT_EQUIPMENT(11), FOOT_BUFF(12), TAIL_EQUIPMENT(13), TAIL_BUFF(14), AVATAR_BODY(15), RIDE_EQUIPMENT(16)// 坐骑
		;

		public final int value;

		private AVATAR_TYPE(int value) {
			this.value = value;
		}
	}

	public static enum DungeonType {
		CAN_INVITE(1), INVITIED(2), REFUSED(3);

		public final int value;

		private DungeonType(int value) {
			this.value = value;
		}
	}

	public static enum HandsUpState {

		ACCEPT(1), REFUSE(2), WAITING(3);

		public final int value;

		private HandsUpState(int value) {
			this.value = value;
		}
	}

	public static enum OpenRuleType {
		EVERY_DAY(1), EVERY_WEEK(2);
		private final int value;

		private OpenRuleType(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum HardModel {
		NORMAL(1), // 普通
		ELITE(2), // 精英
		HERO(3); // 英雄

		public final int value;

		private HardModel(int value) {
			this.value = value;
		}
	}

	public static enum MESSAGE {
		MAX_COUNT(50);
		private final int value;

		private MESSAGE(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	// 消息操作
	public static enum MESSAGE_OPERATE {
		TYPE_ACCEPT(1), TYPE_REFUSE(2);
		private final int value;

		private MESSAGE_OPERATE(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum SkillType {
		PASSIVE(0), ACTIVE(1), STRENGTHEN_TALENT(2), NORMAL(3), BATTLE_PASSIVE(4), EFFECT_PASSIVE(5), PET_PASSIVE(6);
		private final int value;

		private SkillType(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum SkillHandleType {
		ALL(0), CHANGE(1), ADD(2), DELETE(3);
		private final int value;

		private SkillHandleType(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum GOTO_TYPE {
		SALE_SHOP(1), // 随身商店
		PICK_ITEM(2); // 拾取道具

		public final int value;

		private GOTO_TYPE(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum KickReason {
		NEW_LOGIN(1), // 顶号
		SERVER_SHUT_DOWN(2), // 服务器维护
		LOGIC_ERROR(3), // 逻辑错误
		GM_KICK(4); // GM踢人

		public final int value;

		private KickReason(int value) {
			this.value = value;
		}
	}

	public static enum ConsignmentOrderType {
		TIME_DES(0), TIME_ASC(1), PRICE_ASC(2), PRICE_DES(3), LEVEL_ASC(4), LEVEL_DES(5);
		private final int value;

		private ConsignmentOrderType(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}

	}

	public static enum InteractType {
		FRIEND(1), UNFRIEND(2);
		private final int value;

		private InteractType(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static final int DAY_BY_MILLISECOND = 24 * 60 * 60 * 1000;

	public static enum FriendMessageType {
		TYPE_INVITE(1), TYPE_CONCERN(2);
		private final int value;

		private FriendMessageType(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum OfflineMessageType { // 离线数据类型
		SEND_MAIL(1), FRIEND(2), PREPAID(3);
		private final int value;

		private OfflineMessageType(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum RemoveFriendType {
		FRIEND(1), FRIEND_MESSAGE(2), FRIEND_APPLY(3), BLACK_LIST(4), FRIEND_RECORD(5), CHOU_REN(6);
		private final int value;

		private RemoveFriendType(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum PlayerStatus {
		online(1), faraway(2), offline(3);
		private final int value;

		private PlayerStatus(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum ALLY_CHAT_TYPE {
		NOTIFY(1), LEAVE_WORD(2), SYS(3);
		private ALLY_CHAT_TYPE(int value) {
			this.vaule = value;
		}

		private final int vaule;

		public int getVaule() {
			return vaule;
		}
	}

	public static enum VitalityID {
		ALLY_FIGHT(3), ARENA(4);
		private VitalityID(int value) {
			this.vaule = value;
		}

		private final int vaule;

		public int getVaule() {
			return vaule;
		}
	}

	public static enum ALLY_FIGHT_PUSH_TYPE {
		JOIN(1), // 参与
		ENTER(2); // 进入场景
		private ALLY_FIGHT_PUSH_TYPE(int value) {
			this.vaule = value;
		}

		private final int vaule;

		public int getVaule() {
			return vaule;
		}
	}

	public static enum RecommendPlayFunType {
		DUNGEON(1), GUILD_DUNGEON(4), SOUL_DAILY_TASK(7), SOLO(13);
		private RecommendPlayFunType(int value) {
			this.vaule = value;
		}

		private final int vaule;

		public int getVaule() {
			return vaule;
		}
	}

	public static enum GuildBuilding {// 公会建筑
		GUILD(1), // 大厅
		BLESS(2), // 祈福
		DEPOT(3), // 仓库
		COPY(4), // 副本
		TECH(5); // 科技

		private GuildBuilding(int value) {
			this.value = value;
		}

		private final int value;

		public int getValue() {
			return value;
		}
	}

	public static enum GuildMode {
		AUTO_MODE(1), CHECK_MODE(2);

		private GuildMode(int value) {
			this.value = value;
		}

		private final int value;

		public int getValue() {
			return value;
		}
	}

	public static enum GuildJob {
		PRESIDENT(1), VICE_PRESIDENT(2), ELDER(3), ELITE(4), MEMBER(5);

		private GuildJob(int value) {
			this.value = value;
		}

		private final int value;

		public int getValue() {
			return value;
		}
	}

	public static enum LuckyDrawType {
		EXCELLENT_ITEM(1), NORMAL_ITEM(2), BUFF_ITEM(3), RECOMMEND_ITEM(4), VIEW_ITEM(5);
		private LuckyDrawType(int value) {
			this.value = value;
		}

		private final int value;

		public int getValue() {
			return value;
		}
	}

	public static enum ActivityRewardType {
		FIRST_PAY(1), SECOND_PAY(2),
		/**
		 * 累计充值
		 */
		TOTAL_PAY(3), TOTAL_CONSUME(4), LEVEL(5), FIGHT_POEWR(6), FOUNDATION(7), INVITE_FRIEND(8), INVITE_CODE(9), GAME_NOTICE(10), LUCKY_REWARD(13), OPEN_SEVEN_DAY(14), HAOLI_CHANGE(15), SIGN(16), ONLINE_GIFT(17), LUCK_DRAW(18), SUPER_PACKAGE(19), RECOVERY(20), DAILY_RECHARGE(21),
		/**
		 * 单笔充值
		 */
		SINGLE_RECHARGE(23), SPRING_DRAW(25),
		/**
		 * 冲榜累计
		 */
		REVELRY_RECHARGE(26);
		private ActivityRewardType(int value) {
			this.value = value;
		}

		private final int value;

		public int getValue() {
			return value;
		}

		public static ActivityRewardType valueOf(int type) {
			for (ActivityRewardType t : ActivityRewardType.values()) {
				if (t.getValue() == type) {
					return t;
				}
			}
			return null;
		}
	}

	public static enum HAOLI_CHANGE_STATE {
		CANNOT_RECEIVE(0), CAN_RECEIVE(1), RECEIVED(2);
		private HAOLI_CHANGE_STATE(int value) {
			this.value = value;
		}

		private final int value;

		public int getValue() {
			return value;
		}
	}

	public static enum NotifyType {
		// 公会通知
		GUILD_REFRESH(0), // 只刷新场景玩家，不推送
		GUILD_PUSH_START(1), // 公会推送开始
		GUILD_JOIN_PUSH(1), // 入会刷新并推送
		GUILD_EXIT_PUSH(2), // 退会并推送
		GUILD_JOB_CHANGE(3), // 职位变更
		GUILD_CHANGE_NAME(4), // 公会名字变更
		// 仓库通知
		DEPOT_PUSH_START(10), // 仓库推送开始值
		DEPOT_DEPOSIT_PUSH(10), // 仓库存入
		DEPOT_REMOVE_PUSH(11), // 仓库取出
		DEPOT_UPGRADE_PUSH(12), // 仓库升级
		DEPOT_CONDITION_PUSH(13), // 仓库权限
		// 祈福通知
		BLESS_PUSH_START(20), // 祈福推送开始值
		BLESS_FINISH_PUSH(20), // 祈福进度
		BLESS_NEW_DAY_PUSH(21), // 祈福过天刷新
		// 商店通知
		SHOP_PUSH_START(30), // 商店推送开始
		SHOP_NEW_DAY_PUSH(30), // 商店过天
		// 科技通知
		TECH_PUSH_START(40), // 科技推送开始
		TECH_NEW_DAY_PUSH(40), // 科技过天刷新
		TECH_LEVEL_PUSH(41), // 科技升级
		TECH_BUFF_LEVEL_PUSH(42), // 科技增益等级
		GUILD_DUNGEON_OPEN(43), // 副本开启
		GUILD_DUNGEON_PASS(44), GUILD_DUNGEON_PLAYER_NUM(45), GUILD_DUNGEON_OPEN_CHAT(46);

		private NotifyType(int value) {
			this.value = value;
		}

		public final int value;

		public int getValue() {
			return value;
		}
	}

	public static enum VitalityType {
		EXP_FARM(1), // 经验农场
		SOLO(2), // 单挑王
		ALLY_FIGHT(3), // 盟战
		ARENA(4), // 竞技场
		GUILD_DUNGEON(5), // 工会副本
		TEAM_DUNGEON(6), // 组队副本
		WORLD_BOSS(7), // Boss之家
		VIP_BOSS(8), // 贵宾之家
		DAILY_TASK(9), // 完成今日日常
		HORSE_DUNGEON(10), // 坐骑材料副本
		WING_DUNGEON(11), // 翅膀材料副本
		EQUIP_STRENGTHEN(12), // 强化装备
		FILL_GEM(13), // 镶嵌宝石
		EQUIP_ENCHANT(14); // 附魔装备

		public final int value;

		private VitalityType(int value) {
			this.value = value;
		}
	}

	// 日常活动（活跃度）
	public static enum DailyType {
		DEFAULT(0), DEMON_TOWER(1), // 镇妖塔
		ILLUSION(2), // 幻境
		DUNGEON(3), // 副本
		DAILY_TASK(4), // 师门任务
		LOOP_TASK(5), // 皓月镜 一条龙
		WORLD_LEVEL(6), // 膜拜
		SOLO(7), // 问道大会
		ARENA(8), // 五岳一战
		PVP_5V5(9), // 试炼大赛
		RESOURCE_CHALLENGE(10), // 极限挑战
		RESOURCE_WATCH_PET(11), // 守护神宠
		RESOURCE_FARM(12), // 幻妖农场
		ILLSION_BOSS(13), // 领主击杀boss

		ILLUSION2(14), // 秘境
		
		RED_PACKET(15),//红包

		DAILY_MAX(99999);

		public final int value;

		private DailyType(int value) {
			this.value = value;
		}
	}

	public static enum GuildRecord {
		JOIN(1), // 入会
		EXIT(2), // 退会
		KICK(3), // 踢人
		UPGRADE(4), // 升级
		JOB(5), // 官职变动
		OFFICE_NAME(6), // 官职名称变动
		IMPEACH(7), // 弹劾
		IMPEACH_TIMEOUT(8), // 弹劾过期
		TRANSFER_IMPEACH_BECOME_INVALID(9), // 会长变更导致弹劾失效
		ONLINE_IMPEACH_BECOME_INVALID(9), // 会长弹劾期间上线导致弹劾失效
		DEPOSIT_EQUIP(10), // 存入仓库
		TAKE_OUT_EQUIP(11), // 从仓库取出
		DELETE_EQUIP(12), // 从仓库删除
		BLESS_USE_ITEM(13), // 使用道具进行祈福
		UPGRADE_BUILDING(14), // 升级建筑
		GUILD_NAME(15), // 公会改名
		OPEN_GUILD_DUNGEON(16), // 开启副本
		GUILD_DUNGEON_PASS(17), // 副本通关
		TRANSFER_PRESIDENT(18), // 会长转让
		CREATE_GUILD(19),

		MAX(99999);

		private GuildRecord(int value) {
			this.value = value;
		}

		public final int value;

		public int getValue() {
			return value;
		}
	}

	public static enum ACTIVITY_CENTER_TYPE {
		LEVEL_GIFT(5), INVITE_CODE(9), NOTICE(10);
		private ACTIVITY_CENTER_TYPE(int value) {
			this.value = value;
		}

		public final int value;

		public int getValue() {
			return value;
		}
	}

	public static enum PrepaidType {

		PREPAID_REQUEST(1), WP_PREPAID_REQUEST(2);
		private PrepaidType(int value) {
			this.value = value;
		}

		private final int value;

		public int getValue() {
			return value;
		}
	}

	public static enum PlayerBtlData {
		MaxHP(1, "生命"), HPPer(2, "生命%"), Attack(3, "攻击"), AttackPer(4, "攻击%"), Phy(5, "物攻"), PhyPer(6, "物攻%"), Mag(7, "魔攻"), MagPer(8, "魔攻%"), Hit(9, "命中"), HitPer(10, "命中%"), HitRate(11, "命中率"), Dodge(12, "闪避"), DodgePer(13, "闪避%"), DodgeRate(14, "闪避率"), Crit(15, "暴击"), CritPer(16, "暴击%"), CritRate(17, "暴击率"), ResCrit(18, "抗暴"), ResCritPer(19, "抗暴%"), ResCritRate(20, "抗暴率"), CritDamage(21, "暴击伤害%"), CritDamageRes(22, "暴伤抵御%"), Def(23, "防御"), DefPer(24, "防御%"), Ac(25, "物防"), AcPer(26,
				"物防%"), PhyDamageReduce(27, "受到物伤减少%"), Resist(28, "魔防"), ResistPer(29, "魔防%"), MagicDamageReduce(30, "受到魔伤减少%"), IgnoreAc(31, "无视敌人物防"), IgnoreResist(32, "无视敌人魔防"), IgnoreAcPer(33, "无视敌人物防%"), IgnoreResistPer(34, "无视敌人魔防%"), IncAllDamage(35, "所有伤害增加%"), AllDamageReduce(36, "受到伤害减免%"), HitLeechHP(37, "命中回复生命"), CtrlTimeReduce(38, "被控时间减少%"), SkillCD(39, "技能冷却减少%"), HPRegen(40, "生命恢复"), ExdGold(41, "银两掉落"), ExdExp(42, "杀怪经验"), HPRecover(43,
						"恢复生命"), HPRecoverPer(44, "恢复生命%"), HealEffect(45, "治疗效果"), HealedEffect(46, "被治疗效果"), RunSpeed(47, "移动速度"), Stun(48, "眩晕成功率"), Freeze(49, "冻结成功率"), Silence(50, "禁魔成功率"), Durance(51, "禁锢成功率"), Taunt(52, "嘲讽成功率"), SlowDown(53, "减速成功率"), ResStun(54, "眩晕抵抗"), ResFreeze(55, "冻结抵抗"), ResSilence(56, "禁魔抵抗"), ResDurance(57, "禁锢抵抗"), ResTaunt(58, "嘲讽抵抗"), ResSlowDown(59, "减速抵抗"), AllCtrl(60, "控制成功率"), AllResCtrl(61, "控制抵抗"), SkillDamage(62, "技能加成%"), HateRatio(63, "仇恨值");

		private PlayerBtlData(int id, String chName) {
			this.id = id;
			this.chName = chName;
		}

		public final int id;
		public final String chName;

		public static PlayerBtlData getE(String chName) {
			for (PlayerBtlData pd : values()) {
				if (pd.chName.equals(chName))
					return pd;
			}
			return null;
		}

		public static PlayerBtlData getE(int id) {
			for (PlayerBtlData pd : values()) {
				if (pd.id == id)
					return pd;
			}
			return null;
		}

		public static PlayerBtlData getEByKey(String key) {
			for (PlayerBtlData pd : values()) {
				if (pd.name().equals(key))
					return pd;
			}
			return null;
		}
	}

	public static enum DaoYou {
		DaoYouNameMaxLength(7), DaoYouNameMinLength(1), DaoYouKickDaoYouMail(20215), DaoYouLeaveMessageMaxLength(20), DaoYouNoticeMaxLength(20), DaoYouEditNameInterval(7), DaoYouRebateMail(20214), DaoYouMessageTypeSystem(1), DaoYouMessageTypeLeave(2);
		private final int value;

		private DaoYou(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum TimeState {
		TIME_NOT_UP(0), TIME_UP(1), TIME_OVER(2);

		private final int value;

		private TimeState(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}

	public static enum VipType {
		none(0), month(1), forever(2), sb_double(3);
		public final int value;

		private VipType(int value) {
			this.value = value;
		}

		public static VipType getE(int value) {
			for (VipType vt : values()) {
				if (vt.value == value)
					return vt;
			}
			return null;
		}
	}

	public static enum Five2Five {
		five2five_choice_giveup(1), five2five_choice_ready(2), five2five_choice_type_agree(1), five2five_choice_type_ready(2), five2five_thread_delay_time(3000);// 毫秒
		public final int value;

		private Five2Five(int value) {
			this.value = value;
		}
	}

	public static enum PlayerEventType {
		UPGRADE, // 升级
		CLASS_UPGRADE, // 进阶
		EQUIPMENT_CHANGE, REFRESH_NEWDAY, AFTER_LOGIN, // 上线后
		OFFLINE, PET_CHANGE, // 换宠物
		PET_PROP_CHANGE, // 宠物属性变化
		GUILD_BLESS_CHANGE, // 公会祈福属性变化
		GUILD_TECH_CHANGE, // 公会科技属性变化
		TITLE_CHANGE, // 称号属性变化
		PAY, // 充值
		FASHION_CHANGE, // 时装
		UPGRADE_TALENT_PASSIVE_SKILL, // 升级天赋的被动技能
		ARMOUR_ACTIVE, // 元始圣甲激活
		EXCHANGE_PROPARTY, // 兑换属性
		BLOOD,// 血脉
	}

	public static enum ManagerType {
		BASE_DATA, BTL_DATA, SKILL, SKILL_KEY, MOUNT, PET, VIP, MAIL, PREPAID, FIST_PAY, FIVE_2_FIVE, MONSTER_DROP, GUILD_BOSS, AUCTION, FARM, ACTIVITY, FASHION, BLOOD, GUILD_FORT, RICH, SEVEN_GOAL,
	}

	public static enum Arena {
		ARENA_GREEDY(1033), // 贪婪buffer该常量与策划约定
		ARENA_TIANSHEN(1031), // 天神buffer该常量与策划约定
		FIGHT_POWER_UP(510023);// 战力上升
		public int value;

		private Arena(int value) {
			this.value = value;
		}
	}

	/** 装备 词缀类型 */
	public static enum AffixType {
		normal(0), // 扩展属性
		legend(1); // 传奇属性

		public int value;

		private AffixType(int value) {
			this.value = value;
		}
	}

	/**
	 * 副本难度
	 *
	 */
	public static enum DungeonHardModel {

		// 1=普通副本难度（Dungeon-Normal强度表）
		// 2=精英副本难度（Dungeon-NightMare强度表）
		// 3=英雄副本难度（Dungeon-Hell强度表）
		Normal(1, "简单", "00d0ff"), NightMare(2, "普通", "ffae00"), Hell(3, "困难", "ed2a0a");
		public int value;
		public String desc;
		public String color;

		private DungeonHardModel(int value, String desc, String color) {
			this.value = value;
			this.desc = desc;
			this.color = color;
		}

		public static DungeonHardModel getE(int value) {
			for (DungeonHardModel dhm : values()) {
				if (dhm.value == value)
					return dhm;
			}
			return null;
		}
	}

	public static enum BiLogType {

		Smelt("熔炼"), Sale("出售"), Gold("银两"), Gold_Total("银两_总变更"), Cash("绑元"), Cash_Total("绑元_总变更"), Diamond("元宝"), Diamond_Total("元宝_总变更"), Exp("经验"), Exp_Total("经验_总变更"), FightPower("战力"), FightPower_Total("战力_总变更"), Consignment("寄卖"), Consignment_Total("寄卖_总变更"), Regist("注册"), Gift("礼包"), Gift_Total("礼包_总变更"), Pk("PK"), Charge("充值"), Charge_First("充值_首充"), Mail("邮件"), DiamondChange("元宝详细"), CashChange("绑元详细"),;

		public String desc;

		private BiLogType(String desc) {
			this.desc = desc;
		}

		public static BiLogType get(String key) {
			for (BiLogType blt : values()) {
				if (blt.name().equals(key)) {
					return blt;
				}
			}
			return null;
		}
	}

	public static String raffletickets = "raffletickets";
}
