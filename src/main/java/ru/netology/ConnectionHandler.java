package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class ConnectionHandler implements Runnable {
    private final BufferedReader in;
    private final BufferedOutputStream out;
    private final List<String> validPaths;

    public ConnectionHandler(BufferedReader in, BufferedOutputStream out, List<String> validPaths) {
        this.in = in;
        this.out = out;
        this.validPaths = validPaths;
    }

    @Override
    public void run() {
        while (true) {
            final String requestLine;
            try {
                requestLine = in.readLine();
                final var parts = requestLine.split(" ");

                if (parts.length != 3) {
                    continue;
                }

                final var path = parts[1];
                if (!validPaths.contains(path)) {
                    out.write((
                            "HTTP/1.1 404 Not Found\r\n" +
                                    "Content-Length: 0\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes());
                    out.flush();
                    continue;
                }

                final var filePath = Path.of(".", "public", path);
                final var mimeType = Files.probeContentType(filePath);

                if (path.equals("/classic.html")) {
                    final var template = Files.readString(filePath);
                    final var content = template.replace(
                            "{time}",
                            LocalDateTime.now().toString()
                    ).getBytes();
                    out.write((
                            "HTTP/1.1 200 OK\r\n" +
                                    "Content-Type: " + mimeType + "\r\n" +
                                    "Content-Length: " + content.length + "\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes());
                    out.write(content);
                    out.flush();
                    continue;
                }

                final var length = Files.size(filePath);
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                Files.copy(filePath, out);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
