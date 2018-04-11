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
package com.wanniu.game.poes;

import java.time.LocalDateTime;
import java.util.Set;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBField;
import com.wanniu.game.item.po.PlayerItemPO;

/**
 * 竟拍物品包装类.
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
public class AuctionItemPO extends GEntity {
	@DBField(isPKey = true, fieldType = "varchar", size = 50)
	public String id;
	public PlayerItemPO db;

	public int state;// 竞拍状态（1=展示，2=竞拍中，3=已结束）
	public LocalDateTime stateOverTime;// 状态结束时间

	public int curPrice;// 竞价
	public int nextPrice;// 再次竞价
	public int maxPrice;// 一口价

	public String guildId;// 仙盟ID

	public String playerId;// 竟拍人
	public int diamond;// 竟拍时所花充值元宝
	public int ticket;// 竟拍时间所花绑定元宝
	public Set<String> participant;// 所有参与者
	public String source;// 来源
}