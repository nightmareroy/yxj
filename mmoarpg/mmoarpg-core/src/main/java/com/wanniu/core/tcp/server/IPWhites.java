package com.wanniu.core.tcp.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentSkipListSet;

import com.wanniu.core.GGlobal;
import com.wanniu.core.logfs.Out;

/**
 * IP白名单
 * @author agui
 */
public final class IPWhites extends ConcurrentSkipListSet<String> {

	private static final long serialVersionUID = 8350520844632354637L;

	private static IPWhites instance = new IPWhites();

	private static final String __IP_FILE_WHITE__ = GGlobal.DIR_COMMON + "white.ip";
	
	private IPWhites() { }

	public static IPWhites getInstance() {
		return instance;
	}

	/**
	 * 加载IP白名单
	 */
	public void loadWhiteIps() {
		BufferedReader br = null;
		FileReader reader = null;
		File file = new File(__IP_FILE_WHITE__);
		if(file.exists()){
			try {
				reader = new FileReader(file);
				br = new BufferedReader(reader);
				String line = null;
				while ((line = br.readLine()) != null) {
					if(!instance.contains(line)) {
						instance.add(line);
					}
				}
			} catch (Exception e) {
				Out.error("loadWhiteIps", e);
			} finally {
				try {
					if (br != null) {
						br.close();
					}
				} catch (IOException e) {
				}
				try {
					if (reader != null) {
						reader.close();
					}
				} catch (IOException e) {
				}
			}
			Out.info("IP白名单已加载：" , instance.size());
		} else {
			Out.info("IP白名单【" , __IP_FILE_WHITE__ , "】未定义！");
		}
	}

	/** 白名单检查策略 */
	public boolean check(String host) {
		return contains(host) ||  host.startsWith("192.168.");
	}
	
}
