package com.wanniu.game.equip;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 重铸专用，为了解决替换老代码不能满足新需求的问题
 * 
 * @author wfy
 *
 * @param <K>
 * @param <V>
 */
public class RepeatKeyMap<K, V> implements Serializable {

	public static class Pair<K, V> implements Serializable {
		private static final long serialVersionUID = 6716065295786812835L;
		public K k;
		public V v;

		public Pair() {

		}

		public Pair(K k, V v) {
			this.k = k;
			this.v = v;
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Pair<K, V>> values = null;

	public List<Pair<K, V>> getValues() {
		return values;
	}

	public void clear() {
		if (values != null) {
			values.clear();
		}
	}

	public boolean isEmpty() {
		return values == null || values.isEmpty();
	}

	public void setValues(List<Pair<K, V>> values) {
		this.values = values;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public RepeatKeyMap() {
		values = new ArrayList<>();
	}

	public V put(K key, V value) {
		values.add(new Pair<K, V>(key, value));
		return value;
	}

	/**
	 * 找到有key为0的pair，就直接替换为对应的key，value
	 * 
	 * @param key
	 * @param value
	 */
	public void putIfEmpty(K key, V value) {
		boolean hit = false;
		for (Pair<K, V> pair : values) {
			if (String.valueOf(pair.k).equals("0")) {
				pair.k = key;
				pair.v = value;
				hit = true;
				break;
			}
		}
		if (!hit) {// 没找到空缺，就加到末尾
			values.add(new Pair<K, V>(key, value));
		}

	}

	public void put(Pair<K, V> pair) {
		values.add(pair);
	}

	public List<K> keySet() {
		List<K> list = new ArrayList<>();
		for (Pair<K, V> pair : values) {
			list.add(pair.k);
		}
		return list;
	}

	public List<Pair<K, V>> entrySet() {
		return values;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (Pair<K, V> pair : values) {
			sb.append(pair.k).append("=").append(pair.v).append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("}");
		return sb.toString();
	}

	public boolean containsKey(K key) {
		for (Pair<K, V> pair : values) {
			if (pair.k.equals(key)) {
				return true;
			}
		}

		return false;
	}

	public int size() {
		return values.size();
	}

	public static void main(String[] args) {
		RepeatKeyMap<Integer, Integer> tm = new RepeatKeyMap<>();
		for (int i = 1; i <= 10; i++) {
			tm.put(100 + i, 100 * i);
		}

		System.out.println(tm.toString());
	}
}
