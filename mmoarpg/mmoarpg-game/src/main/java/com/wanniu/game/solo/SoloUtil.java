package com.wanniu.game.solo;

import java.util.Date;

import com.wanniu.core.util.RandomUtil;
import com.wanniu.game.area.MonsterUnit;
import com.wanniu.game.common.Const;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.SoloMonsterCO;
import com.wanniu.game.data.SoloRankCO;

public class SoloUtil {
	// 计算排行榜的排序值
	public static int calRankScore(int rankId, int starPoint, Date rankGotTime) {
		// Redis zset的排序score值最大支持2^53，所以，
		// 右边32位保存时间权值，左边21位保存段位权值，
		// 即，最大可以保存2^21-1(209W+)段位星级。

		int maxRankStar = 10;

		// 段位权值
		int rankValue = rankId * (maxRankStar + 1) + starPoint;
		// 时间权值
		int timeValue = (int) ((Math.pow(2, 32) - 1) - Math.floor(rankGotTime.getTime() / 1000));

		return (int) (rankValue * Math.pow(2, 32) + timeValue);
	};

	// 根据段位获取最高星级
	public static int getRankStar(int rankId) {
		SoloRankCO prop = GameData.SoloRanks.get(rankId);
		// return prop!=null ? prop.rankStar : 0;
		return 0;
	};

	public static int diffDays(Date t1, Date t2) {
		long diff = t2.getTime() - t1.getTime();
		return (int) (diff / (24 * 3600 * 1000));
	}

	// public static void diffDays (long t1,long t2) {
	// t1 = +new Date((new Date(t1)).toLocaleDateString());
	// t2 = +new Date((new Date(t2)).toLocaleDateString());
	// return (t2 - t1) / (24*3600*1000);
	// };

	public static SoloMonsterCO getSoloMonsterPropByPro(int pro) {
		int size = GameData.SoloMonsters.size();
		SoloMonsterCO robot = null;
		while (robot == null || robot.availably == 0) {
			robot = GameData.SoloMonsters.get(RandomUtil.getInt(size));
		}
		return robot;
	};

	public static MonsterUnit getRobot(int pro) {
		SoloMonsterCO soloProp = SoloUtil.getSoloMonsterPropByPro(pro);
		String startPoint = String.valueOf(soloProp.startPoint);

		MonsterUnit monsterData = new MonsterUnit();
		monsterData.id = soloProp.monID;
		monsterData.force = Const.AreaForce.FORCEB.value;
		monsterData.flag = startPoint;
		monsterData.autoGuard = true;

		return monsterData;
	}

}
