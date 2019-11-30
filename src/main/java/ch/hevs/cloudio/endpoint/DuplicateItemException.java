package ch.hevs.cloudio.endpoint;

/**
 * This exception is thrown if an item (object) that disposes a name or UUID is added to a set of those items and there is
 * already an item with the same name or UUID in the set present.
 */
public class DuplicateItemException extends Exception {
    /**
     * Creates a new exception with the given message.
     *
     * @param name   The duplicate name.
     */
    DuplicateItemException(String name) {
        super("Can not add item, an item with the local name \"" + name + "\" exists already!");
    }

    /**
     * Creates a new exception with the given message.
     *
     * @param uuid   The duplicate UUID.
     */
    DuplicateItemException(Uuid uuid) {
        super("Can not add item, an item with the UUID \"" + uuid.toString() + "\" exists already!");
    }
}
