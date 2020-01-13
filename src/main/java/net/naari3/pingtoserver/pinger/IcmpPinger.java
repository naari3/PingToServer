package net.naari3.pingtoserver.pinger;

import org.icmp4j.IcmpPingRequest;
import org.icmp4j.IcmpPingResponse;
import org.icmp4j.IcmpPingUtil;

import java.io.IOException;
import java.net.URISyntaxException;

public class IcmpPinger extends Pinger {
    public IcmpPinger(String host, int timeout) throws URISyntaxException {
        super(host, timeout);
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
}
