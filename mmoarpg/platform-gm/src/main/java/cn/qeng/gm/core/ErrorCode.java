/*
 * Copyright © 2015 www.noark.xyz All Rights Reserved.
 * 
 * 感谢您选择Noark框架，希望我们的努力能为您提供一个简单、易用、稳定的服务器端框架 ！
 * 除非符合Noark许可协议，否则不得使用该文件，您可以下载许可协议文件：
 * 
 * 		http://www.noark.xyz/LICENSE
 *
 * 1.未经许可，任何公司及个人不得以任何方式或理由对本框架进行修改、使用和传播;
 * 2.禁止在本项目或任何子项目的基础上发展任何派生版本、修改版本或第三方版本;
 * 3.无论你对源代码做出任何修改和改进，版权都归Noark研发团队所有，我们保留所有权利;
 * 4.凡侵犯Noark版权等知识产权的，必依法追究其法律责任，特此郑重法律声明！
 */
package cn.qeng.gm.core;

/**
 * 错误编码.
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
public class ErrorCode {
	/**
	 * 操作成功
	 */
	public static final int OK = 0;
	/**
	 * 非法操作
	 */
	public static final int ERROR = 1;

	/**
	 * 服务器未找到，用来表示正在维护.
	 */
	public static final String SERVER_NOT_FOUND = "SERVER_NOT_FOUND";
	/**
	 * 指定目标未找到，用来表示查询无果.
	 */
	public static final String TARGET_NOT_FOUND = "TARGET_NOT_FOUND";
	/**
	 * 玩家未找到，请确认输出的玩家名称.
	 */
	public static final String PLAYER_NOT_FOUND = "PLAYER_NOT_FOUND";

	/**
	 * 登录操作
	 */
	// 没有这个用户名称
	public static final int ACCOUNT_DOES_NOT_EXIST = 1001;
	// 密码错误
	public static final int PASSWORD_ERROR = 1002;
	/** 账号已锁定 */
	public static final int ACCOUNT_STATUS_LOCK = 1003;
	/** 连错10次，那10分钟后再尝试... */
	public static final int ACCOUNT_STATUS_EXCEPTION = 1004;
	/** IP访问限制 */
	public static final int IP_ACCESS_RESTRICTION = 1005;

	/** 没有找到该玩家 */
	public static final int THE_PLAYER_NOT_FOUND = 3;
}