package own.star.scatter.controller.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.websocket.MessageHandler;

import com.alibaba.fastjson.JSON;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import own.star.scatter.controller.domain.bean.Host;
import own.star.scatter.controller.domain.msg.BatchFinishMsg;
import own.star.scatter.controller.domain.msg.HostFinishMsg;
import own.star.scatter.controller.domain.msg.HostReadyMsg;
import own.star.scatter.controller.domain.msg.Message;
import own.star.scatter.controller.domain.msg.PlanFinishMsg;
import own.star.scatter.controller.executor.ExeRequest;
import own.star.scatter.controller.executor.ExeResponse;
import own.star.scatter.controller.executor.ExecutorConstants;
import own.star.scatter.controller.executor.impl.HostExecutorInMemSync;
import own.star.scatter.controller.mq.MqService;
import own.star.scatter.controller.repository.HostService;

@Service
public class HostHandler extends MsgHandler {
    private Logger logger = LoggerFactory.getLogger(HostHandler.class);

    /**
     * 是不是不应该在实现类上加上 template 呢?
     */
    @Autowired
    HostExecutorInMemSync hostExecutorInMemSync;
    @Autowired
    HostService hostService;
    @Autowired
    MqService mqService;

    @Override
    public boolean support(Message msg) {
        if (msg instanceof HostReadyMsg ||
            msg instanceof HostFinishMsg) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void doReceive(Message msg) {
        if (msg instanceof HostReadyMsg) {
            doReady((HostReadyMsg)msg);
        } else if(msg instanceof HostFinishMsg) {
            doFinish((HostFinishMsg)msg);
        } else {
            logger.error("host handler message type not found: {}", JSON.toJSONString(msg));
        }
    }

    public void doReady(HostReadyMsg hostReadyMsg) {
        Host theHost = hostService.getHostByHostId(hostReadyMsg.getId());

            new Host(hostReadyMsg.getId(), hostReadyMsg.getHostName());
        ExeResponse<String> response = hostExecutorInMemSync.execute(
            new ExeRequest<>(hostReadyMsg.getId(), theHost));

        hostService.statusTo(theHost, response.getBody());

        doFinish(new HostFinishMsg(theHost.getId(), theHost.getHostName(),
            response.getBody(), response.getBody()));
    }

    /**
     * 检查是否所有的机器都已经完成了 (成功或者失败)
     * @param hostFinishMsg
     */
    public void doFinish(HostFinishMsg hostFinishMsg) {
        Host theHost = hostService.getHostByHostId(hostFinishMsg.getId());
        String planId = theHost.getPlanId();
        List<Host> hostListInBatch = hostService
            .getHostByPlanId(planId)
            .stream().filter(host -> host.getBatchNum() == theHost.getBatchNum())
            .collect(Collectors.toList());

        if (checkFinish(hostListInBatch)) {
            BatchFinishMsg batchFinishMsg = new BatchFinishMsg();
            batchFinishMsg.setBatchNum(theHost.getBatchNum());
            batchFinishMsg.setPlanId(planId);
            batchFinishMsg.setMsg(ExecutorConstants.SUCCESS);
            mqService.sendMsg(batchFinishMsg);
        }
    }

    public boolean checkFinish(List<Host> hostList) {
        boolean result = hostList
            .stream()
            .allMatch(host ->
                host.getStatus().equalsIgnoreCase(ExecutorConstants.SUCCESS) ||
                    host.getStatus().equalsIgnoreCase(ExecutorConstants.FAIL));
        return result;
    }


}
