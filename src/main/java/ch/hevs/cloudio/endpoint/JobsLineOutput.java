package ch.hevs.cloudio.endpoint;

public class JobsLineOutput {
    private String data;
    private String correlationID;

    public JobsLineOutput( String data, String correlationID){
        this.data = data;
        this.correlationID = correlationID;
    }

    public String getCorrelationID() {
        return correlationID;
    }

    public String getData() {
        return data;
    }

    public void setCorrelationID(String correlationID) {
        this.correlationID = correlationID;
    }

    public void setData(String data) {
        this.data = data;
    }
}
