package com.wanniu.game.fashion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.FashionExt;
import com.wanniu.game.poes.PlayerBasePO;

import pomelo.Common.Avatar;

/**
 * 时装工具类
 * 
 * @author Yangzz
 *
 */
public class FashionUtil {

	public static List<Avatar> getAvatarData(PlayerBasePO playerBasePO) {
		List<Avatar> data = new ArrayList<>();

		// 时装单独的属性
		for (Map.Entry<Integer, String> entry : playerBasePO.fashions_equiped.entrySet()) {
			if (entry.getValue() == null) {
				continue;
			}
			String itemcode = entry.getValue();

			FashionExt fashion = GameData.Fashions.get(itemcode);
			Avatar.Builder avatar = Avatar.newBuilder();
			avatar.setEffectType(0);

			avatar.setTag(fashion.avatarTag);
			avatar.setFileName(fashion.avatarId);
			data.add(avatar.build());
		}
		return data;
	}
}
