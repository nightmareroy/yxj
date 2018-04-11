package com.wanniu.core.groovy;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.wanniu.core.logfs.Out;

import groovy.lang.GroovyClassLoader;

public class GameGroovyManager {

	@SuppressWarnings("resource")
	public String sendGroovyCodeText(String codeText) {
		GroovyClassLoader loader = new GroovyClassLoader(GameGroovyManager.class.getClassLoader());
		Class<?> clz = loader.parseClass(codeText);
		try { // 执行groovy代码
			IGameGroovy object = (IGameGroovy) clz.newInstance();
			return object.execute();
		} catch (Exception e) {
			Out.error("GameGroovyManager sendGroovyCodeText", e);
			try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
				e.printStackTrace(pw);
				return sw.toString();
			} catch (IOException e1) {}
			return "请求助研发人员...";
		}
	}

	private GameGroovyManager() {}

	private static class GameGroovyManagerHolder {
		public final static GameGroovyManager INSTANCE = new GameGroovyManager();
	}

	public static GameGroovyManager getInstance() {
		return GameGroovyManagerHolder.INSTANCE;
	}
}
