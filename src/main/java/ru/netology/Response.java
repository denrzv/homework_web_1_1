package ru.netology;


public class Response {
    private final String response;
    private final String contentType;
    private final long contentLength;


    public Response(String response, String contentType, long contentLength) {
        this.response = response;
        this.contentType = contentType;
        this.contentLength = contentLength;
    }

    @Override
    public String toString() {
        return "HTTP/1.1 " + response + "\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + contentLength + "\r\n" +
                "Connection: close\r\n" +
                "\r\n";
    }
}
