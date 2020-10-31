package own.star.scatter.controller.mq.impl;

import java.util.List;
import java.util.Optional;

import com.alibaba.fastjson.JSON;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import own.star.scatter.controller.domain.msg.Message;
import own.star.scatter.controller.mq.MqService;
import own.star.scatter.controller.service.MsgHandler;

@Service
public class MqServiceImMem implements MqService {
    Logger logger = LoggerFactory.getLogger(MqServiceImMem.class);

    @Autowired
    List<MsgHandler> msgHandler;

    @Override
    public void sendMsg(Message msg) {
        Optional<MsgHandler> handlerOpt = msgHandler
            .stream()
            .filter(handler -> handler.support(msg))
            .findFirst();

        if (handlerOpt.isPresent()) {
            handlerOpt.get().onReceive(msg);
        } else {
            logger.error("no handler support msg: {}", JSON.toJSONString(msg));
        }
    }

    @Override
    public List<Message> poll() {
        throw new RuntimeException("mq.poll, not supported yet");
    }
}
