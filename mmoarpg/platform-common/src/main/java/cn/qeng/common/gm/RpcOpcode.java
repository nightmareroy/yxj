package cn.qeng.common.gm;

/**
 * 后台请求游戏服所用RPC的Opcode
 *
 * @author 小流氓(176543888@qq.com)
 */
public class RpcOpcode {
	/**
	 * 同步服务器基本信息.
	 */
	public static final short OPCODE_SYNC_GAME_INFO = 0xff;

	/**
	 * 执行GROOVY脚本.
	 */
	public static final short OPCODE_GROOVY_SCRIPT = 0x0001;
	/**
	 * 热修复.
	 */
	public static final short OPCODE_HOTFIX = 0x0002;
	/**
	 * 游戏服T人
	 */
	public static final short OPCODE_KICKALL = 0x0003;
	/**
	 * 备份Redis
	 */
	public static final short OPCODE_BACKUP_REDIS = 0x0004;
	/**
	 * 删小号
	 */
	public static final short OPCODE_DELETE_PLAYER = 0x0005;
	/**
	 * 聊天黑名单
	 */
	public static final short OPCODE_CHAT_BACKLIST = 0x0006;
	/**
	 * Redis命令
	 */
	public static final short OPCODE_REDIS_COMMAND = 0x0008;

	/**
	 * 发送邮件
	 */
	public static final short OPCODE_SEND_MAIL = 0x11e1;
	/**
	 * 回收邮件
	 */
	public static final short OPCODE_DELETE_MAIL = 0x11e2;
	/**
	 * 推送滚动公告.
	 */
	public static final short OPCODE_ROLL_NOTICE = 0x1182;
	/**
	 * 查询充值
	 */
	public static final short OPCODE_QUERY_RECHARGE = 0x2010;
	/**
	 * 充值
	 */
	public static final short OPCODE_RECHARGE = 0x2030;
	/**
	 * 查询玩家处罚信息.
	 */
	public static final short OPCODE_QUERY_PUBLISH = 0x3001;
	/**
	 * 处罚.
	 */
	public static final short OPCODE_PUBLISH = 0x3002;
	/**
	 * 查询玩家信息
	 */
	public static final short OPCODE_QUERY_PLAYER_INFO = 0x3020;
	/**
	 * 查询背包物品信息
	 */
	public static final short OPCODE_QUERY_BAG_ITEM_INFO = 0x3021;
	/**
	 * 查询个人排行信息
	 */
	public static final short OPCODE_QUERY_PLAYER_RANK = 0x3023;
	/**
	 * 查询个人技能信息
	 */
	public static final short OPCODE_QUERY_PLAYER_SKILL = 0x3024;

	/**
	 * 查询个人宠物
	 */
	public static final short OPCODE_QUERY_PLAYER_PET = 0x3025;
	/**
	 * 查询个人坐骑
	 */
	public static final short OPCODE_QUERY_PLAYER_MOUNT = 0x3027;
	/**
	 * 查询仙盟信息
	 */
	public static final short OPCODE_QUERY_GUILD_INFO = 0x3031;
	/**
	 * 修改仙盟公告信息
	 */
	public static final short OPCODE_GUILD_UPDATE_NOTICE = 0x3032;

	/**
	 * 查询道友信息
	 */
	public static final short OPCODE_QUERY_DAOYOU_INFO = 0x4010;
}