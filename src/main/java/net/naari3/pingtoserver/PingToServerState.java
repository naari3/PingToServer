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

    public PingStatus getStatus()  {
        return this.status;
    }

    public void setStatus(PingStatus status) {
        this.status = status;
    }
}
