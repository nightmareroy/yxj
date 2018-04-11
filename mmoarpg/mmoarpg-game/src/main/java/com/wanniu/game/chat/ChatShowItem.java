package com.wanniu.game.chat;

/**
 * 聊天查看物品对象
 * {"MsgType":1,"data":{"Id":"807a9010-006d-11e6-bef5-87e444c3683f","PlayerId":"80692af0-006d-11e6-bef5-87e444c3683f","Quality":4,"TemplateId":"ch4"}}
 * 
 * @author Yangzz
 *
 */
public class ChatShowItem {

	public int MsgType;

	public ChatShowItemData data;

	public static class ChatShowItemData {
		public String Id;

		public String PlayerId;

		public int Quality;

		public String TemplateId;

		public int needQuery;
	}
}
