package own.star.scatter.controller.executor.impl;

import org.springframework.stereotype.Service;
import own.star.scatter.controller.domain.bean.Host;
import own.star.scatter.controller.executor.ExeRequest;
import own.star.scatter.controller.executor.ExeResponse;
import own.star.scatter.controller.executor.Executor;
import own.star.scatter.controller.executor.ExecutorConstants;

@Service
public class HostExecutorInMemSync implements Executor<Host, String> {
    @Override
    public ExeResponse<String> execute(ExeRequest<Host> request) {

        return new ExeResponse<String>(request.getRequestId(), ExecutorConstants.SUCCESS);
    }

    @Override
    public ExeResponse<String> asyncExecute(ExeRequest<Host> request) {
        throw new RuntimeException("not supported");
    }

    @Override
    public ExeResponse<String> query(String requestId) {
        throw new RuntimeException("not supported");
    }
}
