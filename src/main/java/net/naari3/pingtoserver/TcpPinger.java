package net.naari3.pingtoserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class TcpPinger {
    private static final Logger LOGGER = LogManager.getLogger();

    private String host;
    private int port = 25565;
    private int timeout;

    private long responseTime = 0;
    private boolean isSuccess;
    private boolean isTimeouted;

    TcpPinger(String host, int timeout) {
        this.timeout = timeout;

        if (host.contains(":")) {
            String[] hostAndPort = host.split(":");
            this.host = hostAndPort[0];
            this.port = Integer.parseInt(hostAndPort[1]);
        } else {
            this.host = host;
        }
    }

    public boolean ping() throws IOException {
        Socket socket = new Socket();
        long start, end;

        try {
            start = System.currentTimeMillis();
            socket.connect(new InetSocketAddress(this.host, this.port), this.timeout);
            end = System.currentTimeMillis();

            this.responseTime = end - start;
            this.isSuccess = true;
            return true;
        } catch (SocketTimeoutException err) {
            this.isTimeouted = false;
            this.isSuccess = false;

            return false;
        } catch (IOException err) {
            this.isSuccess = false;
            throw err;
        }
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
