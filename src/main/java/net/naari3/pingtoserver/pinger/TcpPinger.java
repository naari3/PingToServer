package net.naari3.pingtoserver.pinger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class TcpPinger extends Pinger {
    private int port = 25565;

    TcpPinger(String host, int timeout) {
        super(host, timeout);
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
}
