package org.learning.basic.utils;

import org.junit.Assert;
import org.junit.Test;
import org.learning.basic.utils.UnsafeUtils.BPlus;
import org.learning.basic.utils.UnsafeUtils.BPlus.Caps;

import java.util.regex.Pattern;

public class UnsafeUtilsTest {

    @Test
    public void test1() {
        System.out.println("\n--- test1 -----------------------------------------------------------------------------");
        BPlus bplus = new BPlus();
        try {
            bplus.setCapacity(Caps.METADATA + Caps.ENTRY * 3);
            bplus.init();
            long address = bplus.put(3l, 3l);
            String text = text1(bplus, address);
            System.out.println(text);
            Assert.assertEquals("2 1 -> 3=3 ", text);
            address = bplus.put(5l, 5l);
            text = text1(bplus, address);
            System.out.println(text);
            Assert.assertEquals("2 2 -> 3=3 5=5 ", text);
            address = bplus.put(8l, 8l);
            text = text1(bplus, address);
            System.out.println(text);
            Assert.assertEquals("2 3 -> 3=3 5=5 8=8 ", text);
            address = bplus.put(10l, 10l);
            text = text1(bplus, address);
            System.out.println(text);
            Assert.assertEquals("2 1 -> 10=10 ", text);
        } finally {
            bplus.destroy();
        }
    }

    @Test
    public void test2() {
        System.out.println("\n--- test2 -----------------------------------------------------------------------------");
        BPlus bplus = new BPlus();
        try {
            bplus.setCapacity(Caps.METADATA + Caps.ENTRY * 3);
            bplus.init();
            long address = bplus.put(3l, 3l);
            String text = text1(bplus, address);
            System.out.println(text);
            Assert.assertEquals("2 1 -> 3=3 ", text);
            address = bplus.put(5l, 5l);
            text = text1(bplus, address);
            System.out.println(text);
            Assert.assertEquals("2 2 -> 3=3 5=5 ", text);
            address = bplus.put(8l, 8l);
            text = text1(bplus, address);
            System.out.println(text);
            Assert.assertEquals("2 3 -> 3=3 5=5 8=8 ", text);
            address = bplus.put(6l, 6l);
            text = text1(bplus, address);
            System.out.println(text);
            Assert.assertEquals("2 3 -> 3=3 5=5 6=6 ", text);
            text = text1(bplus, bplus.root());
            System.out.println(text);
            Assert.assertEquals(Pattern.compile("1 2 -> 3=[0-9]+ 8=[0-9]+ ").matcher(text).matches(), true);
            text = text1(bplus, bplus.value(bplus.root(), 0));
            System.out.println(text);
            Assert.assertEquals("2 3 -> 3=3 5=5 6=6 ", text);
            text = text1(bplus, bplus.value(bplus.root(), 1));
            System.out.println(text);
            Assert.assertEquals("2 1 -> 8=8 ", text);
        } finally {
            bplus.destroy();
        }
    }

    @Test
    public void test3() {
        System.out.println("\n--- test3 -----------------------------------------------------------------------------");
        BPlus bplus = new BPlus();
        try {
            bplus.setCapacity(Caps.METADATA + Caps.ENTRY * 3);
            bplus.init();
            long address = bplus.put(3l, 3l);
            String text = text1(bplus, address);
            System.out.println(text);
            Assert.assertEquals("2 1 -> 3=3 ", text);
            address = bplus.put(5l, 5l);
            text = text1(bplus, address);
            System.out.println(text);
            Assert.assertEquals("2 2 -> 3=3 5=5 ", text);
            address = bplus.put(8l, 8l);
            text = text1(bplus, address);
            System.out.println(text);
            Assert.assertEquals("2 3 -> 3=3 5=5 8=8 ", text);
            address = bplus.put(10l, 10l);
            text = text1(bplus, address);
            System.out.println(text);
            Assert.assertEquals("2 1 -> 10=10 ", text);
            address = bplus.put(12l, 12l);
            text = text1(bplus, address);
            System.out.println(text);
            Assert.assertEquals("2 2 -> 10=10 12=12 ", text);
            text = text1(bplus, bplus.root());
            System.out.println(text);
            Assert.assertEquals(Pattern.compile("1 2 -> 3=[0-9]+ 10=[0-9]+ ").matcher(text).matches(), true);
            text = text1(bplus, bplus.value(bplus.root(), 0));
            System.out.println(text);
            Assert.assertEquals("2 3 -> 3=3 5=5 8=8 ", text);
            text = text1(bplus, bplus.value(bplus.root(), 1));
            System.out.println(text);
            Assert.assertEquals("2 2 -> 10=10 12=12 ", text);
        } finally {
            bplus.destroy();
        }
    }

    @Test
    public void test4() {
        System.out.println("\n--- test4 -----------------------------------------------------------------------------");
        long now = System.currentTimeMillis();
        BPlus bplus = new BPlus();
        try {
            bplus.setCapacity(Caps.METADATA + Caps.ENTRY * 3);
            bplus.init();
            for (long i = 1; i <= 100; i++) {
                bplus.put(i, i);
            }
            long address = bplus.root();
            String text = text3(bplus, address);
            System.out.println(text);
        } finally {
            bplus.destroy();
        }
        System.out.println(System.currentTimeMillis() - now + "ms");
    }

    @Test
    public void test5() {
        System.out.println("\n--- test5 -----------------------------------------------------------------------------");
        long now = System.currentTimeMillis();
        BPlus bplus = new BPlus();
        try {
            bplus.setCapacity(Caps.METADATA + Caps.ENTRY * 3);
            bplus.init();
            for (long i = 100; i >= 1; i--) {
                bplus.put(i, i);
            }
            long address = bplus.root();
            String text = text3(bplus, address);
            System.out.println(text);
        } finally {
            bplus.destroy();
        }
        System.out.println(System.currentTimeMillis() - now + "ms");
    }

    @Test
    public void test6() {
        System.out.println("\n--- test6 -----------------------------------------------------------------------------");
        BPlus bplus = new BPlus();
        try {
            bplus.setCapacity(Caps.METADATA + Caps.ENTRY * 3);
            bplus.init();
            for (long i = 1; i <= 100; i++) {
                bplus.put(i, i);
                Assert.assertEquals(i, bplus.get(i));
            }
            long address = bplus.root();
            String text = text3(bplus, address);
            System.out.println(text);
        } finally {
            bplus.destroy();
        }
    }

    @Test
    public void test7() {
        System.out.println("\n--- test7 -----------------------------------------------------------------------------");
        BPlus bplus = new BPlus();
        try {
            bplus.setCapacity(Caps.METADATA + Caps.ENTRY * 3);
            bplus.init();
            for (long i = 100; i >= 1; i--) {
                bplus.put(i, i);
                Assert.assertEquals(i, bplus.get(i));
            }
            long address = bplus.root();
            String text = text3(bplus, address);
            System.out.println(text);
        } finally {
            bplus.destroy();
        }
    }

    private String text1(BPlus bplus, long address) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(bplus.type(address)).append(" ").append(bplus.size(address)).append(" -> ");
        int size = bplus.size(address);
        for (int i = 0; i < size; i++) {
            buffer.append(bplus.key(address, i)).append("=").append(bplus.value(address, i)).append(" ");
        }
        return buffer.toString();
    }

    private String text2(BPlus bplus, long address) {
        StringBuffer buffer = new StringBuffer();
        int size = bplus.size(address);
        for (int i = 0; i < size; i++) {
            buffer.append(bplus.key(address, i)).append("=").append(bplus.value(address, i)).append(" ");
        }
        return StringUtils.removeEnd(buffer.toString(), " ");
    }

    private String text3(BPlus bplus, long address) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(text2(bplus, address)).append("\n");
        byte type = bplus.type(address);
        while (type == BPlus.Types.NODE) {
            address = bplus.value(address, 0);
            buffer.append(text2(bplus, address));
            long next = bplus.next(address);
            while (next != BPlus.Defaults.NEXT_SIBLING) {
                buffer.append(" -> ").append(text2(bplus, next));
                next = bplus.next(next);
            }
            buffer.append("\n");
            type = bplus.type(address);
        }
        return buffer.toString();
    }
}
