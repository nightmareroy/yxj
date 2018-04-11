package com.wanniu.tcp.protocol;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.wanniu.util.Out;

import io.netty.buffer.ByteBuf;

/**
 * 通讯协议包头
 * @author agui
 */
public class Header {
	
	/** 协议类型 */
	protected short type;
	
	/** 协议包体字节数 */
	protected int length;

	/**
	 * 从in里解析包头的各个属性 建议只在codec的decoder里使用
	 * 
	 * @param in
	 */
	public void decode(ByteBuf in) {
		type = in.readShort();
		length = in.readInt();
	}
	
	public void decodeHeader(ByteBuf in) {
		decode(in);
	}
	
	public void decode(Header header) {
		type = header.getType();
		length = header.getLength();
	}
	
	/**
	 * 把包头的各个属性编码到out里 建议只在codec的encoder里使用
	 */
	public void encode(ByteBuf out) {
		out.writeShort(type);
		out.writeInt(length);
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public short getType() {
		return type;
	}

	/**
	 * 获取type的16进制表示
	 */
	public String getTypeHexString() {
		return "0x" + Integer.toHexString(type);
	}

	public void setType(short type) {
		this.type = type;
	}

	/**
	 * 计算包头的字节数，不包括static和非基本类型
	 */
	private static byte calcBytes() {
		Field[] fs = Header.class.getDeclaredFields();
		return (byte) (calcSize(fs) - 2);
	}

	public static int calcSize(Field[] fs) {
		int bits = 0;
		for (Field f : fs) {
			int mod = f.getModifiers();
			String type = f.getGenericType().toString().intern();
			if (!Modifier.isStatic(mod)) {
				if (type == "byte") {
					bits += Byte.SIZE;
				} else if (type == "short") {
					bits += Short.SIZE;
				} else if (type == "int") {
					bits += Integer.SIZE;
				} else if (type == "long") {
					bits += Long.SIZE;
				} else if (type == "char") {
					bits += Character.SIZE;
				} else if (type == "float") {
					bits += Float.SIZE;
				} else if (type == "double") {
					bits += Double.SIZE;
				} else {
					Out.warn("过滤了非基础数据类型属性：" + Modifier.toString(mod) + " - " + type + "[" + f.getName() + "]");
				}
			} else {
				if(!"SIZE".equals(f.getName())){
					Out.warn("过滤了静态属性：" + Modifier.toString(mod) + " - " + type + "[" + f.getName() + "]");
				}
			}
		}
		bits /= 8;
		if (bits > Byte.MAX_VALUE || bits < Byte.MIN_VALUE) {
			Out.error("数值[" + bits + "]超过byte边界");
			return 0;
		}
		return bits;
	}

	/** 包头的字节数 */
	public final static byte SIZE = calcBytes();

}
