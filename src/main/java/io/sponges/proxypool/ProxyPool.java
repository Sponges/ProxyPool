package io.sponges.proxypool;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ProxyPool {

    private final Queue<HttpProxy> active = new ConcurrentLinkedQueue<>();
    private final Queue<HttpProxy> queued = new ConcurrentLinkedQueue<>();

    public ProxyPool(List<InetSocketAddress> proxies) {
        new ProxyPool(proxies, 10);
    }

    public ProxyPool(List<InetSocketAddress> proxies, int size) {
        for (InetSocketAddress address : proxies) {
            HttpProxy proxy = new HttpProxy(this, address);
            queued.add(proxy);
        }
        for (int i = 0; i < size; i++) {
            active.add(queued.poll());
        }
    }

    public HttpProxy getProxy() {
        HttpProxy proxy = active.poll();
        return proxy.isWorking() ? proxy : getProxy();
    }

    public void returnResource(HttpProxy proxy) {
        active.add(proxy);
    }

    public void removeResource(HttpProxy proxy) {
        active.remove(proxy);
        active.add(queued.poll());
    }

    public List<InetSocketAddress> getWorking() {
        List<InetSocketAddress> working = new ArrayList<>();
        for (HttpProxy proxy : active) {
            if (proxy.isWorking()) {
                InetSocketAddress address = proxy.getAddress();
                working.add(address);
            }
        }
        return working;
    }

}
