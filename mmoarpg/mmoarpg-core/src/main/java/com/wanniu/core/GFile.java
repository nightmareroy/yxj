package com.wanniu.core;

import java.io.File;



/**
 * 游戏文件系统的常量定义
 * @author agui
 */
interface GFile {

	/** 目录结构 */
	String _ROOT_DIR_ = System.getProperty("user.dir") + File.separator + "%s" + File.separator;

	/** 游戏世界服务私有配置文件目录 */
	String DIR_CONF_SERVER = String.format(_ROOT_DIR_, "conf");
	
	/** 游戏世界服务一致性配置文件目录 */
	String DIR_CONF_GAME = GConfig.getInstance().get("dir.conf.game", DIR_CONF_SERVER);
	
	/** 游戏服务私有文件存储目录 */
	String DIR_DATA = GConfig.getInstance().get("dir.data", String.format(_ROOT_DIR_, "data"));

	/** 游戏服务策划文件配置目录 */
	String DIR_COMMON = GConfig.getInstance().get("dir.common", DIR_DATA + "common" + File.separator);

	/** 游戏服务脚本类文件根目录 */
	String DIR_SCRIPT_ROOT = GConfig.getInstance().get("dir.scripts", String.format(_ROOT_DIR_, "classes"));

	/** 游戏服务资源文件根目录 */
	String DIR_RESOURCE_ROOT = new File(GConfig.getInstance().get("dir.resource.root", String.format(_ROOT_DIR_, "resource"))).getPath() + File.separator;
	
	/** 过滤字符文件目录 */
	String DIR_FILTER = GConfig.getInstance().get("dir.filter", DIR_COMMON + "filter"+ File.separator);

	/** 昵称文件目录 */
	String DIR_NICKNAME = GConfig.getInstance().get("dir.nickname", DIR_COMMON + "nickname"+ File.separator);

	/** 语言包文件目录 */
	String DIR_LANGUAGE = GConfig.getInstance().get("dir.language", DIR_COMMON + "language"+ File.separator);
		
	/** 语言包文件目录 */
	String DIR_VERSION = GConfig.getInstance().get("dir.version", DIR_COMMON + "version"+ File.separator);

	/** 数据库服务配置文件名 */
	String FILE_CONF_DS = DIR_CONF_SERVER + "conf.ds.xml";

	/** 文件日志存储目录 */
	String DIR_LOG = GConfig.getInstance().get("log.dir", System.getProperty("user.dir"));
	
}
