package own.star.scatter.controller.service;

import com.alibaba.fastjson.JSON;

import brave.ScopedSpan;
import brave.Span;
import brave.Span.Kind;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.TraceContextOrSamplingFlags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import own.star.scatter.controller.domain.msg.Message;
import own.star.scatter.controller.utils.MessageTracingUtils;

public abstract class MsgHandler {
    Logger logger = LoggerFactory.getLogger(MsgHandler.class);

    @Autowired
    protected Tracing tracing;
    @Autowired
    protected Tracer tracer;

    public abstract boolean support(Message msg);
    public abstract void doReceive(Message msg);

    public void onReceive(Message msg) {
        logger.info("message content msg: {}", JSON.toJSONString(msg));
        // 把数据从 message 消息中读出, 放到 thread local 中
        TraceContextOrSamplingFlags result = tracing.propagation()
            .extractor(MessageTracingUtils.msgGetter)
            .extract(msg);

        // new child 会注入进来 local 里面吗?

        //Span span = tracer.newChild(result.context());
        //span.kind(Kind.CONSUMER)
        //    .name("Mq.onReceive")
        //    .remoteServiceName("consumer")
        //    .start();
        // 官方教程里, 希望 span 开始后发生结束, 说是为了防止多个 consumer 共享 span
        // 我没理解其中的意思, 但是我明确一点, 就是 span finish 以后, 后续的 span 就断开了, 所以
        // 这里我就先不让其结束

        ScopedSpan scopedSpan = tracer.startScopedSpanWithParent("Mq.onReceive", result.context());

        try {
            doReceive(msg);
        } catch (Exception exp) {
            scopedSpan.error(exp);
            throw exp;
        } finally {
            scopedSpan.finish();
        }
        // 是不是要获取当前 span 当做上一个 span 的 parent 呢?
    }
}
