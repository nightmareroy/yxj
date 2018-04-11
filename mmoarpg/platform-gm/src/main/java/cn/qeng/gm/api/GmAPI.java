package cn.qeng.gm.api;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wanniu.GServer;
import com.wanniu.tcp.protocol.Message;

import cn.qeng.gm.api.rpc.RpcManager;
import cn.qeng.gm.api.rpc.RpcResponse;
import cn.qeng.gm.api.rpc.RpcTimeoutException;
import cn.qeng.gm.core.ErrorCode;
import cn.qeng.gm.module.maintain.domain.Server;
import cn.qeng.gm.module.maintain.service.ServerService;
import io.netty.channel.Channel;

public abstract class GmAPI {
	private final static Logger logger = LogManager.getLogger(GmAPI.class);

	protected abstract short getOp();

	protected abstract String getArgs();

	protected byte[] getBytes() {
		return null;
	}

	public RpcResponse request(int serverId) {
		RpcResponse response = RpcManager.genRpcResponse();
		Channel channel = GServer.getInstance().getChannel(serverId);
		// 可能是合服了.
		if (channel == null) {
			Server server = ServerService.getServer(serverId);
			if (server != null && server.getMaster() > 0) {
				channel = GServer.getInstance().getChannel(server.getMaster());
			}
		}

		// 没有链接就是正在维护，所以返回值都要判定.
		if (channel == null) {
			response.setResult(ErrorCode.SERVER_NOT_FOUND);
			return response;
		}

		channel.writeAndFlush(new Message() {
			@Override
			protected void write() throws IOException {
				body.writeLong(response.getKey());
				body.writeShort(getOp());
				body.writeString(getArgs());
				// 2进制的协议才会有此参数.
				byte[] bytes = getBytes();
				if (bytes != null) {
					body.writeBytes(bytes);
				}
			}

			@Override
			public short getType() {
				return 0xABC;
			}
		}.getContent());
		logger.info("RPC request, {} key={}, opcode={}, args={}", this.getClass().getSimpleName(), response.getKey(), getOp(), getArgs());

		try {// 等10秒
			response.getCounter().await(100_000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {}

		// 移除
		if (RpcManager.removeRpc(response.getKey()) != null) {
			logger.info("RPC request timeout, key={}", response.getKey());
			throw new RpcTimeoutException();// 超时...
		}

		logger.info("RPC response, {} key={}, result={},status={}", this.getClass().getSimpleName(), response.getKey(), response.getResult(), response.getStatus());
		return response;
	}
}