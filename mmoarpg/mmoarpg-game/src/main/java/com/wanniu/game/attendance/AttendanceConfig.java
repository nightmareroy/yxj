package com.wanniu.game.attendance;

import java.util.ArrayList;

import com.wanniu.core.logfs.Out;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.AccumulateExt;
import com.wanniu.game.data.ext.LuxurySignExt;
import com.wanniu.game.data.ext.NormalSignExt;

/**
 * 签到脚本管理
 * 
 * @author tanglt
 */
public class AttendanceConfig {

	private static AttendanceConfig instance;

	public static AttendanceConfig getInstance() {
		if (instance == null) {
			instance = new AttendanceConfig();
		}
		return instance;
	}

	private AttendanceConfig() {

	}

	public final AccumulateExt findDaccumulateWithIDAndRound(int round, int id) {
		for (AccumulateExt dac : GameData.Accumulates.values()) {
			if (dac.id == id && dac.round == round) {
				return dac;
			}
		}
		Out.error(this.getClass().getName(), " : Can`t find Daccumulate By id = ", id, "  round = ", round);
		return null;
	}

	public final LuxurySignExt findDluxurySignWithID(int id) {
		for (LuxurySignExt dac : GameData.LuxurySigns.values()) {
			if (dac.id == id) {
				return dac;
			}
		}
		Out.error(this.getClass().getName(), " : Can`t find Dluxury_sign By id = ", id);
		return null;
	}

	public final NormalSignExt findDnormalSignWithIDAndRound(int round, int id) {
		for (NormalSignExt dac : GameData.NormalSigns.values()) {
			if (dac.id == id && dac.round == round) {
				return dac;
			}
		}
		Out.error(this.getClass().getName(), " : Can`t find Dnormal_sign By id = ", id, "  round = ", round);
		return null;
	}

	public final ArrayList<NormalSignExt> getDnormalSignsWithRound(int round) {
		ArrayList<NormalSignExt> list = new ArrayList<>();
		for (NormalSignExt dac : GameData.NormalSigns.values()) {
			if (dac.round == round) {
				list.add(dac);
			}
		}
		return list;
	}

	public final ArrayList<AccumulateExt> getDaccumulateWithRound(int round) {
		ArrayList<AccumulateExt> list = new ArrayList<>();
		for (AccumulateExt dac : GameData.Accumulates.values()) {
			if (dac.round == round) {
				list.add(dac);
			}
		}
		return list;
	}
}
