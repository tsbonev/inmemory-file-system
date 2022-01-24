package com.tsbonev.filesys.domain;

public class File extends Node {
    private final String content;

    public File(Node parent, String name, String content) {
        super(parent, name);
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
