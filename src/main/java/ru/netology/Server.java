package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
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

    public void startServer() {
        try(final var serverSocket = new ServerSocket(port)) {
            while (true) {
                try {
                    final var socket = serverSocket.accept();
                    final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    final var out = new BufferedOutputStream(socket.getOutputStream());
                    handleConnection(in, out);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleConnection(BufferedReader in, BufferedOutputStream out) {
        threadPool.submit(new ConnectionHandler(in, out, validPaths));
    }
}
