package io.zero._03_netty.packets.demo1;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;

/**
 * Netty粘包与拆包解决方案一
 * 加标记,使用LineBasedFrameDecoder解码处理器，该处理器会对数据包根据`\n`或者`\r\n`标识符进行数据包读取;
 *
 * 客户端
 * @author Zero.
 * @date 2023/8/15 9:19 AM
 */
public class Client {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        ChannelFuture channelFuture = new Bootstrap()
                .group(worker)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline()
                                // 添加解码器，LineBasedFrameDecoder解码器会根据`\n`或者`\r\n`进行数据包解析,负责解决粘包与拆包
                                // 数据包内必须要有\n或者\r\n标识，如果没有解码器将等待更多数据，直到遇到换行符或达到一定的最大限制。
                                .addLast(new LineBasedFrameDecoder(10))
                                // 将解析完的ByteBuf转换为String类型
                                .addLast(new StringDecoder(CharsetUtil.UTF_8))
                                // 添加事件处理器，这里直接使用SimpleChannelInboundHandler处理器，将数据包类型设置为String即可
                                // 因为经过了StringDecoder的处理，所以这里可以直接使用String类型作为参数
                                .addLast(new SimpleChannelInboundHandler<String>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                        System.out.println("server recv data: [" + msg + "]");
                                    }
                                    @Override
                                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                        new Thread(()->{
                                            for (int i = 0; i < 100; i++) {
                                                // 发送数据包，以\n作为结尾
                                                // 必须要有\n或者\r\n标识符，否则服务端的LineBasedFrameDecoder解码器将无法解析数据包
                                                ctx.channel().writeAndFlush(Unpooled.copiedBuffer("Hello\n".getBytes()));
                                            }
                                        }).start();
                                    }
                                });
                    }
                }).connect("127.0.0.1", 9696).sync();
        channelFuture.channel().closeFuture().sync();
        worker.shutdownGracefully();
    }
}
