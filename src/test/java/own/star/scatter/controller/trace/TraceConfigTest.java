package own.star.scatter.controller.trace;

import java.util.concurrent.TimeUnit;

import brave.ScopedSpan;
import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.context.log4j2.ThreadContextCurrentTraceContext;
import brave.propagation.B3Propagation;
import brave.propagation.ExtraFieldPropagation;
import org.junit.Test;
import zipkin2.codec.SpanBytesEncoder;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.okhttp3.OkHttpSender;

import static org.junit.Assert.*;

public class TraceConfigTest {

    Tracer tracer;

    @Test
    public void testCase() {
        Sender sender = OkHttpSender.create("http://localhost:9411/api/v2/spans");

        AsyncReporter asyncReporter = AsyncReporter.builder(sender)
            .closeTimeout(500, TimeUnit.MILLISECONDS)
            .build(SpanBytesEncoder.JSON_V2);

        Tracing tracing = Tracing.newBuilder()
            .localServiceName("tracer-demo")
            .spanReporter(asyncReporter)
            .propagationFactory(ExtraFieldPropagation.newFactory(B3Propagation.FACTORY, "user-name"))
            .currentTraceContext(ThreadContextCurrentTraceContext.create())
            .build();

        tracer = tracing.tracer();
        Span span = tracer.newTrace().name("encode").start();
        try {
            doSomethingExpensive();
        } finally {
            span.finish();
        }


        Span twoPhase = tracer.newTrace().name("twoPhase").start();
        try {
            Span prepare = tracer.newChild(twoPhase.context()).name("prepare").start();
            try {
                prepare();
            } finally {
                prepare.finish();
            }
            Span commit = tracer.newChild(twoPhase.context()).name("commit").start();
            try {
                commit();
            } finally {
                commit.finish();
            }
        } finally {
            twoPhase.finish();
        }

        // 等待异步消息发送
        sleep(3000);

    }

    private void doSomethingExpensive() {
        Span span = tracer.nextSpan().name("HostService.findHostByAppName");
        try {
            sleep(500);
        } catch (Exception exp) {
        } finally {
            span.finish();
        }

    }

    private static void commit() {
        sleep(500);
    }

    private static void prepare() {
        sleep(500);
    }

    private static void sleep(long milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}