package net.naari3.pingtoserver;

import java.io.IOException;
import java.net.InetAddress;

public class Pinger {
    private String host;
    private int timeout = 5000;

    private long responseTime = 0;

    public Pinger(String host, int timeout) {
        this.host = host;
        this.timeout = timeout;
    }

    public long getResponseTime() {
        return this.responseTime;
    }

    public boolean ping() throws IOException {
        boolean isReachable = false;
        long start, end;

        InetAddress inetAddress = InetAddress.getByName(this.host);
        start = System.currentTimeMillis();
        isReachable = inetAddress.isReachable(this.timeout);
        end = System.currentTimeMillis();

        if (isReachable) {
            this.responseTime = start - end;
        } else {
            this.responseTime = -1;
        }
        return isReachable;
    }

}
