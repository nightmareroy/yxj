package com.wanniu.game.util;

import java.util.ArrayList;
import java.util.List;

import com.wanniu.core.XLang;
import com.wanniu.game.GWorld;
import com.wanniu.game.data.BlackWordCO;
import com.wanniu.game.data.GameData;

/**
 * 字符检查过滤工具类
 * 
 * @author c
 */
public final class BlackWordUtil {
	private static DFAScanner scaner = null;

	public static DFAScanner getScaner() {
		if (scaner == null) {
			synchronized (BlackWordUtil.class) {
				if (scaner == null) {
					List<String> words = new ArrayList<>(GameData.BlackWords.size());
					for (BlackWordCO bw : GameData.BlackWords.values()) {
						words.add(bw.word);
					}
					scaner = new DFAScanner(words);
				}
			}
		}
		return scaner;
	}

	/**
	 * 判断一个字符串是否包含屏蔽字, 大小写不敏感
	 * 
	 * @param srcString 需要判断的字符串
	 * @returns {boolean} true:包含屏蔽字 false：不包含屏蔽字
	 */
	public static boolean isIncludeBlackString(String srcString) {
		return getScaner().findSensitiveWord(srcString);
	}

	/**
	 * 判断一个字符串是否包含非法字符
	 * 
	 * @param srcString
	 * @returns {boolean} true:包含非法字符 false：不包含非法字符
	 */
	public static boolean isIncludeSpecialChar(String srcString) {
		int unicodeStart = GWorld.__SERVER_LANG.getUnicodeStart();
		int unicodeEnd = GWorld.__SERVER_LANG.getUnicodeEnd();

		for (int i = 0; i < srcString.length(); i++) {
			char _char = srcString.charAt(i);
			int charUnicode = (int) _char;
			// TODO这个写法不知道是否正确
			if ((_char >= 'a' && _char <= 'z') || (_char >= 'A' && _char <= 'Z') || (_char >= '0' && _char <= '9') || (charUnicode >= unicodeStart && charUnicode <= unicodeEnd)) {
				continue;
			}

			// 越南可以使用空格
			if (GWorld.__SERVER_LANG == XLang.VN && _char == ' ') {
				continue;
			}

			return true;
		}
		return false;
	};

	public static String replaceBlackString(String srcString) {
		return getScaner().replaceSensitiveWord(srcString, false);
	}

	/**
	 * 不区分大小写替换屏蔽字为*,
	 * 
	 * @param srcString，需要屏蔽的源字符串 注：屏蔽字列表在加载配置的时候排序，确保先替换长的屏蔽字，再替换短的
	 *            eg:配置中同时有'中国’，'中国共产党'，则会先替换'中国共产党'
	 */
	public static String replaceBlackString(String srcString, boolean ignoreCode) {
		return getScaner().replaceSensitiveWord(srcString, ignoreCode);
	}

	public static boolean isNumberString(String str) {
		for (int i = str.length(); --i >= 0;) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}