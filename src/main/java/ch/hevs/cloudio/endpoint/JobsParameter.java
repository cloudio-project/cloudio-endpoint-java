package ch.hevs.cloudio.endpoint;

public class JobsParameter {
    private String jobURI;
    private String correlationID;
    private String data = ""; //data is an optional field
    private boolean sendOutput;

    public boolean getSendOutput() {
        return sendOutput;
    }

    public String getCorrelationID() {
        return correlationID;
    }

    public String getJobURI() {
        return jobURI;
    }

    public void setSendOutput(boolean sendOutput) {
        this.sendOutput = sendOutput;
    }

    public void setCorrelationID(String correlationID) {
        this.correlationID = correlationID;
    }

    public void setJobURI(String jobURI) {
        this.jobURI = jobURI;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}