package own.star.scatter.controller.service;

import own.star.scatter.controller.domain.msg.Message;

public interface MsgHandler {

    boolean support(Message msg);
    void onReceive(Message msg);
}
