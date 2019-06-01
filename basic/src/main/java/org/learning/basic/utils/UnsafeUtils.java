package org.learning.basic.utils;

import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public abstract class UnsafeUtils {

    private static final Unsafe US = _getUnsafe();

    public static Unsafe getUnsafe() {
        return US;
    }

    private static Unsafe _getUnsafe() {
        Field field = ReflectionUtils.findField(Unsafe.class, "theUnsafe");
        Assert.notNull(field, Unsafe.class.getName() + ".theUnsafe not found");
        ReflectionUtils.makeAccessible(field);
        return (Unsafe) ReflectionUtils.getField(field, null);
    }

    public static final class BPlus {

        private Unsafe unsafe;
        private int capacity;
        private int maxsize;
        private long root;

        public void init() {
            Assert.isNull(unsafe, BPlus.class.getName() + " already initialized");
            maxsize = (capacity - Caps.METADATA) / Caps.ENTRY;
            Assert.isTrue(maxsize >= Caps.MIN_SIZE, "set capacity >= " + (Caps.METADATA + Caps.ENTRY * Caps.MIN_SIZE) + " and capacity < " + Short.MAX_VALUE);
            unsafe = UnsafeUtils.getUnsafe();
            root = allocate(Types.DATA);
        }

        public long get(long key) {
            return get(key, Defaults.NOT_FOUND);
        }

        public long get(long key, long defaultValue) {
            long address = root;
            while (true) {
                byte type = type(address);
                int size = size(address), low = 0, high = size - 1;
                while (low <= high) {
                    int mid = (low + high) >>> 1;
                    long current = key(address, mid);
                    if (current < key) {
                        low = mid + 1;
                    } else if (current > key) {
                        high = mid - 1;
                    } else {
                        while (type == Types.NODE) {
                            address = value(address, mid);
                            type = type(address);
                            mid = 0;
                        }
                        return value(address, mid);
                    }
                }
                if (type == Types.DATA) {
                    return defaultValue;
                } else if (type == Types.NODE) {
                    address = value(address, low > 0 ? --low : 0);
                } else {
                    throw new IllegalArgumentException("undefined type#" + type);
                }
            }
        }

        public long put(long key, long value) {
            // 从根节点往下找
            long address = root;
            // 经过的父节点
            long[] parents = ArrayUtils.EMPTY_LONG_ARRAY;
            while (true) {
                // 当前节点类型
                byte type = type(address);
                // 按当前节点的 size，二分法查找
                int size = size(address), low = 0, high = size - 1;
                while (low <= high) {
                    int mid = (low + high) >>> 1;
                    long current = key(address, mid);
                    if (current < key) {
                        low = mid + 1;
                    } else if (current > key) {
                        high = mid - 1;
                    } else {
                        // 命中目标
                        while (type == Types.NODE) {
                            address = value(address, mid);
                            type = type(address);
                            mid = 0;
                        }
                        return address;
                    }
                }
                // 没找到，往下层找
                if (type == Types.DATA) {
                    // 已到底层了，插入新值
                    return insert(parents, address, size, low, key, value);
                } else if (type == Types.NODE) {
                    // 记住经过的节点
                    parents = ArrayUtils.add(parents, address);
                    // 从最接近的节点再找
                    address = value(address, low > 0 ? --low : 0);
                } else {
                    throw new IllegalArgumentException("undefined type#" + type);
                }
            }
        }

        protected long insert(long[] parents, long address, int size, int i, long key, long value) {
            if (size < maxsize) { // 当前节点还没满
                // 假如是第一个，并且已经设置过值，记住当前的 key，用于重新设置父节点的指针值，否则用 MIN_VALUE 表示不需要更改
                long old = i == 0 && size > 0 ? key(address, i) : Long.MIN_VALUE;
                // 要插入的位置之后的数据往后偏移
                offset(address, size, i);
                // 设置 key
                key(address, i, key);
                // 设置 value
                value(address, i, value);
                // 设置当前节点的 size
                size(address, ++size);
                // 判断是否要更改路径上指向首元素的值
                if (old != Long.MIN_VALUE) {
                    changed(parents, i, old, key);
                }
                // 返回节点的地址
                return address;
            } else if (i == 0) { // 当前节点已经满了，并且要插入的位置是父节点的第一个
                long next = nextLinking(address);
                // 把父节点的所有数据拷贝到 next 节点
                unsafe.copyMemory(null, address + Caps.METADATA, null, next + Caps.METADATA, maxsize * Caps.ENTRY);
                // 设置 next 的 size 是 maxsize
                size(next, maxsize);
                changed(parents, key(next, 0), next);
                size(address, 0);
                insert(parents, address, 0, 0, key, value);
                mergeL(parents, next, address);
                return address;
            } else if (i == maxsize) { // 当前节点已经满了，并且要插入的位置是父节点的最后一个
                long next = nextLinking(address);
                insert(parents, next, 0, 0, key, value);
                mergeR(parents, address, next);
                return next;
            } else { // 当前节点已经满了，并且插入位置不是父节点的第一个，并且插入位置不是父节点的最后一个
                long next = nextLinking(address);
                int count = maxsize - i;
                unsafe.copyMemory(null, address + Caps.METADATA + i * Caps.ENTRY, null, next + Caps.METADATA, count * Caps.ENTRY);
                size(next, count);
                size(address, i);
                insert(parents, address, i, i, key, value);
                mergeR(parents, address, next);
                return address;
            }
        }

        /**
         * 节点里的内容往右偏移
         * @param address 当前节点的地址
         * @param size 当前节点的 size
         * @param from 从什么地方开始偏移
         */
        protected void offset(long address, int size, int from) {
            for (int i = size - 1; i >= from; i--) {
                unsafe.copyMemory(null, address + Caps.METADATA + i * Caps.ENTRY, null, address + Caps.METADATA + (i + 1) * Caps.ENTRY, Caps.ENTRY);
            }
        }

        protected void changed(long[] parents, long key, long value) {
            if (parents.length > 0) {
                long parent = parents[parents.length - 1];
                int position = keyAt(parent, key);
                value(parent, position, value);
            }
        }

        protected void mergeL(long[] parents, long address, long prev) {
            long parent = parent(parents, address);
            int size = size(parent);
            if (size == maxsize) {
                long nextToUse = nextLinking(parent);
                unsafe.copyMemory(null, parent + Caps.METADATA, null, nextToUse + Caps.METADATA, maxsize * Caps.ENTRY);
                size(nextToUse, maxsize);
                changed(parents, key(nextToUse, 0), nextToUse);
                size(parent, 0);
                long[] parentsToUse = ArrayUtils.subarray(parents, 0, parents.length - 1);
                insert(parentsToUse, parent, 0, 0, key(prev, 0), prev);
                mergeL(parentsToUse, nextToUse, parent);
            } else {
                child(parents, parent, size, prev);
            }
        }

        /**
         * 更改路径上指向首元素的值
         * @param parents 路径地址
         * @param i
         * @param oldKey 旧的 key
         * @param newKey 新的 key
         */
        protected void changed(long[] parents, int i, long oldKey, long newKey) {
            for (int j = parents.length - 1; j >= 0; j--) {
                int position = keyAt(parents[j], oldKey);
                oldKey = key(parents[j], position);
                key(parents[j], position, newKey);
            }
        }

        protected void mergeR(long[] parents, long address, long next) {
            long parent = parent(parents, address);
            int size = size(parent);
            if (size == maxsize) {
                long nextToUse = nextLinking(parent);
                long[] parentsToUse = ArrayUtils.subarray(parents, 0, parents.length - 1);
                insert(parentsToUse, nextToUse, 0, 0, key(next, 0), next);
                mergeR(parentsToUse, parent, nextToUse);
            } else {
                child(parents, parent, size, next);
            }
        }

        /**
         * 创建当前地址的下一个连表地址
         * @param address 当前地址
         * @return 创建的连表地址
         */
        protected long nextLinking(long address) {
            // 根据当前地址的节点类型申请内存块
            long next = allocate(type(address));
            // 子地址的 next 设置为父地址的 next
            next(next, next(address));
            // 父地址的 next 设置为当前创建的地址
            next(address, next);
            // 下一个连表地址
            return next;
        }

        protected int closet(long address, int size, long key) {
            int low = 0, high = size - 1;
            while (low <= high) {
                int mid = (low + high) >>> 1;
                long current = key(address, mid);
                if (current < key) {
                    low = mid + 1;
                } else if (current > key) {
                    high = mid - 1;
                } else {
                    throw new IllegalArgumentException("hit " + key + " in block#" + address + " at " + mid);
                }
            }
            return low;
        }

        protected long parent(long[] parents, long address) {
            long parent = parents.length > 0 ? parents[parents.length - 1] : 0;
            if (parents.length == 0) {
                parent = root = allocate(Types.NODE);
                insert(parents, parent, 0, 0, key(address, 0), address);
            }
            return parent;
        }

        protected void child(long[] parents, long parent, int size, long child) {
            long key = key(child, 0);
            int low = closet(parent, size, key);
            insert(ArrayUtils.subarray(parents, 0, parents.length - 1), parent, size, low, key, child);
        }

        private long allocate(byte type) {
            long address = unsafe.allocateMemory(capacity);
            type(address, type);
            size(address, Defaults.SIZE);
            next(address, Defaults.NEXT_SIBLING);
            return address;
        }

        protected long root() {
            return root;
        }

        protected void type(long address, byte type) {
            unsafe.putByte(address, type);
        }

        protected byte type(long address) {
            return unsafe.getByte(address);
        }

        /**
         * 设置节点的 size
         * @param address 节点的地址
         * @param size size 值
         */
        protected void size(long address, int size) {
            unsafe.putInt(address + Caps.TYPE, size);
        }

        protected int size(long address) {
            return unsafe.getInt(address + Caps.TYPE);
        }

        protected void next(long address, long next) {
            unsafe.putLong(address + Caps.TYPE_SIZE, next);
        }

        protected long next(long address) {
            return unsafe.getLong(address + Caps.TYPE_SIZE);
        }

        /**
         * 设置 key
         * @param address 当前节点的地址
         * @param i 节点中的第几个 key
         * @param key key 的值
         */
        protected void key(long address, int i, long key) {
            unsafe.putLong(address + Caps.METADATA + i * Caps.ENTRY, key);
        }

        protected long key(long address, int i) {
            return unsafe.getLong(address + Caps.METADATA + i * Caps.ENTRY);
        }

        protected int keyAt(long address, long key) {
            int size = size(address), low = 0, high = size - 1;
            while (low <= high) {
                int mid = (low + high) >>> 1;
                long current = key(address, mid);
                if (current < key) {
                    low = mid + 1;
                } else if (current > key) {
                    high = mid - 1;
                } else {
                    return mid;
                }
            }
            throw new IllegalArgumentException("key#" + key + " not found at " + address);
        }

        /**
         * 设置 value
         * @param address 当前节点的地址
         * @param i 节点中的第几个 value
         * @param value value 的值
         */
        protected void value(long address, int i, long value) {
            unsafe.putLong(address + Caps.METADATA + i * Caps.ENTRY + Caps.KEY, value);
        }

        protected long value(long address, int i) {
            return unsafe.getLong(address + Caps.METADATA + i * Caps.ENTRY + Caps.KEY);
        }

        protected String text(long address) {
            StringBuffer buf = new StringBuffer();
            buf.append(type(address)).append(" ");
            int size = size(address);
            for (int i = 0; i < size; i++) {
                buf.append(key(address, i)).append("=").append(value(address, i)).append(" ");
            }
            return StringUtils.removeEnd(buf.toString(), " ");
        }

        public void destroy() {
            long address = root;
            long[] levels = new long[]{address};
            byte type = type(address);
            while (type == Types.NODE) {
                address = value(address, 0);
                type = type(address);
                levels = ArrayUtils.add(levels, address);
            }
            for (int i = levels.length - 1; i >= 0; i--) {
                long[] blocks = new long[]{address = levels[i]};
                long next = next(address);
                while (next != Defaults.NEXT_SIBLING) {
                    blocks = ArrayUtils.add(blocks, next);
                    next = next(next);
                }
                for (int j = 0; j < blocks.length; j++) {
                    unsafe.freeMemory(blocks[j]);
                }
            }
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        public static final class Types {

            public static final byte NODE = 1;
            public static final byte DATA = 2;
        }

        public static final class Caps {

            public static final byte BYTE = 1;
            public static final byte TYPE = BYTE;
            public static final byte INT = 4;
            public static final byte SIZE = INT;
            public static final byte TYPE_SIZE = TYPE + SIZE;
            public static final byte LONG = 8;
            // public static final byte NEXT_SIBLING = LONG;
            public static final byte KEY = LONG;
            public static final byte VALUE = LONG;
            public static final byte METADATA = 16; // TYPE(1) + SIZE(4) + NEXT(8) = 13 -> 剩 3 个字节还没用到先预留
            public static final byte ENTRY = KEY + VALUE;
            public static final byte MIN_SIZE = 3;
        }

        public static final class Defaults {

            public static final short SIZE = 0;
            public static final long NEXT_SIBLING = 0L;
            public static final long NOT_FOUND = 0L;
        }
    }
}
