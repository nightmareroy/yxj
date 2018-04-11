/*
 * Copyright © 2017 qeng.cn All Rights Reserved.
 * 
 * 感谢您加入清源科技，不用多久，您就会升职加薪、当上总经理、出任CEO、迎娶白富美、从此走上人生巅峰
 * 除非符合本公司的商业许可协议，否则不得使用或传播此源码，您可以下载许可协议文件：
 * 
 * 		http://www.noark.xyz/qeng/LICENSE
 *
 * 1、未经许可，任何公司及个人不得以任何方式或理由来修改、使用或传播此源码;
 * 2、禁止在本源码或其他相关源码的基础上发展任何派生版本、修改版本或第三方版本;
 * 3、无论你对源代码做出任何修改和优化，版权都归清源科技所有，我们将保留所有权利;
 * 4、凡侵犯清源科技相关版权或著作权等知识产权者，必依法追究其法律责任，特此郑重法律声明！
 */
package cn.qeng.gm.core.log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * 需要刻录操作日志的类型.
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
public enum OperationType {

	// --------------------登录相关日志-----------------------------------------
	LOGIN("登录(账号+密码)", LogClassify.LOGIN), //
	LOGIN_SEVEN_DAYS("登录(七日免登)", LogClassify.LOGIN), //
	LOGOUT("退出(主动)", LogClassify.LOGIN), //
	LOCKSCREEN("退出(一键锁定)", LogClassify.LOGIN), //
	LOGOUT_SESSION_TIMEOUT("退出(Session超时)", LogClassify.LOGIN), //
	LOGIN_PLATFORM("第三方平台登录", LogClassify.LOGIN), //

	// 数据仓库
	DATA_DAILY("访问了每日数据报表界面", LogClassify.DATA_WAREHOUSE), //
	DATA_SHOP_MANAGER("访问了商城销售分布查询界面", LogClassify.DATA_WAREHOUSE), //
	DATA_SHOP_LIST("查询商城销售分布", LogClassify.DATA_WAREHOUSE), //
	DATA_ONLINE("访问了同时在线数据", LogClassify.DATA_WAREHOUSE), //
	DATA_RECHARGE("访问了充值大盘走势", LogClassify.DATA_WAREHOUSE), //
	DATA_RECHARGE_SECTION("访问了充值区间分布", LogClassify.DATA_WAREHOUSE), //
	DATA_PRODUCE("访问了货币产出分布", LogClassify.DATA_WAREHOUSE), //
	DATA_CONSUME("访问了货币消耗分布", LogClassify.DATA_WAREHOUSE), //
	DATA_LEVEL("访问了玩家等级分布", LogClassify.DATA_WAREHOUSE), //
	DATA_REMAIN("访问了留存率", LogClassify.DATA_WAREHOUSE), //

	// ~~~~~~~~~~~~~~~~~~~~~~~~游戏管理~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
	// 游戏查询
	QUERY_RECHARGE_MANAGE("访问了查询玩家充值记录界面", LogClassify.GAME_QUERY), //
	QUERY_RECHARGE_REQEST("查询了玩家[{0}]的充值记录", LogClassify.GAME_QUERY), //
	QUERY_PLAYER_MANAGE("访问了查询玩家详情数据界面", LogClassify.GAME_QUERY), //
	QUERY_PLAYER_REQEST("查询了玩家[{0}]的详情数据", LogClassify.GAME_QUERY), //
	QUERY_GUILD_MANAGE("访问了查询仙盟详情数据界面", LogClassify.GAME_QUERY), //
	QUERY_GUILD_REQEST("查询了仙盟[{0}]的详情数据", LogClassify.GAME_QUERY), //
	QUERY_UPATE_GUILD("修改了仙盟[{0}]的公告信息", LogClassify.GAME_QUERY), //
	QUERY_DAOYOU_MANAGE("访问了查询道友信息界面", LogClassify.GAME_QUERY), //
	QUERY_DAOYOU_REQEST("查询了道友[{0}]的信息", LogClassify.GAME_QUERY), //
	// 登录公告日志
	MANAGER_LOGIN_NOTICE_LIST("访问了登录公告列表", LogClassify.LOGIN_NOTICE), //
	MANAGER_LOGIN_NOTICE_ADDUI("访问了添加登录公告界面", LogClassify.LOGIN_NOTICE), //
	MANAGER_LOGIN_NOTICE_ADD("添加了登录公告[{0}]", LogClassify.LOGIN_NOTICE), //
	MANAGER_LOGIN_NOTICE_EDITUI("访问了编辑登录公告界面", LogClassify.LOGIN_NOTICE), //
	MANAGER_LOGIN_NOTICE_EDIT("编辑了登录公告[{0}]", LogClassify.LOGIN_NOTICE), //
	MANAGER_LOGIN_NOTICE_ENABLE("启用了登录公告[{0}]", LogClassify.LOGIN_NOTICE), //
	MANAGER_LOGIN_NOTICE_DELETE("删除了登录公告[{0}]", LogClassify.LOGIN_NOTICE), //
	// 滚动公告日志
	MANAGER_ROLL_NOTICE_LIST("访问了滚动公告列表", LogClassify.ROLL_NOTICE), //
	MANAGER_ROLL_NOTICE_ADDUI("访问了添加滚动公告界面", LogClassify.ROLL_NOTICE), //
	MANAGER_ROLL_NOTICE_ADD("添加了滚动公告[{0}]", LogClassify.ROLL_NOTICE), //
	MANAGER_ROLL_NOTICE_EDITUI("访问了编辑滚动公告界面", LogClassify.ROLL_NOTICE), //
	MANAGER_ROLL_NOTICE_EDIT("编辑了滚动公告[{0}]", LogClassify.ROLL_NOTICE), //
	MANAGER_ROLL_NOTICE_DELETE("删除了滚动公告[{0}]", LogClassify.ROLL_NOTICE), //
	// 游戏内公告
	MANAGER_GAME_NOTICE_LIST("访问了游戏内公告列表", LogClassify.GAME_NOTICE), //
	MANAGER_GAME_NOTICE_ADDUI("访问了添加游戏内公告界面", LogClassify.GAME_NOTICE), //
	MANAGER_GAME_NOTICE_ADD("添加了游戏内公告[{0}]", LogClassify.GAME_NOTICE), //
	MANAGER_GAME_NOTICE_EDITUI("访问了编辑游戏内公告界面", LogClassify.GAME_NOTICE), //
	MANAGER_GAME_NOTICE_EDIT("编辑了游戏内公告[{0}]", LogClassify.GAME_NOTICE), //
	MANAGER_GAME_NOTICE_DELETE("删除了游戏内公告[{0}]", LogClassify.GAME_NOTICE), //
	// 处罚管理
	MANAGER_PUBLISHLOG_LIST("访问了处罚管理界面", LogClassify.PUBLISH), //
	MANAGER_PUBLISHLOG_ADDUI("访问了新增处罚界面", LogClassify.PUBLISH), //
	MANAGER_PUBLISHLOG_QUERY("访问了搜索处罚记录界面", LogClassify.PUBLISH), //
	MANAGER_PUBLISHLOG_ADD("对玩家【{0}】进行了【{1}】处罚", LogClassify.PUBLISH), //
	// 充值补单
	MANAGER_RECHARGE_INDEX("访问了充值补单界面", LogClassify.RECHARGE), //
	MANAGER_RECHARGE_ADD("充值补单[{0}({1})]", LogClassify.RECHARGE), //
	MANAGER_RECHARGE_LIST("访问了历史补单记录", LogClassify.RECHARGE), //
	// 福利管理
	MANAGER_MAIL_MANAGE("访问了福利申请界面", LogClassify.MAIL_MANAGE), //
	MANAGER_MAIL_ADD("申请了福利[{0}]", LogClassify.MAIL_MANAGE), //
	MANAGER_MAIL_APPROVAL("访问了福利审批界面", LogClassify.MAIL_MANAGE), //
	MANAGER_MAIL_AGREE("同意了福利审批", LogClassify.MAIL_MANAGE), //
	MANAGER_MAIL_REFUSE("拒绝了福利审批", LogClassify.MAIL_MANAGE), //
	MANAGER_MAIL_DELETE("回收了已审批的福利", LogClassify.MAIL_MANAGE), //
	MANAGER_MAIL_BATCH("访问了批量补偿界面", LogClassify.MAIL_MANAGE), //
	MANAGER_MAIL_CLEAN("执行了清空批量补偿数据", LogClassify.MAIL_MANAGE), //
	MANAGER_MAIL_UPLOADFILE("上传了批量补偿数据", LogClassify.MAIL_MANAGE), //
	MANAGER_MAIL_SEND("确认发送了批量补偿", LogClassify.MAIL_MANAGE), //

	// 模拟充值
	MANAGER_FICTITIOUS_LIST("访问了模拟充值记录", LogClassify.RECHARGE), //
	MANAGER_FICTITIOUS_INDEX("访问了模拟充值界面", LogClassify.RECHARGE), //
	MANAGER_FICTITIOUS_ADD("模拟充值[{0}({1})]", LogClassify.RECHARGE), //
	MANAGER_FICTITIOUS_AGREE("同意了模拟充值", LogClassify.MAIL_MANAGE), //
	MANAGER_FICTITIOUS_REFUSE("拒绝了模拟充值", LogClassify.MAIL_MANAGE), //

	// CDK管理
	MANAGER_CDKEY_LIST("访问了CDKEY礼包列表", LogClassify.CDKEY_MANAGE), //
	MANAGER_CDKEY_ADDUI("访问了CDKEY添加礼包界面", LogClassify.CDKEY_MANAGE), //
	MANAGER_CDKEY_ADD("添加CDKEY礼包[{0}]", LogClassify.CDKEY_MANAGE), //
	MANAGER_CDKEY_EXPORT("导出CDKEY[{0}]", LogClassify.CDKEY_MANAGE), //
	MANAGER_CDKEY_QUERY_UI("访问了查询CDKEY使用情况界面", LogClassify.CDKEY_MANAGE), //
	MANAGER_CDKEY_QUERY("查询CDKEY使用情况[{0}]", LogClassify.CDKEY_MANAGE), //

	// ~~~~~~~~~~~~~~~~~~~~~~~~日志查询~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
	QUERY_ITEMS_MANAGE("访问了查询玩家道具记录界面", LogClassify.LOG_QUERY), //
	QUERY_ITEMS_CHECK("查询了玩家的道具记录[{0}]", LogClassify.LOG_QUERY), //
	QUERY_RECHARGE_CHECK("查询了玩家的充值记录[{0}]", LogClassify.LOG_QUERY), //
	QUERY_RECHARGE_SHOW("查询了充值流水", LogClassify.LOG_QUERY), //
	QUERY_MONEY_MANAGE("访问了查询玩家货币记录界面", LogClassify.LOG_QUERY), //
	QUERY_MONEY_CHECK("查询了玩家货币记录[{0}]", LogClassify.LOG_QUERY), //
	QUERY_LEVELUP_MANAGE("访问了查询玩家升级记录界面", LogClassify.LOG_QUERY), //
	QUERY_LEVELUP_CHECK("查询了玩家升级记录[{0}]", LogClassify.LOG_QUERY), //
	QUERY_EQUIP_MANAGE("访问了查询玩家装备升级记录界面", LogClassify.LOG_QUERY), //
	QUERY_EQUIP_CHECK("查询了玩家的装备升级记录[{0}]", LogClassify.LOG_QUERY), //
	QUERY_PET_MANAGE("访问了查询玩家宠物升级记录界面", LogClassify.LOG_QUERY), //
	QUERY_PET_CHECK("查询了玩家的宠物升级记录[{0}]", LogClassify.LOG_QUERY), //
	QUERY_MOUNT_MANAGE("访问了查询玩家坐骑升级记录界面", LogClassify.LOG_QUERY), //
	QUERY_MOUNT_CHECK("查询了玩家的坐骑升级记录[{0}]", LogClassify.LOG_QUERY), //
	QUERY_RECHARGE_RANK_MANAGE("访问了查询充值排名界面", LogClassify.LOG_QUERY), //
	QUERY_RECHARGE_RANK_LIST("查询了一次充值排名", LogClassify.LOG_QUERY), //

	// ~~~~~~~~~~~~~~~~~~~~~~~~运维工具~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
	MAINTAIN_SERVER_MANAGE("访问了区服管理界面", LogClassify.MAINTAIN), //
	MAINTAIN_GROOVY_INDEX("访问了执行脚本界面", LogClassify.MAINTAIN), //
	MAINTAIN_GROOVY_EXEC("执行了一次脚本", LogClassify.MAINTAIN), //
	MAINTAIN_HOTFIX_INDEX("访问了代码热更新界面", LogClassify.MAINTAIN), //
	MAINTAIN_HOTFIX_EXEC("执行了一次代码热更新", LogClassify.MAINTAIN), //
	CHATCONFIG_ADD("反广告配置添加了敏感词[{0}]", LogClassify.MAINTAIN), //
	CHATCONFIG_DEL("反广告配置删除了敏感词[{0}]", LogClassify.MAINTAIN), //
	CHATCONFIG_STATUS_EDIT("修改反广告系统状态为[{0}]", LogClassify.MAINTAIN), //
	CHATCONFIG_LIST("查看了反广告配置", LogClassify.MAINTAIN), //
	EAMIL_CONFIG_LIST("访问了监控邮箱列表", LogClassify.MAINTAIN), //
	EAMIL_CONFIG_ADD_UI("访问了添加监控邮箱界面", LogClassify.MAINTAIN), //
	EAMIL_CONFIG_ADD("添加了监控邮箱[{0}]", LogClassify.MAINTAIN), //
	EAMIL_CONFIG_EDIT_UI("访问了编辑监控邮箱界面[{0}]", LogClassify.MAINTAIN), //
	EAMIL_CONFIG_EDIT("编辑了监控邮箱[{0}]", LogClassify.MAINTAIN), //
	EAMIL_CONFIG_DELETE("删除监控邮箱[{0}]", LogClassify.MAINTAIN), //
	MAINTAIN_WHITELIST_MANAGE("访问了登录白名单管理界面", LogClassify.MAINTAIN), //
	MAINTAIN_WHITELIST_ADDUI("访问了添加名单界面", LogClassify.MAINTAIN), //
	MAINTAIN_WHITELIST_ADD("添加了名单[{0}]", LogClassify.MAINTAIN), //
	MAINTAIN_WHITELIST_EDITUI("访问了编辑名单[{0}]界面", LogClassify.MAINTAIN), //
	MAINTAIN_WHITELIST_EDIT("编辑了名单[{0}]", LogClassify.MAINTAIN), //
	MAINTAIN_WHITELIST_DELETE("删除了名单[{0}]", LogClassify.MAINTAIN), //
	MAINTAIN_STOP_REDAY_UI("访问了停机维护准备界面", LogClassify.MAINTAIN_AUTOMATION), //
	MAINTAIN_STOP_REDAY_RESET("停机维护准备重置了对外时间[{0}]", LogClassify.MAINTAIN_AUTOMATION), //
	MAINTAIN_STOP_KICK_UI("访问了停机维护踢人界面", LogClassify.MAINTAIN_AUTOMATION), //
	MAINTAIN_STOP_KICK("停机维护踢人操作", LogClassify.MAINTAIN_AUTOMATION), //
	MAINTAIN_REBATE_UI("访问了返利数据列表界面", LogClassify.MAINTAIN_AUTOMATION), //
	MAINTAIN_REBATE_ADDUI("访问了上传返利数据界面", LogClassify.MAINTAIN_AUTOMATION), //
	MAINTAIN_REBATE_UPLOAD("上传了返利数据", LogClassify.MAINTAIN_AUTOMATION), //
	MAINTAIN_REBATE_CLEAN("清空了返利数据", LogClassify.MAINTAIN_AUTOMATION), //

	MAINTAIN_MLOG_LIST("访问了日志提取界面", LogClassify.MAINTAIN), //
	MAINTAIN_MLOG_CREATE("提取了日志[{0}]", LogClassify.MAINTAIN), //
	MAINTAIN_MLOG_DELETE("删除了日志[{0}]", LogClassify.MAINTAIN), //
	MAINTAIN_MLOG_DOWNLOAD("下载了日志[{0}]", LogClassify.MAINTAIN), //

	ONLINE_UPDATE_MANAGE("访问了在线更新版本界面", LogClassify.MAINTAIN_AUTOMATION), //
	ONLINE_UPDATE_UPLOADUI("访问了上传版本界面", LogClassify.MAINTAIN_AUTOMATION), //
	ONLINE_UPDATE_UPLOAD("上传了新版本", LogClassify.MAINTAIN_AUTOMATION), //
	ONLINE_UPDATE_USEUI("访问了使用版本界面", LogClassify.MAINTAIN_AUTOMATION), //
	ONLINE_UPDATE_UPDATE("一键更新版本", LogClassify.MAINTAIN_AUTOMATION), //
	ONLINE_UPDATE_DELETE("删除了一个版本", LogClassify.MAINTAIN_AUTOMATION), //

	GAME_CONFIG_MANAGE("访问了区服配置中心界面", LogClassify.MAINTAIN_AUTOMATION), //
	GAME_CONFIG_ADDUI("访问了添加新区界面", LogClassify.MAINTAIN_AUTOMATION), //
	GAME_CONFIG_ADD("添加了一个新区[{0}]", LogClassify.MAINTAIN_AUTOMATION), //
	GAME_CONFIG_EDITUI("访问了编辑区服[{0}]配置界面", LogClassify.MAINTAIN_AUTOMATION), //
	GAME_CONFIG_EDIT("编辑了一个区服[{0}]配置", LogClassify.MAINTAIN_AUTOMATION), //

	MONITOR_CHAT("访问了聊天监控系统", LogClassify.MONITOR), //
	MONITOR_PAY("访问了充值监控系统", LogClassify.MONITOR), //

	// --------------------修改紧急联系人------------------------------------------
	BACKSTAGE_POSTER_EDIT("修改了紧急联系人", LogClassify.MAINTAIN), //

	// ~~~~~~~~~~~~~~~~~~~~~~~~后台管理~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
	// -------------------个人信息-修改密码---------------------------------------
	BACKSTAGE_MODIFY_PASSWORD_UI("访问了修改密码页面", LogClassify.USER_MANAGE), //
	BACKSTAGE_MODIFY_PASSWORD("修改了密码", LogClassify.USER_MANAGE), //
	// -------------------后台账号管理相关日志-------------------------------------
	BACKSTAGE_USER_MANAGE("访问了账号管理界面", LogClassify.USER_MANAGE), //
	BACKSTAGE_USER_ADD_UI("访问了账号添加界面", LogClassify.USER_MANAGE), //
	BACKSTAGE_USER_ADD("添加了一个账号[{0}({1})]", LogClassify.USER_MANAGE), //
	BACKSTAGE_USER_EDIT_UI("访问了后台编辑账号页面", LogClassify.USER_MANAGE), //
	BACKSTAGE_USER_EDIT("编辑了账号[{0}({1})]信息", LogClassify.USER_MANAGE), //
	BACKSTAGE_USER_DELETE("删除账号[{0}]", LogClassify.USER_MANAGE), //
	// --------------------后台登录平台-----------------------------------------
	BACKSTAGE_LOGINPALTFORM_MANAGE("访问了登录平台界面", LogClassify.BACKSTAGE), //
	BACKSTAGE_LOGINPALTFORM_ADD_UI("访问了添加登录平台界面", LogClassify.BACKSTAGE), //
	BACKSTAGE_LOGINPALTFORM_ADD("添加登录平台[{0}]", LogClassify.BACKSTAGE), //
	BACKSTAGE_LOGINPALTFORM_EDIT_UI("访问了编辑登录平台界面", LogClassify.BACKSTAGE), //
	BACKSTAGE_LOGINPALTFORM_EDIT("编辑了登录平台[{0}]", LogClassify.BACKSTAGE), //
	BACKSTAGE_LOGINPALTFORM_DELETE("删除了登录平台", LogClassify.BACKSTAGE), //
	// --------------------后台权限组-------------------------------------------
	BACKSTAGE_AUTH_MANAGE("访问了后台的权限管理页面", LogClassify.AUTH_MANAGE), //
	BACKSTAGE_AUTH_ADD_UI("访问了后台添加权限组页面", LogClassify.AUTH_MANAGE), //
	BACKSTAGE_AUTH_ADD("添加了权限组[{0}]", LogClassify.AUTH_MANAGE), //
	BACKSTAGE_AUTH_EDIT_UI("访问了后台编辑权限组页面", LogClassify.AUTH_MANAGE), //
	BACKSTAGE_AUTH_EDIT("编辑了权限组", LogClassify.AUTH_MANAGE), //
	BACKSTAGE_AUTH_DELETE("删除了权限组", LogClassify.AUTH_MANAGE), //
	// --------------------后台访问限制------------------------------------------
	BACKSTAGE_ACCESS_MANAGE("访问了访问限制界面", LogClassify.BACKSTAGE), //
	BACKSTAGE_ACCESS_ADD_UI("访问了添加访问限制界面", LogClassify.BACKSTAGE), //
	BACKSTAGE_ACCESS_ADD("添加了访问限制[{0}]", LogClassify.BACKSTAGE), //
	BACKSTAGE_ACCESS_EDIT_UI("访问了编辑访问限制界面", LogClassify.BACKSTAGE), //
	BACKSTAGE_ACCESS_EDIT("编辑了访问限制[{0}]", LogClassify.BACKSTAGE), //
	BACKSTAGE_ACCESS_DELETE("删除访问限制", LogClassify.BACKSTAGE), //
	// --------------------后台操作日志------------------------------------------
	BACKSTAGE_LOGGER_LIST("访问了后台操作日志", LogClassify.BACKSTAGE), //
	;

	private final String value;
	private final LogClassify classify;

	private OperationType(String value, LogClassify classify) {
		this.value = value;
		this.classify = classify;
	}

	public String getValue() {
		return value;
	}

	public LogClassify getClassify() {
		return classify;
	}

	public static void main(String[] args) throws IOException {
		final StringBuilder sb = new StringBuilder();
		for (OperationType type : OperationType.values()) {
			sb.append(type.name()).append("=").append(type.value).append("\n");
			FileOutputStream fos = new FileOutputStream("E:\\mmoarpg\\platform-gm\\src\\main\\resources\\template-log.properties");
			@SuppressWarnings("resource")
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			osw.write(sb.toString());
			osw.flush();
		}
	}
}