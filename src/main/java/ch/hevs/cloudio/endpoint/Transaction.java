package ch.hevs.cloudio.endpoint;

import java.util.List;

class Transaction {
    private final UniqueItemSet<CloudioAttribute.InternalAttribute> attributes = new UniqueItemSet<>();

    public synchronized List<CloudioAttribute.InternalAttribute> getAttributes() {
        return attributes.toList();
    }

    public synchronized void addAttribute(CloudioAttribute.InternalAttribute attribute) {
        try {
            attributes.addItem(attribute);
        } catch (DuplicateItemException ignored) {}
    }

    public synchronized void clearAttributes(){
        attributes.clear();
    }
}
