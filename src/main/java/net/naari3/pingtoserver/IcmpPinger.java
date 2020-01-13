package net.naari3.pingtoserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.icmp4j.IcmpPingRequest;
import org.icmp4j.IcmpPingResponse;
import org.icmp4j.IcmpPingUtil;

import java.io.IOException;

public class IcmpPinger {
    private static final Logger LOGGER = LogManager.getLogger();

    private String host;
    private int timeout;

    private long responseTime = 0;
    private boolean isSuccess;
    private boolean isTimeouted;

    public IcmpPinger(String host, int timeout) {
        this.host = host;
        this.timeout = timeout;
    }

    public boolean ping() throws IOException {
        IcmpPingRequest request = IcmpPingUtil.createIcmpPingRequest();
        request.setHost(this.host);
        request.setTimeout(this.timeout);

        IcmpPingResponse response = IcmpPingUtil.executePingRequest(request);
        String formattedResponse = IcmpPingUtil.formatResponse(response);

        System.out.println(formattedResponse);

        this.isSuccess = response.getSuccessFlag();
        this.isTimeouted = response.getTimeoutFlag();
        this.responseTime = response.getRtt();

        if (response.getSuccessFlag()) {
            LOGGER.debug(String.format("%s response: %d ms", this.host, response.getRtt()));
        } else if (response.getTimeoutFlag()) {
            LOGGER.warn(String.format("%s timeout", this.host));
        } else {
            LOGGER.warn("Cannot connect to %s", this.host);
        }

        return response.getSuccessFlag();
    }

    public void pingAsync() {
        Thread thread = new Thread(() -> {
            try {
                ping();
            } catch (IOException err) {
                LOGGER.warn(err);
            }
        });
        thread.start();
    }

    public String getContent() {
        if (!this.isSuccess) {
            return "Failed";
        }
        if (this.isTimeouted) {
            return "Timeout";
        }
        return String.format("%d ms", this.responseTime);
    }
}
