玩具项目, 用来异步和基于消息的项目的 tracing 最佳实践, 单从项目来看, 这个项目要比 whip 
简单的多


已解决问题
[1] 怎么通过 msg 传递数据
    通过重写 Getter, Setter 的方式
[2] 怎么把 context 放到 thread local 里
通过 scoped 的方式能解决一部分问题, 但是仅创建 span 并不会更新 thread local, 通过参数传递的方式肯定行不通

[3]  
 
遗留问题
[1] kafka tracing client

kafka 代码还是没看懂, 不知道为什么 span.start().finish() 的意义, 以及一个消息被多个 consumer 消费
到底会带来哪些问题

[2] tracing, tracer 到底有啥区别

[3] zipkin 的显示还是以时间维度展开的
发送消息和接受消息没能连在一起, 而我的代码里明明写了 

```java
      ScopedSpan scopedSpan = tracer
         .startScopedSpanWithParent("Mq.onReceive[" + msg.getClass().getSimpleName() + "]", result.context());
```

