package com.wanniu.game.solo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.wanniu.core.game.LangService;
import com.wanniu.core.util.DateUtil;
import com.wanniu.game.chat.ChannelUtil;
import com.wanniu.game.common.msg.MessageUtil;
import com.wanniu.game.data.GameData;
import com.wanniu.game.data.SoloNewsCO;
import com.wanniu.game.data.SoloRankCO;
import com.wanniu.game.equip.NormalEquip;
import com.wanniu.game.item.NormalItem;
import com.wanniu.game.player.WNPlayer;

/**
 * 传闻消息处理类
 * 
 * @author WFY
 *
 */
public class SoloNewsHandler {
	private WNPlayer player;

	public SoloNewsHandler(WNPlayer player) {
		this.player = player;
	}

	/**
	 * 当连胜发生时
	 * 
	 * @param contWinTimes
	 */
	public void onStraightWin(int contWinTimes) {
		for (SoloNewsCO prop : GameData.SoloNewss.values()) {
			if (prop.newsType == 1 && prop.newsPar == contWinTimes) {// 类型是连杀胜利并且等于某个连杀次数
				String news = this._genWinNews(contWinTimes, prop.newsContent);
				SoloService.getInstance().addSoloNews(news);
				return;
			}
		}
	}

	/**
	 * 当连胜被终结时
	 * 
	 * @param contWinTimes
	 * @param vsPlayerName
	 */
	public void onStraightWinStopped(int contWinTimes, String vsPlayerName) {
		for (SoloNewsCO prop : GameData.SoloNewss.values()) {
			if (prop.newsType == 2 && prop.newsPar == contWinTimes) {// 类型是连杀终止并且之前的连杀达到连杀次数
				String news = this._genWinStoppedNews(contWinTimes, vsPlayerName, prop.newsContent);
				SoloService.getInstance().addSoloNews(news);
				return;
			}
		}
	}

	/**
	 * 当段位提升时
	 * 
	 * @param rankId
	 */
	public void onRankIdChanged(int rankId) {
		for (SoloNewsCO prop : GameData.SoloNewss.values()) {
			if (prop.newsType == 3 && prop.newsPar == rankId) {// 类型是段位提升并且等于某个段位
				String news = _genRankIdUpNews(rankId, prop.newsContent);
				SoloService.getInstance().addSoloNews(news);
				return;
			}
		}
	}

	/**
	 * 当领取奖励时
	 * 
	 * @param typeStr
	 * @param items
	 */
	public void onGetGift(String typeStr, List<NormalItem> items) {
		for (NormalItem item : items) {
			if (item.isEquip()) {
				NormalEquip equip = (NormalEquip) item;
				for (SoloNewsCO prop : GameData.SoloNewss.values()) {
					if (prop.newsType == 4 && equip.getQColor() >= prop.newsPar) {// 类型是段位提升并且等于某个段位
						String news = _genGetgiftNews(typeStr, equip, prop.newsContent);
						SoloService.getInstance().addSoloNews(news);
						return;
					}
				}
			}

		}
	}

	/**
	 * 返回player的聊天链接
	 * 
	 * @param player
	 * @return
	 */
	private static String getPlayerChatLink(WNPlayer player) {
		String playerLink = ChannelUtil.setPlayerInfo(player);
		String playerText = LangService.format("NAME_LINK", playerLink, player.getName());
		return playerText;
	}

	// 生成连胜传闻
	private String _genWinNews(int contWinTimes, String originalNews) {
		String news = originalNews;

		String playerText = getPlayerChatLink(this.player);
		news = news.replace("{playerName}", playerText).replace("{straightwinNum}", "" + contWinTimes);
		news = System.currentTimeMillis() + "|" + news;
		return news;
	};

	// 生成连胜终结传闻
	private String _genWinStoppedNews(int contWinTimes, String vsPlayerName, String originalNews) {
		String news = originalNews;

		String playerText = getPlayerChatLink(this.player);
		news = news.replace("{playerName}", playerText).replace("{straightwinNum}", "" + contWinTimes);
		news = news.replace("{vsPlayerName}", vsPlayerName);
		news = System.currentTimeMillis() + "|" + news;
		return news;
	};

	// 生成晋级传闻
	private String _genRankIdUpNews(int rankId, String originalNews) {
		String news = originalNews;

		String playerText = getPlayerChatLink(this.player);
		String rankText = "" + rankId;
		SoloRankCO prop = GameData.SoloRanks.get(rankId);
		if (prop != null) {
			// rankText = prop.TextColour;
			// TextColour 原配置格式 "<font color="ffffba00">{a}</font>"
			// 为兼容前端配置格式改为 "ffffba00"
			rankText = "<font color=\"" + prop.textColour + "\">{a}</font>";
			rankText = rankText.replace("{a}", prop.rankName);
		}

		news = news.replace("{playerName}", playerText).replace("{RankName}", rankText);
		news = System.currentTimeMillis() + "|" + news;
		return news;
	};

	// 生成领取礼包传闻
	private String _genGetgiftNews(String typeName, NormalItem item, String originalNews) {
		String news = originalNews;

		String playerText = getPlayerChatLink(this.player);

		String allItemText = "";

		if (item != null) {
			String itemText = LangService.getValue(MessageUtil.getColorLink(item.prop.qcolor));
			String itemLink = ChannelUtil.setItemInfo(item);
			itemText = itemText.replace("{a}", item.prop.name).replace("{b}", itemLink);

			String numText = LangService.getValue("DEFAULT");
			numText = numText.replace("{a}", "*" + item.getNum());

			if (allItemText.equals("")) {
				allItemText += itemText + numText;
			} else {
				allItemText += "," + itemText + numText;
			}
		}

		news = news.replace("{playerName}", playerText).replace("{giftType}", typeName).replace("{item}", allItemText);

		news = System.currentTimeMillis() + "|" + news;
		return news;
	};

	/**
	 * 根据给定的logicServerId获取所有传闻
	 * 
	 * @param logicServerId
	 * @return
	 */
	public static Map<String, List<String>> getAllSoloNews(int logicServerId) {
		Map<String, List<String>> newsList = new TreeMap<>();
		List<String> newsAll = SoloService.getInstance().getAllSoloNews();
		;

		for (String news : newsAll) {
			String[] str = news.split("\\|");// 可能str。length<2会有异常 currentMillies|news
			Date date = new Date(Long.parseLong(str[0]));// = new Date(Number(str[0]));
			String strDate = DateUtil.format(date, DateUtil.F_yyyyMMdd);

			String timeText = LangService.getValue("TIME_COLOUR");// strList["TIME_COLOUR"];
			timeText = timeText.replace("{a}", "[" + strDate + "]");
			timeText = timeText + str[1];

			if (newsList.containsKey(strDate)) {
				List<String> values = newsList.get(strDate);
				values.add(timeText);
			} else {
				List<String> values = new ArrayList<>();
				values.add(timeText);
				newsList.put(strDate, values);
			}
		}
		return newsList;
	};

}
