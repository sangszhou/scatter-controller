package own.star.scatter.controller.resource;

import java.sql.Date;

import javax.sound.midi.SysexMessage;

import brave.ScopedSpan;
import brave.Tracer;
import brave.Tracing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import own.star.scatter.controller.domain.bean.Host;
import own.star.scatter.controller.domain.resource.RestResponse;
import own.star.scatter.controller.repository.HostService;

/**
 * test tracing capabilities
 */
@RestController
@RequestMapping("/trace")
public class TraceSampleResource {
    @Autowired
    Tracer tracer;
    @Autowired
    Tracing tracing;
    @Autowired
    HostService hostService;

    @RequestMapping("/twoChildren")
    public RestResponse<Host> twoChildren(String id, boolean withException) {
        ScopedSpan scopedSpan = tracer.startScopedSpan("TraceSample.twoChildren");
        scopedSpan.tag("hostId", id);
        try {
            hostService.storeHost(new Host(id,
                "planId", "hostName", new Date(System.currentTimeMillis())));

            Host theHost = hostService.getHostByHostId(id);

            if (withException) {
                scopedSpan.tag("withException", "true");
                throw new RuntimeException("exception on purpose");
            } else {
                scopedSpan.tag("withException", "false");
            }
            return new RestResponse<Host>().withCode(200).withData(theHost);
        } catch (Exception exp) {
            scopedSpan.error(exp);
            return new RestResponse<Host>().withCode(400).withMsg(exp.getMessage());
        } finally {
            scopedSpan.finish();
        }


    }



}
