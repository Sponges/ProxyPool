package io.sponges.proxypool;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ProxyPool {

    private final Queue<HttpProxy> queue = new ConcurrentLinkedQueue<>();

    public ProxyPool(List<String[]> proxies) {
        for (String[] arr : proxies) {
            HttpProxy proxy = new HttpProxy(this, new InetSocketAddress(arr[0], Integer.valueOf(arr[1])));
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

    public List<String[]> getWorking() {
        List<String[]> working = new ArrayList<>();
        for (HttpProxy proxy : queue) {
            if (proxy.isWorking()) {
                InetSocketAddress address = proxy.getAddress();
                working.add(new String[]{
                        address.getHostString(),
                        String.valueOf(address.getPort())
                });
            }
        }
        return working;
    }

}
