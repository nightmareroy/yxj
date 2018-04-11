package com.wanniu.game.chat;

import com.wanniu.core.util.StringUtil;
import com.wanniu.game.GMain;
import com.wanniu.game.chat.command.GmCommand;
import com.wanniu.game.common.Utils;
import com.wanniu.game.guild.guildBoss.GuildBossService;
import com.wanniu.game.player.PlayerUtil;
import com.wanniu.game.player.WNPlayer;
import com.wanniu.game.prepaid.PrepaidService;

public class GMChatUtil {

	public static GMChatResult checkContent(WNPlayer player, String content) {
		String[] contents = content.split(" ");

		// 处理成功后，直接结束了，处理失败由原来老的流程继续处理.
		GmCommand command = GmCommandManager.find(contents);
		if (command != null) {
			return command.call(player, contents);
		}

		String startContent = contents[0];
		String operate = contents[1];
		String helpData = "@gm help \t帮助\n" + "@gm add exp num \t添加经验\n" + "@gm add gold num \t添加金币\n" + "@gm add itemCode num \t添加道具\n" + "@gm finishTask id \t结束任务\n" + "@gm finishTask 0:主线 1:支线 2:师门 3:一条龙 \t任务类型\n" + "@gm tp mapId \t传送场景\n" + "@gm addRank id\n" + "@gm addReiki num\n" + "@gm openWing \n" + "@gm addPetExp petId num\n" + "@gm prepaid MoneyNum\n" + "@gm openFunc functionID\n" + "@gm config k v\n" + "@gm guildAdd name num \n" + "@gm guildFundAdd num \t添加仙盟基金\n"
				+ "@gm guildExpAdd num \t添加仙盟声望\n" + "@gm allSkin \t获得所有坐骑皮肤\n" + "@gm give name \t把某个玩家绑定到自己身上\n";
		if (StringUtil.isNotEmpty(startContent) && startContent.toLowerCase().equals("@gm") && StringUtil.isNotEmpty(operate)) {
			// operate = operate.toLowerCase();
			if (operate.equals("help")) {
				return new GMChatResult(true, helpData);
			}
			if (operate.equals("refresh")) {
				if (contents.length < 2 || "lang".equals(contents[2])) {
				} else {
					GMain.initData();
				}
				return new GMChatResult(true, "success");
			} else if (operate.equals("add")) {
				try {
					String itemCode = contents[2];
					int num = Integer.parseInt(contents[3]);
					if (StringUtil.isNotEmpty(itemCode) && num > 0) {
						PlayerUtil.handleGMChat(player, Utils.ofMap("operate", "add", "itemCode", itemCode, "num", num));
					}
				} catch (Exception e) {
					return new GMChatResult(true, helpData);
				}
				return new GMChatResult(true, "success");
			} else if (operate.equalsIgnoreCase("addRank") || operate.equalsIgnoreCase("finishTask") || operate.equalsIgnoreCase("acceptTask") || operate.equals("finishTaskTarget") || operate.equalsIgnoreCase("discardTask") || operate.equalsIgnoreCase("newTask")) {
				try {
					String id = contents[2];
					if (StringUtil.isNotEmpty(id) || operate.equals("discardTask")) {
						PlayerUtil.handleGMChat(player, Utils.ofMap("operate", operate, "id", Integer.parseInt(id)));
					}
				} catch (Exception e) {
					return new GMChatResult(true, helpData);
				}
				return new GMChatResult(true, "success");
			} else if (operate.equals("config")) {
				try {
					String key = contents[2];
					String v = contents[3];
					if (StringUtil.isNotEmpty(key)) {
						player.setClientCustomConfig(key, v, true);
					}
				} catch (Exception e) {
					return new GMChatResult(true, helpData);
				}
				return new GMChatResult(true, "success");
			} else if (operate.equals("addReiki")) {
				try {
					String num = contents[2];
					if (StringUtil.isNotEmpty(num)) {
						int _num = Integer.parseInt(num);
						PlayerUtil.handleGMChat(player, Utils.ofMap("operate", "addReiki", "num", _num));
					}
				} catch (Exception e) {
					return new GMChatResult(true, helpData);
				}
				return new GMChatResult(true, "success");
			} else if (operate.equals("openWing")) {
				try {
					PlayerUtil.handleGMChat(player, Utils.ofMap("operate", "openWing"));
				} catch (Exception e) {
					return new GMChatResult(true, helpData);
				}
				return new GMChatResult(true, "success");
			} else if (operate.equals("addPetExp")) {
				try {
					String petId = contents[2];
					String num = contents[3];
					PlayerUtil.handleGMChat(player, Utils.ofMap("operate", "addPetExp", "petId", petId, "num", num));
				} catch (Exception e) {
					return new GMChatResult(true, helpData);
				}
				return new GMChatResult(true, "success");
			} else if (operate.equals("test")) {
				try {
					// HotUpdateService.test();
				} catch (Exception e) {
					return new GMChatResult(true, helpData);
				}
				return new GMChatResult(true, "success");
			} else if (operate.equals("prepaid")) {
				try {
					int money = Integer.parseInt(contents[2]);
					PlayerUtil.handleGMChat(player, Utils.ofMap("operate", "prepaid", "money", money));
				} catch (Exception e) {
					return new GMChatResult(true, helpData);
				}
				return new GMChatResult(true, "success");
			} else if (operate.equals("openFunc")) {
				try {
					int functionID = 0;
					if (contents.length >= 3) {
						functionID = Integer.parseInt(contents[2]);
					}
					PlayerUtil.handleGMChat(player, Utils.ofMap("operate", "openFunc", "functionID", functionID));
				} catch (Exception e) {
					return new GMChatResult(true, helpData);
				}
				return new GMChatResult(true, "success");
			} else if (operate.equals("guildAdd")) {
				try {
					String moneyName = contents[2];
					int num = StringUtil.isNotEmpty(contents[3]) ? Integer.parseInt(contents[3]) : 1;
					if (StringUtil.isNotEmpty(moneyName) && num > 0) {
						PlayerUtil.handleGMChat(player, Utils.ofMap("operate", "guildAdd", "moneyName", moneyName, "num", num));
					}
				} catch (Exception e) {
					return new GMChatResult(true, helpData);
				}
				return new GMChatResult(true, "success");
			} else if (operate.equals("tp")) {
				int mapID = 0;
				if (contents.length >= 3) {
					mapID = Integer.parseInt(contents[2]);
				}
				PlayerUtil.handleGMChat(player, Utils.ofMap("operate", "tp", "mapID", mapID));
				return new GMChatResult(true, "success");
			} else if (operate.equals("guildboss")) {
				String tp = null;
				if (contents.length >= 3) {
					tp = contents[2];
				}
				if ("begin".equals(tp)) {
					GuildBossService.getInstance().gmBegin();
				} else {
					GuildBossService.getInstance().gmEnd();
				}

				return new GMChatResult(true, "success");
			} else if (operate.equals("guildFundAdd")) {
				try {
					String num = contents[2];
					if (StringUtil.isNotEmpty(num)) {
						int _num = Integer.parseInt(num);
						PlayerUtil.handleGMChat(player, Utils.ofMap("operate", "guildFundAdd", "num", _num));
					}
				} catch (Exception e) {
					return new GMChatResult(true, helpData);
				}
				return new GMChatResult(true, "success");
			} else if (operate.equals("guildExpAdd")) {
				try {
					String num = contents[2];
					if (StringUtil.isNotEmpty(num)) {
						int _num = Integer.parseInt(num);
						PlayerUtil.handleGMChat(player, Utils.ofMap("operate", "guildExpAdd", "num", _num));
					}
				} catch (Exception e) {
					return new GMChatResult(true, helpData);
				}
				return new GMChatResult(true, "success");
			} else if (operate.equals("allSkin")) {
				try {
					player.mountManager.addAllSkin();
				} catch (Exception e) {
					return new GMChatResult(true, helpData);
				}
				return new GMChatResult(true, "success");
			} else if (operate.equals("unlock")) {
				int num = Integer.parseInt(contents[2]);
				player.getWnBag().addBagGridCount(num);
				return new GMChatResult(true, "success");
			} else if (operate.equals("paySuccess")) {
				String orderId = contents[2];
				PrepaidService.getInstance().onPaySuccess(orderId);
				return new GMChatResult(true, "success");
			} else {
				return new GMChatResult(true, helpData);
			}
		} else {
			return new GMChatResult(false, content);
		}
	};

	public static class GMChatResult {
		public boolean isGm;
		public String content;

		public GMChatResult() {

		}

		public GMChatResult(boolean isGm, String content) {
			this.isGm = isGm;
			this.content = content;
		}
	}
}
