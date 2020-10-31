package own.star.scatter.controller.repository.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import own.star.scatter.controller.domain.bean.Plan;
import own.star.scatter.controller.repository.PlanService;

@Service
public class PlanServiceImMem implements PlanService {
    Map<String, Plan> planMap = new HashMap<>();

    @Override
    public void storePlan(Plan plan) {
        planMap.put(plan.getId(), plan);
    }

    @Override
    public Plan getById(String planId) {
        return planMap.get(planId);
    }
}
