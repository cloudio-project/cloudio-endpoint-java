package ch.hevs.cloudio.endpoint;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public class CloudioMapdbPersistence implements CloudioPersistence{

    private String PERSISTENCE_FILE;
    private String PERSISTENCE_MAP_NAME;
    private String PERSISTENCE_MAP_MQTT_UPDATE;
    private String PERSISTENCE_MAP_MQTT_LOG;

    private DB dbPersistenceData;
    private ConcurrentMap propertyMap;
    private ConcurrentMap mqttUpdateMap;
    private ConcurrentMap mqttLogMap;
    private int updatePersistenceLimit;
    private int logPersistenceLimit;

    CloudioMapdbPersistence(String PERSISTENCE_FILE, String PERSISTENCE_MAP_NAME, String PERSISTENCE_MAP_MQTT_UPDATE,
                            String PERSISTENCE_MAP_MQTT_LOG, int updatePersistenceLimit, int logPersistenceLimit){
        this.PERSISTENCE_FILE = PERSISTENCE_FILE;
        this.PERSISTENCE_MAP_NAME = PERSISTENCE_MAP_NAME;
        this.PERSISTENCE_MAP_MQTT_UPDATE = PERSISTENCE_MAP_MQTT_UPDATE;
        this.PERSISTENCE_MAP_MQTT_LOG = PERSISTENCE_MAP_MQTT_LOG;
        this.updatePersistenceLimit = updatePersistenceLimit;
        this.logPersistenceLimit = logPersistenceLimit;

    }

    @Override
    public void openDatabase(){
        dbPersistenceData = DBMaker.fileDB(PERSISTENCE_FILE).transactionEnable().make();
        propertyMap = dbPersistenceData.hashMap(PERSISTENCE_MAP_NAME).createOrOpen();
        mqttUpdateMap = dbPersistenceData.treeMap(PERSISTENCE_MAP_MQTT_UPDATE).createOrOpen();
        mqttLogMap = dbPersistenceData.hashMap(PERSISTENCE_MAP_MQTT_LOG).createOrOpen();
    }


    @Override
    public void closeDatabase(){
        dbPersistenceData.close();
    }

    @Override
    public synchronized  void setPersistentProperty(String key, Object value){
        propertyMap.put(key, value);
        dbPersistenceData.commit();
    }

    @Override
    public synchronized byte[] getPersistentUpdate(String key) {
        return (byte[]) mqttUpdateMap.get(key);
    }

    @Override
    public synchronized byte[] getPersistentLog(String key) {
        return (byte[]) mqttLogMap.get(key);
    }

    @Override
    public synchronized void removePersistentUpdate(String key) {
        mqttUpdateMap.remove(key);
        dbPersistenceData.commit();
    }

    @Override
    public synchronized void removePersistentLog(String key) {
        mqttLogMap.remove(key);
        dbPersistenceData.commit();
    }

    @Override
    public Set<String> getPersistentUpdateKeySet() {
        return mqttUpdateMap.keySet();
    }

    @Override
    public Set<String> getPersistentLogKeySet() {
        return mqttLogMap.keySet();
    }

    @Override
    public synchronized Object getPersistentProperty(String key, Object defaultValue){
        return propertyMap.getOrDefault(key, defaultValue);
    }

    @Override
    public synchronized void addPersistentUpdate(String key, byte[] data){
        addPersistentLogMessage(key, mqttUpdateMap, updatePersistenceLimit, data);
    }

    @Override
    public synchronized void addPersistentLog(String key, byte[] data){
        addPersistentLogMessage(key, mqttLogMap, logPersistenceLimit, data);
    }

    private void addPersistentLogMessage(String key, ConcurrentMap map, int persistenceLimit, byte[] data){
        if(persistenceLimit>0) {
            synchronized (dbPersistenceData) {

                map.put(key, data);

                if (map.size() > persistenceLimit) {
                    Iterator<String> keysItr = map.keySet().iterator();

                    while (keysItr.hasNext() && map.size() > persistenceLimit) {
                        map.remove(keysItr.next());
                    }
                }
                dbPersistenceData.commit();
            }
        }
    }
}
