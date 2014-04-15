package com.findwise.neo;

import org.springframework.data.neo4j.repository.GraphRepository;

public interface AuthorRepository extends GraphRepository<Author> {

    Author findByName(String name);

    Iterable<Author> findByDocumentsTitle(String title);
}