package own.star.scatter.controller.resource;

import java.util.Random;

import brave.ScopedSpan;
import brave.Tracer;
import brave.Tracing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import own.star.scatter.controller.domain.bean.Plan;
import own.star.scatter.controller.domain.msg.PlanReadyMsg;
import own.star.scatter.controller.domain.resource.RestResponse;
import own.star.scatter.controller.mq.MqService;
import own.star.scatter.controller.repository.PlanService;

@RestController
@RequestMapping("/schedule")
public class ScheduleResource {

    @Autowired
    MqService mqService;
    @Autowired
    PlanService planService;
    @Autowired
    Tracing tracing;

    @RequestMapping("/create")
    public RestResponse<String> createPlan(String planName, String appName, int totalBatchNum) {
        Tracer tracer = tracing.tracer();

        PlanReadyMsg planReadyMsg = new PlanReadyMsg();
        planReadyMsg.setPlanId("plan_" + new Random().nextInt(999));
        planReadyMsg.setPlanName(planName);
        planReadyMsg.setAppName(appName);
        planReadyMsg.setTotalBatchNum(totalBatchNum);

        // 和 Span 的区别是, 是否在一个线程中, 是否能够 close 掉它
        ScopedSpan scopedSpan = tracer
            .startScopedSpan("ScheduleResource.create")
            .tag("planId", planReadyMsg.getPlanId())
            .tag("planName", planReadyMsg.getPlanName())
            .tag("appName", planReadyMsg.getAppName());

        try {
            mqService.sendMsg(planReadyMsg);
            scopedSpan.finish();

            RestResponse<String> result = new RestResponse<String>()
                .withCode(200)
                .withMsg("success")
                .withData(planReadyMsg.getPlanId());

            return result;
        } catch (Exception exp) {
            scopedSpan.error(exp);
            return new RestResponse<String>()
                .withMsg("failed")
                .withMsg(exp.getMessage());
        } finally {
            scopedSpan.finish();
        }
    }

    @RequestMapping("/query")
    public RestResponse<Plan> queryPlan(String planId) {
        Plan plan = planService.getById(planId);
        RestResponse<Plan> planRestResponse = new RestResponse<>();
        planRestResponse.setBody(plan);
        planRestResponse.setCode(200);
        planRestResponse.setMsg("success");
        return planRestResponse;
    }

}
