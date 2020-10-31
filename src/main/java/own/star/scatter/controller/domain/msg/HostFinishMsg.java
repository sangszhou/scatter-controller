package own.star.scatter.controller.domain.msg;

public class HostFinishMsg extends Message {
    String id;
    String hostName;
    String result;
    String msg;

    public HostFinishMsg() {
    }

    public HostFinishMsg(String id, String hostName, String result, String msg) {
        this.id = id;
        this.hostName = hostName;
        this.result = result;
        this.msg = msg;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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
}
