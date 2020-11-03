package own.star.scatter.controller.domain.msg;

public class BatchReadyMsg extends Message {
    String planId;
    int batchNum;

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public int getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(int batchNum) {
        this.batchNum = batchNum;
    }
}
