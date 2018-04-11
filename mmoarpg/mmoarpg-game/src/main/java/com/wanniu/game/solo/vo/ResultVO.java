package com.wanniu.game.solo.vo;

import java.util.HashMap;
import java.util.Map;

public class ResultVO {
	public static enum KEY {
		HAS_REWARD("hasReward"), AVG_MATCHTIME("avgMatchTime"), START_JOINTIME("startJoinTime");
		private String key;

		private KEY(String key) {
			this.key = key;
		}

		public String getValue() {
			return this.key;
		}
	}

	public ResultVO() {
		this(true);
	}

	public ResultVO(boolean defResult) {
		this(defResult, "");
	}

	public ResultVO(boolean defResult, String defInfo) {
		this.result = defResult;
		this.info = defInfo;
		otherResult = new HashMap<>();
	}

	private Map<KEY, Integer> otherResult;
	public boolean result;
	public String info;

	public void set(KEY key, Integer value) {
		otherResult.put(key, value);
	}

	public Integer get(KEY key) {
		return otherResult.get(key);
	}

	// public String getString(KEY key){
	// return (String)otherResult.get(key);
	// }
	//

	// public int getInt(KEY key){
	// String res = getString(key);
	// if(res==null){
	// return 0;
	// }
	// return Integer.parseInt(res);
	// }

}
