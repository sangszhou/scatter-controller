package own.star.scatter.controller.domain.msg;

import java.util.HashMap;
import java.util.Map;

public class Message {
    // 用来传递一些元数据
    Map<String, Object> header = new HashMap<>();

    public Map<String, Object> getHeader() {
        return header;
    }

    public void setHeader(Map<String, Object> header) {
        this.header = header;
    }
}
