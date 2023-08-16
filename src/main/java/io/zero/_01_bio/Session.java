package io.zero._01_bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * 客户端连接封装
 */
public class Session extends Thread {
    // 客户端连接
    private final Socket clientSocket;

    public Session(Socket socket) {
        this.clientSocket = socket;
    }

    /**
     * 读取客户端数据
     */
    @Override
    public void run() {
        System.out.printf("[%s] 连接服务器... \n", this.clientSocket.getRemoteSocketAddress().toString());
        InputStream in = null;
        byte[] buf = new byte[1024];
        try {
            // 获取连接的输入流
            in = this.clientSocket.getInputStream();
            while (true) {
                // 阻塞读取客户端数据
                // 将客户端数据读取到缓冲区
                int len = in.read(buf);
                if (len == -1){
                    // 客户端关闭
                    System.out.printf("[%s] 断开连接... \n",clientSocket.getRemoteSocketAddress().toString());
                    this.clientSocket.close();
                    return;
                }
                // 去除\n符
                String line = new String(buf, 0, len - 1);
                // 输出数据
                System.out.println(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
