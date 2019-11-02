package org.learning.basic.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.learning.basic.core.Errors.Patterns.handler;

public abstract class ByteUtils {

    public static final String CHARSET_NAME = "UTF-8";

    /**
     * 使用 BIG_ENDIAN
     *
     * @param content
     * @return
     * @see java.nio.Bits#getIntB()
     * @see ByteOrder#BIG_ENDIAN
     */
    public static int ints(byte[] content) {
        return ints(content, 0);
    }

    /**
     * 使用 BIG_ENDIAN
     *
     * @param content
     * @param offset
     * @return
     * @see java.nio.Bits#getIntB()
     * @see ByteOrder#BIG_ENDIAN
     */
    public static int ints(byte[] content, int offset) {
        return ((content[offset + 0] & 0xff) << 24)
                | ((content[offset + 1] & 0xff) << 16)
                | ((content[offset + 2] & 0xff) << 8)
                | ((content[offset + 3] & 0xff) << 0);
    }

    /**
     * 使用 BIG_ENDIAN
     *
     * @param content
     * @return
     * @see java.nio.Bits#getLongB()
     * @see ByteOrder#BIG_ENDIAN
     */
    public static long longs(byte[] content) {
        return longs(content, 0);
    }

    /**
     * 使用 BIG_ENDIAN
     *
     * @param content
     * @param offset
     * @return
     * @see java.nio.Bits#getLongB()
     * @see ByteOrder#BIG_ENDIAN
     */
    public static long longs(byte[] content, int offset) {
        return (((long) content[offset + 0] & 0xff) << 56) |
                (((long) content[offset + 1] & 0xff) << 48) |
                (((long) content[offset + 2] & 0xff) << 40) |
                (((long) content[offset + 3] & 0xff) << 32) |
                (((long) content[offset + 4] & 0xff) << 24) |
                (((long) content[offset + 5] & 0xff) << 16) |
                (((long) content[offset + 6] & 0xff) << 8) |
                (((long) content[offset + 7] & 0xff) << 0);
    }

    public static byte[] bytes(String content) {
        if (StringUtils.isEmpty(content)) {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
        try {
            return content.getBytes(CHARSET_NAME);
        } catch (UnsupportedEncodingException e) {
            return handler(null, e);
        }
    }

    /**
     * 使用 BIG_ENDIAN
     *
     * @param content
     * @param offset
     * @param value
     * @see java.nio.Bits#putIntB()
     * @see ByteOrder#BIG_ENDIAN
     */
    public static void put(byte[] content, int offset, int value) {
        content[offset + 0] = (byte) (value >> 24);
        content[offset + 1] = (byte) (value >> 16);
        content[offset + 2] = (byte) (value >> 8);
        content[offset + 3] = (byte) (value >> 0);
    }

    /**
     * 使用 BIG_ENDIAN
     *
     * @param content
     * @param offset
     * @param value
     * @see java.nio.Bits#putIntB()
     * @see ByteOrder#BIG_ENDIAN
     */
    public static void put(byte[] content, int offset, long value) {
        content[offset + 0] = (byte) (value >> 56);
        content[offset + 1] = (byte) (value >> 48);
        content[offset + 2] = (byte) (value >> 40);
        content[offset + 3] = (byte) (value >> 32);
        content[offset + 4] = (byte) (value >> 24);
        content[offset + 5] = (byte) (value >> 16);
        content[offset + 6] = (byte) (value >> 8);
        content[offset + 7] = (byte) (value >> 0);
    }

    public static InetSocketAddress socketAddress(ByteBuffer buffer) {
        return new InetSocketAddress(inet4Address(buffer), buffer.getInt());
    }

    public static Inet4Address inet4Address(ByteBuffer buffer) {
        byte[] address = new byte[4];
        buffer.get(address);
        return (Inet4Address) inetAddress(address);
    }

    public static InetAddress inetAddress(byte[] address) {
        try {
            return InetAddress.getByAddress(address);
        } catch (UnknownHostException e) {
            return handler(null, e);
        }
    }

    public static String strings(byte[] content) {
        return strings(content, 0, ArrayUtils.getLength(content));
    }

    public static String strings(byte[] content, int offset, int length) {
        try {
            return new String(content, offset, length, CHARSET_NAME);
        } catch (UnsupportedEncodingException e) {
            return handler(null, e);
        }
    }
}
