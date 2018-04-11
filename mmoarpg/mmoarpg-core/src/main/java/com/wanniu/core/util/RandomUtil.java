package com.wanniu.core.util;

import java.util.Random;

/**
 * 
 * @author agui
 */
public final class RandomUtil {
	/**
	 * 随机数生成器
	 */
	private static final Random __RANDOM__ = new Random();

	/**
	 * 获取列表的随机索引或数组的随机下标
	 */
	public static int getIndex(int size) {
		if (size <= 1)
			return 0;
		return __RANDOM__.nextInt(size);
	}

	/**
	 * 获取从1开始到范围上限内的随机整数
	 */
	public static int getInt(int limit) {
		if (limit <= 1)
			return 1;
		return __RANDOM__.nextInt(limit) + 1;
	}

	/**
	 * 获取范围内的随机整数（包含上、下限数值）
	 */
	public static int getInt(int lower, int limit) {
		if (limit <= lower) {
			return lower;
		}
		return __RANDOM__.nextInt(limit - lower + 1) + lower;
	}

	/**
	 * 获取随机小数
	 */
	public static float getFloat() {
		return __RANDOM__.nextFloat();
	}

	/**
	 * 获取随机数(0 - max)
	 */
	public static int random(int max) {
		return __RANDOM__.nextInt(max);
	}

	/**
	 * 获取随机数(0.0 - 0.9)
	 * 
	 * @return
	 */
	public static double randomDouble() {
		return __RANDOM__.nextDouble();
	}

	/**
	 * 随机选择，返回选中从0开始的序号
	 */
	public static int[] select(int srcCount, int dstCount) {
		if (dstCount > srcCount) {
			dstCount = srcCount;
		}
		int[] result = new int[dstCount];
		if (dstCount < srcCount) {
			// 如果选择数量小于选择范围
			int[] source = new int[srcCount];
			for (int i = 0; i < source.length; i++) {
				source[i] = i;
			}
			int index;
			for (int i = 0; i < dstCount; i++, srcCount--) {
				index = __RANDOM__.nextInt(srcCount);
				result[i] = source[index];
				source[index] = source[srcCount - 1];
			}
		} else {
			// 如果选择数量=选择范围，全部选出
			for (int i = 0; i < result.length; i++) {
				result[i] = i;
			}
		}
		return result;
	}

	public static int randomValue(int range) {
		if (range <= 0) {
			return 0;
		}
		return __RANDOM__.nextInt(range);
	}

	public static boolean hasHitRate(int range, int rate) {
		int cos = randomValue(range);
		if (cos < rate) {
			return true;
		}
		return false;
	}

	/**
	 * 根据概率不同随机选中某个下标
	 * 
	 * @param weights 每个下标选中概率数组
	 * @return 返回选中的下标
	 */
	public static int hit(int[] weights) {
		int weight = 0;
		// 将各下标几率转化为和
		for (int i = 0; i < weights.length; i++) {
			weight += weights[i];
			weights[i] = weight;
		}
		int random = random(weight);
		// 查找选中区间
		for (int index = 0; index < weights.length; index++) {
			if (random < weights[index]) {
				return index;
			}
		}
		return 0;// 默认选中第一个,虽然不会执行到这
	}

}
