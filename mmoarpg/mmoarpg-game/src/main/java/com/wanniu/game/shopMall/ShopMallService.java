package com.wanniu.game.shopMall;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.wanniu.core.GGame;
import com.wanniu.core.game.JobFactory;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const;
import com.wanniu.game.player.WNPlayer;

/**
 * 商城
 * 
 * @author Yangzz
 *
 */
public class ShopMallService {

	private ShopMallService() {
		resetWeekBuy();
	}

	private final static class Holder {
		public static final ShopMallService instance = new ShopMallService();
	}

	public static ShopMallService getInstance() {
		return Holder.instance;
	}

	public Date getResetTime() {
		Calendar monDay = Calendar.getInstance();
		monDay.set(Calendar.DAY_OF_WEEK, 2); // 1是从星期天算起，周一是2
		monDay.set(Calendar.HOUR_OF_DAY, 5);
		monDay.set(Calendar.MINUTE, 0);
		monDay.set(Calendar.SECOND, 0);
		monDay.set(Calendar.MILLISECOND, 0);
		return monDay.getTime();
	}

	/**
	 * 重置每周限购 每周一早上5点重置
	 */
	private void resetWeekBuy() {
		Date monday = getResetTime();

		long delay = monday.getTime() - System.currentTimeMillis();
		if (delay < 0) {
			delay += Const.Time.Day.getValue()*7;
		}
		Out.info("shopMall resetWeekBuy delay : ", delay);
		JobFactory.addFixedRateJob(() -> {
			Out.info("resetWeekBuy...");

			Map<String, GPlayer> players = GGame.getInstance().getOnlinePlayers();
			WNPlayer player = null;
			for (GPlayer gplayer : players.values()) {
				player = (WNPlayer) gplayer;

				player.shopMallManager.refreshNewWeek();
			}

		}, delay, Const.Time.Day.getValue());
	}
}
