package own.star.scatter.controller.trace;

import java.util.concurrent.TimeUnit;

import brave.Tracing;
import org.springframework.cloud.sleuth.zipkin2.DefaultZipkinRestTemplateCustomizer;
import org.springframework.cloud.sleuth.zipkin2.ZipkinProperties;
import org.springframework.cloud.sleuth.zipkin2.ZipkinRestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import zipkin2.codec.SpanBytesEncoder;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Reporter;
import zipkin2.reporter.Sender;

public class TraceConfig {

    @Bean
    public ZipkinRestTemplateCustomizer myCustomizer() {
        ZipkinProperties zipkinProperties = new ZipkinProperties();
        zipkinProperties.setBaseUrl("http://localhost:9411/");
        return new DefaultZipkinRestTemplateCustomizer(zipkinProperties);
    }

    // sender 查看 ZipkinRestTemplateSenderConfiguration,
    // 如果没有的话, 那就去找 sender configuration
    //@Bean
    //public Sender myRestTemplateSender(ZipkinProperties zipkin,
    //    ZipkinRestTemplateCustomizer zipkinRestTemplateCustomizer) {
    //    RestTemplate restTemplate = new RestTemplate();
    //    zipkinRestTemplateCustomizer.customize(restTemplate);
    //    return Sender(zipkin, restTemplate);
    //}

    //public Reporter globalReporter() {
    //    Sender sender = OkHttpSender.create("http://localhost:9411/api/v2/spans");
    //    AsyncReporter asyncReporter = AsyncReporter.builder(sender)
    //        .closeTimeout(500, TimeUnit.MILLISECONDS)
    //        .build(SpanBytesEncoder.JSON_V2);
    //    return asyncReporter;
    //}


    /**
     * 全局唯一的 tracing
     * 1. 采用默认的日志 reporter (如果要渲染 UI, 需要 async http reporter)
     * @return
     */
    @Bean
    public Tracing globalTracing() {
        Tracing tracing = Tracing.newBuilder()
            .localServiceName("scatter-controller")
            .build();
        return tracing;
    }
}
