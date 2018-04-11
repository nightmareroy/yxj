package com.wanniu.core.util;

/**
 * <p>
 * Title: 类
 * </p>
 */

public class NumberUtil {
	
	/**
	 * 判断某个给定的传入参数是否在某个区间(指定范围)
	 * 
	 * @param number
	 *            给定的传入参数
	 * @param area
	 *            格式如："100>A>=85;85>B>=76;76>C>=70;70>D>=60;60>E>=0;"<br>
	 *            (其中字符(区间值)、数值(区间参数)可以在合理范围任意设定，<br>
	 *            '>'、'>='和';'固定格式<br>
	 *            最后一个区间数值无需给定，需满足<倒数第二个数值<br>
	 *            其中要求给定最大值，和最小值)
	 * @return 区间别名
	 * @throws NumberFormatException 数值类型的格式转换错误
	 */
	public static String inAssignArea(double number, String area) throws NumberFormatException {
		String[] newArea = area.split(";");			// 区间字符串组合成数组
		int index = newArea.length;					// 区间范围大小(自动获取)
		String[] name = new String[index];		// 区间别名(任意类型)
		double[] hight = new double[index];	// 区间最大值(数值型)
		double[] low = new double[index];		// 区间最小值(数值型)
		// 将区间字符串数组转化为特定数组
		// 包括：较大值、别名、较小值
		for (int i = 0; i < index; i++) {
			int fromIndex = 0;						// 截取字符串的开始索引
			int toIndex = newArea[i].indexOf(">");	// 截取字符串的结束索引
			// 获取区间较大值
			hight[i] = Double.parseDouble(newArea[i].substring(fromIndex, toIndex));
			
			fromIndex = ++toIndex;
			toIndex = newArea[i].lastIndexOf(">");
			// 获取区间别名
			name[i] = newArea[i].substring(fromIndex, toIndex);
			
			fromIndex = newArea[i].indexOf("=", fromIndex)+1;
			// 获取区间较小值
			low[i] = Double.parseDouble(newArea[i].substring(fromIndex));
		}
		
		// 根据给定参数比较后返回区间别名
		for(int i=0; i<index; i++) {
			if(number >= low[i] && (i==0
					? number <= hight[i]
					: number < hight[i])){
				return name[i];
			}
		}
		
		// throw new RuntimeException("您要求数字不在范围内");
		return "无";
	}
	
	/**
	 * 判断某个给定的传入参数是否在某个区间
	 * 
	 * @param number
	 *            给定的传入参数
	 * @param area
	 *            格式如："A>85;B>76;C>70;D>60;E<60;"<br>
	 *            (其中字符(区间值)、数值(区间参数)可以在合理范围任意设定，'>'和';'固定格式<br>
	 *             最后一个数值无需给定，需满足<倒数第二个数值)
	 * @return 区间值
	 */
	public static String inArea(double number, String area) throws NumberFormatException {
		String[] newArea = area.split(";");	// 区间字符串组合成数组
		int index = newArea.length;			// 区间范围大小(自动获取)
		String[] name = new String[index];	// 区间别名(任意类型)
		double[] num = new double[index];	// 区间最大值(数值型)
		// 将区间字符串数组转化为特定数组
		// 包括：较大值、别名
		for (int i = 0; i < index; i++) {
			int fromIndex = 0;						// 截取字符串的开始索引
			// 是否是数组的最后一个数值(如果是以 < 截取字符串，否则以 > 截取字符串)
			boolean flag = (i == index - 1);
			// 截取字符串的结束索引
			int toIndex = flag 
						? newArea[i].indexOf("<", fromIndex) 
						: newArea[i].indexOf(">", fromIndex);
			// 获取区间别名
			name[i] = newArea[i].substring(fromIndex, toIndex);
			fromIndex = ++toIndex;
			// 获取区间数值
			num[i] = Double.parseDouble(newArea[i].substring(fromIndex));
		}
		
		// 数组中的最后一位保留，用于保存最后一个特殊区间(其余以 >=或<= 传入参数做判断)
		for (int i = 0; i < index - 1; i++) {
			// 区间参数为降序只需判断传入参数是否<=循环参数即可
			// 如果区间参数是升序排列，则需要判断传入参数是否>=最大的循环参数(保留)
			if (num[i] <= number) {
				return name[i];
			}
		}
		// 判断数字是否在数组中的最后一个区间(以 < 传入参数做判断)
		if (number < num[index - 1]) {
			return name[index - 1];
		}
		// throw new RuntimeException("您要求数字不在范围内");
		return "无";
	}
	
	public static Integer createInteger(String str) {
		return Integer.decode(str);
	}
	
}
