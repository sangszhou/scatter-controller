package own.star.scatter.controller.repository.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import own.star.scatter.controller.domain.bean.Host;
import own.star.scatter.controller.executor.ExecutorConstants;
import own.star.scatter.controller.repository.HostService;

@Service
public class InMemHostService implements HostService {

    Map<String, Host> hostRepo = new HashMap<>();

    @Override
    public List<Host> findHostByAppName(String appName) {
        int id = new Random().nextInt(998);
        int nextId = id + 1;
        return Arrays.asList(
            new Host("id_" + id, "name_" + id),
            new Host("id_" + nextId, "name_" + nextId)
        );
    }

    @Override
    public void storeHost(Host host) {
        hostRepo.put(host.getId(), host);
    }

    @Override
    public Host getHostByHostId(String hostId) {
        return hostRepo.get(hostId);
    }

    @Override
    public List<Host> getHostByPlanId(String planId) {
        return hostRepo.values()
            .stream()
            .filter(host -> host.getPlanId().equalsIgnoreCase(planId))
            .collect(Collectors.toList());
    }

    @Override
    public void statusToRunning(Host host) {
        hostRepo.get(host.getId()).setStatus(ExecutorConstants.RUNNING);
    }

    @Override
    public void statusToSuccess(Host host) {
        hostRepo.get(host.getId()).setStatus(ExecutorConstants.SUCCESS);

    }

    @Override
    public void statusToFail(Host host) {
        hostRepo.get(host.getId()).setStatus(ExecutorConstants.FAIL);

    }

    @Override
    public void statusToWait(Host host) {
        hostRepo.get(host.getId()).setStatus(ExecutorConstants.WAIT);
    }

    @Override
    public void statusTo(Host host, String status) {
        hostRepo.get(host.getId()).setStatus(status);
    }
}
