package own.star.scatter.controller.resource;

import java.util.Random;

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

    @RequestMapping("/create")
    public RestResponse<String> createPlan(String planName, String appName) {
        PlanReadyMsg planReadyMsg = new PlanReadyMsg();
        planReadyMsg.setPlanId("plan_" + new Random().nextInt(999));
        planReadyMsg.setPlanName(planName);
        planReadyMsg.setAppName(appName);

        mqService.sendMsg(planReadyMsg);
        RestResponse<String> result = new RestResponse<>();
        result.setMsg("success");
        result.setBody(planReadyMsg.getPlanId());
        result.setCode(200);
        return result;
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
