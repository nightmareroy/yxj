package com.wanniu.gm.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanniu.core.util.DateUtil;
import com.wanniu.game.common.ConstsTR;
import com.wanniu.game.daoyou.DaoYouService;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.CharacterExt;
import com.wanniu.game.guild.GuildUtil;
import com.wanniu.game.player.PlayerDao;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.poes.DaoYouPO;
import com.wanniu.game.poes.PlayerPO;
import com.wanniu.gm.GMErrorResponse;
import com.wanniu.gm.GMEvent;
import com.wanniu.gm.GMJsonResponse;
import com.wanniu.gm.GMResponse;
import com.wanniu.redis.GameDao;

import cn.qeng.common.gm.RpcOpcode;
import cn.qeng.common.gm.vo.GmPlayerInfoVO;

/**
 * 查询玩家所有信息
 * 
 * @author lxm
 *
 */
@GMEvent
public class QueryRoleInfoHandler extends GMBaseHandler {

	public GMResponse execute(JSONArray arr) {
		String roleName = arr.getString(0);
		int type = arr.getIntValue(1);
		if (type == 0) {// 精确查找
			String id = PlayerDao.getIdByName(roleName);
			if (id == null) {
				return new GMErrorResponse();
			}
			PlayerPO po = PlayerUtil.getPlayerBaseData(id);
			CharacterExt character = GameData.Characters.get(po.pro);
			String vip = "无";
			if (po.vip == 1) {
				vip = "月惠卡";
			} else if (po.vip == 2) {
				vip = "尊享卡";
			}
			DaoYouPO daoyou = DaoYouService.getInstance().getDaoYou(po.id);
			GmPlayerInfoVO vo = new GmPlayerInfoVO(po.id, po.uid, po.name, po.level, po.exp + "/" + GameData.CharacterLevels.get(po.level).experience, po.diamond, po.gold, vip, po.loginTime != null ? DateUtil.format(po.loginTime) : "", character.proName, PlayerUtil.isOnline(id) ? "是" : "否", po.logoutTime != null ? DateUtil.format(po.logoutTime) : "", po.isDelete == 1 ? "已删除" : "正常", po.classExp + "/" + GameData.UpLevelExps.get(po.upOrder + 1).reqClassExp, po.ticket, GuildUtil.getGuildName(po.id),
					daoyou == null ? "" : daoyou.name, po.fightPower);

			return new GMJsonResponse(JSON.toJSONString(vo));
		} else {// 模糊查询
			List<GmPlayerInfoVO> list = new ArrayList<>();
			Set<String> names = GameDao.hkeys(ConstsTR.NAME_MODULE.value);
			for (String n : names) {
				if (n.contains(roleName)) {
					String id = PlayerDao.getIdByName(n);
					PlayerPO po = PlayerUtil.getPlayerBaseData(id);
					if (po == null) {
						continue;
					}
					GmPlayerInfoVO vo = new GmPlayerInfoVO();
					vo.setName(po.name);
					vo.setLevel(po.level);
					vo.setPro(GameData.Characters.get(po.pro).proName);
					list.add(vo);
				}
			}
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("total", list.size());
			data.put("rows", list);
			JSONObject jo = new JSONObject(data);
			return new GMJsonResponse(jo);
		}
	}

	public short getType() {
		return RpcOpcode.OPCODE_QUERY_PLAYER_INFO;
	}
}