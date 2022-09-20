package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {
  public static void main(String[] args) {
    final var validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html",
            "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");

    final var server = new Server(9999, validPaths);


    server.addHandler("GET", "/messages", (request, responseStream) -> {
      final var text = "<h1>GET /messages</h1>\n" +
              "Headers: " + request.getHeaders();
      sendResponse(text, responseStream);
    });
    server.addHandler("POST", "/messages", (request, responseStream) -> {
      final var text = "<h1>POST /messages</h1>\n" +
              "Headers: " + request.getHeaders() + "\n" +
              "Body: " + request.getBody();
      sendResponse(text, responseStream);
    });

    server.startServer();
  }

  private static void sendResponse(String content, BufferedOutputStream out) {
    final var response = "HTTP/1.1 200 OK\r\n" +
            "Content-Type: text/html\r\n" +
            "Content-Length: " + content.length() + "\r\n" +
            "Connection: close\r\n" +
            "\r\n";
    try {
      out.write(response.getBytes());
      out.write(content.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}


