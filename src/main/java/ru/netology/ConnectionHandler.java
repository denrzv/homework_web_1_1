package ru.netology;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ConnectionHandler implements Runnable {
    private final List<String> validPaths;
    private final List<String> allowedMethods;
    private final Map<String, Handler> handlers;
    private final Socket socket;


    public ConnectionHandler(Socket socket, Server server) {
        validPaths = server.getValidPaths();
        allowedMethods = server.getAllowedMethods();
        handlers = server.getHandlers();
        this.socket = socket;
    }
    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        while (true) {
            try {
                final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final var out = new BufferedOutputStream(socket.getOutputStream());
                Request request = readRequest(in);

                System.out.println(request);

                final var handler = handlers.get(request.getMethod() + " " + request.getPath());
                if (handler == null) {
                    sendResponse(out, request.getPath(), !validPaths.contains(request.getPath()));
                } else {
                    System.out.printf("Используется кастомный Handler для пути %s и метода %s",
                            request.getPath(), request.getMethod());
                    handler.handle(request, out);
                }
                out.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    private Request readRequest(BufferedReader in) {
        Request request = new Request();
        final var body = new StringBuilder();
        var hasBody = false;
        try {
            var requestLine = in.readLine();

            var parts = requestLine.split(" ");

            if (parts.length == 3 && allowedMethods.contains(parts[0])) {
                request.addMethod(parts[0]);
                if (parts[1].contains("?")) {
                    var pathParts = parts[1].split("\\?");
                    request.addPath(pathParts[0]);
                    request.addQueryParams(pathParts[1]);
                } else {
                    request.addPath(parts[1]);
                }
            }

            while (requestLine.length() > 0) {
                request.addHeader(requestLine);
                if (requestLine.startsWith("Content-Length: ")) {
                    var index = requestLine.indexOf(':') + 1;
                    var len = requestLine.substring(index).trim();
                    if (Integer.parseInt(len) > 0) {
                        hasBody = true;
                    }
                }
                requestLine = in.readLine();
            }

            if (hasBody) {
                requestLine = in.readLine();
                while (requestLine != null && requestLine.length() > 0) {
                    body.append(requestLine);
                    request.addBody(String.valueOf(body));
                    requestLine = in.readLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return request;
    }

    private void sendResponse(BufferedOutputStream out, String path, boolean error) {
        if (error) path = "/404.html";
        try {
            final Path filePath = Path.of(".", "public", path);
            final String mimeType = Files.probeContentType(filePath);
            // special case for classic
            if (path.equals("/classic.html")) {
                final String template = Files.readString(filePath);
                final byte[] content = template.replace(
                        "{time}",
                        LocalDateTime.now().toString()
                ).getBytes();
                final var response = new Response("200 OK", mimeType, content.length);
                out.write(response.toString().getBytes());
                out.write(content);
            } else {
                final long length = Files.size(filePath);
                Response response;
                if (error) {
                    response = new Response("404", mimeType, length);
                } else {
                    response = new Response("200 OK", mimeType, length);
                }
                out.write(response.toString().getBytes());
                Files.copy(filePath, out);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
