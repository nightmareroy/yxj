package com.wanniu.game.sale;

import java.util.ArrayList;
import java.util.List;

import com.wanniu.game.data.GameData;
import com.wanniu.game.data.MenuUISaleCO;
import com.wanniu.game.data.NpcSaleCO;
import com.wanniu.game.data.base.SaleBase;

public class SaleConfig {

	private static SaleConfig instance;

	public static SaleConfig getInstance() {
		if (instance == null) {
			instance = new SaleConfig();
		}
		return instance;
	}

	private ArrayList<SaleBase> listSaleProp = new ArrayList<>();

	private SaleConfig() {
		for (MenuUISaleCO sale : GameData.MenuUISales.values()) {
			sale.initProperty();
			listSaleProp.add(sale);
		}
		for (NpcSaleCO sale : GameData.NpcSales.values()) {
			sale.initProperty();
			listSaleProp.add(sale);
		}
	}

	public final ArrayList<SaleBase> findPropByTypeId(List<Integer> typeIds) {
		ArrayList<SaleBase> list = new ArrayList<>();
		for (SaleBase sale : listSaleProp) {
			for (int value : typeIds) {
				if (sale.typeID == value) {
					list.add(sale);
					break;
				}
			}
		}
		return list;
	}

	public final ArrayList<SaleBase> findPropByItemCode(String itemCode) {
		ArrayList<SaleBase> list = new ArrayList<>();
		for (SaleBase sale : listSaleProp) {
			if (sale.itemCode.equals(itemCode)) {
				list.add(sale);
			}
		}
		return list;
	}

	public final SaleBase findPropByTypeIdAndItemId(int typeId, int itemId) {
		for (SaleBase sale : listSaleProp) {
			if (sale.typeID == typeId && sale.itemID == itemId) {
				return sale;
			}
		}
		return null;
	}
}
