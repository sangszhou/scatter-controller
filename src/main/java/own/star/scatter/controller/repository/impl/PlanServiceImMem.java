package own.star.scatter.controller.repository.impl;

import java.util.HashMap;
import java.util.Map;

import brave.ScopedSpan;
import brave.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import own.star.scatter.controller.domain.bean.Plan;
import own.star.scatter.controller.repository.PlanService;

@Service
public class PlanServiceImMem implements PlanService {
    Map<String, Plan> planMap = new HashMap<>();

    @Autowired
    Tracer tracer;

    @Override
    public void storePlan(Plan plan) {
        ScopedSpan scopedSpan = tracer.startScopedSpan("storePlan");
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
        return planMap.get(planId);
    }
}
