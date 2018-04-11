package com.wanniu.game.attendance;

/**
 * 签到工具类
 * 
 * @author tanglt
 */
public class AttendanceUtil {

	private static AttendanceUtil instance;

	public static AttendanceUtil getInstance() {
		if (instance == null) {
			instance = new AttendanceUtil();
		}
		return instance;
	}

	private AttendanceUtil() {

	}

}
