package ch.hevs.cloudio.endpoint;

import java.util.Set;

public interface CloudioPersistence {

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
     * Get data of update message from the persistence database
     *
     * @param key key reference of the update message to get
     * @return update message from persistence database
     */
    byte[] getPersistentUpdate(String key);

    /**
     *  Get data of log message from the persistence database
     *
     * @param key key reference of the log message to get
     * @return log message from persistence database
     */
    byte[] getPersistentLog(String key);

    /**
     * Remove data of update message according to its key from the persistence database
     *
     * @param key key reference of the update message to remove
     */
    void removePersistentUpdate(String key);

    /**
     * Remove data of log message according to its key from the persistence database
     *
     * @param key key reference of the log message to remove
     */
    void removePersistentLog(String key);

    /**
     * Get the set of the keys of the update messages stored in the persistence database
     *
     * @return Set of keys
     */
    Set<String> getPersistentUpdateKeySet();

    /**
     * Get the set of the keys of the log messages stored in the persistence database
     *
     * @return Set of keys
     */
    Set<String>  getPersistentLogKeySet();

    /**
     * Remove data of update message according to its key from the persistence database
     *
     * @param key key reference of the update message to remove
     */

    /**
     * Add data of update message to the persistence database
     *
     * @param key key reference of the update message to add
     * @param data data of the update message
     */
    void addPersistentUpdate(String key, byte[] data);

    /**
     * Add data of log message to the persistence database
     *
     * @param key key reference of the log message to add
     * @param data data of the log message
     */
    void addPersistentLog(String key, byte[] data);
}
