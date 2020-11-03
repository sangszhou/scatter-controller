package own.star.scatter.controller.service;

import java.sql.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import own.star.scatter.controller.domain.bean.Host;
import own.star.scatter.controller.domain.bean.Plan;
import own.star.scatter.controller.domain.msg.BatchReadyMsg;
import own.star.scatter.controller.domain.msg.HostReadyMsg;
import own.star.scatter.controller.domain.msg.Message;
import own.star.scatter.controller.domain.msg.PlanFinishMsg;
import own.star.scatter.controller.domain.msg.PlanReadyMsg;
import own.star.scatter.controller.executor.ExecutorConstants;
import own.star.scatter.controller.mq.MqService;
import own.star.scatter.controller.repository.HostService;
import own.star.scatter.controller.repository.PlanService;

@Service
public class PlanHandler extends MsgHandler {
    private Logger logger = LoggerFactory.getLogger(PlanHandler.class);

    @Autowired
    HostService hostService;
    @Autowired
    PlanService planService;
    @Autowired
    MqService mqService;

    @Override
    public boolean support(Message msg) {
        if (msg instanceof PlanReadyMsg
            || msg instanceof PlanFinishMsg) {
            return true;
        }
        return false;
    }

    @Override
    public void doReceive(Message msg) {
        if (msg instanceof PlanReadyMsg) {
            doReady((PlanReadyMsg)msg);
        } else if(msg instanceof PlanFinishMsg) {
            doFinish((PlanFinishMsg)msg);
        } else {
            logger.error("plan handler message type not found: {}", JSON.toJSONString(msg));
        }
    }

    public void doReady(PlanReadyMsg readyMsg) {
        List<Host> hostList = fetchHostList(readyMsg);
        Plan plan = new Plan();
        plan.setId(readyMsg.getPlanId());
        plan.setStartTime(new Date(System.currentTimeMillis()));
        plan.setPlanName(readyMsg.getPlanName());
        plan.setStatus(ExecutorConstants.RUNNING);
        planService.storePlan(plan);

        // split and set batch number
        List<List<Host>> batchedHostList = partition(hostList, readyMsg.getTotalBatchNum());

        batchedHostList.stream().forEach(
            oneBatchHostList -> oneBatchHostList.stream().forEach(host -> {
                host.setPlanId(readyMsg.getPlanId());
                host.setStartTime(new Date(System.currentTimeMillis()));
                host.setStatus(ExecutorConstants.WAIT);
                hostService.storeHost(host);
            })
        );

        // trigger
        BatchReadyMsg batchReadyMsg = new BatchReadyMsg();
        batchReadyMsg.setPlanId(readyMsg.getPlanId());
        batchReadyMsg.setBatchNum(0);
        mqService.sendMsg(batchReadyMsg);
    }

    public void doFinish(PlanFinishMsg finishMsg) {

        Plan plan = planService.getById(finishMsg.getPlanId());
        plan.setStatus(ExecutorConstants.SUCCESS);
        planService.storePlan(plan);
    }

    public List<Host> fetchHostList(PlanReadyMsg planReadyMsg) {
        String appName = planReadyMsg.getAppName();
        List<Host> hostList = hostService.findHostByAppName(appName);
        return hostList;
    }

    /**
     * split host into sub list, and tag each
     * @param hostList
     */
    public List<List<Host>> partition(List<Host> hostList, int totalBatchNum) {
        int hostNumInBatch = hostList.size() / totalBatchNum;
        List<List<Host>> partitionedHostList = Lists.partition(hostList, hostNumInBatch);
        // 把最后一批的机器和第一批组合
        if (hostList.size() % totalBatchNum != 0) {
            List<Host> lastBatch = partitionedHostList.remove(partitionedHostList.size() - 1);
            partitionedHostList.get(0).addAll(lastBatch);
        }

        // 打上批次的标号
        for (int i = 0; i < partitionedHostList.size(); i ++) {
            int batchNum = i;
            partitionedHostList.get(batchNum)
                .forEach(host -> host.setBatchNum(batchNum));
        }
        return partitionedHostList;
    }
}
