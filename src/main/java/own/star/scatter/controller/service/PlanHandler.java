package own.star.scatter.controller.service;

import java.sql.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;

import com.netflix.discovery.converters.Auto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import own.star.scatter.controller.domain.bean.Host;
import own.star.scatter.controller.domain.bean.Plan;
import own.star.scatter.controller.domain.msg.HostReadyMsg;
import own.star.scatter.controller.domain.msg.Message;
import own.star.scatter.controller.domain.msg.PlanFinishMsg;
import own.star.scatter.controller.domain.msg.PlanReadyMsg;
import own.star.scatter.controller.executor.ExecutorConstants;
import own.star.scatter.controller.mq.MqService;
import own.star.scatter.controller.repository.HostService;
import own.star.scatter.controller.repository.PlanService;

@Service
public class PlanHandler implements MsgHandler {
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

    public void onReceive(Message msg) {
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

        hostList.stream().forEach(
            host -> {
                host.setPlanId(readyMsg.getPlanId());
                host.setStartTime(new Date(System.currentTimeMillis()));
                host.setStatus(ExecutorConstants.RUNNING);
                hostService.storeHost(host);

                HostReadyMsg hostReadyMsg = new HostReadyMsg();
                hostReadyMsg.setId(host.getId());
                hostReadyMsg.setHostName(host.getHostName());

                mqService.sendMsg(hostReadyMsg);

            }
        );

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

}
