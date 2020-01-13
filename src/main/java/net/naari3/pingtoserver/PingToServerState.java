package net.naari3.pingtoserver;

public class PingToServerState {
    protected enum PingStatus {
        Started,
        NotStarted
    };

    private PingStatus status;

    PingToServerState() {
        this.status = PingStatus.NotStarted;
    }

    private PingStatus getStatus()  {
        return this.status;
    }

    private void setStatus(PingStatus status) {
        this.status = status;
    }

    public void start() {
        setStatus(PingStatus.Started);
    }

    public void stop() {
        setStatus(PingStatus.NotStarted);
    }

    public boolean isStarted() {
        return this.status == PingStatus.Started;
    }

    public boolean isStopped() {
        return this.status == PingStatus.NotStarted;
    }
}
