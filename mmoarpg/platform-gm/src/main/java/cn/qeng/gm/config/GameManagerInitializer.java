package cn.qeng.gm.config;

import org.springframework.stereotype.Service;

import com.wanniu.GConfig;
import com.wanniu.GServer;

@Service
public class GameManagerInitializer {

	public void init() {
		GConfig.getInstance().init(WebInitializer.class.getResource("/").getFile() + "/config.properties");
		// loadServers();
		GServer.getInstance().start();
	}
}
