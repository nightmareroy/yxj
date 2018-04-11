package com.wanniu.gm;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.wanniu.core.gm.request.GMHandler;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.tcp.protocol.Packet;
import com.wanniu.gm.handler.GMBaseHandler;

public class GMRequest extends GMHandler {

	private final static Map<Short, GMBaseHandler> handlers = new HashMap<>();

	public static void addHandler(GMBaseHandler handler) {
		handlers.put(handler.getType(), handler);
	}

	@Override
	public void execute(Packet pak) {
		long key = pak.getLong();
		short op = pak.getShort();
		JSONArray arr = JSON.parseArray(pak.getString());

		GMResponse res = null;
		GMBaseHandler handler = handlers.get(op);
		if (handler != null) {
			try {
				// 使用2进制的参数
				if (handler instanceof GMByteArgsHandler) {
					res = ((GMByteArgsHandler) handler).execute(arr, pak.getRemaingBytes());
				}
				// Json格式的参数
				else {
					res = handler.execute(arr);
				}
			} catch (Exception e) {
				Out.error(e);
				res = new GMErrorResponse();
			}
		} else {
			Out.error("GMRequest : ", key, " - 0x", Integer.toHexString(op), " - ");
			res = new GMErrorResponse();
		}
		if (res != null) {
			res.setKey(key);
			pak.getSession().writeAndFlush(res);
		}
	}

	@Override
	public short getType() {
		return 0xABC;
	}
}