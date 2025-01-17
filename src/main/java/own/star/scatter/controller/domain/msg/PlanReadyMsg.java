package own.star.scatter.controller.domain.msg;

public class PlanReadyMsg extends Message {
    String planId;
    String planName;
    String appName;
    int totalBatchNum;

    public int getTotalBatchNum() {
        return totalBatchNum;
    }

    public void setTotalBatchNum(int totalBatchNum) {
        this.totalBatchNum = totalBatchNum;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
