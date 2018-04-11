package com.wanniu.core.game.request;

import com.wanniu.core.GGame;
import com.wanniu.core.game.protocol.PomeloHeader;
import com.wanniu.core.tcp.protocol.NetHandler;

/**
 * 游戏客户端报文处理句柄基类
 * @author agui
 */
public abstract class GameHandler extends NetHandler {

	public static class Watcher {

		public String handlerName;

		public int upcount;
		public int downcount;
		public long upbytes;
		public long downbytes;
		public int useTime;

		public long beginTime;

		public void begin(int byteCount) {
			if (GGame.MONITOR) {
				upbytes += byteCount;
				upbytes += PomeloHeader.SIZE;
				upcount++;
				beginTime = System.currentTimeMillis();
			}
		}

		public void end(int byteCount) {
			if (GGame.MONITOR) {
				downbytes += byteCount;
				downcount++;
				useTime += (System.currentTimeMillis() - beginTime);
			}
		}

		public float avgtime() {
			if(upcount == 0) return upcount;
			return useTime / (float) upcount;
		}

		public void echo() {
			if (upcount == 0) {
//				System.out.println("\t" + handlerName + " : 未使用");
			} else {
				System.out.println(toString());
			}
		}

		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("\t").append(handlerName).append(" : { up:").append(upcount).append("/").append(upbytes).append(" , down:")
					.append(downcount).append("/").append(downbytes).append(", avg:").append(avgtime()).append("/").append(useTime).append(" }");
			return builder.toString();
		}

	}

	public final Watcher watcher = new Watcher();

}
