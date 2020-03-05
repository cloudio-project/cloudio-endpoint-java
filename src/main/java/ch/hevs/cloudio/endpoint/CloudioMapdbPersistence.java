package ch.hevs.cloudio.endpoint;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public class CloudioMapdbPersistence implements CloudioPersistence{

    private String PERSISTENCE_FILE;
    private String PERSISTENCE_NAME;

    private DB dbPersistenceData;
    private ConcurrentMap propertyMap;

    private boolean persistenceQueueFifo;

    CloudioMapdbPersistence(String PERSISTENCE_FILE, String PERSISTENCE_NAME, boolean persistenceQueueFifo){
        this.PERSISTENCE_FILE = PERSISTENCE_FILE;
        this.PERSISTENCE_NAME = PERSISTENCE_NAME;
        this.persistenceQueueFifo = persistenceQueueFifo;

    }

    @Override
    public void open(){
        dbPersistenceData = DBMaker.fileDB(PERSISTENCE_FILE).transactionEnable().make();
        propertyMap = dbPersistenceData.hashMap(PERSISTENCE_NAME).createOrOpen();
    }

    @Override
    public void close(){
        dbPersistenceData.close();
    }

    @Override
    public synchronized  void setPersistentProperty(String key, Object value){
        propertyMap.put(key, value);
        dbPersistenceData.commit();
    }

    @Override
    public synchronized Object getPersistentProperty(String key, Object defaultValue){
        return propertyMap.getOrDefault(key, defaultValue);
    }

    @Override
    public synchronized void storeMessage(String category, int persistenceLimit, Message message) {
        if(persistenceLimit>0) {
            ConcurrentMap map = dbPersistenceData.treeMap(category).createOrOpen();

            map.put(message.timestamp +" "+ message.topic, message.data);

            if (map.size() > persistenceLimit) {
                Iterator<String> keysItr = map.keySet().iterator();

                while (keysItr.hasNext() && map.size() > persistenceLimit) {
                    map.remove(keysItr.next());
                }
            }
            dbPersistenceData.commit();
        }
    }

    @Override
    public synchronized Message getPendingMessage(String category) {
        ConcurrentMap map = dbPersistenceData.treeMap(category).createOrOpen();
        Set<String> keys = map.keySet();

        String key;
        if(persistenceQueueFifo)
            key = (String) keys.toArray()[0];
        else
            key = (String) keys.toArray()[keys.size()-1];

        byte[] data = (byte[]) map.get(key);

        String[] splitKey = key.split(" ");

        long timestamp = Long.parseLong(splitKey[0]);
        String topic = splitKey[1];

        return new Message(timestamp, topic, data);
    }

    @Override
    public synchronized void removePendingMessage(String category) {
        ConcurrentMap map = dbPersistenceData.treeMap(category).createOrOpen();
        Set<String> keys = map.keySet();

        String key;
        if(persistenceQueueFifo)
            key = (String) keys.toArray()[0];
        else
            key = (String) keys.toArray()[keys.size()-1];

        map.remove(key);
        dbPersistenceData.commit();
    }

    @Override
    public long messageCount(String category) {
        return dbPersistenceData.treeMap(category).createOrOpen().size();
    }
}
