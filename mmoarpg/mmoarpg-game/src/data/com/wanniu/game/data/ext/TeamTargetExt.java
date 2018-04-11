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
package com.wanniu.game.data.ext;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomUtils;

import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.data.TeamTargetCO;

/**
 * 
 *
 * @author 周明凯(zhoumingkai@qeng.cn)
 */
public class TeamTargetExt extends TeamTargetCO {

	public Map<PlayerBtlData, Integer> randomAttrs() {
		Map<PlayerBtlData, Integer> result = new HashMap<>();
		if (min1 > 0 && max1 >= min1) {
			PlayerBtlData attr1 = Const.PlayerBtlData.getE(prop1);
			if (attr1 != null) {
				result.put(attr1, RandomUtils.nextInt(min1, max1));
			}
		}

		if (min2 > 0 && max2 >= min2) {
			PlayerBtlData attr2 = Const.PlayerBtlData.getE(prop2);
			if (attr2 != null) {
				result.put(attr2, RandomUtils.nextInt(min2, max2));
			}
		}

		if (min3 > 0 && max3 >= min3) {
			PlayerBtlData attr3 = Const.PlayerBtlData.getE(prop3);
			if (attr3 != null) {
				result.put(attr3, RandomUtils.nextInt(min3, max3));
			}
		}
		return result;
	}
}
