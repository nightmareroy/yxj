package com.wanniu.game.poes;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Table;
import com.wanniu.game.rank.TitleManager.AwardRankData;

/**
 * @author agui
 */
@DBTable(Table.player_title)
public final class TitlePO extends GEntity {

	public int selectedRankId;

	public Map<Integer, AwardRankData> awardRanks;

	public TitlePO() {
		this.awardRanks = new HashMap<>();
	}

}
