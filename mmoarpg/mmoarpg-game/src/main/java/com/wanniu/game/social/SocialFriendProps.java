package com.wanniu.game.social;

import java.util.ArrayList;
import java.util.Map;

import com.wanniu.game.data.GameData;
import com.wanniu.game.data.SocialFriendCO;

public class SocialFriendProps {

	public static SocialFriendCO findByID(int id) {
		Map<Integer, SocialFriendCO> SocialFriends = GameData.SocialFriends;
		for (Map.Entry<Integer, SocialFriendCO> node : SocialFriends.entrySet()) {
			SocialFriendCO friend = node.getValue();
			if (friend.iD == id) {
				return friend;
			}
		}
		return null;
	}

	public static SocialFriendCO findByMSocialAction(int action) {
		Map<Integer, SocialFriendCO> SocialFriends = GameData.SocialFriends;
		for (Map.Entry<Integer, SocialFriendCO> node : SocialFriends.entrySet()) {
			SocialFriendCO friend = node.getValue();
			if (friend.mSocialAction == action) {
				return friend;
			}
		}
		return null;
	}

	public static ArrayList<SocialFriendCO> find(String key, Object value) {
		ArrayList<SocialFriendCO> list = new ArrayList<>();
		Map<Integer, SocialFriendCO> SocialFriends = GameData.SocialFriends;
		for (Map.Entry<Integer, SocialFriendCO> node : SocialFriends.entrySet()) {
			SocialFriendCO friend = node.getValue();
			if (key.equals("iD")) {
				if (friend.iD == (int) value) {
					list.add(friend);
				}
			} else if (key.equals("mSocialAction")) {
				if (friend.mSocialAction == (int) value) {
					list.add(friend);
				}
			} else if (key.equals("favorNum")) {
				if (friend.favorNum == (int) value) {
					list.add(friend);
				}
			} else if (key.equals("friendshipNum")) {
				if (friend.friendshipNum == (int) value) {
					list.add(friend);
				}
			} else if (key.equals("killNum")) {
				if (friend.killNum == (int) value) {
					list.add(friend);
				}
			}
		}
		return list;
	}

}
