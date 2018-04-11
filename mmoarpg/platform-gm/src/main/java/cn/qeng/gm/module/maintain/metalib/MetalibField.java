/*
 * Copyright © 2017 qeng.cn All Rights Reserved.
 * 
 * 感谢您加入清源科技，不用多久，您就会升职加薪、当上总经理、出任CEO、迎娶白富美、从此走上人生巅峰
 * 除非符合本公司的商业许可协议，否则不得使用或传播此源码，您可以下载许可协议文件：
 * 
 * 		http://www.noark.xyz/qeng/LICENSE
 *
 * 1、未经许可，任何公司及个人不得以任何方式或理由来修改、使用或传播此源码;
 * 2、禁止在本源码或其他相关源码的基础上发展任何派生版本、修改版本或第三方版本;
 * 3、无论你对源代码做出任何修改和优化，版权都归清源科技所有，我们将保留所有权利;
 * 4、凡侵犯清源科技相关版权或著作权等知识产权者，必依法追究其法律责任，特此郑重法律声明！
 */
package cn.qeng.gm.module.maintain.metalib;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 
 *
 * @author 小流氓(mingkai.zhou@qeng.net)
 */
public class MetalibField {
	private final int index;
	private final String name;
	private final String type;
	private final String desc;
	private final boolean fixdate;

	public MetalibField(int index, String name, String type, String desc) {
		this.index = index;
		this.name = name;
		this.type = type;
		this.fixdate = "date".equalsIgnoreCase(type);
		this.desc = desc;
	}

	public String getName() {
		return name;
	}

	public Object getValue(String[] array) {
		if (index >= array.length) {
			return getDefaultValue();
		}
		switch (type) {
		case "int":
			return Integer.parseInt(array[index]);
		case "long":
			return Long.parseLong(array[index]);
		case "float":
			return Float.parseFloat(array[index]);
		case "double":
			return Double.parseDouble(array[index]);
		case "boolean":
			return Boolean.parseBoolean(array[index]);
		case "string":
			return array[index];
		case "date":
			try {
				return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(array[index]);
			} catch (ParseException e) {
				return null;// FIXME
			}
		default:
			return null;
		}
	}

	private Object getDefaultValue() {
		switch (type) {
		case "int":
			return 0;
		case "long":
			return 0L;
		case "float":
			return 0F;
		case "double":
			return 0D;
		case "boolean":
			return false;
		default:
			return null;
		}
	}

	public boolean isFixdate() {
		return fixdate;
	}

	public int getIndex() {
		return index;
	}

	public String getType() {
		return type;
	}

	public String getDesc() {
		return desc;
	}
}