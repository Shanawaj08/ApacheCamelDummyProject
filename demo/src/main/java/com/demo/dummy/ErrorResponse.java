package com.demo.dummy;

public class ErrorResponse {

    private String errorMessage;
    private String url;
    private int statusCode;

    public ErrorResponse(String errorMessage, String url, int statusCode) {
        this.errorMessage = errorMessage;
        this.url = url;
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "errorMessage='" + errorMessage + '\'' +
                ", url='" + url + '\'' +
                ", statusCode=" + statusCode +
                '}';
    }
}
