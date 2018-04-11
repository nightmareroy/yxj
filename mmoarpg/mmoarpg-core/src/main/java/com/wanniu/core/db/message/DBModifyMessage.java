package com.wanniu.core.db.message;

import java.io.IOException;

import com.wanniu.core.db.DBType;
import com.wanniu.core.db.ModifyVo;
import com.wanniu.core.tcp.protocol.RequestMessage;

/**
 * @author agui
 *
 */
public class DBModifyMessage extends RequestMessage {
	private ModifyVo vo;

	public DBModifyMessage(ModifyVo vo) {
		this.vo = vo;
	}

	@Override
	protected void write() throws IOException {
		body.writeString(vo.getModifyTR());
		body.writeString(vo.getModifyPKey());
		body.writeByte(vo.getModifyOperate());
		body.writeByte(vo.getModifyDataType());
	}

	@Override
	public short getType() {
		return DBType.UPDATE;
	}
}
