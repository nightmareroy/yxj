package com.wanniu.game.guild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wanniu.core.game.LangService;
import com.wanniu.core.tcp.protocol.Message;
import com.wanniu.core.util.StringUtil;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Utils;
import com.wanniu.game.common.Const.GuildJob;
import com.wanniu.game.data.BlessItemCO;
import com.wanniu.game.data.GDungeonCO;
import com.wanniu.game.data.GTechnologyItemCO;
import com.wanniu.game.data.GTechnologyLevelCO;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.GuildBuildingCO;
import com.wanniu.game.data.GuildContributeCO;
import com.wanniu.game.data.GuildLevelCO;
import com.wanniu.game.data.GuildPositionCO;
import com.wanniu.game.data.WareHouseLevelCO;
import com.wanniu.game.data.WareHouseValueCO;
import com.wanniu.game.data.ext.BlessBuffExt;
import com.wanniu.game.data.ext.BlessLevelExt;
import com.wanniu.game.data.ext.GBuffExt;
import com.wanniu.game.data.ext.GShopExt;
import com.wanniu.game.data.ext.GTechnologyExt;
import com.wanniu.game.data.ext.GuildSettingExt;
import com.wanniu.game.guild.dao.GuildApplyDao;
import com.wanniu.game.guild.dao.GuildBlessDao;
import com.wanniu.game.guild.dao.GuildDao;
import com.wanniu.game.guild.dao.GuildDepotDao;
import com.wanniu.game.guild.dao.GuildImpeachDao;
import com.wanniu.game.guild.dao.GuildMemberDao;
import com.wanniu.game.guild.guidDepot.GuildCond;
import com.wanniu.game.guild.guidDepot.GuildDepotCondition;
import com.wanniu.game.guild.guidDepot.GuildRecordData;
import com.wanniu.game.guild.guildImpeach.GuildImpeachData;
import com.wanniu.game.guild.po.GuildBlessPO;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.poes.GuildApplyPO;
import com.wanniu.game.poes.GuildDepotPO;
import com.wanniu.game.poes.GuildDungeonPO;
import com.wanniu.game.poes.GuildMemberPO;
import com.wanniu.game.poes.GuildPO;

import pomelo.guild.GuildManagerHandler.QualityCond;

public class GuildUtil {
	public static String getUpLevelName(int upLevel) {
		return "";
	}

	public static GuildSettingExt getGuildSettingExtProp() {
		Map<Integer, GuildSettingExt> list = GameData.GuildSettings;
		for (GuildSettingExt setting : list.values()) {
			return setting;
		}
		return null;
	}

	public static Map<Integer, GuildLevelCO> getGuileLevelPropList() {
		return GameData.GuildLevels;
	}

	public static GuildLevelCO getGuildLevelPropByLevel(int level) {
		return GameData.GuildLevels.get(level);
	}

	public static Map<Integer, GuildPositionCO> getGuildJobPositon() {
		return GameData.GuildPositions;
	}

	public static Map<Integer, String> getJobNameMap() {
		Map<Integer, GuildPositionCO> list = getGuildJobPositon();
		Map<Integer, String> jobNames = new HashMap<Integer, String>();
		for (Integer key : list.keySet()) {
			jobNames.put(key, list.get(key).position);
		}
		return jobNames;
	}

	public static GuildPositionCO getGuildJobPropByJobId(int job) {
		return GameData.GuildPositions.get(job);
	}

	public static Map<String, GuildContributeCO> getGuildContributePropMap() {
		return GameData.GuildContributes;
	}

	static GuildContributeCO getGuildContributePropByType(int type) {
		List<GuildContributeCO> list = GameData.findGuildContributes((t) -> {
			return t.type == type;
		});
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public static WareHouseLevelCO getDepotLevelProp(int level) {
		return GameData.WareHouseLevels.get(level);
	}

	public static WareHouseValueCO getDepotDepositValueProp(int level, int qColor) {
		List<WareHouseValueCO> list;
		list = GameData.findWareHouseValues((t) -> t.equipLv == level && t.equipColor == qColor);

		if (list.size() > 0) {
			return list.get(0);
		}

		return null;
	}

	public static GuildBuildingCO getGuildBuildingProp(int buildingId) {
		return GameData.GuildBuildings.get(buildingId);
	}

	public static List<BlessItemCO> getBlessItemListByLevel(int level) {
		return GameData.findBlessItems((t) -> {
			return t.blessLevel == level;
		});
	}

	public static BlessItemCO getBlessItemById(int id) {
		return GameData.BlessItems.get(id);
	}

	public static BlessLevelExt getBlessPropByLevel(int level) {
		return GameData.BlessLevels.get(level);
	}

	public static BlessBuffExt getBlessBuffProp(int buffID) {
		return GameData.BlessBuffs.get(buffID);// dataAccessor.guildBlessBuffProps.find({BlessBuffID
												// : buffID});
	}

	// 返回合并后的属性对象
	public static Map<String, Integer> getBlessBuffAttrs(List<Integer> buffIdArray) {
		Map<String, Integer> attrs = new HashMap<String, Integer>();
		for (int i = 0; i < buffIdArray.size(); ++i) {
			BlessBuffExt buffProp = getBlessBuffProp(buffIdArray.get(i));
			String attrKey = buffProp.attr.getString("attrKey");
			int attrValue = buffProp.attr.getIntValue("attrValue");

			if (!attrs.containsKey(attrKey)) {
				attrs.put(attrKey, attrValue);
			} else {
				int tmpValue = attrs.get(attrKey);
				attrs.put(attrKey, tmpValue + attrValue);
			}
		}
		return attrs;// eg : {ExdExp:1, ExdGold:3}
	}

	// 返回属性数组
	public static List<Map<String, Integer>> getBlessBuffAttrsList(List<Integer> buffIdArray) {
		List<Map<String, Integer>> attrs = new ArrayList<Map<String, Integer>>();// 返回列表
		for (int i = 0; i < buffIdArray.size(); ++i) {
			BlessBuffExt buffProp = getBlessBuffProp(buffIdArray.get(i)); // {attrKey:'ExdGold',value:1}
			if (null == buffProp || null == buffProp.attr) {
				continue;
			}

			String attrKey = buffProp.attr.getString("attrKey");
			int attrValue = buffProp.attr.getInteger("attrValue");
			Map<String, Integer> attr = new HashMap<String, Integer>();
			attr.put(attrKey, attrValue);
			attrs.add(attr);
		}
		return attrs; // eg: [{ExdExp:1},{ExdExp:2},{ExdGold:3}];
	}

	public static List<GShopExt> getShopPropList() {
		return GameData.findGShops((t) -> {
			return t.isValid == 1;
		});
	}

	public static GShopExt getShopPropById(int id) {
		return GameData.GShops.get(id);
	}

	public static GTechnologyLevelCO getTechLevelPropByLevel(int level) {
		return GameData.GTechnologyLevels.get(level);
	}

	public static List<GTechnologyExt> findGuildTechSkillPropsByTechIDAndTechLevel(int techID, int techLevel) {
		return GameData.findGTechnologys((t) -> t.techID == techID && t.techLevel == techLevel);
	}

	public static GTechnologyExt getTechSkillPropByIdLevel(int id, int level) {
		List<GTechnologyExt> list = findGuildTechSkillPropsByTechIDAndTechLevel(id, level);
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public static List<GTechnologyExt> findGuildTechSkillPropsByTechLevel(int techLevel) {
		return GameData.findGTechnologys((t) -> {
			return t.techLevel == techLevel;
		});
	}

	public static List<Integer> getTechSkillIdList() {
		List<Integer> skills = new ArrayList<Integer>();
		List<GTechnologyExt> list = findGuildTechSkillPropsByTechLevel(1);
		for (int i = 0; i < list.size(); ++i) {
			skills.add(list.get(i).techID);
		}
		return skills;
	}

	public static Map<Integer, GTechnologyItemCO> getTechProducePropList() {
		return GameData.GTechnologyItems;// dataAccessor.guildTechProduceProps.data();
	}

	public static GTechnologyItemCO getTechProducePropById(int id) {
		return GameData.GTechnologyItems.get(id);
	}

	public static GBuffExt getTechBuffPropByLevel(int level) {
		return GameData.GBuffs.get(level);
	}

	public static void updateGuild(GuildPO guildData) {
		GuildDao.updateGuild(guildData);
	}

	public static void removeGuild(GuildPO guildPo) {
		GuildDao.removeGuild(guildPo);
	}

	public static void updateGuildMember(GuildMemberPO memberData) {
		GuildMemberDao.updateGuildMember(memberData);
	}

	public static void addGuildApply(GuildApplyPO applyData) {
		GuildApplyDao.addGuildApply(applyData);
	}

	public static void updateGuildImpeach(GuildImpeachData impeachData) {
		GuildImpeachDao.updateGuildImpeach(impeachData);
	}

	public static GuildPO getGuild(String guildId) {
		return GuildDao.getGuild(guildId);
	}

	public static GuildPO getPlayerGuild(String playerId) {
		GuildMemberPO po = getGuildMember(playerId);
		if (null == po) {
			return null;
		}

		return getGuild(po.guildId);
	}

	public static String getGuildName(String playerId) {
		GuildPO guild = getPlayerGuild(playerId);
		return guild == null ? LangService.getValue("TEAM_NO_GUILD") : guild.name;
	}

	/**
	 * 根据玩家id获取公会成员信息
	 * 
	 * @param playerId
	 * @return 公会成员信息
	 */
	public static GuildMemberPO getGuildMember(String playerId) {
		return GuildMemberDao.getGuildMember(playerId);
	}

	public static GuildApplyPO getGuildApply(String applyId) {
		return GuildApplyDao.getGuildApply(applyId);
	}

	public static GuildImpeachData getGuildImpeach(String guildId) {
		return GuildImpeachDao.getGuildImpeach(guildId);
	}

	public static void removeGuildNameIndex(int logicServerId, String guildName) {
		GuildDao.removeGuildNameIndex(guildName);
	}

	public static void removeGuildMember(String playerId) {
		GuildMemberDao.removeGuildMember(playerId);
	}

	public static void removeGuildApply(String applyId) {
		GuildApplyDao.removeGuildApply(applyId);
	}

	public static List<GuildPO> getGuildList(int logicServerId, String name) {
		return GuildDao.getGuildList(logicServerId, name);
	}

	/**
	 * Find all guild members if member's job in jobs
	 * @param jobs
	 * @return list of player id
	 */
	public static List<String> getMemberIds(GuildJob... jobs){
		List<String> ids = new ArrayList<>();
		
		for(GuildMemberPO member : GuildMemberDao.PlayerMemberMap.values()) {
			for(GuildJob job: jobs) {
				if(member.job == job.getValue()) {
					ids.add(member.playerId);
				}
			}
		}
		
		return ids;
	}
	
	public static Set<String> getGuildMemberIdList(String guildId) {
		return GuildMemberDao.getGuildMemberIdList(guildId);
	}

	public static int getGuildMemberCount(String guildId) {
		return GuildMemberDao.getGuildMemberCount(guildId);
	}

	public static List<GuildMemberPO> getGuildMemberList(String guildId) {
		List<GuildMemberPO> list = new ArrayList<GuildMemberPO>();
		Set<String> idList = getGuildMemberIdList(guildId);
		for (String id : idList) {
			GuildMemberPO member = getGuildMember(id);
			list.add(member);
		}
		return list;
	}

	public static int getGuildMemberCountByJob(String guildId, int job) {
		int count = 0;
		Set<String> idList = getGuildMemberIdList(guildId);
		for (String id : idList) {
			GuildMemberPO member = getGuildMember(id);
			if (member.job == job) {
				count++;
			}
		}
		return count;
	}

	public static int getGuildApplyCount(String guildId) {
		return GuildApplyDao.getGuildApplyCount(guildId);
	}

	public static List<GuildApplyPO> getGuildApplyList(String guildId) {
		return GuildApplyDao.getGuildApplyList(guildId);
	}

	public static List<GuildApplyPO> getPlayerApplyIdList(String playerId) {
		return GuildApplyDao.getPlayerApplyIdList(playerId);
	}

	public static boolean isInGuild(String playerId) {
		GuildMemberPO guildMember = getGuildMember(playerId);
		if (null != guildMember) {
			return true;
		}
		return false;
	}

	public static String getGuildByFullName(String name) {
		return GuildDao.getGuildIdByName(name);
	}

	public static boolean existGuildName(int logicServerId, String name) {
		String guildId = getGuildByFullName(name);
		return StringUtil.isNotEmpty(guildId);
	}

	public static GuildDepotPO getGuildDepot(String guildId) {
		return GuildDepotDao.getDepot(guildId);
	}

	public static ArrayList<GuildDepotPO> getGuildDepotList() {
		return GuildDepotDao.getDepotList();
	}

	public static void updateGuildDepot(GuildDepotPO data) {
		GuildDepotDao.updateDepot(data);
	}

	/**
	 * 检查一个条件是否合格
	 * 
	 * @param cond eg:{minCond:{level:1,upLevel:1,qColor:1},
	 *            maxCond:{level:1,upLevel:1,qColor:1}}
	 */
	public static boolean checkCondition(GuildDepotCondition cond) {
		GuildSettingExt settingProp = getGuildSettingExtProp();
		if (cond.minCond.qColor < settingProp.warehouseMinQ || cond.maxCond.qColor < settingProp.warehouseMinQ) {
			return false; // 品质太低
		}
		// if (cond.minCond.upLevel == 0 && cond.minCond.upLevel <
		// settingProp.warehouseMinLv) {
		// return false;
		// }
		if (cond.maxCond.level == 0 && cond.maxCond.level < settingProp.warehouseMinLv) {
			return false;
		}
		return lessCompare(cond.minCond, cond.maxCond, false);
	}

	/**
	 * @param quality
	 * @param cond 格式同checkCondition
	 * @returns {*}
	 */
	public static boolean isInCondition(GuildCond quality, GuildDepotCondition cond) {
		return (lessCompare(cond.minCond, quality, true) && lessCompare(quality, cond.maxCond, true));
	}

	/**
	 * 小于判断
	 * 
	 * @param minCond
	 * @param maxCond
	 * @param canEqual，true：小于等于 false：小于
	 * @returns {boolean}
	 */
	public static boolean lessCompare(GuildCond minCond, GuildCond maxCond, boolean canEqual) {
		if (minCond.level != maxCond.level) {
			return minCond.level < maxCond.level;
		} else if (minCond.qColor != maxCond.qColor) {
			return minCond.qColor < maxCond.qColor;
		}

		if (canEqual) {// 小于等于
			return true;
		}
		return false;
	}

	// var getGuildBless(guildId){
	// return guildBlessDao.getGuildBless(guildId);
	// }

	public static ArrayList<GuildBlessPO> getGuildBlessList() {
		return GuildBlessDao.getGuildBlessList();
	}

	public static void updateGuildBless(GuildBlessPO blessData) {
		GuildBlessDao.updateGuildBless(blessData);
	}

	public static GDungeonCO getGuildDungeonConfig() {
		Map<String, GDungeonCO> map = GameData.GDungeons;
		if (map.size() > 0) {
			return map.get("0");
		}
		return null;
	}

	public static GuildDungeonPO getGuildDungeon(String id) {
		return GuildServiceCenter.getInstance().getGuildDungeon(id);
	}

	public static void updateGuildDungeon(GuildDungeonPO guildDungeonData) {
		GuildDao.updateGuildDungeon(guildDungeonData);
	}

	public static ArrayList<GuildDungeonPO> getAllGuildDungeon() {
		return new ArrayList<GuildDungeonPO>();// guildDao.getAllGuildDungeon();
	}

	public static QualityCond qualityCond(int level, int upLevel, int qColor) {
		QualityCond.Builder cond = QualityCond.newBuilder();
		cond.setLevel(level);
		cond.setUpLevel(upLevel);
		cond.setQColor(qColor);

		return cond.build();
	}

	public GuildRecordData getNewGuildReocrd(int type, RoleInfo role1, RoleInfo role2) {
		GuildRecordData record = new GuildRecordData();
		record.type = Const.GuildRecord.UPGRADE.getValue();
		if (null != role1) {
			record.role1 = new RoleInfo();
			record.role1.pro = role1.pro;
			record.role1.name = role1.name;
		}

		if (null != role2) {
			record.role2.pro = role2.pro;
			record.role2.name = role2.name;
		}

		return record;
	}

	public static GuildResult checkGuildMember(String playerId) {
		GuildResult ret = new GuildResult();
		GuildMemberPO myInfo = GuildServiceCenter.getInstance().getGuildMember(playerId);
		GuildPO myGuild = GuildServiceCenter.getInstance().getGuild(myInfo.guildId);
		if (null == myInfo || null == myGuild) {
			ret.result = -1;
			ret.des = "不是公会成员";
			return ret;
		}
		return null;
	}

	public static void broadcast(String guildId, Message msg) {
		getGuildMemberIdList(guildId).forEach(playerId -> {
			WNPlayer player = PlayerUtil.getOnlinePlayer(playerId);
			if (player != null) {
				player.receive(msg);
			}
		});
	}

	// 推送公会数据
	public static void refreshGuildJobPush(WNPlayer player) {
		if (null != player) {
			GuildManager guildManager = player.guildManager;
			String guildId = guildManager.getGuildId();
			String guildName = guildManager.getGuildName();
			int guildJob = guildManager.getJob();
			String guildIcon = guildManager.getGuildIcon();
			player.pushDynamicData(Utils.ofMap("guildId", guildId, "guildName", guildName, "guildJob", guildJob, "guildIcon", guildIcon));
		}
	}

}
