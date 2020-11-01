package own.star.scatter.controller.utils;

import brave.propagation.Propagation.Getter;
import brave.propagation.Propagation.Setter;
import own.star.scatter.controller.domain.msg.Message;

public class MessageTracingUtils {
    public static final Setter<Message, String> msgSetter = new Setter<Message, String>() {
        @Override
        public void put(Message carrier, String key, String value) {
            carrier.getHeader().put(key, value);
        }
    };

    public static final Getter<Message, String> msgGetter = new Getter<Message, String>() {
        @Override
        public String get(Message carrier, String key) {
            return carrier.getHeader().get(key).toString();
        }
    };

}
