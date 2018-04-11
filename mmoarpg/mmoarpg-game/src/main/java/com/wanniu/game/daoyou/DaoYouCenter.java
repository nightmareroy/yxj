package com.wanniu.game.daoyou;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.wanniu.core.game.JobFactory;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DateUtil;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.daoyou.dao.DaoYouDao;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.TeamPropExt;
import com.wanniu.game.player.AttributeUtil;
import com.wanniu.game.poes.DaoYouMemberPO;
import com.wanniu.game.poes.DaoYouPO;
import com.wanniu.game.rank.RankType;

/**
 * @author wanghaitao
 *
 */
public class DaoYouCenter {

	private static DaoYouCenter instance;

	public static DaoYouCenter getInstance() {
		if (instance == null) {
			instance = new DaoYouCenter();
		}
		return instance;
	}

	/** 道友集合<道友ID,道友信息> */
	public ConcurrentHashMap<String, DaoYouPO> daoYouMap;

	/** 道友名称集合<道友名称,道友ID> */
	public ConcurrentHashMap<String, String> daoYouNameMap;

	/** 道友成员集合<道友成员玩家ID,道友成员信息> */
	public ConcurrentHashMap<String, DaoYouMemberPO> daoYouMemberMap;

	/** 道友和道友成员集合<道友ID,<道友成员玩家ID>> */
	public ConcurrentHashMap<String, List<String>> daoYouMemberIdMap;

	/** 道友和道友增加的属性集合<道友ID,<属性,属性值>> */
	public ConcurrentHashMap<String, Map<PlayerBtlData, Integer>> daoYouBtlAdd;

	private DaoYouCenter() {
		daoYouMap = new ConcurrentHashMap<>();
		daoYouNameMap = new ConcurrentHashMap<>();
		daoYouMemberMap = new ConcurrentHashMap<>();
		daoYouMemberIdMap = new ConcurrentHashMap<>();
		daoYouBtlAdd = new ConcurrentHashMap<>();
		init();
	}

	/**
	 * 初始化
	 */
	public void init() {
		List<DaoYouMemberPO> daoYouMemberList = DaoYouDao.getAllDaoYouMember();
		for (DaoYouMemberPO dymp : daoYouMemberList) {
			addDaoYouMember(dymp, true);
		}

		List<DaoYouPO> daoYouList = DaoYouDao.getAllDaoYouList();
		for (DaoYouPO dyp : daoYouList) {
			addDaoYou(dyp, true);

			// 计算道友增加的属性
			calDaoYouData(dyp.id);
		}

		// 发放返还
		long period = 24 * 60 * 60 * 1000;
		long initialDelay = DateUtil.getFiveDelay();
		JobFactory.addFixedRateJob(new Runnable() {

			@Override
			public void run() {
				try {
					ConcurrentHashMap<String, DaoYouMemberPO> daoYouMem = DaoYouCenter.getInstance().daoYouMemberMap;
					Iterator<Entry<String, DaoYouMemberPO>> daoYouMemIt = daoYouMem.entrySet().iterator();
					while (daoYouMemIt.hasNext()) {
						Entry<String, DaoYouMemberPO> daoYouMemEntry = daoYouMemIt.next();
						String daoYouMemPlayer = daoYouMemEntry.getKey();
						DaoYouMemberPO dymp = daoYouMemEntry.getValue();
						int needReciveRebate = dymp.todayReciveRebate;
						Map<String, Integer> todaySendRebate = dymp.todaySendRebate;
						int todaySendRebateNumber = 0;
						if (todaySendRebate != null) {
							for (int sendRebateNumber : todaySendRebate.values()) {
								todaySendRebateNumber += sendRebateNumber;
							}
						}
						dymp.totalSendRebate += todaySendRebateNumber;
						dymp.todaySendRebate = null;
						dymp.totalReciveRebate += needReciveRebate;
						dymp.todayReciveRebate = 0;
						if (needReciveRebate > 0) {
							DaoYouService.getInstance().sendRebateMail(daoYouMemPlayer, needReciveRebate);
						}
						DaoYouDao.updateDaoYouMember(dymp);
					}

				} catch (Exception e) {
					Out.error(e);
				}
			}
		}, initialDelay, period);
	}

	/**
	 * 获取道友信息
	 * 
	 * @param daoYouId
	 * @return
	 */
	public DaoYouPO getDaoYou(String daoYouId) {
		return daoYouMap.get(daoYouId);
	}

	/**
	 * 获取道友ID
	 * 
	 * @param daoYouName
	 * @return
	 */
	public String getDaoYouId(String daoYouName) {
		return daoYouNameMap.get(daoYouName);
	}

	/**
	 * 获取道友成员信息
	 * 
	 * @param dyMemPlayerId
	 * @return
	 */
	public DaoYouMemberPO getDaoYouMember(String dyMemPlayerId) {
		return daoYouMemberMap.get(dyMemPlayerId);
	}

	/**
	 * 获取道友增加的属性
	 * 
	 * @param daoYouId
	 * @return
	 */
	public Map<PlayerBtlData, Integer> getDaoYouBtl(String daoYouId) {
		return daoYouBtlAdd.get(daoYouId);
	}

	/**
	 * 获取所有道友成员的玩家ID
	 * 
	 * @param daoYouId
	 * @return
	 */
	public List<String> getAllDaoYouMember(String daoYouId) {
		return daoYouMemberIdMap.get(daoYouId);
	}

	/**
	 * 增加道友
	 * 
	 * @param dyp
	 * @param isInit
	 */
	public void addDaoYou(DaoYouPO dyp, boolean isInit) {
		String dypId = dyp.id;
		if (daoYouMap.get(dypId) != null) {
			return;
		}
		daoYouMap.put(dypId, dyp);
		addDaoYouName(dyp.name, dypId);
		if (!isInit) {
			saveDaoYou(dyp);
		}
	}

	/**
	 * 增加道友名称
	 * 
	 * @param logicName
	 * @param daoYouId
	 */
	public void addDaoYouName(String logicName, String daoYouId) {
		if (logicName != null && !logicName.isEmpty() && daoYouId != null && !daoYouId.isEmpty()) {
			daoYouNameMap.put(logicName, daoYouId);
		}
	}

	/**
	 * 保存道友到redis
	 * 
	 * @param dyp
	 */
	public void saveDaoYou(DaoYouPO dyp) {
		if (dyp == null) {
			return;
		}
		DaoYouDao.updateDaoYou(dyp);
	}

	/**
	 * 增加道友成员
	 * 
	 * @param dymp
	 * @param isInit
	 */
	public void addDaoYouMember(DaoYouMemberPO dymp, boolean isInit) {
		String dyMemPlayerId = dymp.playerId;
		if (daoYouMemberMap.get(dyMemPlayerId) != null) {
			return;
		}
		daoYouMemberMap.put(dyMemPlayerId, dymp);
		String daoYouId = dymp.daoYouId;
		List<String> dyMemPIds = daoYouMemberIdMap.get(daoYouId);
		if (dyMemPIds == null) {
			dyMemPIds = new ArrayList<>();
		}
		String playerId = dymp.playerId;
		dyMemPIds.add(playerId);
		daoYouMemberIdMap.put(daoYouId, dyMemPIds);
		if (!isInit) {
			saveDaoYouMember(dymp);
		}
		calDaoYouData(daoYouId);
	}

	/**
	 * 
	 * 保存道友成员到redis
	 * 
	 * @param dymp
	 */
	public void saveDaoYouMember(DaoYouMemberPO dymp) {
		if (dymp == null) {
			return;
		}
		DaoYouDao.updateDaoYouMember(dymp);
	}

	/**
	 * 移除道友
	 * 
	 * @param daoYouId
	 */
	public void removeDaoYou(String daoYouId) {
		DaoYouPO dyp = daoYouMap.get(daoYouId);
		String dypName = dyp.name;
		daoYouNameMap.remove(dypName);
		daoYouMap.remove(daoYouId);
		daoYouBtlAdd.remove(daoYouId);
		DaoYouDao.removeDaoYou(dyp);
		// 从排行榜中移除
		RankType.DAOYOU.getHandler().delRankMember(GWorld.__SERVER_ID, daoYouId);
	}

	/**
	 * 移除道友成员
	 * 
	 * @param dyMemPlayerId
	 */
	public void removeDaoYouMember(String dyMemPlayerId) {
		DaoYouMemberPO dymp = daoYouMemberMap.get(dyMemPlayerId);
		if (dymp == null) {
			return;
		}
		String dyId = dymp.daoYouId;
		List<String> dyMemPlayerIds = daoYouMemberIdMap.get(dyId);
		if (dyMemPlayerIds.size() == 2) {// 当只有2个人时删除一个后只有一个玩家则认为道友解散
			daoYouMemberIdMap.remove(dyId);
			removeDaoYou(dyId);
			DaoYouMemberPO dympA = daoYouMemberMap.get(dyMemPlayerIds.get(0));
			DaoYouMemberPO dympB = daoYouMemberMap.get(dyMemPlayerIds.get(1));
			daoYouMemberMap.remove(dyMemPlayerIds.get(0));
			daoYouMemberMap.remove(dyMemPlayerIds.get(1));
			DaoYouDao.removeDaoYouMember(dympA);
			DaoYouDao.removeDaoYouMember(dympB);
		} else {
			dyMemPlayerIds.remove(dyMemPlayerId);
			calDaoYouData(dyId);
			daoYouMemberMap.remove(dyMemPlayerId);
			DaoYouDao.removeDaoYouMember(dymp);
		}
	}

	/**
	 * 计算道友带来的属性
	 * 
	 * @param daoYouId
	 * @return
	 */
	public void calDaoYouData(String daoYouId) {
		Map<PlayerBtlData, Integer> thisDaoYouBtlAdd = getDaoYouBtl(daoYouId);
		if (thisDaoYouBtlAdd == null) {
			thisDaoYouBtlAdd = new HashMap<>();
		} else {
			thisDaoYouBtlAdd.clear();
		}
		List<TeamPropExt> props = GameData.findTeamProps((t) -> t.iD == 1);
		if (props.isEmpty())
			return;
		TeamPropExt prop = props.get(0);
		List<String> dyMemPlayerId = getAllDaoYouMember(daoYouId);
		if (dyMemPlayerId == null) {
			Out.error(daoYouId, "============找不到成员");
			return;
		}
		int length = dyMemPlayerId.size();
		for (int i = 0; i < length - 1; i++) {
			AttributeUtil.addData2AllDataByKey(prop.attrs, thisDaoYouBtlAdd);
		}
		daoYouBtlAdd.put(daoYouId, thisDaoYouBtlAdd);
	}
}
