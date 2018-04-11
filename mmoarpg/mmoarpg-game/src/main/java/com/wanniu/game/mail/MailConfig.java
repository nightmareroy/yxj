package com.wanniu.game.mail;

import java.util.Map;

import com.wanniu.core.logfs.Out;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.MailOperateCO;

public class MailConfig {

	private static MailConfig instance;

	public static MailConfig getInstance() {
		if (instance == null) {
			instance = new MailConfig();
		}
		return instance;
	}

	private MailConfig() {

	}

	// private Map<Integer, MailSystemCO> sysMailTable = GameData.MailSystems ;//=
	// new TreeMap<>();
	private Map<Integer, MailOperateCO> opreateMailTable = GameData.MailOperates;// = new TreeMap<>();

	// public final MailSystemCO findDSysMailByMailId(int mailId){
	// if(sysMailTable.containsKey(mailId)){
	// return sysMailTable.get(mailId);
	// }
	// Out.error(this.getClass().getName() + " : Can`t find SysMail By mailId = " +
	// mailId);
	// return null;
	// }

	public final MailOperateCO findDOperateMailByMailId(int mailId) {
		if (opreateMailTable.containsKey(mailId)) {
			return opreateMailTable.get(mailId);
		}
		Out.error(this.getClass().getName(), " : Can`t find MailOperate By mailId = ", mailId);
		return null;
	}
}
