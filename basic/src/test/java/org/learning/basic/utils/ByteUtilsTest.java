package org.learning.basic.utils;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import org.junit.Test;

public class ByteUtilsTest {

	@Test
	public void testGetInt() {
		byte[] content = new byte[1024];
		ByteBuffer buffer = ByteBuffer.wrap(content);
		buffer.order(ByteOrder.BIG_ENDIAN);
		buffer.putInt(1111);
		buffer.flip();
		assertEquals(1111, ByteUtils.getInt(content, 0));
	}

	@Test
	public void testGetLong() {
		byte[] content = new byte[1024];
		ByteBuffer buffer = ByteBuffer.wrap(content);
		buffer.order(ByteOrder.BIG_ENDIAN);
		buffer.putLong(1111);
		buffer.flip();
		assertEquals(1111, ByteUtils.getLong(content, 0));
	}

	@Test
	public void testPutByteArrayIntInt() {
		byte[] content1 = new byte[4];
		ByteBuffer buffer = ByteBuffer.wrap(content1);
		buffer.order(ByteOrder.BIG_ENDIAN);
		buffer.putInt(1112);
		buffer.flip();
		byte[] content2 = new byte[4];
		ByteUtils.put(content2, 0, 1112);
		assertEquals(Arrays.toString(content1), Arrays.toString(content2));
	}

	@Test
	public void testPutByteArrayIntLong() {
		byte[] content1 = new byte[8];
		ByteBuffer buffer = ByteBuffer.wrap(content1);
		buffer.order(ByteOrder.BIG_ENDIAN);
		buffer.putLong(1112);
		buffer.flip();
		byte[] content2 = new byte[8];
		ByteUtils.put(content2, 0, (long) 1112);
		assertEquals(Arrays.toString(content1), Arrays.toString(content2));
	}


}
