package cn.qeng.paycenter.api;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.wanniu.GServer;
import com.wanniu.tcp.protocol.Message;

import cn.qeng.paycenter.api.rpc.RpcManager;
import cn.qeng.paycenter.api.rpc.RpcResponse;
import cn.qeng.paycenter.api.rpc.RpcTimeoutException;
import io.netty.channel.Channel;

public abstract class GmAPI {
	private final static Logger logger = LogManager.getLogger(GmAPI.class);

	protected abstract Map<String, Object> getArgs();

	public RpcResponse request(int serverId) {
		RpcResponse response = RpcManager.genRpcResponse();
		Channel channel = GServer.getInstance().getChannel(serverId);

		// 没有链接就是正在维护，所以返回值都要判定.
		if (channel == null) {
			response.setResult(ErrorCode.SERVER_NOT_FOUND);
			return response;
		}

		channel.writeAndFlush(new Message() {
			@Override
			protected void write() throws IOException {
				body.writeLong(response.getKey());
				body.writeString(JSON.toJSONString(getArgs()));
			}

			@Override
			public short getType() {
				return 0xf5;
			}
		});

		logger.info("RPC request, {} key={}, args={}", this.getClass().getSimpleName(), response.getKey(), getArgs());

		try {// 等10秒
			response.getCounter().await(10_000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {}

		// 移除
		if (RpcManager.removeRpc(response.getKey()) != null) {
			logger.info("RPC request timeout, key={}", response.getKey());
			throw new RpcTimeoutException();// 超时...
		}

		logger.info("RPC response, {} key={}, result={}", this.getClass().getSimpleName(), response.getKey(), response.getResult());
		return response;
	}
}