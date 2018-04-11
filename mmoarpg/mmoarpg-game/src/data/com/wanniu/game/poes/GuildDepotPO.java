package com.wanniu.game.poes;

import java.util.ArrayList;
import java.util.Date;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBField;
import com.wanniu.game.guild.guidDepot.GuildBagItem;
import com.wanniu.game.guild.guidDepot.GuildDepotCondition;
import com.wanniu.game.guild.guidDepot.GuildRecordData;

public class GuildDepotPO extends GEntity {
	@DBField(isPKey=true,fieldType="varchar",size=50)
	public String id;
	public int logicServerId;
	public int level;
	public Date createTime;
	public GuildDepotCondition condition;
	public ArrayList<GuildRecordData> news;
	public GuildBagItem bag;
	public int deleteCount;
	public int deleteCountMax;
	public Date refreshTime;

	public GuildDepotPO() {
		condition = new GuildDepotCondition();
		news = new ArrayList<GuildRecordData>();
		bag = new GuildBagItem();
		refreshTime = new Date(0);
	}
}
