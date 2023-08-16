package io.zero._01_bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * BIO 回顾
 * bio 简称 blocking io 也就是传统的同步阻塞IO
 * <p>
 *
 * 基于bio实现服务端与客户端心跳机制
 * @author Zero.
 * @date 2023/8/11 10:13 AM
 */
public class Server {
    private final int port;
    public Server(int port){
        this.port = port;
    }

    /**
     * 服务运行
     */
    public void run() throws IOException {
        // 创建一个BIO网络服务
        ServerSocket serverSocket = new ServerSocket(this.port);
        System.out.println("服务器启动...");
        // 循环处理连接
        while (true){
            // 阻塞等待连接...
            Socket socket = serverSocket.accept();
            // 开启一个线程，处理连接
            new Session(socket).start();
        }
    }
    public static void main(String[] args) throws IOException {
        new Server(8090).run();
    }
}
