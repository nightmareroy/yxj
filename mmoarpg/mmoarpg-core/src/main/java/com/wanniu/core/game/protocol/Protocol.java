package com.wanniu.core.game.protocol;

import com.wanniu.core.tcp.BufferUtil;

import io.netty.buffer.ByteBuf;

public final class Protocol {

	public static final byte TYPE_HANDSHAKE = 1;
	public static final byte TYPE_HANDSHAKE_ACK = 2;
	public static final byte TYPE_HEARTBEAT = 3;
	public static final byte TYPE_DATA = 4;
	public static final byte TYPE_KICK = 5;

	public static final byte TYPE_REQUEST = 0;
	public static final byte TYPE_NOTIFY = 1;
	public static final byte TYPE_RESPONSE = 2;
	public static final byte TYPE_PUSH = 3;

	public static final byte MSG_FLAG_BYTES = 1;
	public static final byte MSG_ROUTE_CODE_BYTES = 2;
	public static final byte MSG_ID_MAX_BYTES = 5;
	public static final byte MSG_ROUTE_LEN_BYTES = 1;

	public static final int MSG_ROUTE_CODE_MAX = 0xffff;

	public static final byte MSG_COMPRESS_ROUTE_MASK = 0x1;
	public static final byte MSG_COMPRESS_GZIP_MASK = 0x1;
	public static final byte MSG_COMPRESS_GZIP_ENCODE_MASK = 1 << 4;
	public static final byte MSG_TYPE_MASK = 0x7;

	/**
	 * pomele client encode id message id; route message route msg message body
	 * socketio current support string
	 */
	public static ByteBuf strencode(String str) {
		ByteBuf buf = BufferUtil.getAutoBuffer(str.length() * 3);
		for (int i = 0; i < str.length(); i++) {
			byte charCode = (byte) str.charAt(i);
			byte[] codes = null;
			if (charCode <= 0x7f) {
				codes = new byte[] { charCode };
			} else if (charCode <= 0x7ff) {
				codes = new byte[] { (byte) (0xc0 | (charCode >> 6)), (byte) (0x80 | (charCode & 0x3f)) };
			} else {
				codes = new byte[] { (byte) (0xe0 | (charCode >> 12)), (byte) (0x80 | ((charCode & 0xfc0) >> 6)),
						(byte) (0x80 | (charCode & 0x3f)) };
			}
			for (int j = 0; j < codes.length; j++) {
				buf.writeByte(codes[j]);
			}
		}
		return buf;
	}

	/**
	 * client decode msg String data return Message Object
	 */
	public static String strdecode(byte[] bytes) {
		ByteBuf array = BufferUtil.getAutoBuffer(bytes.length);
		int offset = 0;
		int charCode = 0;
		int end = bytes.length;
		while (offset < end) {
			if (bytes[offset] < 128) {
				charCode = bytes[offset];
				offset += 1;
			} else if (bytes[offset] < 224) {
				charCode = ((bytes[offset] & 0x3f) << 6) + (bytes[offset + 1] & 0x3f);
				offset += 2;
			} else {
				charCode = ((bytes[offset] & 0x0f) << 12) + ((bytes[offset + 1] & 0x3f) << 6)
						+ (bytes[offset + 2] & 0x3f);
				offset += 3;
			}
			array.writeByte((byte) charCode);
		}
		byte[] b = array.array();
		return new String(b);
	}

	private static final int LEFT_SHIFT_BITS = 1 << 7;

	public static byte[] writeLength(int length, int contentLen) {
		int offset = contentLen - 1, b;
		byte[] bytes = new byte[contentLen];
		for (; offset >= 0; offset--) {
			b = length % LEFT_SHIFT_BITS;
			if (offset < contentLen - 1) {
				b |= 0x80;
			}
			bytes[offset] = (byte) b;
			length = length >> 7;
		}
		return bytes;
	}

	public static int calLengthSize(int length) {
		int res = 0;
		while (length > 0) {
			length >>= 7;
			res++;
		}
		return res;
	}

	public static int caculateMsgIdBytes(int id) {
		int len = 0;
		do {
			len += 1;
			id >>= 7;
		} while (id > 0);
		return len;
	}

	public static boolean msgHasId(byte type) {
		return type == TYPE_REQUEST || type == TYPE_RESPONSE;
	}

	public static boolean msgHasRoute(int type) {
		return type == TYPE_REQUEST || type == TYPE_NOTIFY || type == TYPE_PUSH;
	}

}
