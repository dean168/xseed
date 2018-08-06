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
            long address = root;
            long[] parents = ArrayUtils.EMPTY_LONG_ARRAY;
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
                        return address;
                    }
                }
                if (type == Types.DATA) {
                    return insert(parents, address, size, low, key, value);
                } else if (type == Types.NODE) {
                    parents = ArrayUtils.add(parents, address);
                    address = value(address, low > 0 ? --low : 0);
                } else {
                    throw new IllegalArgumentException("undefined type#" + type);
                }
            }
        }

        protected long insert(long[] parents, long address, int size, int i, long key, long value) {
            if (size < maxsize) {
                long old = i == 0 && size > 0 ? key(address, i) : -1;
                offset(address, size, i);
                key(address, i, key);
                value(address, i, value);
                size(address, ++size);
                changed(parents, i, old, key);
                return address;
            } else if (i == 0) {
                long next = nextLinking(address);
                unsafe.copyMemory(null, address + Caps.METADATA, null, next + Caps.METADATA, maxsize * Caps.ENTRY);
                size(next, maxsize);
                changed(parents, key(next, 0), next);
                size(address, 0);
                insert(parents, address, 0, 0, key, value);
                mergeL(parents, next, address);
                return address;
            } else if (i == maxsize) {
                long next = nextLinking(address);
                insert(parents, next, 0, 0, key, value);
                mergeR(parents, address, next);
                return next;
            } else {
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

        protected void changed(long[] parents, int i, long oldKey, long newKey) {
            for (int j = parents.length - 1, position = i; j >= 0 && position == 0 && oldKey != -1; j--) {
                position = keyAt(parents[j], oldKey);
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

        protected long nextLinking(long address) {
            long next = allocate(type(address));
            next(next, next(address));
            next(address, next);
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
            public static final byte METADATA = 16;
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
