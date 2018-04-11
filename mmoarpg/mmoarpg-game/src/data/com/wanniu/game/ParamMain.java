package com.wanniu.game;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.alibaba.fastjson.JSONArray;
import com.wanniu.core.GConfig;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.FileUtil;

/**
 * @author agui 读取json/config/GameConfig/Parameters.json
 *         自动生成com.wanniu.game.player.GlobalConfig 常量参数
 *         不需要手动复制然后覆盖GlobalConfig参数
 */
public class ParamMain {

	public final static String START = "//================================ParamMain auto generate start================================//";
	public final static String END = "//================================ParamMain auto generate end================================//";

	public static void main(String[] args) {
		GConfig.getInstance().init(false);
		Out.setting();

		File parameters = new File(GConfig.getInstance().get("dir.game.data") + "/config/GameConfig/Parameters.json");
		JSONArray doc = JSONArray.parseArray(FileUtil.readText(parameters));
		StringBuilder builder = new StringBuilder(START);
		for (int i = 3; i < doc.size(); i++) {
			JSONArray values = (JSONArray) doc.get(i);
			String type = values.getString(1);
			builder.append("\n\tpublic static ").append("NUMBER".equals(type) ? "int " : "FLOAT".equals(type) ? "float " : "String ").append(values.get(0).toString().replace(".", "_")).append(";");
		}

		builder.append("\n" + END);

		File globalFile = new File("src/main/java/com/wanniu/game/player/GlobalConfig.java");
		System.out.println(globalFile.getAbsolutePath());
		StringBuilder globalContent = new StringBuilder(FileUtil.readText(globalFile));
		int start = globalContent.indexOf(START);
		int end = globalContent.indexOf(END) + END.length();
		globalContent.replace(start, end, builder.toString());
		System.out.println(globalContent.toString());
		try {
			FileWriter newGlobalFile = new FileWriter(globalFile);
			newGlobalFile.write(globalContent.toString());
			newGlobalFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}