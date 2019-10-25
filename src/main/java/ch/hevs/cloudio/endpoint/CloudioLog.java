package ch.hevs.cloudio.endpoint;

class CloudioLog{

    private String level;
    private Double timestamp ;
    private String message;
    private String loggerName;
    private String logSource;

    public CloudioLog(String level, Double timestamp, String message, String loggerName, String logSource){
        this.level = level;
        this.timestamp = timestamp;
        this.message = message;
        this.loggerName = loggerName;
        this.logSource = logSource;
    }

    public Double getTimestamp() {
        return timestamp;
    }

    public String getLevel() {
        return level;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public String getLogSource() {
        return logSource;
    }

    public String getMessage() {
        return message;
    }
}