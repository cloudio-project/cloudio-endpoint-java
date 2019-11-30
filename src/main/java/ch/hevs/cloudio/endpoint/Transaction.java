package ch.hevs.cloudio.endpoint;

import java.util.List;

class Transaction {
    private final UniqueItemSet<CloudioAttribute.InternalAttribute> attributes = new UniqueItemSet<>();

    public List<CloudioAttribute.InternalAttribute> getAttributes() {
        return attributes.toList();
    }

    public void addAttribute(CloudioAttribute.InternalAttribute attribute) {
        try {
            attributes.addItem(attribute);
        } catch (DuplicateItemException ignored) {}
    }

    public void clearAttributes(){
        attributes.clear();
    }
}
