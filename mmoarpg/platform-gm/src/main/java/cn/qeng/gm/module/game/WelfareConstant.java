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
package cn.qeng.gm.module.game;

/**
 * 福利相关的常量类.
 *
 * @since 2.0
 * @author 小流氓(176543888@qq.com)
 */
public class WelfareConstant {
	/**
	 * 待审批
	 */
	public static final int PENDING_APPROVAL = 0;
	/**
	 * 已审批
	 */
	public static final int ALREADY_APPROVE = 1;
	/**
	 * 已拒绝
	 */
	public static final int HAS_REFUSED = 2;
	/**
	 * 失败了
	 */
	public static final int MAIL_SEND_FAIL = 3;
	/**
	 * 回收了
	 */
	public static final int MAIL_SEND_BACK = 4;
	/**
	 * 回收失败
	 */
	public static final int MAIL_SEND_BACK_FAIL = 5;
	/**
	 * 线上全服未删除
	 */
	public static final int MAIL_NOT_ALL_DELETE_ONLINE = 0;
	/**
	 * 线上单个未删除
	 */
	public static final int MAIL_NOT_DELETE_ONLINE = 1;
	/**
	 * 删除成功
	 */
	public static final int MAIL_DELETE_ONLINE_SUCCESS = 2;
	/**
	 * 删除失败
	 */
	public static final int MAIL_DELETE_ONLINE_FAIL = 3;
	/**
	 * 发送邮件成功
	 */
	public static final int MAIL_SEND_SUCCESS = 1;
	/**
	 * 删除邮件失败
	 */
	public static final int MAIL_DELETE_FAIL = 0;
	
	/**
	 * 尚未发送邮件到玩家
	 */
	public static final int POST_WAITTING = 0;
	/**
	 * 已发送邮件到玩家
	 */
	public static final int POST_SUCCESS = 1;
	/**
	 * 发送邮件到玩家失败
	 */
	public static final int POST_FALSE = 2;
}