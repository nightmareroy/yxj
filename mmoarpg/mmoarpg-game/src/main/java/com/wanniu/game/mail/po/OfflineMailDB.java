package com.wanniu.game.mail.po;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.wanniu.game.mail.data.MailData;

public class OfflineMailDB {
	public String id;
	public int logicServerId;
	public Date modifyTime;
	public Date oldestTime;
	public List<MailData> mails = new ArrayList<>();
}
