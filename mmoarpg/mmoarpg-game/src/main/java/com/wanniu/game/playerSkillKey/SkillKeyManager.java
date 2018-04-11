package com.wanniu.game.playerSkillKey;

import java.util.ArrayList;
import java.util.List;

import com.wanniu.game.common.Const.ManagerType;
import com.wanniu.game.common.Const.PlayerEventType;
import com.wanniu.game.common.ModuleManager;
import com.wanniu.game.data.ext.SkillDataExt;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.playerSkill.SkillUtil;
import com.wanniu.game.playerSkill.po.SkillDB;
import com.wanniu.game.poes.SkillsPO;

import pomelo.Common.SkillKeyStruct;

public class SkillKeyManager extends ModuleManager {
	public WNPlayer player;
	public SkillsPO player_skills;

	public SkillKeyManager(WNPlayer player, SkillsPO skills) {
		this.player = player;
		this.player_skills = skills;
	}

	/**
	 * @return
	 */
	public List<SkillKeyStruct> toJson4Payload() {
		List<SkillKeyStruct> list = new ArrayList<>();
		for (SkillDB skill : player_skills.skills.values()) {
			// if(skill.flag==1){
			SkillKeyStruct.Builder sk = SkillKeyStruct.newBuilder();
			sk.setKeyPos(skill.pos);
			sk.setFlag(skill.flag);
			sk.setBaseSkillId(skill.id);
			SkillDataExt prop = SkillUtil.getProp(skill.id);
			sk.setIcon(prop.skillIcon);
			sk.setName(prop.skillName);
			sk.setAdvancedSkillId(skill.id);
			sk.setUnlockLevel(prop.lvReqData.get(0));
			list.add(sk.build());
			// }
		}
		return list;
	}

	@Override
	public void onPlayerEvent(PlayerEventType eventType) {

	}

	@Override
	public ManagerType getManagerType() {
		return ManagerType.SKILL_KEY;
	}
}
