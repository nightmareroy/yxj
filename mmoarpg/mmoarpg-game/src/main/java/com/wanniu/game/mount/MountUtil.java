package com.wanniu.game.mount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanniu.game.common.Const;
import com.wanniu.game.common.Const.PlayerBtlData;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.ext.RideListExt;
import com.wanniu.game.data.ext.SkinListExt;
import com.wanniu.game.player.AttributeUtil;
import com.wanniu.game.poes.MountPO;

import pomelo.Common.Avatar;

/**
 * 坐骑
 * 
 * @author Yangzz
 *
 */
public class MountUtil {

	public static final List<Avatar> getCurMountAvatarInfo(MountPO mountData) {
		ArrayList<Avatar> avatarData = new ArrayList<>();
		if (mountData != null && mountData.rideFlag == Const.MOUNT_RIDING_STATE.on.getValue()) {
			int avatarPoint = 16;// MountConfig.getInstance().getMountPropByID(mountData.mountId).ridePoint;
			// int avatarPoint
			// =MountConfig.getInstance().getMountSkinPropByID(mountData.usingSkinId).ridePoint;
			if (mountData.usingSkinId > 0) {
				SkinListExt skinProp = MountConfig.getInstance().getMountSkinPropByID(mountData.usingSkinId);
				Avatar.Builder builder = Avatar.newBuilder();
				builder.setTag(avatarPoint);
				builder.setFileName(skinProp.modelFile);
				builder.setEffectType(0);
				avatarData.add(builder.build());
			}
			// else if (mountData.usingMountId > 0) {
			// Avatar.Builder builder = Avatar.newBuilder();
			// builder.setTag(avatarPoint);
			//// RideListExt mountProp =
			// MountConfig.getInstance().getMountPropByID(mountData.usingMountId);
			//// builder.setFileName(mountProp.modelFile);
			// builder.setEffectType(0);
			// avatarData.add(builder.build());
			// }
		}
		return avatarData;
	}

	public static final Map<PlayerBtlData, Integer> getMountBaseProp(int rideLevel, int starLv) {
		Map<PlayerBtlData, Integer> baseMap = new HashMap<>();
		RideListExt prop = GameData.RideLists.get(rideLevel);
		if (prop == null)
			return baseMap;

		AttributeUtil.addData2AllData(prop.levelAttrs, baseMap);
		Map<PlayerBtlData, Integer> map = prop.starAttrs;
		Map<PlayerBtlData, Integer> map_star = new HashMap<>();
		for (PlayerBtlData pbd : map.keySet()) {
			int value = map.get(pbd);
			value = value * starLv;
			value += prop.totalPreStarAttrs.get(pbd);
			map_star.put(pbd, value);
		}
		AttributeUtil.addData2AllData(map_star, baseMap);
		return baseMap;
	}
}
