package com.wanniu.game;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import com.alibaba.fastjson.JSONArray;
import com.wanniu.core.util.FileUtil;


public class MailConfigMain {

	public static void main(String[] args) {
		File file = new File("../json/json/mail/Mail/MailSystem.json");
		JSONArray doc = JSONArray.parseArray(FileUtil.readText(file));
//		Config.instance().init();
		FileWriter writer=null;
		HashMap<String, String> strKey_map=new HashMap<>();
		HashMap<String, String> idKey_map = new HashMap<>();
		try {
			writer = new FileWriter("./src/com/wanniu/game/mail/SysMailConst.java");
			writer.write("package com.wanniu.game.mail;"+"\r\n");
			writer.write("\r\n");
			writer.write("public class SysMailConst{"+"\r\n");
			writer.write("\r\n");
			
			for (int i = 3; i < doc.size(); i++) {
				JSONArray values = (JSONArray) doc.get(i);
				String field = values.getString(6);
				if(field == null || field.trim().equals("")){
//					throw new Exception("变量为空");
				}else
					writer.write("	"+"public static final String "+field.toUpperCase()+" = \""+field+"\";\r\n");
				if(strKey_map.containsKey(field))
				{
					throw new Exception("重复的ParamName："+field);
				}
				String id = values.getString(0);
				strKey_map.put(field, field);
				if(idKey_map.containsKey(id))
				{
					throw new Exception("重复的id："+id);
				}
				strKey_map.put(id, id);
			}
			
			
			
			writer.write("\r\n");
			writer.write("}");
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
