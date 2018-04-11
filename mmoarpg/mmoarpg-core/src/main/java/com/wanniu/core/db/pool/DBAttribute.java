package com.wanniu.core.db.pool;

import org.w3c.dom.Node;

import com.wanniu.core.util.xml.XW3CParsable;

/**
 * 数据库连接配置参数
 * 
 * @author agui
 */
public final class DBAttribute implements XW3CParsable {

	private static final long CHECK_INTERVAL = 1000 * 60 * 5;

	private String dsName;

	private String driver; // 数据库驱动程序

	private String url; // 数据连接的URL

	private String username; // 数据库用户名

	private String password; // 数据库密码

	private int minConnection = 2; // 最小连接数

	private int maxConnection = 30; // 最大连接数

	private long timeout = 300000; // 连接的超时时间

	private int waitTime = 10000; // 取连接的时超过连接上限后，尝试重新获取的等待时间

	private boolean checkAble = true; // 是否进行维护检测

	private long checkInterval = CHECK_INTERVAL; // 维护检测时间

	public String getDriver() {
		return driver;
	}

	public int getMaxConnection() {
		return maxConnection;
	}

	public int getMinConnection() {
		return minConnection;
	}

	public String getPassword() {
		return password;
	}

	public long getTimeout() {
		return timeout;
	}

	public String getUrl() {
		return url;
	}

	public String getUserName() {
		return username;
	}

	public int getWaitTime() {
		return waitTime;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public void setMaxConnection(int maxConnection) {
		this.maxConnection = maxConnection;
	}

	public void setMinConnection(int minConnection) {
		this.minConnection = minConnection < 0 ? 0 : minConnection;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setTimeoutValue(long timeoutValue) {
		this.timeout = timeoutValue;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUserName(String username) {
		this.username = username;
	}

	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}

	public String getDsName() {
		return dsName;
	}

	public void setDsName(String dsName) {
		this.dsName = dsName;
	}

	public long getCheckInterval() {
		return checkInterval;
	}

	public boolean isCheckAble() {
		return checkAble;
	}

	public boolean parse(Node xmlBean) {
		if (xmlBean.getNodeName().equals("pool")) {
			for (Node node = xmlBean.getFirstChild(); node != null; node = node.getNextSibling()) {
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					String nodeName = node.getNodeName().intern();
					if (nodeName.equals("dsname")) {
						dsName = node.getTextContent();
					} else if (nodeName.equals("driver")) {
						driver = node.getTextContent();
					} else if (nodeName.equals("url")) {
						url = node.getTextContent();
					} else if (nodeName.equals("username")) {
						username = node.getTextContent();
					} else if (nodeName.equals("password")) {
						password = node.getTextContent();
					} else if (nodeName.equals("minconn")) {
						minConnection = Integer.parseInt(node.getTextContent());
					} else if (nodeName.equals("maxconn")) {
						maxConnection = Integer.parseInt(node.getTextContent());
					} else if (nodeName.equals("timeout")) {
						timeout = Long.parseLong(node.getTextContent());
					} else if (nodeName.equals("waittime")) {
						waitTime = Integer.parseInt(node.getTextContent());
					} else if (nodeName.equals("checkable")) {
						checkAble = Boolean.parseBoolean(node.getTextContent());
					} else if (nodeName.equals("checkinterval")) {
						checkInterval = Long.parseLong(node.getTextContent());
					}
				}
			}

			return true;
		}
		return false;
	}

}
