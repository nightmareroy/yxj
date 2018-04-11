package com.wanniu.game.poes;

import java.util.Date;
import java.util.Map;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Table;

/**
 * 仙缘实体类
 * 
 * @author wanghaitao
 *
 */
@DBTable(Table.player_xianyuan)
public class XianYuanPO extends GEntity {

	public Map<Integer, Integer> reviceNumbers;

	public int xianYuanNum;
	
	public int sumXianYuan;

	public Date createTime;

	public Date updateTime;
	
	public XianYuanPO () {
		
	}

}
