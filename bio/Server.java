package bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

public class Server {
    public static void main(String[] args) {
        AtomicReference<ServerSocket> serverSocketRef = new AtomicReference<>();
        try {
            ServerSocket serverSocket = new ServerSocket(12345);
            serverSocketRef.set(serverSocket);
            System.out.println("Server is running on port 12345");

            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    ServerSocket socket = serverSocketRef.get();
                    if (socket != null && !socket.isClosed()) {
                        System.out.println("Shutting down server...");
                        socket.close();
                        System.out.println("Server has been closed");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));

            new Thread(() -> {
                // Main server loop
                while (true) {
                    try {
                        Socket socket = serverSocket.accept();
                        new Thread(() -> {
                            try {
                                int len;
                                byte[] buffer = new byte[1024];
                                InputStream inputStream = socket.getInputStream();
                                while ((len = inputStream.read(buffer)) != -1) {
                                    System.out.println(new String(buffer, 0, len));
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}