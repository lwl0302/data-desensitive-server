package com.mrray.datadesensitiveserver.entity.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RestResponseBody<T> {

    private String message;

    private String error;

    private T data;

    public RestResponseBody() {
        this.message = RestResponseMessage.SUCCESS.getMsg();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public RestResponseBody setError(String error) {
        this.message = RestResponseMessage.FAIL.getMsg();
        this.error = error;
        return this;
    }

    public T getData() {
        return data;
    }

    public RestResponseBody setData(T data) {
        this.data = data;
        return this;
    }
}