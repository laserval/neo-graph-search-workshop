package com.findwise.neo;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;

@NodeEntity
public class Subject {

    @GraphId
    Long id;

    String name;

    public Subject() {
    }

    public Subject(String name) {
        this.name = name;
    }
}
