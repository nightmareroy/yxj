package cn.qeng.gm.core.dfa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 敏感词扫描器.
 *
 * @author 小流氓(zhoumingkai@qeng.cn)
 */
public final class DFAScanner {
	// 全角对应于ASCII表的可见字符从！开始，偏移值为65281
	private static final char SBC_CHAR_START = 65281; // 全角！
	// 全角对应于ASCII表的可见字符到～结束，偏移值为65374
	private static final char SBC_CHAR_END = 65374; // 全角～
	// ASCII表中除空格外的可见字符与对应的全角字符的相对偏移
	private static final int CONVERT_STEP = 65248; // 全角半角转换间隔
	// 全角空格的值，它没有遵从与ASCII的相对偏移，必须单独处理
	private static final char SBC_SPACE = 12288; // 全角空格 12288
	// 半角空格的值，在ASCII中为32(Decimal)
	private static final char DBC_SPACE = ' '; // 半角空格
	// 分隔符号
	private final Set<Integer> separatesSymbols = new HashSet<>();
	private final Map<Integer, DFANode> nodes = new HashMap<>(); // 存储节点

	/**
	 * 构建一个敏感词扫描器.
	 * 
	 * <pre>
	 * 默认的分隔停顿符：`~!1@2#3$4%5^6&amp;7*8(9)0_-+={[}]|\\:;\&quot;'&lt;,&gt;.?/！￥%……｛｝【】
	 * </pre>
	 * 
	 * @param sensitivewords 敏感词库
	 */
	public DFAScanner(List<String> sensitivewords) {
		this(" `~!1@2#3$4%5^6&7*8(9)0_-+={[}]|\\:;\"'<,>.?/！￥%……｛｝【】", sensitivewords);
	}

	/**
	 * 构建一个敏感词扫描器.
	 * 
	 * @param symbols 分隔停顿符
	 * @param sensitivewords 敏感词库
	 */
	public DFAScanner(String symbols, List<String> sensitivewords) {
		this.initSeparatesSymbol(symbols);
		this.initSensitiveWords(sensitivewords);
	}

	// 初始化敏感词库树
	private void initSensitiveWords(List<String> sensitivewords) {
		if (!sensitivewords.isEmpty()) {
			char[] chs;
			int fchar;
			int lastIndex;
			DFANode fnode; // 首字母节点

			for (String curr : sensitivewords) {
				chs = curr.toCharArray();
				fchar = charConvert(chs[0]);
				if (!nodes.containsKey(fchar)) {// 没有首字定义
					fnode = new DFANode(fchar, chs.length == 1);
					nodes.put(fchar, fnode);
				} else {
					fnode = nodes.get(fchar);
					if (!fnode.isLast() && chs.length == 1)
						fnode.setLast(true);
				}
				lastIndex = chs.length - 1;
				for (int i = 1; i < chs.length; i++) {
					fnode = fnode.addIfNoExist(charConvert(chs[i]), i == lastIndex);
				}
			}
		}
	}

	// 初始化分隔停顿集合
	private void initSeparatesSymbol(String symbols) {
		for (int i = 0, len = symbols.length(); i < len; i++) {
			this.separatesSymbols.add(charConvert(symbols.charAt(i)));
		}
	}

	/**
	 * 全角转换半角
	 */
	private int qj2bj(char src) {
		int r = src;
		if (src >= SBC_CHAR_START && src <= SBC_CHAR_END) { // 如果位于全角！到全角～区间内
			r = src - CONVERT_STEP;
		} else if (src == SBC_SPACE) { // 如果是全角空格
			r = DBC_SPACE;
		}
		return r;
	}

	/**
	 * 大写转化为小写 全角转化为半角
	 */
	private int charConvert(char src) {
		int r = qj2bj(src);
		return (r >= 'A' && r <= 'Z') ? r + 32 : r;
	}

	/**
	 * 查找一个文本中是否包含了敏感字.
	 * 
	 * @param text 文本
	 * @return 敏感字
	 */
	public boolean findSensitiveWord(String text) {
		char[] chs = text.toCharArray();
		for (int i = 0, length = chs.length; i < length; i++) {
			int currc = charConvert(chs[i]);// 当前检查的字符

			DFANode node = nodes.get(currc);
			if (node == null) {
				continue;
			}

			boolean couldMark = false;
			if (node.isLast()) {// 单字匹配（日）
				couldMark = true;
			}

			for (int k = i; ++k < length;) {
				int temp = charConvert(chs[k]);

				// 停顿词忽略掉...
				if (separatesSymbols.contains(temp)) {
					continue;
				}

				node = node.querySub(temp);
				if (node == null) {// 没有了
					break;
				}

				// 最小的一个匹配...
				if (node.isLast()) {
					couldMark = true;
					break;
				}
			}
			if (couldMark) {
				return true;
			}
		}
		return false;
	}
}