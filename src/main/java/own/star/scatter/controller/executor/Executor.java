package own.star.scatter.controller.executor;

public interface Executor<T, R> {
    ExeResponse<R> execute(ExeRequest<T> request);

    ExeResponse<R> asyncExecute(ExeRequest<T> request);

    ExeResponse<R> query(String requestId);

}
