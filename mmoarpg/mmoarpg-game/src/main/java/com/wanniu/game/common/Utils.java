package com.wanniu.game.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.wanniu.core.logfs.Out;
import com.wanniu.core.util.RandomUtil;
import com.wanniu.game.common.Const.ForceType;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.base.FourProp;
import com.wanniu.game.data.ext.AffixExt;
import com.wanniu.game.equip.RepeatKeyMap;
import com.wanniu.game.item.data.AttsObj;

/**
 * 
 * @author Yangzz
 *
 */
public class Utils {

	private static SerializeConfig mapping = new SerializeConfig();
	private static String dateFormat;
	static {
		dateFormat = "yyyy-MM-dd HH:mm:ss";
		mapping.put(Date.class, new SimpleDateFormatSerializer(dateFormat));
	}

	public static boolean randomPercent(int per) {
		int ran = random(1, 100);
		return (ran >= 1 && ran <= per);
	};

	public static int getIndexByRareByList(List<Integer> rareArray) {
		if (rareArray == null || rareArray.size() == 0) {
			return 0;
		}
		int[] array = new int[rareArray.size()];
		for (int i = 0; i < rareArray.size(); i++) {
			array[i] = rareArray.get(i);
		}
		return getIndexByRare(array);
	}

	/**
	 * 通过权值获得相应下表 params: rares[] 权值数组
	 */
	public static int getIndexByRare(int[] rareArray) {
		if (rareArray == null || rareArray.length == 0) {
			return 0;
		}
		int result = 0;
		int rares = 0;
		for (int rare : rareArray) {
			rares = rares + rare;
		}
		int ranRare = random(0, rares);
		int maxRare = 0;
		for (int i = 0; i < rareArray.length; i++) {
			int rare = rareArray[i];
			maxRare = maxRare + rare;
			if (ranRare <= maxRare) {
				result = i;
				break;
			}
		}
		return result;
	}

	/**
	 * 获取指定范围的随机数
	 */
	public static int random(int min, int max) {
		return RandomUtil.getInt(min, max);
	}

	public static final Map<String, Integer> splitItems(String itemStr, String separator1, String separator2) {
		Map<String, Integer> ret = new TreeMap<String, Integer>();
		itemStr = itemStr.trim();
		if (itemStr != null && itemStr.length() > 0) {
			String[] items = itemStr.trim().split(separator1);
			for (String item : items) {
				String[] tmp = item.split(separator2);
				if (tmp.length > 1) {
					ret.put(tmp[0], Integer.parseInt(tmp[1]));
				} else {
					ret.put(tmp[0], 1);
				}
			}
		}
		return ret;
	}

	public static final List<Map<String, Object>> splitItems2(String itemStr, String separator1, String separator2) {
		List<Map<String, Object>> ret = new ArrayList<>();
		itemStr = itemStr.trim();
		if (itemStr != null && itemStr.length() > 0) {
			String[] items = itemStr.trim().split(separator1);
			for (String item : items) {
				String[] tmp = item.split(separator2);

				Map<String, Object> map = new HashMap<>();
				map.put("itemCode", tmp[0]);
				map.put("itemNum", tmp.length > 1 ? Integer.parseInt(tmp[1]) : 1);
				map.put("forceType", tmp.length > 2 ? ForceType.getE(Integer.parseInt(tmp[1])) : Const.ForceType.DEFAULT);
				ret.add(map);
			}
		}
		return ret;
	}

	public static final int[] listToArray(List<Integer> list) {
		int[] ret = new int[list.size()];
		for (int i = list.size() - 1; i >= 0; i--) {
			ret[i] = list.get(i).intValue();
		}
		return ret;
	}

	/**
	 * 获取今天0点时间
	 * 
	 * @returns {Date}
	 */
	public static Date getZeroDate() {
		Date date = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		return c.getTime();
	};

	/**
	 * 深度克隆实现,必须实现 Serializable. 浅克隆可以直接调用ICloneable的clone()
	 */
	@SuppressWarnings("unchecked")
	public static <T> T clone(T t) {
		T newObj = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(t);

			// 将流序列化成对象
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			newObj = (T) ois.readObject();
		} catch (Exception e) {
			Out.error(e);
		}
		return newObj;
	}

	/**
	 * 复制随机词条属性对象
	 * 
	 * @param source<affix:ID, value>
	 */
	public static void deepCopyAffix(Map<String, Integer> data, Map<Integer, Integer> source, int qcolor) {
		if (source == null) {
			return;
		}
		for (Map.Entry<Integer, Integer> entry : source.entrySet()) {
			AffixExt affix = GameData.Affixs.get(entry.getKey());
			if(affix == null) {
				Out.warn("key="+entry.getKey() + " val=" + entry.getValue());
				continue;
			}
			FourProp pair = affix.props.get(qcolor);
			if(pair==null) {
				continue;
			}
			if (data.get(pair.prop) != null && data.get(pair.prop) > 0) {
				data.put(pair.prop, data.get(pair.prop) + entry.getValue());
			} else {
				data.put(pair.prop, entry.getValue());
			}
		}
	}

	/**
	 * 复制随机词条属性对象
	 * 
	 * @param source<affix:ID, value>
	 */
	public static void deepCopyAffix(Map<String, Integer> data, RepeatKeyMap<Integer, Integer> source, int qcolor) {
		if (source == null) {
			return;
		}
		for (RepeatKeyMap.Pair<Integer, Integer> entry : source.entrySet()) {
			AffixExt affix = GameData.Affixs.get(entry.k);
			if(affix == null) {
				Out.warn("key="+entry.k + " val=" + entry.v);
				continue;
			}
			FourProp pair = affix.props.get(qcolor);
			if(pair==null) {
				continue;
			}
			if (data.get(pair.prop) != null && data.get(pair.prop) > 0) {
				data.put(pair.prop, data.get(pair.prop) + entry.v);
			} else {
				data.put(pair.prop, entry.v);
			}
		}
	}

	/**
	 * 复制Map对象
	 */
	public static void deepCopy(Map<String, Integer> data, Map<String, Integer> source) {
		if (source == null) {
			return;
		}
		for (Map.Entry<String, Integer> entry : source.entrySet()) {
			if (data.get(entry.getKey()) != null && data.get(entry.getKey()) > 0) {
				data.put(entry.getKey(), data.get(entry.getKey()) + entry.getValue());
			} else {
				data.put(entry.getKey(), entry.getValue());
			}
		}
	};

	/**
	 * 复制Map对象
	 */
	public static void deepCopy(Map<String, Integer> data, List<AttsObj> source) {
		for (AttsObj entry : source) {
			if (data.get(entry.key) != null && data.get(entry.key) > 0) {
				data.put(entry.key, data.get(entry.key) + entry.value);
			} else {
				data.put(entry.key, entry.value);
			}
		}
	};

	public static void deepCopy(Map<String, Integer> data, AttsObj source) {
		if (source == null) {
			return;
		}
		if (data.get(source.key) != null && data.get(source.key) > 0) {
			data.put(source.key, data.get(source.key) + source.value);
		} else {
			data.put(source.key, source.value);
		}
	};

	public static Date getTodayTimeFromString(String stringTime, String separator) {
		String separatorStr = ":";
		if (separator != null) {
			separatorStr = separator;
		}
		String[] numbers = stringTime.split(separatorStr);
		Date date = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		if (numbers.length == 3) {
			c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(numbers[0]));
			c.set(Calendar.MINUTE, Integer.parseInt(numbers[1]));
			c.set(Calendar.SECOND, Integer.parseInt(numbers[2]));
		}
		return c.getTime();
	};

	/**
	 * 构造Map对象
	 * 
	 * @param <T>
	 * @param ts
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> ofMap(Object... params) {
		LinkedHashMap<K, V> result = new LinkedHashMap<K, V>();

		// 无参 返回空即可
		if (params == null || params.length == 0) {
			return result;
		}

		// 处理成对参数
		int len = params.length;
		for (int i = 0; i < len; i += 2) {
			K key = (K) params[i];
			V val = (V) params[i + 1];

			result.put(key, val);
		}

		return result;
	}

	public static String toJSONString(Object... params) {
		JSONObject json = toJSON(params);
		return json.toJSONString();
	}

	public static JSONObject toJSON(Object... params) {
		JSONObject json = new JSONObject(params.length / 2);
		// 无参 返回空即可
		if (params != null) {
			// 处理成对参数
			for (int i = 0, len = params.length; i < len; i += 2) {
				json.put(params[i].toString(), params[i + 1]);
			}
		}
		return json;
	}

	public static String serialize(Object obj) {
		return JSON.toJSONString(obj, mapping);
	}

	public static <T> T deserialize(String buf, Class<T> clazz) {
		return JSON.parseObject(buf, clazz);
	}

	/** long类型转成byte数组 */
	public static byte[] longToByte(long number) {
		long temp = number;
		byte[] b = new byte[8];
		for (int i = 0; i < b.length; i++) {
			b[i] = new Long(temp & 0xff).byteValue();// 将最低位保存在最低位
			temp = temp >> 8; // 向右移8位
		}
		return b;
	}

	/** byte数组转成long */
	public static long byteToLong(byte[] b) {
		long s = 0;
		long s0 = b[0] & 0xff;// 最低位
		long s1 = b[1] & 0xff;
		long s2 = b[2] & 0xff;
		long s3 = b[3] & 0xff;
		long s4 = b[4] & 0xff;// 最低位
		long s5 = b[5] & 0xff;
		long s6 = b[6] & 0xff;
		long s7 = b[7] & 0xff;

		// s0不变
		s1 <<= 8;
		s2 <<= 16;
		s3 <<= 24;
		s4 <<= 8 * 4;
		s5 <<= 8 * 5;
		s6 <<= 8 * 6;
		s7 <<= 8 * 7;
		s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
		return s;
	}

	public static int getStackLineNumber() {
		return Thread.currentThread().getStackTrace()[0].getLineNumber();
	}

	public static int getSecMills(int min, int max) {
		return RandomUtil.getInt(min, max) * 1000;
	}

}
