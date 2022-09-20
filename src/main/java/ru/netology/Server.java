package ru.netology;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int port;
    private final List<String> validPaths;
    private final ExecutorService threadPool;
    private final Map<String, Handler> handlers;
    private final List<String> allowedMethods;

    public Server(int port, List<String> validPaths) {
        this.port = port;
        this.validPaths = validPaths;
        final int THREADS = 64;
        threadPool = Executors.newFixedThreadPool(THREADS);
        handlers = new ConcurrentHashMap<>();
        allowedMethods = List.of("GET", "POST");
    }

    public void addHandler(String method, String path, Handler handler) {
        handlers.put(method + " " + path, handler);
    }

    public List<String> getAllowedMethods() {
        return allowedMethods;
    }

    public List<String> getValidPaths() {
        return Collections.unmodifiableList(validPaths);
    }

    public Map<String, Handler> getHandlers() {
        return handlers;
    }

    private void handleConnection(Socket socket, Server server) {
        threadPool.submit(new ConnectionHandler(socket, server));
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void startServer() {
        try(final var serverSocket = new ServerSocket(port)) {
            while (true) {
                try {
                    final var socket = serverSocket.accept();
                    handleConnection(socket, this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
