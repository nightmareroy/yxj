package com.wanniu.game.guild.guidDepot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.wanniu.game.item.ItemUtil;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.item.po.PlayerItemPO;
import com.wanniu.game.poes.PlayerBasePO;

import pomelo.guild.GuildManagerHandler.BagGridsInfo;
import pomelo.item.ItemOuterClass.Grid;
import pomelo.item.ItemOuterClass.ItemDetail;

public class EquipBag {
	public int bagGridCount;
	public Map<Integer, NormalItem> bagGrids;
	public int usedGridCount;
	public int bagTotalCount;

	public EquipBag(GuildBagItem bagItem) {
		this._init(bagItem);
	}

	public void _init(GuildBagItem bagData) {
		this.bagGridCount = bagData.bagGridCount;
		this.bagGrids = new HashMap<Integer, NormalItem>();
		this.usedGridCount = 0;

		Map<Integer, PlayerItemPO> grids = bagData.bagGrids;
		for (Integer key : grids.keySet()) {
			NormalItem item = ItemUtil.createItemByDbOpts(grids.get(key));
			if (item != null) {
				this.bagGrids.put(key, item);
				this.usedGridCount++;
			}
		}

		this.bagTotalCount = bagData.bagTotalCount;
	}

	public GuildBagItem toJson4Serialize() {
		GuildBagItem data = new GuildBagItem();

		for (int index = 1; index <= this.bagGridCount; ++index) {
			NormalItem item = this.getItem(index);
			if (item != null) {
				data.bagGrids.put(index, item.cloneItemDB());
			}
		}

		data.bagGridCount = this.bagGridCount;
		data.bagTotalCount = this.bagTotalCount;
		return data;
	}

	public BagGridsInfo toJson4Payload() {
		BagGridsInfo.Builder data = BagGridsInfo.newBuilder();
		data.addAllBagGrids(this.getGrids4PayLoad());
		data.setBagGridCount(this.bagGridCount);
		data.setBagTotalCount(this.bagTotalCount);
		return data.build();
	}

	public NormalItem getItem(int index) {
		return this.bagGrids.get(index);
	}

	public void _addUsedGridCount(int num) {
		if (num != 0) {
			this.usedGridCount += num;
		}
	}

	public boolean openGrid(int num) {
		if (num <= 0) {
			return false;
		}
		if (num + this.bagGridCount > this.bagTotalCount) {
			return false;
		}

		this.bagGridCount += num;
		return true;
	}

	// 背包格子道具的数据结构
	public ArrayList<Grid> getGrids4PayLoad() {
		ArrayList<Grid> data = new ArrayList<Grid>();
		for (int index = 1; index <= this.bagGridCount; ++index) {
			NormalItem item = this.getItem(index);
			if (item != null) {
				data.add(this.getGrid4PayLoad(index));
			}
		}
		return data;
	}

	public Grid getGrid4PayLoad(int index) {
		Grid.Builder grid = Grid.newBuilder();
		grid.setGridIndex(index);
		NormalItem item = this.getItem(index);
		if (null != item) {
			grid.setItem(item.toJSON4GridPayload());
		}

		return grid.build();
	}

	public ArrayList<ItemDetail> getAllEquipDetails4PayLoad(PlayerBasePO basePO) {
		ArrayList<ItemDetail> data = new ArrayList<ItemDetail>();
		for (int index = 1; index <= this.bagGridCount; ++index) {
			NormalItem item = this.getItem(index);
			if (item != null) {
				data.add(item.getItemDetail(basePO).build());
			}
		}
		return data;
	}

	public GuildDepotOneGrid getGridAndDetailByIndex(PlayerBasePO basePO, int index) {
		GuildDepotOneGrid data = new GuildDepotOneGrid();
		Grid.Builder grid = Grid.newBuilder();
		grid.setGridIndex(index);
		NormalItem item = this.getItem(index);
		if (null != item) {
			grid.setItem(item.toJSON4GridPayload());
			data.detail = item.getItemDetail(basePO).build();
		}

		return data;
	}

	/**
	 * 空余的格子数量
	 * 
	 * @returns {number}
	 */
	public int emptyGridNum() {
		return this.bagGridCount - this.usedGridCount;
	}

	/**
	 * 查找num数量的背包空格子
	 * 
	 * @param num,数量, 未传此值默认返回所有
	 * @returns {Array}
	 */
	public ArrayList<Integer> findEmptyGrids(int num) {
		ArrayList<Integer> emptyIndex = new ArrayList<Integer>();
		for (int i = 1; i <= this.bagGridCount; ++i) {
			NormalItem item = this.bagGrids.get(i);
			if (item == null) {
				emptyIndex.add(i);
			}

			if (emptyIndex.size() == num) {
				break;
			}
		}
		return emptyIndex;
	}

	/**
	 * 检查空余格子数是否大于num
	 * 
	 * @param num
	 * @returns {boolean}
	 */
	public boolean testEmptyGridLarge(int num) {
		if (this.emptyGridNum() >= num) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 增加一个实体道具
	 * 
	 * @param item 实体道具
	 * @returns {number} 返回格子索引，失败返回0
	 */
	public int addItem(NormalItem item) {
		if (item == null) {
			return 0;
		}

		if (this.emptyGridNum() == 0) {
			return 0;
		}

		ArrayList<Integer> emptyIndexs = this.findEmptyGrids(1);
		if (emptyIndexs.size() <= 0) {
			return 0;
		}
		return this.addItemToPos(emptyIndexs.get(0), item);
	}

	/**
	 * 在指定位置增加一个实体道具
	 * 
	 * @param item 实体道具
	 * @returns {number} 返回格子索引，失败返回0
	 */
	public int addItemToPos(int gridIndex, NormalItem item) {
		if (item != null) {
			NormalItem oldItem = this.getItem(gridIndex);
			if (oldItem == null) {
				this.bagGrids.put(gridIndex, item);
				this._addUsedGridCount(1);
				return gridIndex;
			}
		}
		return 0;
	}

	/**
	 * 根据序列化结构增加一个道具
	 * 
	 * @param data 结构数据
	 */
	public int addItemByData(PlayerItemPO data) {
		NormalItem item = ItemUtil.createItemByDbOpts(data);
		if (item == null) {
			return 0;
		}
		return this.addItem(item);
	}

	/**
	 * 在指定位置根据序列化结构增加一个道具
	 * 
	 * @param gridIndex 指定位置
	 * @param data 结构数据
	 */
	public int addItemByPosData(int gridIndex, PlayerItemPO data) {
		NormalItem item = ItemUtil.createItemByDbOpts(data);
		if (item == null || gridIndex < 0) {
			return 0;
		}
		return this.addItemToPos(gridIndex, item);
	}

	public void _delete(int pos) {
		this.bagGrids.remove(pos);
		this._addUsedGridCount(-1);
	}

	/**
	 * 按位置丢弃物品并不对物品做任何操作
	 */
	public int removeItemByPos(int pos) {
		NormalItem item = this.getItem(pos);
		if (item != null) {
			this._delete(pos);
			return pos;
		}
		return 0;
	}
}
