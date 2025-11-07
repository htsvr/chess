package server;

import java.net.http.HttpResponse;

public class ResponseException extends Exception {
    private final int statusCode;
    public ResponseException(HttpResponse<String> res) {
        super(res.body());
        statusCode = res.statusCode();
    }

    public int statusCode() {
        return statusCode;
    }
}
