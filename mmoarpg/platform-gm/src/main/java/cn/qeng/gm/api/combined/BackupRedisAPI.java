package cn.qeng.gm.api.combined;

import com.alibaba.fastjson.JSON;

import cn.qeng.common.gm.RpcOpcode;
import cn.qeng.gm.api.GmAPI;

/**
 * 备份Redis.
 *
 * @author 小流氓(176543888@qq.com)
 */
public class BackupRedisAPI extends GmAPI {

	public BackupRedisAPI() {}

	@Override
	protected short getOp() {
		return RpcOpcode.OPCODE_BACKUP_REDIS;
	}

	@Override
	protected String getArgs() {
		return JSON.toJSONString(new Object[] {});
	}
}