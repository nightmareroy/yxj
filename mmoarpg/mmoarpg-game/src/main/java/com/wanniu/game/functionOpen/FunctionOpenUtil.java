package com.wanniu.game.functionOpen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wanniu.game.data.GameData;
import com.wanniu.game.data.OpenLvCO;

public class FunctionOpenUtil {

	public static List<OpenLvCO> getPropList() {
		List<OpenLvCO> list = new ArrayList<>();
		for (Map.Entry<Integer, OpenLvCO> node : GameData.OpenLvs.entrySet()) {
			list.add(node.getValue());
		}
		return list;
	}

	public static List<OpenLvCO> findFunctionOpenPropsByIsReq(int isReq) {
		List<OpenLvCO> props = new ArrayList<>();
		for (Map.Entry<Integer, OpenLvCO> node : GameData.OpenLvs.entrySet()) {
			OpenLvCO data = node.getValue();
			if (data.isReq == isReq) {
				props.add(data);
			}
		}
		return props;
	}

	public static List<OpenLvCO> findFunctionOpenPropsByIsReqAndOpenReq(int isReq, String openReq) {
		List<OpenLvCO> props = new ArrayList<>();
		for (Map.Entry<Integer, OpenLvCO> node : GameData.OpenLvs.entrySet()) {
			OpenLvCO each = node.getValue();
			if (each.isReq == isReq && each.openReq.equals(openReq)) {
				props.add(each);
			}
		}
		return props;
	}

	// 通过开启条件筛选
	public static List<OpenLvCO> getPropListByReqEvent(int reqType, String reqEvent) {
		if (reqEvent == null) {
			// return dataAccessor.functionOpenProps.find({IsReq: reqType});
			return findFunctionOpenPropsByIsReq(reqType);
		} else {
			// return dataAccessor.functionOpenProps.find({IsReq: reqType, OpenReq:
			// String(reqEvent)});
			return findFunctionOpenPropsByIsReqAndOpenReq(reqType, reqEvent);
		}
	}

	// 通过开启条件类型筛选
	public static List<OpenLvCO> getPropListByReqTypeArray(List<Integer> reqTypeArray) {
		List<OpenLvCO> props = new ArrayList<>();
		for (Map.Entry<Integer, OpenLvCO> node : GameData.OpenLvs.entrySet()) {
			OpenLvCO each = node.getValue();
			if (reqTypeArray.indexOf(each.isReq) != -1) {
				props.add(each);
			}
		}
		return props;
	}

	// 通过功能id筛选
	public static List<OpenLvCO> getPropListByIdArray(ArrayList<Integer> idArray) {
		List<OpenLvCO> props = new ArrayList<>();
		for (Map.Entry<Integer, OpenLvCO> node : GameData.OpenLvs.entrySet()) {
			OpenLvCO each = node.getValue();
			if (idArray.indexOf(each.iD) != -1) {
				props.add(each);
			}
		}
		return props;
	}

	public static List<OpenLvCO> getPropListByFunctionNameArray(List<String> nameArray) {
		List<OpenLvCO> props = new ArrayList<>();
		for (Map.Entry<Integer, OpenLvCO> node : GameData.OpenLvs.entrySet()) {
			OpenLvCO each = node.getValue();
			if (nameArray.indexOf(each.fun) != -1) {
				props.add(each);
			}
		}
		return props;
	}

	public static OpenLvCO getPropById(int funcId) {
		return GameData.OpenLvs.get(funcId);
	}

	public static OpenLvCO findFunctionOpenPropsByFuncName(String funcName) {
		for (Map.Entry<Integer, OpenLvCO> node : GameData.OpenLvs.entrySet()) {
			OpenLvCO each = node.getValue();
			if (each.fun.equals(funcName)) {
				return each;
			}
		}
		return null;
	}

	public static OpenLvCO getPropByName(String funcName) {
		return findFunctionOpenPropsByFuncName(funcName);
	}

	public static String getTipsByName(String funcName) {
		OpenLvCO prop = getPropByName(funcName);
		if (prop != null) {
			return prop.tips;
		}
		return "";
	}

	// 是否默认开启
	public static boolean isDefaultOpen(String funcName) {
		int state = getDefaultOpenState(funcName);
		return (state == 1);
	}

	public static boolean isDefaultClose(String funcName) {
		int state = getDefaultOpenState(funcName);
		return (state == -1);
	}

	/**
	 * 读取配置查看默认状态
	 * 
	 * @param funcName 功能名字
	 * @returns {number} -1:系统关闭 1：创建角色即开启 0：需要条件需要条件
	 */
	public static int getDefaultOpenState(String funcName) {
		OpenLvCO prop = getPropByName(funcName);
		if (prop == null) {
			return 1;
		}
		if (prop.isOpen == 0) {
			return -1;
		}
		if (prop.type == 0) {
			return 1;
		}
		return 0;
	}

}
