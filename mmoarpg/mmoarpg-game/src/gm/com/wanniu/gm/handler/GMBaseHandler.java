package com.wanniu.gm.handler;

import com.alibaba.fastjson.JSONArray;
import com.wanniu.gm.GMResponse;

public abstract class GMBaseHandler {

	public abstract GMResponse execute(JSONArray arr);

	public abstract short getType();
}