package com.findwise.neo;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.springframework.data.neo4j.annotation.RelatedToVia;

import java.util.HashSet;
import java.util.Set;

@NodeEntity
public class Document {

    @GraphId Long id;

    String title;

    @RelatedTo(type = "HAS_WRITTEN", direction = Direction.INCOMING)
    Set<Author> authors;

    @Query("start document=node({self}) match document-->subject<--similar return similar")
    Iterable<Document> similarDocuments;

    private String content;

    public Document() {
    }

    public Document(String title, String content) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", authors=" + authors +
                ", similarDocuments=" + similarDocuments +
                '}';
    }
}
