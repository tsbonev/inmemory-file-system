package com.tsbonev.filesys.domain;

import java.util.HashSet;
import java.util.Set;

public abstract class Node {
    private String name;
    private Node parent;
    private Set<Node> children;

    public Node(Node parent, String name) {
        this.parent = parent;
        this.name = name;

        this.children = new HashSet<>();

        if(parent != null) this.parent.children.add(this);
    }

    public String getName() {
        return name;
    }

    public Node getParent() {
        return parent;
    }

    public Set<Node> getChildren() {
        return children;
    }

    public boolean removeChild(Node child) {
        return this.children.remove(child);
    }
}
