package com.wanniu.game.mail.po;

import java.util.concurrent.ConcurrentHashMap;

import com.wanniu.game.mail.data.MailGmData;

public class ServerMailsPO {
	public ConcurrentHashMap<String, MailGmData> serverMail = new ConcurrentHashMap<>();
}
