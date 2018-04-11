package com.wanniu.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * 
 * @author agui
 */
public final class ImageUtil {

	private static Random random = new Random();
	
	/**
	 * 向流上输出图像
	 */
	public static String outputImage(int width, int height, OutputStream os) throws IOException {

		Dimension size = new Dimension(width, height);
		
		BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
		// 获取图形上下文
		Graphics imageG2D = image.getGraphics();
		// 设定背景色
		imageG2D.setColor(new Color(255, 255, 255));
		imageG2D.fillRect(0, 0, size.width, size.height);
		// 设定字体
		imageG2D.setFont(new Font("Times New Roman", Font.PLAIN, 18));
		// 画边框
		// imageG2D.setColor(randomRGBColor(100, 50));
		// imageG2D.drawRect(0, 0, size.width-1, size.height-1);
		// 随机产生100 个干扰点
		imageG2D.setColor(randomRGBColor(160, 40));
		for (int i = 0; i < 100; i++) {
			int x = random.nextInt(size.width);
			int y = random.nextInt(size.height);
			int w = random.nextInt(4);
			int d = random.nextInt(4);
			imageG2D.fillOval(x, y, w, d);
		}

		// 取随机产生的认证码(4位数字)
		String verifyCode = "";
		for (int i = 0; i < 4; i++) {
			String digit = String.valueOf(random.nextInt(10));
			verifyCode += digit;
			// 将认证码显示到图象中
			Color fontColor = randomRGBColor(20, 110);
			imageG2D.setColor(fontColor);
			imageG2D.drawString(digit, 11 * i + 6, 16);
		}

		// 图象生效，释放资源
		imageG2D.dispose();

		// 输出图象到页面
		javax.imageio.ImageIO.write(image, "JPEG", os);
		return verifyCode;
	}

	/**
	 * 产生一个随机色彩
	 * 
	 * @param base
	 * @param step
	 * @return
	 */
	private static Color randomRGBColor(int base, int step) { // 给定范围获得随机颜色
		int r = base + random.nextInt(step);
		int g = base + random.nextInt(step);
		int b = base + random.nextInt(step);
		return new Color(r, g, b);
	}
	
}
