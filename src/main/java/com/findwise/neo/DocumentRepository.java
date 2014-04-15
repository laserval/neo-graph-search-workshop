package com.findwise.neo;

import org.springframework.data.neo4j.repository.GraphRepository;

public interface DocumentRepository extends GraphRepository<Document> {

    Document findByTitle(String title);

    Iterable<Document> findByAuthorsName(String name);
}
