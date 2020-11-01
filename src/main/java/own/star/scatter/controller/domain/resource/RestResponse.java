package own.star.scatter.controller.domain.resource;

import org.springframework.web.client.RestClientResponseException;

public class RestResponse<T> {
    T body;
    int code;
    String msg;

    public RestResponse<T> withData(T body) {
        this.setBody(body);
        return this;
    }

    public RestResponse<T> withCode(int code) {
        this.setCode(code);
        return this;
    }

    public RestResponse<T> withMsg(String msg) {
        this.setMsg(msg);
        return this;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
