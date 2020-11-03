package own.star.scatter.controller.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import own.star.scatter.controller.domain.bean.Host;
import own.star.scatter.controller.domain.msg.BatchFinishMsg;
import own.star.scatter.controller.domain.msg.BatchReadyMsg;
import own.star.scatter.controller.domain.msg.HostReadyMsg;
import own.star.scatter.controller.domain.msg.Message;
import own.star.scatter.controller.domain.msg.PlanFinishMsg;
import own.star.scatter.controller.mq.MqService;
import own.star.scatter.controller.repository.HostService;

@Service
public class BatchHandler extends MsgHandler {
    Logger logger = LoggerFactory.getLogger(BatchHandler.class);

    @Autowired
    HostService hostService;
    @Autowired
    PlanHandler planHandler;
    @Autowired
    MqService mqService;

    @Override
    public boolean support(Message msg) {
        if (msg instanceof BatchReadyMsg ||
            msg instanceof BatchFinishMsg) {
            return true;
        }

        return false;
    }

    @Override
    public void doReceive(Message msg) {
        if (msg instanceof BatchReadyMsg) {
            handle((BatchReadyMsg)msg);
        } else if (msg instanceof BatchFinishMsg) {
            handle((BatchFinishMsg)msg);
        } else {
        logger.error("failed to handle msg: {}", JSON.toJSONString(msg));
        }
    }

    /**
     * TODO:
     * [1] retry finished host in batch
     * @param batchReadyMsg
     */
    public void handle(BatchReadyMsg batchReadyMsg) {
        String planId = batchReadyMsg.getPlanId();
        hostService.getHostByPlanId(planId)
            .stream()
            .filter(host -> host.getBatchNum() == batchReadyMsg.getBatchNum())
            .forEach(host -> {
                HostReadyMsg hostReadyMsg = new HostReadyMsg();
                hostReadyMsg.setId(host.getId());
                hostReadyMsg.setHostName(host.getHostName());

                hostService.statusToRunning(host);
                mqService.sendMsg(hostReadyMsg);
            });
    }

    public void handle(BatchFinishMsg batchFinishMsg) {
        // start host in next batch
        String planId = batchFinishMsg.getPlanId();
        List<Integer> batchNumList = hostService.getHostByPlanId(planId)
            .stream().map(host -> host.getBatchNum())
            .collect(Collectors.toList());
        int maxBatchNum = Collections.max(batchNumList);
        if (batchFinishMsg.getBatchNum() >= maxBatchNum) {
            // all batch finished, send plan finish
            PlanFinishMsg planFinishMsg = new PlanFinishMsg();
            planFinishMsg.setPlanId(planId);
            mqService.sendMsg(planFinishMsg);
        } else {
            BatchReadyMsg batchReadyMsg = new BatchReadyMsg();
            batchReadyMsg.setPlanId(planId);
            batchReadyMsg.setBatchNum(batchFinishMsg.getBatchNum() + 1);
            mqService.sendMsg(batchReadyMsg);
        }
    }

}
