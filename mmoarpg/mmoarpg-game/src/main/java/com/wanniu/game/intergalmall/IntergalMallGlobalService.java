package com.wanniu.game.intergalmall;

import java.util.Map;

import com.wanniu.core.db.GCache;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.ConstsTR;

/**
 * 积分商城--全服限购
 * 
 * @author Yangzz
 *
 */
public class IntergalMallGlobalService {

	private IntergalMallGlobalService() {

	}

	public static class Holder {
		public static final IntergalMallGlobalService instance = new IntergalMallGlobalService();
	}

	public static IntergalMallGlobalService getInstance() {
		return Holder.instance;
	}

	/**
	 * 每日0点重置购买次数
	 */
	public void refreshNewDay() {
		Map<String, String> nums = GCache.hgetAll(ConstsTR.intergalMallGlobalTR.value);
		if (nums == null) {
			return;
		}
		for (String key : nums.keySet()) {
			GCache.hset(ConstsTR.intergalMallGlobalTR.value, key, String.valueOf(0));
		}
	}

	public int getGlobalNum(int shopType, int itemId) {
		int result = 0;
		String str = GCache.hget(ConstsTR.intergalMallGlobalTR.value, shopType + "/" + itemId);
		if (StringUtil.isNotEmpty(str)) {
			result = Integer.parseInt(str);
		}
		return result;
	}

	// /**
	// * 更新数据库
	// */
	// public void update(int shopType, int itemId, int globalNum) {
	// GCache.hset(ConstsTR.intergalMallGlobalTR.value, shopType + "/" + itemId,
	// String.valueOf(globalNum));
	// }
}
