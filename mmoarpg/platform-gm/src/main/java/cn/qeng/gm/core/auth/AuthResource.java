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
package cn.qeng.gm.core.auth;

/**
 * 权限资源枚举.
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
public enum AuthResource {
	WELCOME("欢迎界面"),

	DATA_WAREHOUSE("数据仓库"), //
	DATA_DAILY("每日数据报表", AuthResource.DATA_WAREHOUSE), // 每日数据
	DATA_ONLINE("实时在线曲线", AuthResource.DATA_WAREHOUSE), // 实时在线
	DATA_LEVEL("玩家等级分布", AuthResource.DATA_WAREHOUSE), // 玩家等级分布
	DATA_RECHARGE("充值大盘走势", AuthResource.DATA_WAREHOUSE), // 充值大盘走势
	DATA_RECHARGE_SECTION("充值区间分布", AuthResource.DATA_WAREHOUSE), // 充值区间分布
	DATA_PRODUCE("货币产出分布", AuthResource.DATA_WAREHOUSE), // 货币产出分布
	DATA_CONSUME("货币消耗分布", AuthResource.DATA_WAREHOUSE), // 货币消耗分布
	DATA_SHOP_DISTRIBUTION("商城销售额分布", AuthResource.DATA_WAREHOUSE), // 商城销售额分布
	DATA_REMAIN("留存率", AuthResource.DATA_WAREHOUSE), // 留存率

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 客服查询玩家日志功能
	 */
	LOG_QUERY("日志系统"), //
	QUERY_RECHARGE_RANK("查询充值排名", AuthResource.LOG_QUERY), // 查询充值排名
	QUERY_LEVELUP("查询角色升级", AuthResource.LOG_QUERY), // 查询角色升级
	QUERY_MONEY("查询货币流水", AuthResource.LOG_QUERY), // 查询货币流水
	QUERY_ITEMS("查询道具记录", AuthResource.LOG_QUERY), // 查询道具记录
	QUERY_MOUNT("查询坐骑升级", AuthResource.LOG_QUERY), // 查询道具记录
	QUERY_PET("查询宠物升级", AuthResource.LOG_QUERY), // 查询道具记录

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 游戏管理（GM系统）
	 */
	GAME_MANAGER("游戏管理"), // 游戏管理左边导航
	QUERY_RECHARGE("查询充值记录", AuthResource.GAME_MANAGER), // 查看充值记录
	QUERY_PLAYER("查询角色详情", AuthResource.GAME_MANAGER), // 查询角色详情
	QUERY_GUILD("查询仙盟详情", AuthResource.GAME_MANAGER), // 查询仙盟详情
	QUERY_UPATE_GUILD("修改仙盟公告", AuthResource.GAME_MANAGER), // 修改仙盟公告
	QUERY_DAOYOU("查询道友信息", AuthResource.GAME_MANAGER), // 查询道友信息
	QUERY_RANK("查询排行信息", AuthResource.GAME_MANAGER), // 查询道友信息
	MANAGER_LOGIN_NOTICE("登录公告", AuthResource.GAME_MANAGER), // 登录公告管理
	MANAGER_ROLL_NOTICE("滚动公告", AuthResource.GAME_MANAGER), // 滚动公告管理
	MANAGER_GAME_NOTICE("游戏内公告", AuthResource.GAME_MANAGER), // 游戏内公告管理
	MANAGE_PUBLISH("处罚管理", AuthResource.GAME_MANAGER), // 惩处管理
	MANAGE_RECHARGE("充值补单", AuthResource.GAME_MANAGER), // 充值补单
	MANAGE_RECHARGE_LOG("补单记录", AuthResource.GAME_MANAGER), // 充值补单日志

	MANAGE_MAIL_APPLY("福利申请", AuthResource.GAME_MANAGER), // 游戏管理-福利申请
	MANAGE_MAIL_APPROVAL("福利审批", AuthResource.GAME_MANAGER), // 游戏管理-福利审批
	MANAGE_MAIL_BATCH("批量补偿", AuthResource.GAME_MANAGER), //

	MANAGE_FICTITIOUS("模拟充值", AuthResource.GAME_MANAGER), // 模拟充值
	MANAGE_FICTITIOUS_APPROVAL("模拟充值审批", AuthResource.GAME_MANAGER), // 模拟充值审批

	// 查询cdkey
	MANAGE_CDKEY_QUERY("CDKEY查询", AuthResource.GAME_MANAGER), // CDKEY查询
	MANAGE_CDKEY_CONFIG("CDKEY配置", AuthResource.GAME_MANAGER), // CDKEY配置
	MANAGE_CDKEY_EXPORT("CDKEY导出", AuthResource.GAME_MANAGER), // CDKEY导出

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 客服查询玩家日志功能
	 */
	MONITOR("监控系统"), //
	MONITOR_CHAT("聊天监控", AuthResource.MONITOR), //
	MONITOR_MONEY("货币监控", AuthResource.MONITOR), //
	MONITOR_ITEM("道具监控", AuthResource.MONITOR), //
	MONITOR_PAY("充值监控", AuthResource.MONITOR), //

	/**
	 * 运维工具管理游戏区服等信息
	 */
	MAINTAIN_MANAGER("运维工具"), // 运维工具左边导航...
	POSTER_EDIT("修改首页通知", AuthResource.MAINTAIN_MANAGER), // 修改紧急联系人
	SERVER_MANAGE("区服管理", AuthResource.MAINTAIN_MANAGER), // 区服管理
	GROOVY_EXEC("执行脚本", AuthResource.MAINTAIN_MANAGER), // 执行脚本
	JAVA_HOTFIX("代码热更新", AuthResource.MAINTAIN_MANAGER), // 代码热更新
	WHITELIST_MANAGE("登录白名单", AuthResource.MAINTAIN_MANAGER), // 登录白名单
	CHATCONFIG_EDIT("反广告配置", AuthResource.MAINTAIN_MANAGER), // 反广告配置编辑
	EMAIL_CONFIG("监控邮箱配置", AuthResource.MAINTAIN_MANAGER), // 监控邮箱管理
	STOP_READY("停机维护准备", AuthResource.MAINTAIN_MANAGER), // 停机维护准备
	STOP_KICK("停机维护踢人", AuthResource.MAINTAIN_MANAGER), // 停机维护踢人
	REBATE("上传充值返利", AuthResource.MAINTAIN_MANAGER), // 上传充值返利
	MLOG_EXPORT("日志提取", AuthResource.MAINTAIN_MANAGER), // 提取MLOG
	ONLINE_UPDATE("在线更新版本", AuthResource.MAINTAIN_MANAGER), // 在线更新版本
	GAME_CONFIG("区服配置中心", AuthResource.MAINTAIN_MANAGER), // 在线更新版本

	/**
	 * 后台管理(主要是后台账号和权限以及操作日志相关的维护)
	 */
	BACKSTAGE_MANAGER("后台管理"), // 后台管理左边导航功能...
	// 后台管理-登录平台
	LOGINPLATFORM_LIST("查看登录平台", AuthResource.BACKSTAGE_MANAGER), // 查看登录平台
	LOGINPLATFORM_ADD("添加登录平台", AuthResource.BACKSTAGE_MANAGER), // 添加登录平台
	LOGINPLATFORM_EDIT("编辑登录平台", AuthResource.BACKSTAGE_MANAGER), // 编辑登录平台
	LOGINPLATFORM_DELETE("删除登录平台", AuthResource.BACKSTAGE_MANAGER), // 删除登录平台
	// 后台管理-账号管理
	USER_ADD("添加账号", AuthResource.BACKSTAGE_MANAGER), // 添加账号
	USER_DELETE("删除账号", AuthResource.BACKSTAGE_MANAGER), // 删除账号
	USER_EDIT("编辑账号", AuthResource.BACKSTAGE_MANAGER), // 编辑账号
	USER_LIST("查看账号", AuthResource.BACKSTAGE_MANAGER), // 查看账号
	// 后台管理-权限管理
	AUTH_GROUP_ADD("添加权限组", AuthResource.BACKSTAGE_MANAGER), // 添加权限组
	AUTH_GROUP_DELETE("删除权限组", AuthResource.BACKSTAGE_MANAGER), // 删除权限组
	AUTH_GROUP_EDIT("编辑权限组", AuthResource.BACKSTAGE_MANAGER), // 编辑权限组
	AUTH_GROUP_LIST("查看权限组", AuthResource.BACKSTAGE_MANAGER), // 查看权限组
	// 后台管理-访问限制
	ACCESS_LIST("查看访问限制", AuthResource.BACKSTAGE_MANAGER), // 查看访问限制
	ACCESS_ADD("添加访问限制", AuthResource.BACKSTAGE_MANAGER), // 添加访问限制
	ACCESS_EDIT("编辑访问限制", AuthResource.BACKSTAGE_MANAGER), // 编辑访问限制
	ACCESS_DELETE("删除访问限制", AuthResource.BACKSTAGE_MANAGER), // 删除访问限制
	// 后台管理-操作日志
	LOGGER_LIST("查看操作日志", AuthResource.BACKSTAGE_MANAGER), // 访问操作日志
	// 个人信息-修改密码
	MODIFY_PASSWORD("修改密码", AuthResource.BACKSTAGE_MANAGER),// 修改密码
	;

	private final String key;
	private final AuthResource classify;// 导航大类

	private AuthResource(String key) {
		this(key, null);
	}

	private AuthResource(String key, AuthResource classify) {
		this.key = key;
		this.classify = classify;
	}

	public String getKey() {
		return key;
	}

	public AuthResource getClassify() {
		return classify;
	}

	public String getCode() {
		return name();
	}
}