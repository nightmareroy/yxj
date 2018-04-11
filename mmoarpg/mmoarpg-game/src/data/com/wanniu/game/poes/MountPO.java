package com.wanniu.game.poes;
import java.util.List;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Const;
import com.wanniu.game.common.Table;

@DBTable(Table.player_mount)
public class MountPO extends GEntity {

	//	public int isOpen;

	/**星级*/
	public int starLv;
	/**阶级*/
	public int rideLevel;

	public int usingSkinId = 0;
	public List<Integer> mountSkins;
	public int fightPower;
	/**
	 * 坐骑的骑行状态
	 */
	public int rideFlag = (int)Const.MOUNT_RIDING_STATE.off.getValue();
	
	public MountPO() {
		
	}
	
//	public MountPO(String playerId){
//		this.playerId = playerId;
//	}
	/**
	 * 第一次可以选择皮肤，选过了该字段标记为true，以后不能再选了
	 */
	public boolean firstChoose = false;

}
