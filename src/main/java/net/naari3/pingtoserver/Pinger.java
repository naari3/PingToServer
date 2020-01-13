package net.naari3.pingtoserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;

public class Pinger {
    private static final Logger LOGGER = LogManager.getLogger();

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
            this.responseTime = end - start;
            LOGGER.debug(String.format("%s response: %d ms", this.host, this.responseTime));
        } else {
            this.responseTime = -1;
            LOGGER.warn(String.format("%s timeout"));

        }
        return isReachable;
    }

    public String getContent() {
        if (this.responseTime == -1) {
            return "Timeout";
        }
        return String.format("%d ms", this.responseTime);
    }
}
