package com.wanniu.game.buffer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.wanniu.core.logfs.Out;
import com.wanniu.game.data.AttributeCO;
import com.wanniu.game.player.AttributeUtil;
import com.wanniu.game.player.WNPlayer;

import Xmds.RefreshPlayerPropertyChange;
import pomelo.area.PlayerHandler.BuffInfo;
import pomelo.area.PlayerHandler.BuffPropertyPush;

/**
 * 数组管理（主要用于时效性的需要保存的状态）
 * 
 * @author Yangzz
 *
 */
public class BufferManager {

	public WNPlayer player;

	public List<RefreshPlayerPropertyChange> buffs;
	public List<RefreshPlayerPropertyChange> localBuffs;

	protected BufferManager() {

	}

	public BufferManager(WNPlayer player) {
		this.buffs = new ArrayList<>();
		this.localBuffs = new ArrayList<>(); // 后端buffList [{key:'ExdExp', value:100, timestamp:10000}]
		this.player = player;
		// Entity.call(this, opts);
		this._clear();
		this.clearLocalBuff();

	};

	public void _clear() {
		int len = this.buffs.size();
		long nowTime = System.currentTimeMillis();
		for (int i = len - 1; i >= 0; --i) {
			RefreshPlayerPropertyChange buff = this.buffs.get(i);
			if (buff.timestamp < nowTime) {
				// this.buffs.splice(i,1);
				this.buffs.remove(i);
			}
		}
	};

	public void clearLocalBuff() {
		long nowTime = System.currentTimeMillis();
		int len = this.localBuffs.size();
		for (int i = len - 1; i >= 0; --i) {
			RefreshPlayerPropertyChange buff = this.localBuffs.get(i);
			if (buff.timestamp < nowTime) {
				this.localBuffs.remove(i);// .splice(i, 1);
			}
		}
	};

	public void toJson4Serialize() {
		// this._clear();
		// var data = {};
		// data.buffs = this.buffs;
		// data.localBuffs = this.localBuffs;
		// return data;
	};

	public void add(RefreshPlayerPropertyChange buff) {
		this.buffs.add(buff);
	};

	public void send2BattleServer() {
		this._clear();
		Out.debug("send2BattleServer :", this.buffs);
		for (int i = 0; i < this.buffs.size(); ++i) {
			this.player.refreshPlayerPropertyChange(this.buffs.get(i));
		}
	};

	/**
	 * 添加本地buff（后端管理和使用，不需要通知战斗服)
	 * 
	 * @param key 属性key
	 * @param time 持续时间，毫秒
	 * @param value 数值, 如为百分比，则统一转为万分比
	 */
	public boolean addLocalBuff(String key, long time, int value) {
		boolean addResult = false;
		long nowTime = System.currentTimeMillis();
		int len = this.localBuffs.size();
		for (int i = len - 1; i >= 0; --i) {
			RefreshPlayerPropertyChange buff = this.localBuffs.get(i);
			if (buff.key.equals(key) && buff.value == value) {
				long oldExpireTime = Math.max(buff.timestamp, nowTime);
				buff.timestamp = oldExpireTime + time;
				addResult = true;
				break;
			}
		}
		if (!addResult) {
			AttributeCO prop = AttributeUtil.getPropByKey(key);
			if (prop != null) {
				RefreshPlayerPropertyChange buff = new RefreshPlayerPropertyChange();
				buff.key = key;
				buff.value = value;
				buff.timestamp = nowTime + time;
				this.localBuffs.add(buff);
				addResult = true;
			}
		}
		if (addResult) {
			this.pushLocalBuffToClient();
		}
		return addResult;
	};

	/**
	 * 获取某个属性buff数值
	 * 
	 * @param key
	 * @returns {*}
	 */
	public int getBuffAttrValue(String key) {
		int attrValue = 0;
		int len = this.localBuffs.size();
		long nowTime = System.currentTimeMillis();
		for (int i = len - 1; i >= 0; --i) {
			RefreshPlayerPropertyChange buff = this.localBuffs.get(i);
			if (!buff.key.equals(key) || buff.timestamp < nowTime) {
				continue;
			}
			attrValue += buff.value;
		}
		return attrValue;
	};

	public List<BuffInfo> toJson4LocalBuffPayLoad() {
		List<BuffInfo> buffList = new ArrayList<>();
		int len = this.localBuffs.size();
		long nowTime = System.currentTimeMillis();
		for (int i = len - 1; i >= 0; --i) {
			RefreshPlayerPropertyChange buff = this.localBuffs.get(i);
			if (buff.timestamp < nowTime) {
				this.localBuffs.remove(i);
				continue;
			}
			AttributeCO prop = AttributeUtil.getPropByKey(buff.key);
			if (prop == null) {
				continue;
			}
			BuffInfo.Builder temp = BuffInfo.newBuilder();
			temp.setId(prop.iD);
			temp.setValue(buff.value);
			temp.setIsFormat(prop.isFormat);
			temp.setExpireTime((int) Math.floor(buff.timestamp / 1000));

			buffList.add(temp.build());
		}
		buffList.sort(new Comparator<BuffInfo>() {
			@Override
			public int compare(BuffInfo o1, BuffInfo o2) {
				return o1.getExpireTime() - o2.getExpireTime();
			}

		});

		return buffList;
	};

	// 推送buff列表给前端
	public void pushLocalBuffToClient() {
		if (this.localBuffs.isEmpty()) {
			return;
		}
		List<BuffInfo> buffs = this.toJson4LocalBuffPayLoad();
		BuffPropertyPush.Builder data = BuffPropertyPush.newBuilder();
		data.addAllBuffList(buffs);
		player.receive("area.playerPush.buffPropertyPush", data.build());
	};
}
