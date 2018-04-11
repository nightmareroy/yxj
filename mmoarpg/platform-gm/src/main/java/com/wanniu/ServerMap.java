package com.wanniu;

import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSONObject;
import com.wanniu.vo.ServerVo;
import com.wanniu.vo.ServerVo.Icon;

/**
 * 自定义服务集合类
 * @author zhangronggui
 */
public class ServerMap extends ConcurrentHashMap<Integer, ServerVo> {

	private static final long serialVersionUID = 7734136928857516659L;
	
	private ServerVo root = new ServerVo("服务器列表");
	
	public ServerMap() {
		 root.setIconCls(Icon.DEFAULT);
	}

	public ServerVo getRoot() {
		return root;
	}

	@Override
	public void clear() {
		root.children.clear();
		super.clear();
	}

	@Override
	public ServerVo put(Integer key, ServerVo value) {
		if(value.getAreaId() == 0) {
			root.children.add(value);

			JSONObject json = new JSONObject();
			json.put("type", 0x105);
			json.put("appId", value.getAppId());
			json.put("areaId", value.getLogicServerId());
			json.put("areaName", value.getServerName());
			AuthServer.publish(json);

		} else {
			ServerVo parent = get(value.getAreaId());
			if(parent != null) {
				parent.children.add(value);
			}
		}
//		IPSet.getInstance().add(value.getIp());
		return super.put(key, value);
	}

	@Override
	public ServerVo remove(Object key) {
		ServerVo server = super.remove(key);
		if(server != null) {
			JSONObject json = new JSONObject();
			json.put("appId", server.getAppId());
			if (server.getAreaId() == 0) {
				root.children.remove(server);

				json.put("type", 0x106);
				json.put("areaId", server.getLogicServerId());
				AuthServer.publish(json);

			} else {
				ServerVo parent = get(server.getAreaId());
				if (parent != null) {
					parent.children.remove(server);
				}

				json.put("type", 0x107);
				json.put("logicServerId", server.getLogicServerId());
				AuthServer.publish(json);

			}
		}
		return super.remove(key);
	}
	

}
