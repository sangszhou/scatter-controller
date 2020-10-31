package own.star.scatter.controller.repository;

import own.star.scatter.controller.domain.bean.Plan;

public interface PlanService {
    void storePlan(Plan plan);

    Plan getById(String planId);
}
