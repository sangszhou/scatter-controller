package own.star.scatter.controller.mq.impl;

import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.alibaba.fastjson.JSON;

import brave.Tracing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import own.star.scatter.controller.domain.msg.Message;
import own.star.scatter.controller.mq.MqService;
import own.star.scatter.controller.service.MsgHandler;
import own.star.scatter.controller.utils.MessageTracingUtils;

@Service
public class MqServiceImMem implements MqService {
    Logger logger = LoggerFactory.getLogger(MqServiceImMem.class);

    // 用来执行消息的, 需要脱离目前的线程 scope, 模拟异步的效果
    Executor mqExecutor = Executors.newFixedThreadPool(20);

    @Autowired
    List<MsgHandler> msgHandler;

    @Autowired
    Tracing tracing;

    Queue<Message> msgQueue = new ArrayBlockingQueue<Message>(10000);

    @Scheduled(fixedDelay = 1000)
    public void schedule() {
        Message msg = msgQueue.peek();
        if (msg != null) {
            logger.info("mq service in mem, schedule found message: {}", JSON.toJSONString(msg));
            msgQueue.poll();

            Optional<MsgHandler> handlerOpt = msgHandler
                .stream()
                .filter(handler -> handler.support(msg))
                .findFirst();

            if (handlerOpt.isPresent()) {
                mqExecutor.execute(() -> {
                    handlerOpt.get().onReceive(msg);
                });
            } else {
                logger.error("no handler support msg: {}", JSON.toJSONString(msg));
            }
        }
    }

    @Override
    public void sendMsg(Message msg) {
        // decorate msg with
        // 添加的字段什么时候才能搞出去嗯
        tracing.propagation()
            .injector(MessageTracingUtils.msgSetter)
            .inject(tracing.currentTraceContext().get(), msg);

        msgQueue.add(msg);

    }

    @Override
    public List<Message> poll() {
        throw new RuntimeException("mq.poll, not supported yet");
    }
}
