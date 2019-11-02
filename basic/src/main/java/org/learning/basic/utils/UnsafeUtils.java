package org.learning.basic.utils;

import sun.misc.Unsafe;

import static org.learning.basic.core.Asserts.Patterns.isNull;
import static org.learning.basic.core.Asserts.Patterns.isTrue;
import static org.learning.basic.core.Errors.Patterns.handler;

public abstract class UnsafeUtils {

    public static final Unsafe US = org.springframework.objenesis.instantiator.util.UnsafeUtils.getUnsafe();

    public static final class BPlus {

        private Unsafe unsafe;
        private int capacity;
        private int maxsize;
        private long root;

        public void init() {
            isNull(unsafe, "{0} already initialized", BPlus.class);
            maxsize = (capacity - Caps.METADATA) / Caps.ENTRY;
            isTrue(maxsize >= Caps.MIN_SIZE, "set capacity >= {0} and capacity < {1}", Caps.METADATA + Caps.ENTRY * Caps.MIN_SIZE, Short.MAX_VALUE);
            unsafe = US;
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
                    handler("undefined type#" + type);
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
                    handler("undefined type#" + type);
                }
            }
        }

        protected long insert(long[] parents, long address, int size, int i, long key, long value) {
            if (size < maxsize) { // 当前节点还没满
                // 插入当前节点
                insertC(parents, address, size, i, key, value);
                // 返回插入的节点地址
                return address;
            } else if (i == 0) { // 当前节点已经满了，并且要插入的位置是父节点的第一个
                // 左分裂插入
                insertL(parents, address, size, key, value);
                // 返回插入的节点地址
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

        protected void insertC(long[] parents, long address, int size, int i, long key, long value) {
            // 假如是第一个，并且已经设置过值，记住当前的 key，用于重新设置父节点的指针值，否则用 MIN_VALUE 表示不需要更改
            long old = i == 0 && size > 0 ? key(address, i) : Long.MIN_VALUE;
            // 在要插入的位置之后的数据往后偏移
            for (int offset = size - 1, from = i; offset >= from; offset--) {
                unsafe.copyMemory(null, address + Caps.METADATA + offset * Caps.ENTRY, null, address + Caps.METADATA + (offset + 1) * Caps.ENTRY, Caps.ENTRY);
            }
            // 设置 key
            key(address, i, key);
            // 设置 value
            value(address, i, value);
            // 设置当前节点的 size
            size(address, ++size);
            // 判断是否要更改路径上指向首元素的值
            if (old != Long.MIN_VALUE) {
                for (int offset = parents.length - 1; offset >= 0; offset--) {
                    int position = keyAt(parents[offset], old);
                    old = key(parents[offset], position);
                    key(parents[offset], position, key);
                }
            }
        }

        protected void insertL(long[] parents, long address, int size, long key, long value) {
            // 记住参数变量，使用循环递归，不使用方法递归 (方法调用深度影响性能)
            long[] parentsToUse = parents;
            // 初始化要分裂的节点
            long addressToUse = address;
            // 初始化分裂节点的 size，这里应该是 maxsize
            int sizeToUse = size;
            // 初始化 key, value，递归后是首元素的值
            long keyToUse = key, valueToUse = value;
            // 左分裂后的节点地址
            long prevToUse = addressToUse;
            // 父节点的 size 已满的情况就分裂
            while (sizeToUse == maxsize) {
                // 在 parentToUse 的左边分裂一个
                long nextToUse = nextLinking(addressToUse);
                // 迁移 parentToUse 的数据到 nextToUse
                unsafe.copyMemory(null, addressToUse + Caps.METADATA, null, nextToUse + Caps.METADATA, maxsize * Caps.ENTRY);
                // 设置 nextToUse 的 size 为 maxsize
                size(nextToUse, maxsize);
                // 更改父节点中 nextToUse 首元素的指向
                if (parentsToUse.length > 0) {
                    // 获取父节点地址
                    long parent = parentsToUse[parentsToUse.length - 1];
                    // 设置 nextToUse 在 parent 节点中的指向地址是 nextToUse
                    value(parent, keyAt(parent, key(nextToUse, 0)), nextToUse);
                }
                // 重置 parentToUse
                size(addressToUse, 0);
                // 节点路径减一个，开始向上递归判断
                parentsToUse = ArrayUtils.subarray(parentsToUse, 0, parentsToUse.length - 1);
                // 设置当前父节点的首元素为 prev
                insert(parentsToUse, addressToUse, 0, 0, keyToUse, valueToUse);
                // 替换变量，进入下一个递归
                prevToUse = addressToUse;
                // 重新获取 parentToUse
                addressToUse = parent(parentsToUse, nextToUse);
                // 设置 key, value 为当前地址的首元素
                keyToUse = key(addressToUse, 0);
                valueToUse = addressToUse;
                // 重新获取 parent 的 size，进入下一个递归的判断
                sizeToUse = size(addressToUse);
            }
            // parent 的 size 还没满的情况，在 parent 中添加 child
            child(parentsToUse, addressToUse, sizeToUse, prevToUse);
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
         *
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
                    handler("hit " + key + " in block#" + address + " at " + mid);
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

        /**
         * 在 parent 中添加 child 元素
         *
         * @param parents child 的路径
         * @param parent  在 parent 节点添加
         * @param size    parent 节点的 size
         * @param child   需要添加的 child
         */
        protected void child(long[] parents, long parent, int size, long child) {
            // 获取 child 的第一个元素
            long key = key(child, 0);
            // 获取 child 在 parent 中最接近的值
            int low = closet(parent, size, key);
            // 调用插入方法
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
         *
         * @param address 节点的地址
         * @param size    size 值
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
         *
         * @param address 当前节点的地址
         * @param i       节点中的第几个 key
         * @param key     key 的值
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
            return handler("key#" + key + " not found at " + address);
        }

        /**
         * 设置 value
         *
         * @param address 当前节点的地址
         * @param i       节点中的第几个 value
         * @param value   value 的值
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
