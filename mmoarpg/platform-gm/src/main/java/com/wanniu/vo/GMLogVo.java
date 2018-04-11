package com.wanniu.vo;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class GMLogVo {

	public String uname;
	public String op;
	@JSONField(format="yyyy-MM-dd HH:mm:ss")  
	public Date createTime;
	public String ip;
	public String content;
	public String result;
	public int type;

}
