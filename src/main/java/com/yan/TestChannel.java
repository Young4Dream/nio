package com.yan;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.stream.IntStream;

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
 * 1. Java针对支持通道的类提供了getChannel()方法
 * 本地IO：
 * FileInputStream/FileOutputStream
 * RandomAccessFile
 * <p>
 * 网络IO：
 * Socket
 * ServerSocket
 * DatagramSocket
 * 2. FileChannel.open()
 * 3. Files.newByteChannel()
 * <p>
 * 四、通道之间数据传输
 * transferFrom
 * transferTo
 * <p>
 * 五、分散（Scatter）读取与聚集（Gather）写入{@link #scatterAndGather()}
 * <p>
 * 六、字符集：Charset{@link #scatterAndGather()}
 * 编码：字符串 -> 字节数组
 * 解码：字节数组 -> 字符串
 */
public class TestChannel {
    public static void main(String[] args) throws IOException {
        testCharset();
    }

    public static void testCharset() throws CharacterCodingException {
        Charset gbk = Charset.forName("GBK");
        // 获取编码器
        CharsetEncoder charsetEncoder = gbk.newEncoder();
        //获取解码器
        CharsetDecoder charsetDecoder = gbk.newDecoder();
        CharBuffer charBuffer = CharBuffer.allocate(1024);
        charBuffer.put("已知乾坤大，");
        charBuffer.put("犹怜草木青。");
        charBuffer.flip();

        ByteBuffer byteBuffer = charsetEncoder.encode(charBuffer);
        IntStream.range(0, byteBuffer.limit()).forEach(i -> System.out.println(byteBuffer.get()));

        byteBuffer.flip();
        CharBuffer decode = charsetDecoder.decode(byteBuffer);
        IntStream.range(0, decode.limit()).forEach(i -> System.out.println(decode.get()));

    }

    public static void scatterAndGather() throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile("1.txt", "rw");
        //1.获取通道
        FileChannel accessFileChannel = randomAccessFile.getChannel();
        //2.分配制定大小的缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        ByteBuffer buffer2 = ByteBuffer.allocate(1024);

        //3.分散读取
        ByteBuffer[] bufs = {buffer, buffer2};
        accessFileChannel.read(bufs);

        Arrays.stream(bufs).forEach(ByteBuffer::flip);

        //4.聚集写入
        RandomAccessFile randomAccessFile1 = new RandomAccessFile("2.txt", "rw");
        FileChannel channel = randomAccessFile1.getChannel();

        channel.write(bufs);

        accessFileChannel.close();
        channel.close();
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
