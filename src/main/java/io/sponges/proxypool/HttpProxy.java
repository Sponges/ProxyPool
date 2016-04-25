package io.sponges.proxypool;

import java.io.IOException;
import java.net.*;

public class HttpProxy implements AutoCloseable {

    private static final long CHECK_INTERVAL = 1000 * 60 * 60;
    private static final String CHECK_URL = "http://google.com/";

    private final ProxyPool proxyPool;
    private final InetSocketAddress address;
    private final Proxy proxy;

    private long lastChecked = -1;

    protected HttpProxy(ProxyPool proxyPool, InetSocketAddress address) {
        this.proxyPool = proxyPool;
        this.address = address;
        this.proxy = new Proxy(Proxy.Type.HTTP, address);
    }

    public void close() throws Exception {
        proxyPool.returnResource(this);
    }

    public boolean isWorking() {
        long current = System.currentTimeMillis();
        if (current - lastChecked > CHECK_INTERVAL) {
            boolean works = runChecks(3);
            if (works) {
                lastChecked = System.currentTimeMillis();
            } else {
                proxyPool.removeResource(this);
            }
            return works;
        } else {
            return true;
        }
    }

    private boolean runChecks(int times) {
        for (int i = 0; i < times; i++) {
            if (check()) return true;
        }
        return false;
    }

    public boolean check() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(CHECK_URL).openConnection(proxy);
            connection.addRequestProperty("User-Agent", "Mozilla/1.0");
            connection.setReadTimeout(2500);
            connection.setConnectTimeout(2500);
            int code = connection.getResponseCode();
            connection.disconnect();
            return code > 199 && code < 300;
        } catch (SocketException | SocketTimeoutException | ProtocolException e) {
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public Proxy getProxy() {
        return proxy;
    }

}
