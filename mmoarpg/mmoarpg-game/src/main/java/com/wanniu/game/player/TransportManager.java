package com.wanniu.game.player;

import java.util.HashMap;
import java.util.Map;

/**
 * 传输数据保存
 * 
 * @author Yangzz
 *
 */
public class TransportManager {

	public WNPlayer player;
	public Map<Integer, Integer> transportData;

	public TransportManager(WNPlayer player, Map<Integer, Integer> transportData) {
		this.player = player;
		if (transportData == null) {
			transportData = new HashMap<>();
			;
		}
		this.transportData = transportData;
	};

	public void addNum(int id) {
		int num = this.transportData.get(id);
		num = num + 1;
		this.transportData.put(id, num);
	};

	public int getNum(int id) {
		int num = this.transportData.get(id);
		return num;
	};

	// public int toJson4Serialize() {
	// return this.transportData;
	// };
}
