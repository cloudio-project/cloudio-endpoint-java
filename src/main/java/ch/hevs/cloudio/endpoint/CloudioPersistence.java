package ch.hevs.cloudio.endpoint;

import java.util.Calendar;

public interface CloudioPersistence {

    static class Message {
        public long timestamp;
        public String topic;
        public byte[] data;

        public Message(long timestamp, String topic, byte[] data) {
            this.timestamp = timestamp;
            this.topic = topic;
            this.data = data;
        }

        public Message(String topic, byte[] data) {
            this.timestamp = Calendar.getInstance().getTimeInMillis();
            this.topic = topic;
            this.data = data;
        }
    }

    /**
     * Open the persistence database
     */
    void open();

    /**
     * Close the persistence database
     */
    void close();

    /**
     * Set a property in the persistence database
     *
     * @param key key reference of the property to set
     * @param value value of the property to set
     */
    void setPersistentProperty(String key, Object value);

    /**
     * Get a property from the persistence database
     *
     * @param key key reference of the property to get
     * @param defaultValue default value to return
     * @return property from persistence database
     */
    Object getPersistentProperty(String key, Object defaultValue);

    /**
     * Store a pending message to the corresponding category
     *
     * @param category Message category to store
     * @param persistenceLimit Limit of message to be stored in the category
     * @param message message to store
     */
    void storeMessage(String category, int persistenceLimit, Message message);

    /**
     * Return the last/first pending message from the category
     *
     * @param category Message category to retrieve pending message
     * @return
     */
    Message getPendingMessage(String category);

    /**
     * Remove the last/first pending message from the category
     *
     * @param category Message category to remove pending message
     */
    void removePendingMessage(String category);

    /**
     * Count the amount of pending message stored in the category
     *
     * @param category Message category to count pending messages
     * @return amount of pending messages in category
     */
    long messageCount(String category);
}
