package net.naari3.pingtoserver.pinger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public abstract class Pinger {
    protected static final Logger LOGGER = LogManager.getLogger();

    protected String host;
    protected int port = 25565;
    protected int timeout;

    protected long responseTime = 0;
    protected boolean isSuccess;
    protected boolean isTimeouted;

    public Pinger(String host, int timeout) throws URISyntaxException {
        // WORKAROUND: add any scheme to make the resulting URI valid.
        URI uri = new URI("my://" + host); // may throw URISyntaxException
        this.host = uri.getHost();
        this.port = uri.getPort();

        if (uri.getHost() == null) {
            throw new URISyntaxException(uri.toString(),
                    "URI must have host and port parts");
        }

        this.timeout = timeout;
    }

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
