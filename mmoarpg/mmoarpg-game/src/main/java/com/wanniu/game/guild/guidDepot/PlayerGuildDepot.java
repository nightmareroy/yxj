package com.wanniu.game.guild.guidDepot;

import java.util.ArrayList;

import com.wanniu.game.poes.GuildDepotPO;

import pomelo.guild.GuildManagerHandler.BagGridsInfo;
import pomelo.item.ItemOuterClass.ItemDetail;

public class PlayerGuildDepot {
	public GuildDepotPO depotInfo;
	public BagGridsInfo bagInfo;
	public ArrayList<ItemDetail> detailInfo;

	public PlayerGuildDepot() {
		depotInfo = new GuildDepotPO();
		bagInfo = BagGridsInfo.newBuilder().build();
		detailInfo = new ArrayList<ItemDetail>();
	}
}
