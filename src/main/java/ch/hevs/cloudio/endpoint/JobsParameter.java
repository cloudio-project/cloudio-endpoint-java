package ch.hevs.cloudio.endpoint;

public class JobsParameter {
    private String jobURI;
    private boolean getOuput;

    public boolean getGetOuput() {
        return getOuput;
    }

    public String getJobURI() {
        return jobURI;
    }

    public void setGetOuput(boolean getOuput) {
        this.getOuput = getOuput;
    }

    public void setJobURI(String jobURI) {
        this.jobURI = jobURI;
    }
}
