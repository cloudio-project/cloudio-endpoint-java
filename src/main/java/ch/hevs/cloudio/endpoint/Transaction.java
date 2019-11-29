package ch.hevs.cloudio.endpoint;

import java.util.LinkedList;
import java.util.List;

class Transaction {

    private final List<CloudioAttribute.InternalAttribute> attributes =
            new LinkedList<CloudioAttribute.InternalAttribute>();

    public List<CloudioAttribute.InternalAttribute> getAttributes() {
        return attributes;
    }

    public void addAttribute(CloudioAttribute.InternalAttribute attribute){
        attributes.add(attribute);
    }

    public void clearAttributes(){
        attributes.clear();
    }
}
