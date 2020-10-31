package own.star.scatter.controller.executor;

public class ExeRequest<T> {
    String requestId;
    T body;

    public ExeRequest() {
    }

    public ExeRequest(String requestId, T body) {
        this.requestId = requestId;
        this.body = body;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
