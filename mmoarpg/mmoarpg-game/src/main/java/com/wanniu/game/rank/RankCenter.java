package com.wanniu.game.rank;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wanniu.core.db.GCache;
import com.wanniu.core.game.JobFactory;
import com.wanniu.core.logfs.Out;
import com.wanniu.game.GWorld;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.GOODS_CHANGE_TYPE;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.mail.MailUtil;
import com.wanniu.game.mail.SysMailConst;
import com.wanniu.game.mail.data.MailData.Attachment;
import com.wanniu.game.mail.data.MailSysData;
import com.wanniu.game.player.GlobalConfig;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.poes.PlayerRankInfoPO;
import com.wanniu.redis.PlayerPOManager;

public class RankCenter {
	private static final RankCenter instance = new RankCenter();

	public static RankCenter getInstance() {
		return instance;
	}

	public PlayerRankInfoPO findRankPO(String playerId) {
		return PlayerPOManager.findPO(ConstsTR.playerRankTR, playerId, PlayerRankInfoPO.class);
	}

	/**
	 * 启服需要把排行榜数据预热.
	 */
	public void init() {
		String accountTime = GlobalConfig.WorldExp_Bonus_AccountTime;
		String[] rewardTime = accountTime.split("-");
		if (3 == rewardTime.length) {
			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(rewardTime[0]));
			c.set(Calendar.MINUTE, Integer.parseInt(rewardTime[1]));
			c.set(Calendar.SECOND, Integer.parseInt(rewardTime[2]));
			c.set(Calendar.MILLISECOND, 0);
			Date nowDate = new Date();
			long diffTime = c.getTimeInMillis() - nowDate.getTime();

			if (diffTime < 0) {
				diffTime += Const.Time.Day.getValue();
			}

			JobFactory.addDelayJob(() -> {
				this.worldLevelReward();

				JobFactory.addFixedRateJob(() -> {
					this.worldLevelReward();
				}, Const.Time.Day.getValue(), Const.Time.Day.getValue());

			}, diffTime);

		}
	}

	/**
	 * 获取本服排名第一的成员ID
	 */
	public String getFirstRankMemberId(RankType type, int serverId) {
		Set<String> members = GCache.zrevrange(type.getRedisKey(serverId, 0), 0, 0);
		return members.isEmpty() ? "" : members.iterator().next();
	}

	public void worldLevelReward() {
		Out.info("send world level award...");
		String itemList = GlobalConfig.WorldExp_Winner_ItemList;
		List<Attachment> items = new ArrayList<>();
		String[] itemLists = itemList.split(",");
		for (String code : itemLists) {
			Attachment attachment = new Attachment();
			attachment.itemCode = code;
			attachment.itemNum = 1;
			items.add(attachment);
		}

		String playerId = RankType.LEVEL.getHandler().getFirstRankMemberId(GWorld.__SERVER_ID);
		if (PlayerUtil.isPlayerOpenedFunction(playerId, Const.FunctionType.WORLD_EXP.getValue())) {

			MailSysData mailData = new MailSysData(SysMailConst.WORLDEXP_REWARD);
			mailData.attachments = items;
			MailUtil.getInstance().sendMailToOnePlayer(playerId, mailData, GOODS_CHANGE_TYPE.WORLD_LEVEL);
		}
	}

	public static void delRoleClear(String playerId) {
		for (RankType type : RankType.values()) {
			if (type.getHandler() != null) {
				type.getHandler().delRankMember(GWorld.__SERVER_ID, playerId);
			}
		}
	}

	public Map<Integer, SimpleRankData> getSimpleRankData(RankType type, int minRank, int maxRank) {

		return null;
	}

	/**
	 * 自己的score变更，刷新所有好友各自的好友排行数据
	 * 
	 * @param kind
	 */
	// public long updateFriendsRank(Const.LeaderBoardType leaderBoardType,
	// LeaderBoardInfo info) {
	// FriendManager mgr = FriendsCenter.getInstance().getFriendsMgr(info.selfId);
	// if (null == mgr) {
	// Out.error("[" , this.getClass() , "] ", "leaderBoard addFriendRank
	// err,playerId = ", info.selfId);
	// return 0;
	// }
	// Map<String, FriendData> friends = mgr.po.friends;
	// if (null != friends) {
	// // 自己积分变更，刷新好友榜单中自己的积分
	// for (String friendId : friends.keySet()) {
	// String key = getKey(leaderBoardType, friendId);
	// return update(key, info);
	// }
	// }
	// // 更新自己好友榜单中自己的积分
	// return update(getKey(leaderBoardType, info.selfId), info);
	// };
}