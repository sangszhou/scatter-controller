package own.star.scatter.controller.service;

import com.alibaba.fastjson.JSON;

import brave.Tracing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import own.star.scatter.controller.domain.msg.Message;
import own.star.scatter.controller.utils.MessageTracingUtils;

public abstract class MsgHandler {
    Logger logger = LoggerFactory.getLogger(MsgHandler.class);

    @Autowired
    protected Tracing tracing;

    public abstract boolean support(Message msg);
    public abstract void doReceive(Message msg);

    public void onReceive(Message msg) {
        logger.info("message content msg: {}", JSON.toJSONString(msg));
        // 把数据从 message 消息中读出, 放到 thread local 中
        tracing.propagation()
            .extractor(MessageTracingUtils.msgGetter)
            .extract(msg);

        // 是不是要获取当前 span 当做上一个 span 的 parent 呢?

        doReceive(msg);
    }
}
