package own.star.scatter.controller.repository;

import java.util.List;

import own.star.scatter.controller.domain.bean.Host;

public interface HostService {
    List<Host> findHostByAppName(String appName);

    void storeHost(Host host);

    Host getHostByHostId(String hostId);

    List<Host> getHostByPlanId(String planId);

    void statusToRunning(Host host);

    void statusToSuccess(Host host);

    void statusToFail(Host host);

    void statusToWait(Host host);

    void statusTo(Host host, String status);

}
