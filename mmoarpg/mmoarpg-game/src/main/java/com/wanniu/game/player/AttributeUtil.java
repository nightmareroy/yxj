package com.wanniu.game.player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.wanniu.core.logfs.Out;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.data.AttributeCO;
import com.wanniu.game.data.GameData;

import pomelo.Common.AttributeBase;
import pomelo.Common.AttributeSimple;

public class AttributeUtil {

	public static String getKeyByName(String name) {
		for (AttributeCO dAttr : GameData.Attributes.values()) {
			if (dAttr.attName.equals(name)) {
				return dAttr.attKey;
			}
		}
		Out.error(name, " is not found:getKeyByName");
		throw new RuntimeException();
	}

	public static String getNameByKey(String key) {
		for (AttributeCO dAttr : GameData.Attributes.values()) {
			if (dAttr.attKey.equals(key)) {
				return dAttr.attName;
			}
		}
		Out.error(key, " is not found:getNameByKey");
		throw new RuntimeException();
	}

	public static AttributeCO find(String key) {
		for (AttributeCO dAttr : GameData.Attributes.values()) {
			if (dAttr.attKey.equals(key)) {
				return dAttr;
			}
		}
		Out.error(key, " is not found:find");
		throw new RuntimeException();
	}

	public static String getDescByKey(String key) {
		for (AttributeCO dAttr : GameData.Attributes.values()) {
			if (dAttr.attKey.equals(key)) {
				return dAttr.attDesc;
			}
		}
		Out.error(key, " is not found:getDescByKey");
		throw new RuntimeException();
	}

	public static int getIdByKey(String key) {
		for (AttributeCO dAttr : GameData.Attributes.values()) {
			if (dAttr.attKey.equals(key)) {
				return dAttr.iD;
			}
		}
		Out.error(key, " is not found:getIdByKey");
		throw new RuntimeException();
	}

	public static int getIdByName(String name) {
		for (AttributeCO dAttr : GameData.Attributes.values()) {
			if (dAttr.attName.equals(name)) {
				return dAttr.iD;
			}
		}
		Out.error(name, " is not found:name");
		throw new RuntimeException();
	}

	public static int getFormatByName(String name) {
		for (AttributeCO dAttr : GameData.Attributes.values()) {
			if (dAttr.attName.equals(name)) {
				return dAttr.isFormat;
			}
		}
		Out.error(name, " is not found:getFormatByName");
		throw new RuntimeException();
	}

	public static AttributeCO getPropByKey(String key) {
		for (AttributeCO dAttr : GameData.Attributes.values()) {
			if (dAttr.attKey.equals(key)) {
				return dAttr;
			}
		}
		Out.error(key, " is not found:getPropByKey");
		throw new RuntimeException();
	}

	public static AttributeCO getPropByName(String name) {
		for (AttributeCO dAttr : GameData.Attributes.values()) {
			if (dAttr.attName.equals(name)) {
				return dAttr;
			}
		}
		Out.error(name, " is not found:getPropByName");
		throw new RuntimeException();
	}

	public static int getFormatByKey(String key) {
		for (AttributeCO dAttr : GameData.Attributes.values()) {
			if (dAttr.attKey.equals(key)) {
				return dAttr.isFormat;
			}
		}
		Out.error(key, " is not found:getFormatByKey");
		throw new RuntimeException();
	}

	public static List<AttributeSimple> getAttributeSimple(Map<String, Integer> attrs) {
		List<AttributeSimple> data = new ArrayList<AttributeSimple>();
		for (String key : attrs.keySet()) {
			for (AttributeCO da : GameData.Attributes.values()) {
				if (da.attKey.equals(key)) {
					AttributeSimple.Builder as = AttributeSimple.newBuilder();
					as.setId(da.iD);
					as.setValue(attrs.get(key));
					data.add(as.build());
				}
			}
		}
		return data;
	}

	// var getAttributeSimple(attrs){
	// var data = [];
	// for(var key in attrs){
	// var props = attributeProps.find({attKey: key});
	// if(props.length > 0){
	// var prop = props[0];
	// data.push({id:prop.ID, value:attrs[key]});
	// }
	// }
	// return data;
	// };
	/**
	 * TODO 获取属性基本信息，传给客户端
	 * 
	 * @param attrs {key1;value1,key2:value2,...}
	 */
	public static List<AttributeBase> getAttributeBase(Map<String, Integer> attrs) {
		ArrayList<AttributeBase> data = new ArrayList<>();
		for (Map.Entry<String, Integer> node : attrs.entrySet()) {
			for (AttributeCO dAttr : GameData.Attributes.values()) {
				if (dAttr.attKey.equals(node.getKey())) {
					AttributeBase.Builder builder = AttributeBase.newBuilder();
					builder.setId(dAttr.iD);
					builder.setValue(node.getValue());
					builder.setIsFormat(dAttr.isFormat);
					data.add(builder.build());
				}
			}
		}
		return data;
	}

	/**
	 * 获取属性基本信息，传给客户端,参数属性类型可重复,相同属性不合并
	 * 
	 * @param attrArray [{attrKey1:value1},{attrKey2:value2}]
	 */
	public static List<AttributeBase> getAttributeBaseByArray(List<Map<String, Integer>> attrArray) {
		List<AttributeBase> data = new ArrayList<AttributeBase>();
		for (int i = 0; i < attrArray.size(); ++i) {
			Map<String, Integer> attrs = attrArray.get(i);
			for (String key : attrs.keySet()) {
				AttributeCO prop = AttributeUtil.find(key);
				if (null != prop) {
					AttributeBase.Builder attrBase = AttributeBase.newBuilder();
					attrBase.setId(prop.iD);
					attrBase.setValue(attrs.get(key));
					attrBase.setIsFormat(prop.isFormat);
					data.add(attrBase.build());
				}
			}
		}
		return data;
	};

	/**
	 * 获取属性基本信息，包括下一级属性
	 * 
	 * @param attrs {key1;value1,key2:value2,...}
	 * @param nextAttrs {key1;value1,key2:value2,...}
	 * @returns {Array}
	 */
	public static List<AttributeBase> getAttributeWithNext(Map<String, Integer> attrs, Map<String, Integer> nextAttrs) {
		List<AttributeBase> data = new ArrayList<>();
		Iterator<String> nextKeys = nextAttrs.keySet().iterator();
		while (nextKeys.hasNext()) {
			String nextkey = nextKeys.next();
			AttributeCO prop = getPropByKey(nextkey);
			if (prop != null) {
				AttributeBase.Builder ab = AttributeBase.newBuilder();
				if (attrs.get(nextkey) != null && attrs.get(nextkey) > 0) {
					ab.setId(prop.iD);
					ab.setValue(attrs.get(nextkey));
					ab.setNextValue(nextAttrs.get(nextkey));
					ab.setIsFormat(prop.isFormat);
					// ab.setMinValue(value)
					// ab.setMaxValue(value)
					// ab.setParam1(value)
					// ab.setParam2(value)
					// ab.setParam3(value)
				} else {
					ab.setId(prop.iD);
					ab.setValue(0);
					ab.setNextValue(nextAttrs.get(nextkey));
					ab.setIsFormat(prop.isFormat);
				}
				data.add(ab.build());
			}
		}

		return data;
	}

	/**
	 * 获取属性基本信息，包括param1属性
	 * 
	 * @param attrs {key1;value1,key2:value2,...}
	 * @param param1Attrs {key1;value1,key2:value2,...}
	 * @returns {Array}
	 */
	public static ArrayList<AttributeBase> getAttributeBaseWithParam1(Map<String, Integer> attrs, Map<String, Integer> param1Attrs) {
		ArrayList<AttributeBase> data = new ArrayList<>();
		for (Map.Entry<String, Integer> node : attrs.entrySet()) {
			String key = node.getKey();
			AttributeCO prop = AttributeUtil.find(key);
			if (!param1Attrs.containsKey(key)) {
				param1Attrs.put(key, 0);
			}
			AttributeBase.Builder builder = AttributeBase.newBuilder();
			builder.setId(prop.iD);
			builder.setValue(node.getValue());
			builder.setParam1(param1Attrs.get(key));
			builder.setIsFormat(prop.isFormat);
			data.add(builder.build());
		}
		return data;
	}

	public static float getScoreRatioByKey(String key) {
		for (AttributeCO dAttr : GameData.Attributes.values()) {
			if (dAttr.attKey.equals(key)) {
				if (dAttr.isEffect == 1) {
					return dAttr.scoreRatio;
				} else {
					return 0;
				}
			}
		}
		Out.error(key, " is not found:getScoreRatioByKey");
		throw new RuntimeException();
	};

	/**
	 * 将两个属性集合合并到一个集合里面
	 * 
	 * @param src
	 * @param dest
	 */
	public static void addData2AllData(Map<PlayerBtlData, Integer> src, Map<PlayerBtlData, Integer> dest) {
		for (PlayerBtlData key : src.keySet()) {
			if (dest.containsKey(key)) {
				int value = dest.get(key) + src.get(key);
				dest.put(key, value);
			} else {
				dest.put(key, src.get(key));
			}
		}
	}

	/**
	 * 将两个属性集合合并到一个集合里面
	 * 
	 * @param src
	 * @param dest
	 */
	public static void addData2AllDataByKey(Map<String, Integer> src, Map<PlayerBtlData, Integer> dest) {
		for (String key : src.keySet()) {
			PlayerBtlData btlData = PlayerBtlData.getEByKey(key);
			if (btlData == null) {
				Out.error("找不到装备对应的属性：", key);
				continue;
			}
			if (dest.containsKey(btlData)) {
				int value = dest.get(btlData) + src.get(key);
				dest.put(btlData, value);
			} else {
				dest.put(btlData, src.get(key));
			}
		}
	}
}
