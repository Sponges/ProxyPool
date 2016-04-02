package io.sponges.proxypool;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ProxyPool {

    private final Queue<HttpProxy> queue = new ConcurrentLinkedQueue<>();

    public ProxyPool(List<InetSocketAddress> proxies) {
        for (InetSocketAddress address : proxies) {
            HttpProxy proxy = new HttpProxy(this, address);
            queue.add(proxy);
        }
    }

    public HttpProxy getProxy() {
        HttpProxy proxy = queue.poll();
        return proxy.isWorking() ? proxy : getProxy();
    }

    public void returnResource(HttpProxy proxy) {
        queue.add(proxy);
    }

    public void removeResource(HttpProxy proxy) {
        queue.remove(proxy);
    }

    public List<InetSocketAddress> getWorking() {
        List<InetSocketAddress> working = new ArrayList<>();
        for (HttpProxy proxy : queue) {
            if (proxy.isWorking()) {
                InetSocketAddress address = proxy.getAddress();
                working.add(address);
            }
        }
        return working;
    }

}
