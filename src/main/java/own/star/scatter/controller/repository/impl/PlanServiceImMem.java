package own.star.scatter.controller.repository.impl;

import java.util.HashMap;
import java.util.Map;

import brave.ScopedSpan;
import brave.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import own.star.scatter.controller.domain.bean.Plan;
import own.star.scatter.controller.repository.PlanService;

@Service
public class PlanServiceImMem implements PlanService {
    Logger logger = LoggerFactory.getLogger(PlanServiceImMem.class);

    Map<String, Plan> planMap = new HashMap<>();

    @Autowired
    Tracer tracer;

    @Override
    public void storePlan(Plan plan) {
        ScopedSpan scopedSpan = tracer.startScopedSpan("PlanService.storePlan");
        try {
            planMap.put(plan.getId(), plan);
        } catch (Exception exp) {
            scopedSpan.error(exp);
        } finally {
            scopedSpan.finish();
        }
    }

    @Override
    public Plan getById(String planId) {
        ScopedSpan scopedSpan = tracer.startScopedSpan("PlanService.getById");
        try {
            return planMap.get(planId);
        } catch (Exception exp) {
            logger.error("get by id exception, plan id: {}", planId, exp);
            scopedSpan.error(exp);
            return null;
        } finally {
            scopedSpan.finish();
        }
    }
}
