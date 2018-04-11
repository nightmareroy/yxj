/**
 * 
 */
package cn.qeng.gm.module.game.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import cn.qeng.common.gm.po.CdkCode;

/**
 * CDK生成器
 */
public class CdkGenerator {
	private static Random random = new Random();
	private static String candidate = "ABCDEFGHJKMNPQRSTUVWXYZ23456789";

	private static String generateCode(int minSize, int maxSize) {
		int size = random.nextInt(maxSize + 1 - minSize) + minSize;
		String result = "";
		for (int i = 0; i < size; i++) {
			result += candidate.charAt(random.nextInt(candidate.length()));
		}
		return result;
	}

	public static List<CdkCode> generateDistinctCode(String prefix, int number, int minSize, int maxSize) {
		prefix += "X";
		minSize -= 1;
		maxSize -= 1;

		Set<String> exist = new HashSet<>();
		List<CdkCode> list = new ArrayList<>();
		for (int i = 0; i < number; i++) {
			String code = prefix + generateCode(minSize, maxSize);
			while (exist.contains(code)) {
				code = prefix + generateCode(minSize, maxSize);
			}
			exist.add(code);
			list.add(new CdkCode(code, 0));
		}
		return list;
	}
}
