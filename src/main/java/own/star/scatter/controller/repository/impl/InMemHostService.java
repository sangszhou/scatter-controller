package own.star.scatter.controller.repository.impl;

import java.rmi.server.ExportException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import brave.ScopedSpan;
import brave.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import own.star.scatter.controller.domain.bean.Host;
import own.star.scatter.controller.executor.ExecutorConstants;
import own.star.scatter.controller.repository.HostService;

@Service
public class InMemHostService implements HostService {

    Map<String, Host> hostRepo = new HashMap<>();

    @Autowired
    Tracer tracer;

    @Override
    public List<Host> findHostByAppName(String appName) {
        ScopedSpan scopedSpan = tracer.startScopedSpan("HostService.findHostByAppName");
        scopedSpan.tag("appName", appName);
        List<Host> result = new LinkedList<>();
        try {
            int id = new Random().nextInt(998);

            for (int i = 0; i < 6; i++) {
                result.add(new Host("id_" + id+i, "name_" + id+i));
            }

            return result;
        } catch (Exception exp) {
            scopedSpan.error(exp);
            return null;
        } finally {
            scopedSpan.finish();
        }
    }

    @Override
    public void storeHost(Host host) {
        ScopedSpan scopedSpan = tracer.startScopedSpan("HostService.storeHost");
        scopedSpan.tag("hostId", host.getId());
        try {
            hostRepo.put(host.getId(), host);
        } catch (Exception exp) {
            scopedSpan.error(exp);
        } finally {
            scopedSpan.finish();
        }
    }

    @Override
    public Host getHostByHostId(String hostId) {
        ScopedSpan scopedSpan = tracer.startScopedSpan("HostService.getHostByHostId");
        scopedSpan.tag("hostId", hostId);
        try {
            return hostRepo.get(hostId);
        } catch (Exception exp) {
            scopedSpan.error(exp);
            return null;
        } finally {
            scopedSpan.finish();
        }
    }

    @Override
    public List<Host> getHostByPlanId(String planId) {
        ScopedSpan scopedSpan = tracer.startScopedSpan("HostService.getHostByPlanId");
        scopedSpan.tag("planId", planId);
        try {
            return hostRepo.values()
                .stream()
                .filter(host -> host.getPlanId().equalsIgnoreCase(planId))
                .collect(Collectors.toList());
        } catch (Exception exp) {
            scopedSpan.error(exp);
            return null;
        } finally {
            scopedSpan.finish();
        }
    }

    @Override
    public void statusToRunning(Host host) {
        ScopedSpan scopedSpan = tracer.startScopedSpan("HostService.statusToRunning");
        scopedSpan.tag("hostId", host.getId());
        try {
            hostRepo.get(host.getId()).setStatus(ExecutorConstants.RUNNING);
        } catch (Exception exp) {
            scopedSpan.error(exp);
        } finally {
            scopedSpan.finish();
        }
    }

    @Override
    public void statusToSuccess(Host host) {
        statusTo(host, ExecutorConstants.SUCCESS);
    }

    @Override
    public void statusToFail(Host host) {
        statusTo(host, ExecutorConstants.FAIL);
    }

    @Override
    public void statusToWait(Host host) {
        statusTo(host, ExecutorConstants.WAIT);
    }

    @Override
    public void statusTo(Host host, String status) {
        ScopedSpan scopedSpan = tracer.startScopedSpan("HostService.statusTo");
        try {
            hostRepo.get(host.getId()).setStatus(status);
        } catch (Exception exp) {
            scopedSpan.error(exp);
        } finally {
            scopedSpan.finish();
        }
    }
}
