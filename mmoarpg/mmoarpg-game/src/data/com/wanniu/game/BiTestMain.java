package com.wanniu.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.alibaba.fastjson.JSON;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.http.HttpRequester;

/**
 * Only for qeng bi test
 * @author wfy
 *
 */
public class BiTestMain {
	static class Item{
		public String code;
		public String name;
		public String num;
	}
	
	static class Player{
		public String id;
		public String name;
		public String level;
		public String fightLevel;
	}
	
	static class RideTrain{
		public String id;
		public String op;
		public List<Item> consume = new ArrayList<>();
		public Player role;
	}
	
	public static String sortParams(Map<String, String> params) {
		StringBuilder sb = new StringBuilder();
		for (String key : params.keySet()) {
			sb.append(key + "=" + params.get(key) + "&");
		}
		return sb.toString();
	}
	
	public final static String URL = "http://dev.6543.cn:8380/app_dev.php/api/open/report";
	
	public static void main(String[] args) {
		Player p = new Player();
		p.id="01fcb557-7025-4e7b-ac24-7b7eb4c1ce0d";
		p.name = "测试员";
		p.level = "38";
		p.fightLevel = "8555550";
		
		RideTrain rt = new RideTrain();
		rt.id = "fasdfad";
		rt.op = "1";
		rt.role = p;
		for(int i=1; i<+5; i++){
			Item item = new Item();
			item.code = String.valueOf(10000 + i);
			item.num = "inum" + i*2;
			item.name = "name" + i;
			rt.consume.add(item);
		}
		
		String data = JSON.toJSONString(rt);
		System.out.println(data);
		
		Map<String,String> params = new HashMap<>();
		params.put("name", "RideTrain");
		params.put("source", "80");
		params.put("platform", "2");
		params.put("device_id", "02920bdb-7c29-4bec-8068-d4ec8f555283");
		params.put("model", "vivo y66");
		params.put("os", "android 4.0");
		params.put("ip", "192.168.92.8");
		params.put("ts", "" + System.currentTimeMillis());
		params.put("data", data);
		
		System.out.println(sortParams(params));
		
		try {
			String response = new HttpRequester().sendPost(URL, params).getContent();
			System.out.println(response);
		} catch (Exception e) {
			Out.error(e);
		}
		
		
	}
}
