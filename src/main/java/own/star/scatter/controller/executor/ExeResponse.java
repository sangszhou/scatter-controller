package own.star.scatter.controller.executor;

public class ExeResponse<T> {
    String requestId;
    T body;

    public ExeResponse() {
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public ExeResponse(String requestId, T body) {
        this.requestId = requestId;
        this.body = body;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
