package org.learning.basic.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.digest.DigestUtils;

public abstract class UUIDUtils {

	private static final int UUID_LENGTH = 32;
	private static final int SEGMENT_4 = 4;
	private static final int SEGMENT_8 = 8;

	private static final int IP = getIP();
	private static final int JVM = (int) (System.currentTimeMillis() >>> SEGMENT_8);
	private static short counter;

	/**
	 * ID生成器， 通过 ip jvm time counter 生成
	 * @see org.hibernate.id.UUIDHexGenerator
	 * @return 生成的UUID
	 */
	public static String random() {
		return format(IP) + format(JVM) + format(getHTime()) + format(getLTime()) + format(getCount());
	}

	/** 
	 * 根据二级制得到MD5加盟字符串作为名称
	 * @param data 二级制
	 * @return 名称
	 */
	public static String getMd5HexName(byte[] data) {
		return DigestUtils.md5Hex(data);
	}

	/** 
	 * 根据字符串得到MD5加盟字符串作为名称
	 * @param name 字符串
	 * @return 名称
	 */
	public static String getMd5HexName(String name) {
		return DigestUtils.md5Hex(name);
	}

	/** 
	 * 根据字符串得到MD5加盟字符串作为名称
	 * @param data 二级制
	 * @return digest
	 * @throws NoSuchAlgorithmException 异常信息
	 */
	public static byte[] md5Digest(byte[] data) throws NoSuchAlgorithmException {
		//首先把对象写到字节流中，然后对字节流
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(data);
		return md5.digest();
	}

	private static String format(int value) {
		String fmt = Integer.toHexString(value);
		StringBuffer text = new StringBuffer("00000000");
		text.replace(SEGMENT_8 - fmt.length(), SEGMENT_8, fmt);
		return text.toString();
	}

	private static String format(short value) {
		String fmt = Integer.toHexString(value);
		StringBuffer text = new StringBuffer("0000");
		text.replace(SEGMENT_4 - fmt.length(), SEGMENT_4, fmt);
		return text.toString();
	}

	private static short getCount() {
		synchronized (UUIDUtils.class) {
			if (counter < 0) {
				counter = 0;
			}

			return counter++;
		}
	}

	private static short getHTime() {
		return (short) (int) (System.currentTimeMillis() >>> UUID_LENGTH);
	}

	private static int getLTime() {
		return (int) System.currentTimeMillis();
	}

	private static int getIP() {
		int ip = 0;
		try {
			byte[] addr = InetAddress.getLocalHost().getAddress();

			for (int i = 0; i < SEGMENT_4; i++) {
				ip = (ip << SEGMENT_8) - -128 + addr[i];
			}

			return ip;
		} catch (UnknownHostException e) {
			return ip;
		}
	}
}
