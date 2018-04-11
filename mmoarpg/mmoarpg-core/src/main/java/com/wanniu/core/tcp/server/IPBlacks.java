package com.wanniu.core.tcp.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;

import com.wanniu.core.GConfig;
import com.wanniu.core.GGame;
import com.wanniu.core.GGlobal;
import com.wanniu.core.GSystem;
import com.wanniu.core.game.entity.GPlayer;
import com.wanniu.core.logfs.Out;

import io.netty.channel.Channel;

/**
 * IP黑名单
 * @author agui
 */
public final class IPBlacks extends ConcurrentSkipListSet<String> {

	private static final long serialVersionUID = 7814293538258552651L;

	private static IPBlacks instance = new IPBlacks();

	private static final String __IP_FILE_BLACK__ = GGlobal.DIR_COMMON + "black.ip";

	/** 记录异常名单 */
	private ConcurrentMap<String, Byte> ExceptionIPS = new ConcurrentHashMap<String, Byte>();
	
	private static Map<String, Long> __ENDCOOLS = new HashMap<String, Long>();
	private static int BLACKCOOL = GConfig.getInstance().getInt("game.blackcool", 1800000);

	private IPBlacks() { }

	public static IPBlacks getInstance() {
 		return instance;
	}
	
	/**
	 * 加载IP黑名单
	 */
	public void loadBalckIps() {
		BufferedReader br = null;
		FileReader reader = null;
		File file = new File(__IP_FILE_BLACK__);
		if(file.exists()){
			try {
				reader = new FileReader(file);
				br = new BufferedReader(reader);
				String line = null;
				while ((line = br.readLine()) != null) {
					instance.add(line);
				}
			} catch (Exception e) {
				Out.error("loadBalckIps", e);
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
			Out.info("IP黑名单已加载：" , instance.size());
		} else {
			Out.info("IP黑名单【" , __IP_FILE_BLACK__ , "】未定义！");
		}
	}

	public long getEndCool(String ip) {
		return __ENDCOOLS.get(ip);
	}

	public void black(Channel session, String from) {
		SocketAddress remoteAddress = session.remoteAddress();
		session.close();
		if (remoteAddress instanceof InetSocketAddress) {
			InetSocketAddress addr = (InetSocketAddress) remoteAddress;
			String ip = addr.getAddress().getHostAddress();
			Out.error("black", from , ip);
			exceptionIp(ip);
		}
	}

	public void exceptionPlayer(String rid){
		GPlayer player = GGame.getInstance().getPlayer(rid);
		if(player!=null){
			exceptionIp(player.getSession());
		}
	}
	
	public void exceptionIp(Channel session) {
		String ip = getIp(session);
		if (ip != null) {
			exceptionIp(ip);
			if(__ENDCOOLS.containsKey(ip)) {
				session.close();
			}
		}
	}
	
	public void exceptionIp(String ip){
		byte count = ExceptionIPS.containsKey(ip) ? ExceptionIPS.get(ip) : 0;
		if(count > 30){
			if(!contains(ip)) {
				add(ip);
				class Cool implements Runnable {
					private String ip;
					private Cool(String ip) {
						this.ip = ip;
					}
					@Override
					public void run() {
						remove(ip);
						__ENDCOOLS.remove(ip);
						Out.debug("从黑名单中移除 -> " , ip);
					}
				}
				GSystem.addDelayJob(new Cool(ip), BLACKCOOL, TimeUnit.MILLISECONDS);
				__ENDCOOLS.put(ip, System.currentTimeMillis() + BLACKCOOL);
				Out.debug(ip , " -> 黑名单中");
			}
		} else {
			ExceptionIPS.put(ip, ++count);
		}
	}
	
	public void addIp(Channel session) {
		String ip = getIp(session);
		if (ip != null) {
			add(ip);
		}
	}

	public String getIp(Channel session){
		SocketAddress remoteAddress = session.remoteAddress();
		if (remoteAddress instanceof InetSocketAddress) {
			InetSocketAddress addr = (InetSocketAddress) remoteAddress;
			String ip = addr.getAddress().getHostAddress();
			return ip;
		}
		return null;
	}
	
}
