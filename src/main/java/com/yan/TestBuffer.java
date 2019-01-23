package com.yan;

import java.nio.ByteBuffer;

public class TestBuffer {
    private static final ByteBuffer BUFFER = ByteBuffer.allocate(10);
    public static void main(String[] args) {
        String str = "abcde";
        print("allocate");
        // 1.放入数据
        BUFFER.put(str.getBytes());
        print("put");
        // 2.切换读写，limit缩小至有数据的最大位置
        BUFFER.flip();
        print("flip");
        // 读取
        byte[] bytes = new byte[BUFFER.limit()];
        BUFFER.get(bytes);
        print("get");
        // 重置位置，可以重复读数据
        BUFFER.rewind();
        print("rewind");
        // 清空缓冲区，但数据保留
        BUFFER.clear();
        print("clear");
        // 在当前位置做个标记
        BUFFER.get();
        BUFFER.mark();
        print("mark");
        // 将位置重置为以前标记的位置，此方法不会更改或丢弃以前的标记
        BUFFER.position(3);
        print("position");
        BUFFER.reset();
        print("reset");
    }

    private static void print( String op) {
        System.out.println("-----------" + op + "()-----------");
        System.out.println("position:"+BUFFER.position());
        System.out.println("limit:"+BUFFER.limit());
        System.out.println("capacity:"+BUFFER.capacity());
    }
}
