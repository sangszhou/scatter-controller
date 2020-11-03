package own.star.scatter.controller.domain.bean;

import java.sql.Date;

public class Host {
    String id;
    String planId;
    String hostName;
    Integer batchNum;

    Date startTime = new Date(System.currentTimeMillis());
    Date finishTime;
    String status;

    public Host() {
    }

    public Host(String id, String planId, String hostName, Date startTime) {
        this.id = id;
        this.planId = planId;
        this.hostName = hostName;
        this.startTime = startTime;
    }

    public Integer getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(Integer batchNum) {
        this.batchNum = batchNum;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Host(String id, String hostName) {
        this.id = id;
        this.hostName = hostName;
    }

    public Host(String id, String hostName, Date startTime) {
        this.id = id;
        this.hostName = hostName;
        this.startTime = startTime;
    }

    public Host(String id, String hostName, Date startTime, Date finishTime) {
        this.id = id;
        this.hostName = hostName;
        this.startTime = startTime;
        this.finishTime = finishTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }
}
