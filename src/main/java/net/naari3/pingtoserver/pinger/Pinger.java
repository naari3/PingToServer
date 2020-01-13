package net.naari3.pingtoserver.pinger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public abstract class Pinger {
    protected static final Logger LOGGER = LogManager.getLogger();

    protected String host;
    protected int timeout;

    protected long responseTime = 0;
    protected boolean isSuccess;
    protected boolean isTimeouted;

    Pinger(String host, int timeout) {};

    public abstract boolean ping() throws IOException;


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
