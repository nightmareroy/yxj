// package com.wanniu.game.farm;
//
// import java.util.Date;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.Queue;
//
// import com.wanniu.game.common.ConstsTR;
// import com.wanniu.game.farm.FarmMgr.Block;
// import com.wanniu.game.farm.FarmMgr.Crop;
// import com.wanniu.game.farm.FarmMgr.PlayerInfo;
// import com.wanniu.game.farm.FarmMgr.RecordInfo;
// import com.wanniu.redis.PlayerPOManager;
// import com.wanniu.game.poes.FarmPO;
//
/// **
// * 果园管理中心
// *
// * @author jjr
// *
// */
// public class FarmCenter {
//
// /**
// * 田块信息
// *
// * @author liyue
// *
// */
// public class Block {
// public int id; // 果园地块id
// public int blockState; // 地块状态 (0:未开垦 1:已开垦 3:已种植)
// public Crop crop; // 作物属性
// public int nodeState; // 种子成长状态 (0:种子期 1:成长期 2:成熟期 3:可收获)
// public int cultivateType; // 成长周期中的培育操作种类 (0:无需操作 1:浇水 2:除虫 3:除草 )
// public boolean cultivateState; // 成长周期中的培育操作状态 (0:完成 1：未完成 )
// public long nextTime; // 进入下一个阶段的时间戳
// public int friendPlantNum; // 好友种植次数
// public int friendStealNum; // 好友偷取次数
//
//// public float discount; // 折扣
// public Date time; // 种植开始时间
//
//// public FarmInfo() {
//// selfNeedWater = new ArrayList<Integer>();
//// }
// }
//
// /**
// * 作物信息
// *
// * @author liyue
// *
// */
// public class Crop {
// public int id; // 作物id (0:苹果 1:梨子 2:桃子 3:樱桃 4:葡萄)
// public int quality; // 作物品质 (0:蓝 1:紫 2:橙 3:绿 4:红)
//
// }
//
// /**
// * 种子信息
// *
// * @author liyue
// *
// */
// public class Seed {
// public int id; // 作物id (0:苹果 1:梨子 2:桃子 3:樱桃 4:葡萄)
// public int num; // 种子数量
//
// }
//
// /**
// * 日志信息
// *
// * @author liyue
// *
// */
// public class RecordInfo {
// public int recordType; // 记录类型
// public List<String> recordParams;// = new ArrayList<String>(); //
// 记录所需参数（数组默认第一个传时间,其他参数根据recordType 去配置里面查找 ）
// }
//
// /**
// * 玩家信息
// *
// * @author liyue
// *
// */
// public class PlayerInfo {
// public String playerId; // 玩家id
// public String roleName; // 玩家名字
// public int lv; // 玩家等级
// public boolean canSow; // 是否可浇水
// public boolean canSteal; // 是否可偷取
// public List<Block> blockLs;
// public Date lastExpGetDate;//上次获取仙缘值的日期
// public int xianyuan;//本日获取的仙缘值
// }
//
// /**
// * 我的信息
// *
// * @author liyue
// *
// */
// public class MyInfo {
// public PlayerInfo playerInfo;
// public Queue<RecordInfo> recrodLs;
// }
//
//
// private static FarmCenter instance;
//// private Map<String, FarmMgr> farmMgrMap; // 所有果园数据
//
// public static synchronized FarmCenter getInstance() {
// if (null == instance) {
// instance = new FarmCenter();
// }
// return instance;
// }
//
// private FarmCenter() {
//// farmMgrMap = new HashMap<String, FarmMgr>();
// }
//
//
// public static FarmPO GetFarmPO(String playerId)
// {
// return PlayerPOManager.findPO(ConstsTR.player_farmTR, playerId,
// FarmPO.class);
// }
//
// public boolean CreateFarmPO(String playerId)
// {
// if(PlayerPOManager.findPO(ConstsTR.player_farmTR, playerId,
// FarmPO.class)!=null)
// return false;
//
// FarmPO farmPO=new FarmPO(playerId);
// PlayerPOManager.put(ConstsTR.player_farmTR, playerId, farmPO);
// return true;
// }
//
//
//
//
//
//
//
//// public void onPlayerDisponse(String playerId) {
//// farmMgrMap.remove(playerId);
//// }
////
//// public Map<String, FarmMgr> getAllFarmMgr() {
//// return farmMgrMap;
//// }
//
//// /**
//// * 获取农场管理
//// *
//// * @param playerId
//// * @return
//// */
//// public FarmMgr getfarmMgrMap(String playerId) {
//// if (farmMgrMap.containsKey(playerId)) {
//// return farmMgrMap.get(playerId);
//// }
////
//// FarmPO po = PlayerPOManager.findPO(ConstsTR.player_farmTR, playerId,
// FarmPO.class);
////// if(po==null)
////// {
////// po=new FarmPO(playerId);
////// PlayerPOManager.put(ConstsTR.player_farmTR, playerId, po);
////// }
////
//// FarmMgr farmMgr = new FarmMgr(po);
//// farmMgrMap.put(playerId, farmMgr);
//// return farmMgr;
//// }
// }
