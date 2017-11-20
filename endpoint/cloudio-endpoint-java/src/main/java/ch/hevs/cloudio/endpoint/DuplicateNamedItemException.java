package ch.hevs.cloudio.endpoint;

/**
 * This exception is thrown if an item (object) that disposes a name is added to a set of those items and there is
 * already an item with the same name in the set present.
 */
public class DuplicateNamedItemException extends Exception {
    /**
     * Creates a new exception with the given message.
     *
     * @param name   The duplicate name.
     */
    DuplicateNamedItemException(String name) {
        super("Can not add item, an item with the local name \"" + name + "\" exists already!");
    }
}
