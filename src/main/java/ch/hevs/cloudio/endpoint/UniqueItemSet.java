package ch.hevs.cloudio.endpoint;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A NamedItemSet can contain a set of objects that implement the {@link NamedItem} interface. It guarantees that the
 * names of all members are unique within the set.
 */
class UniqueItemSet<T extends UniqueIdentifiable> implements Iterable<T> {
    private final List<T> items = new LinkedList<T>();

    /**
     * Returns the item with the given UUID or null if no such item is in the set.
     *
     * @param uuid  UUID of the item to search for.
     * @return      Item or null if no item with the given name is part of the set.
     */
    public T getItem(Uuid uuid) {
        for (T item: items) {
            if (item.getUuid().equals(uuid))
                return item;
        }
        return null;
    }

    /**
     * Adds the given item to the set. Note that if an item with the same UUID already exists in the set, an
     * {@link DuplicateItemException} is thrown.
     *
     * @param item                     Item to add to the set.
     * @throws DuplicateItemException  If an item with the same name already exists in the set.
     */
    public void addItem(T item) throws DuplicateItemException {
        if (item != null) {
            if (getItem(item.getUuid()) == null) {
                items.add(item);
            } else {
                throw new DuplicateItemException(item.getUuid());
            }
        }
    }

    /**
     * Removes the given item from the set.
     *
     * @param item  Item to remove.
     */
    public void removeItem(T item) {
        if (item != null) {
            items.remove(item);
        }
    }

    /**
     * Removes the item with the given UUID from the set.
     *
     * @param uuid  UUID of the item to remove.
     */
    public void removeItem(Uuid uuid) {
        for (T item: items) {
            if (item.getUuid().equals(uuid)) {
                removeItem(item);
            }
        }
    }

    /**
     * Removes all items from the set.
     */
    public void clear() {
        items.clear();
    }

    /**
     * Returns true if the set is empty.
     *
     * @return  True if empty, false otherwise.
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Returns true if the given item is part of the set, false otherwise.
     *
     * @param item  Item to check if it is part of the set.
     * @return      True if the given object is part of the set, false otherwise.
     */
    public boolean contains(T item) {
        for (T actualItem : items) {
            if (actualItem.equals(item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the list of objects inside the set. Note that the reference to the list used by the implementation is
     * returned, so do not modify it.
     *
     * @return  List of items in the set.
     */
    public List<T> toList() {
        return items;
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return items.iterator();
    }
}
