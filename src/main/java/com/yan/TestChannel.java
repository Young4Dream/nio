package com.yan;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 一、通道，用于源节点与目标节点的连接。在JavaNIO中负责缓冲区中数据
 * 的传输。Channel本身不存储数据，因此需要配合缓冲区进行传输。
 * <p>
 * 二、主要实现类
 * java.nio.channels.Channel接口
 * |-FileChannel
 * |-SocketChannel
 * |-ServerSocketChannel
 * |-DatagramChannel
 * <p>
 * 三、获取通道
 */
public class TestChannel {
    public static void main(String[] args) throws IOException {
        copy3();
    }

    public static void copy1() throws IOException {
        FileInputStream fis = new FileInputStream("1.jpg");
        FileOutputStream fos = new FileOutputStream("2.jpg");

        //1.获取通道
        FileChannel fisChannel = fis.getChannel();
        FileChannel fosChannel = fos.getChannel();
        //2.分配指定大小的缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (fisChannel.read(buffer) != -1) {
            buffer.flip();
            fosChannel.write(buffer);
            buffer.clear();
        }
        fosChannel.close();
        fisChannel.close();
        fos.close();
        fis.close();
    }

    public static void copy2() throws IOException {
        FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("2.jpg"), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        MappedByteBuffer inMappedBuffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        MappedByteBuffer outMappedBuffer = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());
        byte[] dst = new byte[inMappedBuffer.limit()];
        inMappedBuffer.get(dst);
        outMappedBuffer.put(dst);
        inChannel.close();
        outChannel.close();
    }

    public static void copy3() throws IOException {
        FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("2.jpg"), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        inChannel.transferTo(0, inChannel.size(), outChannel);

        inChannel.close();
        outChannel.close();
    }
}
