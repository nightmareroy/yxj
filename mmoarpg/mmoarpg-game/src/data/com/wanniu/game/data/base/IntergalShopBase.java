package com.wanniu.game.data.base;

import java.util.Date;

import com.wanniu.core.util.DateUtil;
import com.wanniu.core.util.StringUtil;

/**
 * 积分商城父类
 * 
 * @author Yangzz
 *
 */
public class IntergalShopBase {

	/** 编号 */
	public int iD;
	/** 道具编号 */
	public String itemCode;
	/** 道具名称 */
	public String name;
	/** 是否有效 */
	public int isShow;
	/** 购买后是否绑定 */
	public int isBind;
	/** 数量 */
	public int num;
	/** 原价 */
	public int price;
	/** 限时起点 */
	public String periodStart;
	/** 限时终点 */
	public String periodEnd;
	/** 限时折扣价 */
	public int price2;
	/** 每天可购买次数 */
	public int buyTimes;
	/** 购买等级 */
	public int reqLvl;
	/** 商品角标 */
	public int series;
	/** 是否显示倒计时 */
	public int countDown;
	/** 是否全服限购 */
	public int serveLimit;
	/** 全服限购数量 */
	public int serveBuyTimes;

	/** 限时起点 */
	public Date periodStartDate;
	/** 限时终点 */
	public Date periodEndDate;

	/** 主键 */
	public int getKey() {
		return this.iD;
	}

	/** 构造属性 */
	public void initProperty() {
		if (StringUtil.isNotEmpty(periodStart)) {
			periodStartDate = DateUtil.format(periodStart);
		}
		if (StringUtil.isNotEmpty(periodEnd)) {
			periodEndDate = DateUtil.format(periodEnd);
		}
	}

	/** 构造前置属性 */
	public void beforeProperty() {
	}
}
