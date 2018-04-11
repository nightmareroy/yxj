package cn.qeng.paycenter.api.rpc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class RpcManager {
	private static final AtomicLong autoKey = new AtomicLong();
	private static final Map<Long, RpcResponse> caches = new ConcurrentHashMap<>();

	public static RpcResponse genRpcResponse() {
		RpcResponse response = new RpcResponse(autoKey.incrementAndGet());
		caches.put(response.getKey(), response);
		return response;
	}

	public static RpcResponse removeRpc(long key) {
		return caches.remove(key);
	}
}