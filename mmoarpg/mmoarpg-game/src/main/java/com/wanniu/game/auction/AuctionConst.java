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
package com.wanniu.game.auction;

/**
 * 竞拍常量类.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
public class AuctionConst {

	/**
	 * 仙盟竞拍大类.
	 */
	public static final int TYPE_GUILD_AUCTION = 1;
	/**
	 * 世界竞拍大类.
	 */
	public static final int TYPE_WORLD_AUCTION = 2;
	/**
	 * 我的竞拍大类.
	 */
	public static final int TYPE_SELF_AUCTION = 3;

	/**
	 * 竟拍物品状态 1=展示
	 */
	public static final int STATE_SHOW = 1;
	/**
	 * 竟拍物品状态 2=竟拍中
	 */
	public static final int STATE_AUCTION = 2;

	/**
	 * 1 [{RecordTime}] {Role1}以{Num}绑元竞价拍得{item} 竞拍
	 */
	public static final int LOG_TYPE_AUCTION_CUR = 1;
	/**
	 * 2 [{RecordTime}] {Role1}以{Num}绑元一口价拍得{item} 一口价
	 */
	public static final int LOG_TYPE_AUCTION_MAX = 2;
	/**
	 * 3 [{RecordTime}] {item}无人竞拍，流入世界拍卖中 流拍
	 */
	public static final int LOG_TYPE_NOT_AUCTION = 3;
}
