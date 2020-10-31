package own.star.scatter.controller.mq;

import java.util.List;

import own.star.scatter.controller.domain.msg.Message;

public interface MqService {
    void sendMsg(Message msg);

    List<Message> poll();
}
