package com.wanniu.game.guild;

public class RecordInfo {
	public RoleInfo role1;
	public RoleInfo role2;
	public int resultNum;
	public String resultStr;
	public String time;
	public int recordType;
	public ItemRecordInfo item;

	public RecordInfo() {
		role1 = new RoleInfo();
		role2 = new RoleInfo();
		resultStr = "";
		time = "";
		item = new ItemRecordInfo();
	}
}
