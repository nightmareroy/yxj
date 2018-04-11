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

/**
 * 日志大类.
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
public enum LogClassify {
	LOGIN("登录相关"), // 登录大类

	SURVEY("概况相关"), //
	SURVEY_TIME("实时数据"), //
	STATISTICS("统计分析"), //

	DATA_WAREHOUSE("数据仓库"),

	LOG_QUERY("查询操作"), // 查询系统产生的操作大类.

	// ~~~~~~~~~游戏管理(GAME)~~~~~~~~~~~~~//
	GAME_QUERY("游戏查询"), //
	LOGIN_NOTICE("登录公告管理"), // 登录公告管理
	ROLL_NOTICE("滚动公告管理"), // 滚动公告管理
	GAME_NOTICE("游戏内公告管理"), // 游戏内公告管理
	PUBLISH("处罚管理"), // 惩处管理
	MAIL_MANAGE("福利管理"), // 福利申请
	CDKEY_MANAGE("CDKEY管理"), //
	RECHARGE("充值补单"), // 充值补单

	MAINTAIN("运维工具"), // 运维工具
	MAINTAIN_AUTOMATION("智能运维"), // 运维工具
	MONITOR("监控系统"), // 监控系统
	BACKSTAGE("后台管理"), //
	USER_MANAGE("账号管理"), //
	AUTH_MANAGE("权限管理"); //

	private final String key;

	private LogClassify(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}