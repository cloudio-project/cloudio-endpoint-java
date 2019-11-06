package ch.hevs.cloudio.endpoint;

public class CloudioPersistentData {

    private String level;

    public CloudioPersistentData(String level) {
        this.level = level;
    }

    public CloudioPersistentData() {
        this.level = "DEBUG";
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
