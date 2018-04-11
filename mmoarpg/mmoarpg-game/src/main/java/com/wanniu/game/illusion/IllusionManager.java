package com.wanniu.game.illusion;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DateUtil;
import com.wanniu.game.area.Area;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.DateUtils;
import com.wanniu.game.data.ExpReduceCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.GoldReduceCO;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.IllusionPO;

import pomelo.area.FightLevelHandler.Illusion2Push;
import pomelo.area.FightLevelHandler.IllusionPush;
import pomelo.area.FightLevelHandler.MJItemMax;

/**
 * 幻境
 * 
 * @author Yangzz
 *
 */
public class IllusionManager {

	public WNPlayer player;

	/** 奖励序列化数据 */
	public IllusionPO illusionPO;

	public IllusionManager(WNPlayer player, IllusionPO illusionPO) {
		this.player = player;
		this.illusionPO = illusionPO;

		if (this.illusionPO == null) {
			this.illusionPO = new IllusionPO();
		}
	}

	public int addAward(String code, int value) {
		if (code.equals("gold")) {
			return addGold(value);
		} else if (code.equals("exp")) {
			return addExp(value);
		} else if (code.equals("upexp")) {
			return addClassExp(value);
		}
		return value;
	}

	/**
	 * 增加并返回 增加的经验
	 */
	public int addExp(int exp) {
		// 模板
		ExpReduceCO limit = this.getExpReduceCO(player.getLevel());
		if (limit == null) {
			return 0;
		}

		int addExp = exp;
		// 80%衰减比例
		if (illusionPO.todayExp >= limit.rate4) {
			addExp = (int) (addExp * 0.2F);
		}
		// 60%衰减比例
		else if (illusionPO.todayExp >= limit.rate3) {
			addExp = (int) (addExp * 0.4F);
		}
		// 40%衰减比例
		else if (illusionPO.todayExp >= limit.rate2) {
			addExp = (int) (addExp * 0.6F);
		}
		// 20%衰减比例
		else if (illusionPO.todayExp >= limit.rate1) {
			addExp = (int) (addExp * 0.8F);
		}
		// 不衰减
		else {
			addExp = addExp * 1;
		}

		this.illusionPO.todayExp += addExp;

		if (addExp > 0) {
			pushChage();
		}
		return addExp;
	}

	private ExpReduceCO getExpReduceCO(int level) {
		List<ExpReduceCO> ts = GameData.findExpReduces(v -> v.minLv <= level && level <= v.maxLv);
		return ts.size() == 1 ? ts.get(0) : null;
	}

	/**
	 * 增加并返回 增加的金币
	 */
	public int addGold(int gold) {
		// 模板
		GoldReduceCO limit = this.getGoldReduceCO(player.getLevel());
		if (limit == null) {
			return 0;
		}

		int addGold = gold;
		// 80%衰减比例
		if (illusionPO.todayGold >= limit.rate4) {
			addGold = (int) (addGold * 0.2F);
		}
		// 60%衰减比例
		else if (illusionPO.todayGold >= limit.rate3) {
			addGold = (int) (addGold * 0.4F);
		}
		// 40%衰减比例
		else if (illusionPO.todayGold >= limit.rate2) {
			addGold = (int) (addGold * 0.6F);
		}
		// 20%衰减比例
		else if (illusionPO.todayGold >= limit.rate1) {
			addGold = (int) (addGold * 0.8F);
		}
		// 不衰减
		else {
			addGold = addGold * 1;
		}

		this.illusionPO.todayGold += addGold;

		if (addGold > 0) {
			pushChage();
		}
		return addGold;
	}

	private GoldReduceCO getGoldReduceCO(int level) {
		List<GoldReduceCO> ts = GameData.findGoldReduces(v -> v.minLv <= level && level <= v.maxLv);
		return ts.size() == 1 ? ts.get(0) : null;
	}

	/**
	 * 增加并返回 增加的修为
	 */
	public int addClassExp(int classExp) {
		return 0;
	}

	public void pushChage() {
		IllusionPush.Builder push = IllusionPush.newBuilder();
		push.setS2CTodayExp(this.illusionPO.todayExp);
		push.setS2CTodayClassexp(this.illusionPO.todayClassExp);
		push.setS2CTodayGold(this.illusionPO.todayGold);
		push.setS2CMaxExp(0);
		push.setS2CMaxClassexp(0);
		push.setS2CMaxGold(0);
		this.player.receive("area.fightLevelPush.illusionPush", push.build());
	}

	/**
	 * 0 点重置
	 */
	public void refreshNewDay() {
		if (illusionPO != null) {
			boolean flag = illusionPO.todayExp > 0 || illusionPO.todayClassExp > 0 || illusionPO.todayGold > 0;
			illusionPO.todayExp = 0;
			illusionPO.todayClassExp = 0;
			illusionPO.todayGold = 0;

			boolean flag2 = illusionPO.hasBoxData();
			illusionPO.resetBoxData();

			boolean flag3 = illusionPO.hasItemData();
			illusionPO.resetItemData();

			if (flag) {
				pushChage();
			}
			if (flag2 || flag3) {
				pushChageBy2();
			}
		}
	}

	/**
	 * 2号类型的玩法.
	 */
	public void pushChageBy2() {
		Illusion2Push.Builder push = Illusion2Push.newBuilder();
		Map<Integer, Integer> boxs = illusionPO.boxs;
		Map<String, Integer> items = illusionPO.items;
		Integer lv1 = null;
		Integer lv2 = null;
		Integer lv3 = null;
		if (boxs != null) {
			lv1 = boxs.get(1);
			lv2 = boxs.get(2);
			lv3 = boxs.get(3);
		}
		push.setS2CTodayLv1(lv1 == null ? 0 : lv1);
		push.setS2CTodayLv2(lv2 == null ? 0 : lv2);
		push.setS2CTodayLv3(lv3 == null ? 0 : lv3);
		push.setS2CMaxNum(GlobalConfig.Mysterious_MaxNumEveryday);
		if (items != null) {
			Set<Entry<String, Integer>> sets = items.entrySet();
			for (Entry<String, Integer> e : sets) {
				Integer vl = GlobalConfig.getItemCount(e.getKey());
				if (vl != null) {
					MJItemMax.Builder bd = MJItemMax.newBuilder();
					bd.setItemCode(e.getKey());
					bd.setS2CMaxMl(vl);
					bd.setS2CTodayMl(e.getValue());
					push.addItemInfo(bd);
				}
			}
		}
		this.player.receive("area.fightLevelPush.illusion2Push", push.build());
	}

	/**
	 * 秘境现在都没有怪了（策划说以后不配怪物了）
	 * 
	 * @return
	 */
	public boolean canMonsterDrop() {
		return illusionPO.calTotalNum() < GlobalConfig.Mysterious_MaxNumEveryday;
	}

	/**
	 * 2号类型的玩法能不能掉落。
	 */
	public boolean canNotDropBy2(String itemcode) {
		Integer lv = GlobalConfig.getMysteriousLv(itemcode);
		if (lv != null) {// 属于宝箱类的,看看今日获取满了没
			return illusionPO.calTotalNum() >= GlobalConfig.Mysterious_MaxNumEveryday;
		}

		Integer itemCount = GlobalConfig.getItemCount(itemcode);
		if (itemCount != null) {// 普通道具类的看看满了没
			return illusionPO.calTotalItemNum(itemcode) >= itemCount;
		}
		// 啥都不属于那肯定可以掉落了
		return false;
	}

	public boolean addItemNum(Area area, String itemcode, int num) {
		if (area.sceneType == Const.SCENE_TYPE.ILLUSION_2.getValue()) {
			Integer lv = GlobalConfig.getMysteriousLv(itemcode);
			Integer itemCount = GlobalConfig.getItemCount(itemcode);
			if (lv == null && itemCount == null) {
				// Out.warn("采集掉落的物品在配表里找不到:code=", itemcode); 经常掉落找不到的配置（有可能会配别的掉落,所以太多了这样的日志）
				return true;
			}
			if (canNotDropBy2(itemcode)) {
				Out.info("幻境2玩法获得超出上限了.playerId=", player.getId(), ",num=", illusionPO.calTotalNum(), ",max=", GlobalConfig.Mysterious_MaxNumEveryday);
				return false;
			}
			if (lv != null) {
				illusionPO.putBox(lv, num);
			} else if (itemCount != null) {
				illusionPO.putItem(itemcode, num);
			}
			this.pushChageBy2();
			Out.info("幻境2玩法计数加加，playerId=", player.getId(), ",itemcode=", itemcode, ",itemnum=", num);
		}
		return true;
	}
	
	public boolean isInDouble() {
		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		Calendar nowDate = Calendar.getInstance();
		try {
			startDate.setTime(DateUtils.parse(GlobalConfig.DoubleGet_Begin, DateUtil.F_yyyyMMddHHmmss));
			endDate.setTime(DateUtils.parse(GlobalConfig.DoubleGet_End, DateUtil.F_yyyyMMddHHmmss));
			nowDate.setTime(new Date());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		if (nowDate.before(startDate)) {
			return false;
		}
		if (nowDate.after(endDate)) {
			return false;
		}
		return true;
	}
}