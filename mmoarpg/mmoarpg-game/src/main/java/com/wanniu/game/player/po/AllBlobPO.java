package com.wanniu.game.player.po;

import java.util.List;
import java.util.Map;

import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.poes.AchievementDataPO;
import com.wanniu.game.poes.CdksUsePO;
import com.wanniu.game.poes.Five2FivePO;
import com.wanniu.game.poes.Five2FivePlayerBtlReportPO;
import com.wanniu.game.poes.HookSetPO;
import com.wanniu.game.poes.PlayerAttachPO;
import com.wanniu.game.poes.PlayerBasePO;
import com.wanniu.game.poes.PlayerChouRenPO;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.game.poes.PlayerTempPO;
import com.wanniu.game.poes.TaskListPO;
import com.wanniu.game.poes.XianYuanPO;

/**
 * Player相关PO对象
 * 
 * @author Yangzz
 *
 */
public class AllBlobPO {

	public PlayerPO player;

	public PlayerBasePO playerBase;

	public PlayerAttachPO playerAttachPO;

	public PlayerTempPO playerTemp;

	/** 任务数据 */
	public TaskListPO tasks;

	/** 成就 */
	public AchievementDataPO achievements;

	public HookSetPO hookSetData;

	public PlayerChouRenPO chouRens;

	public XianYuanPO xianYuan;

	public Five2FivePO five2FivePo;

	public List<Five2FivePlayerBtlReportPO> five2FiveBtlReportPO;

	public CdksUsePO cdksUserPo;

	// 机器人的额外加成属性
	public Map<PlayerBtlData, Integer> robotAttr;

	public AllBlobPO() {

	}

	public AllBlobPO(PlayerPO player, PlayerBasePO playerBase, PlayerTempPO playerTemp) {
		this.player = player;
		this.playerBase = playerBase;
		this.playerTemp = playerTemp;
	}
}
