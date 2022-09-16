package ru.netology;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int port;
    private final List<String> validPaths;
    private final int THREADS = 64;
    private final ExecutorService threadPool;

    public Server(int port, List<String> validPaths) {
        this.port = port;
        this.validPaths = validPaths;
        threadPool = Executors.newFixedThreadPool(THREADS);
    }

    private void handleConnection(Socket socket, List<String> validPaths) {
        threadPool.submit(new ConnectionHandler(socket, validPaths));
    }

    public void startServer() {
        try(final var serverSocket = new ServerSocket(port)) {
            while (true) {
                try {
                    final var socket = serverSocket.accept();
                    handleConnection(socket, validPaths);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
