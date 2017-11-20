package ch.hevs.cloudio.endpoint;

/**
 * An object possessing a name.
 */
interface NamedItem {
    /**
     * Returns the name of the item.
     *
     * @return  Name of item.
     */
    String getName();

    /**
     * Sets the local name of the item. Note that items can not be renamed, so this method should throw a runtime
     * exception if someone tries to rename the item.
     *
     * @param name  The name to identify the item locally.
     */
    void setName(String name);
}
