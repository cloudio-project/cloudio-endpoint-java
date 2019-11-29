package ch.hevs.cloudio.endpoint;

class Transaction {
    private final NamedItemSet<CloudioAttribute.InternalAttribute> attributes = new NamedItemSet<>();

    public NamedItemSet<CloudioAttribute.InternalAttribute> getAttributes() {
        return attributes;
    }

    public void addAttribute(CloudioAttribute.InternalAttribute attribute) {
        try {
            attributes.addItem(attribute);
        } catch (DuplicateNamedItemException ignored) {}
    }

    public void clearAttributes(){
        attributes.clear();
    }
}
