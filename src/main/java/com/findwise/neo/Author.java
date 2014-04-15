package com.findwise.neo;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.HashSet;
import java.util.Set;

@NodeEntity
public class Author {

    @GraphId
    Long id;

    String name;

    @RelatedTo(type = "HAS_WRITTEN", direction = Direction.OUTGOING)
    Set<Document> documents;

    public Author() {
    }

    public Author(String name) {
        this.name = name;
    }

    public void hasWritten(Document document) {
        if (documents == null) {
            documents = new HashSet<Document>();
        }
        documents.add(document);
    }

    @Override
    public String toString() {
        return "Author{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", documents=" + documents +
                '}';
    }
}
