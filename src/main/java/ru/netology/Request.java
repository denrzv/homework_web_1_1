package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Request {
    private String method;
    private String path;
    private final List<String> headers;
    private String body;
    private List<NameValuePair> queries;

    public Request() {
        headers = new ArrayList<>();
        queries = new ArrayList<>();
    }

    public Optional<NameValuePair> getQueryParam(String name) {
        int index = queries.indexOf(name);
        if (index > -1) {
            return Optional.of(queries.get(index));
        } else {
            return Optional.empty();
        }
    }

    public List<NameValuePair> getQueryParams() {
        return Collections.unmodifiableList(queries);
    }

    public String getMethod() {
        return method;
    }

    public Request addMethod(String method) {
        this.method = method;
        return this;
    }

    public void addHeader(String header) {
        headers.add(header);
    }

    public String getPath() {
        return path;
    }

    public Request addPath(String path) {
        this.path = path;
        return this;
    }

    @Override
    public String toString() {
        return "Request{" + "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", queries='" + queries + '\'' +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                '}';
    }

    public List<String> getHeaders() {
        return Collections.unmodifiableList(headers);
    }

    public String getBody() {
        return body;
    }

    public Request addBody(String body) {
        this.body = body;
        return this;
    }

    public Request addQueryParams(String queries) {
        this.queries = URLEncodedUtils.parse(queries, StandardCharsets.UTF_8);
        return this;
    }
}
