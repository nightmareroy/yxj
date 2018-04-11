package com.wanniu.game;

import java.io.File;
import java.io.FileFilter;

import com.wanniu.core.GConfig;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.DataUtil;
import com.wanniu.game.data.ActiveCO;
import com.wanniu.game.data.AthleticShopCO;
import com.wanniu.game.data.BlueEquipCO;
import com.wanniu.game.data.BranchLineCO;
import com.wanniu.game.data.ChestCO;
import com.wanniu.game.data.CircleSceneCO;
import com.wanniu.game.data.DailyCO;
import com.wanniu.game.data.DemonTowerCO;
import com.wanniu.game.data.DungeonCO;
import com.wanniu.game.data.DungeonMapCO;
import com.wanniu.game.data.Dungeon_EliteCO;
import com.wanniu.game.data.Dungeon_HeroCO;
import com.wanniu.game.data.Dungeon_NormalCO;
import com.wanniu.game.data.FashionItemCO;
import com.wanniu.game.data.FateShopCO;
import com.wanniu.game.data.GameMapCO;
import com.wanniu.game.data.GuildShopCO;
import com.wanniu.game.data.JewelCO;
import com.wanniu.game.data.LegendEquipCO;
import com.wanniu.game.data.MainLineCO;
import com.wanniu.game.data.MallShopCO;
import com.wanniu.game.data.MateCO;
import com.wanniu.game.data.MenuUISaleCO;
import com.wanniu.game.data.MiscCO;
import com.wanniu.game.data.NormalEquipCO;
import com.wanniu.game.data.NormalMapCO;
import com.wanniu.game.data.Normal_WorldCO;
import com.wanniu.game.data.NpcSaleCO;
import com.wanniu.game.data.PetItemCO;
import com.wanniu.game.data.PotionCO;
import com.wanniu.game.data.PurpleEquipCO;
import com.wanniu.game.data.QuestCO;
import com.wanniu.game.data.RankCO;
import com.wanniu.game.data.RideEquipCO;
import com.wanniu.game.data.RideItemCO;
import com.wanniu.game.data.SuitEquipCO;
import com.wanniu.game.data.SundryShopCO;
import com.wanniu.game.data.TreasureCO;
import com.wanniu.game.data.UniqueEquipCO;
import com.wanniu.game.data.VirtualCO;
import com.wanniu.game.data.base.DEquipBase;
import com.wanniu.game.data.base.DItemBase;
import com.wanniu.game.data.base.IntergalShopBase;
import com.wanniu.game.data.base.MapBase;
import com.wanniu.game.data.base.MonsterBase;
import com.wanniu.game.data.base.SaleBase;
import com.wanniu.game.data.base.TaskBase;

/**
 * @author agui
 */
public class JS2JMain {

	private static int TableCount = 0;

	public static void writeClass(File parent) {
		File[] tables = parent.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				if (file.isDirectory()) {
					Out.info(file.getAbsolutePath());
					writeClass(file);
				}
				if (file.getName().endsWith(".json")) {
					DataUtil.writeClass(file);
					return true;
				}
				return false;
			}
		});
		TableCount += tables.length;
	}

	public static void main(String[] args) {
		GConfig.getInstance().init(false);
		Out.setting();
		File root = new File(GConfig.getInstance().get("dir.game.data"));
		DataUtil.setDir("src/data/com/wanniu/game/data");
		// 设置某些对象的父类
		/** 怪物 */
		DataUtil.superClasses.put(Normal_WorldCO.class.getSimpleName(), MonsterBase.class.getName());
		DataUtil.superClasses.put(DungeonCO.class.getSimpleName(), MonsterBase.class.getName());
		DataUtil.superClasses.put(DemonTowerCO.class.getSimpleName(), MonsterBase.class.getName());
		DataUtil.superClasses.put(Dungeon_NormalCO.class.getSimpleName(), MonsterBase.class.getName());
		DataUtil.superClasses.put(Dungeon_EliteCO.class.getSimpleName(), MonsterBase.class.getName());
		DataUtil.superClasses.put(Dungeon_HeroCO.class.getSimpleName(), MonsterBase.class.getName());
		/** 任务 */
		DataUtil.superClasses.put(MainLineCO.class.getSimpleName(), TaskBase.class.getName());
		DataUtil.superClasses.put(BranchLineCO.class.getSimpleName(), TaskBase.class.getName());
		DataUtil.superClasses.put(DailyCO.class.getSimpleName(), TaskBase.class.getName());
		DataUtil.superClasses.put(CircleSceneCO.class.getSimpleName(), TaskBase.class.getName());
		DataUtil.superClasses.put(CircleSceneCO.class.getSimpleName(), TaskBase.class.getName());
		DataUtil.superClasses.put(TreasureCO.class.getSimpleName(), TaskBase.class.getName());

		/** 物品 */
		DataUtil.superClasses.put(JewelCO.class.getSimpleName(), DItemBase.class.getName());
		DataUtil.superClasses.put(RideItemCO.class.getSimpleName(), DItemBase.class.getName());
		DataUtil.superClasses.put(PetItemCO.class.getSimpleName(), DItemBase.class.getName());
		DataUtil.superClasses.put(ChestCO.class.getSimpleName(), DItemBase.class.getName());
		DataUtil.superClasses.put(PotionCO.class.getSimpleName(), DItemBase.class.getName());
		DataUtil.superClasses.put(MateCO.class.getSimpleName(), DItemBase.class.getName());
		DataUtil.superClasses.put(MiscCO.class.getSimpleName(), DItemBase.class.getName());
		DataUtil.superClasses.put(VirtualCO.class.getSimpleName(), DItemBase.class.getName());
		DataUtil.superClasses.put(RankCO.class.getSimpleName(), DItemBase.class.getName());
		DataUtil.superClasses.put(QuestCO.class.getSimpleName(), DItemBase.class.getName());
		DataUtil.superClasses.put(ActiveCO.class.getSimpleName(), DItemBase.class.getName());
		DataUtil.superClasses.put(FashionItemCO.class.getSimpleName(), DItemBase.class.getName());

		// /**装备*/
		DataUtil.superClasses.put(NormalEquipCO.class.getSimpleName(), DEquipBase.class.getName());
		DataUtil.superClasses.put(BlueEquipCO.class.getSimpleName(), DEquipBase.class.getName());
		DataUtil.superClasses.put(PurpleEquipCO.class.getSimpleName(), DEquipBase.class.getName());
		DataUtil.superClasses.put(LegendEquipCO.class.getSimpleName(), DEquipBase.class.getName());
		DataUtil.superClasses.put(SuitEquipCO.class.getSimpleName(), DEquipBase.class.getName());
		DataUtil.superClasses.put(RideEquipCO.class.getSimpleName(), DEquipBase.class.getName());

		/** 固定属性装备 */
		DataUtil.superClasses.put(UniqueEquipCO.class.getSimpleName(), DEquipBase.class.getName());

		/** 场景 */
		DataUtil.superClasses.put(NormalMapCO.class.getSimpleName(), MapBase.class.getName());
		DataUtil.superClasses.put(DungeonMapCO.class.getSimpleName(), MapBase.class.getName());
		DataUtil.superClasses.put(GameMapCO.class.getSimpleName(), MapBase.class.getName());
		/** ItemSale.xls */
		DataUtil.superClasses.put(MenuUISaleCO.class.getSimpleName(), SaleBase.class.getName());
		DataUtil.superClasses.put(NpcSaleCO.class.getSimpleName(), SaleBase.class.getName());
		/** 积分商城 */
		DataUtil.superClasses.put(MallShopCO.class.getSimpleName(), IntergalShopBase.class.getName());
		DataUtil.superClasses.put(FateShopCO.class.getSimpleName(), IntergalShopBase.class.getName());
		DataUtil.superClasses.put(AthleticShopCO.class.getSimpleName(), IntergalShopBase.class.getName());
		DataUtil.superClasses.put(GuildShopCO.class.getSimpleName(), IntergalShopBase.class.getName());
		DataUtil.superClasses.put(SundryShopCO.class.getSimpleName(), IntergalShopBase.class.getName());

		DataUtil.addExtClass("com.wanniu.game.data");

		writeClass(root);
		Out.info("生成表格数:", TableCount);

		DataUtil.writeGameData();
		System.exit(0);
	}

}
