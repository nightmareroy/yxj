package com.wanniu.game.poes;

import java.util.HashMap;
import java.util.Map;

import com.wanniu.core.game.entity.GEntity;
import com.wanniu.game.DBField;
import com.wanniu.game.DBTable;
import com.wanniu.game.common.Table;
import com.wanniu.game.mail.WNMail;

@DBTable(Table.player_mails)
public class PlayerMailDataPO extends GEntity {

	@DBField(fieldType = "mediumblob")
	public Map<String, WNMail> mails = new HashMap<>();

	public Map<String, String> serverMailRecord = new HashMap<>();

	public PlayerMailDataPO() {

	}
}
