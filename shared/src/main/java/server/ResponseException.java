package server;

import java.net.http.HttpResponse;

public class ResponseException extends Exception {
    private int statusCode;
    public ResponseException(HttpResponse<String> res) {
        super(res.body());
        statusCode = res.statusCode();
    }

    public String getCodeType() {
        if (statusCode/100 == 5){
            return "SERVER_ERROR";
        } else {
            return "CLIENT_ERROR";
        }
    }
}
