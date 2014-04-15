package com.findwise.neo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.client.HttpClient;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.core.GraphDatabase;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.data.neo4j.rest.SpringRestGraphDatabase;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableNeo4jRepositories()
public class App extends Neo4jConfiguration implements CommandLineRunner {

    @Bean
    GraphDatabaseService graphDatabaseService() {
        return new SpringRestGraphDatabase("http://localhost:7474/db/data");
    }

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    GraphDatabase graphDatabase;

    public App() {
        setBasePackage("com.findwise.neo");
    }

    public void run(String... args) throws Exception {
        index();
        search();
    }

    private void index() throws URISyntaxException, IOException {
        Author greg = new Author("Greg");
        Author roy = new Author("Roy");
        Author craig = new Author("Craig");

        System.out.println("Before linking up with Neo4j...");
        for (Author person : new Author[]{greg, roy, craig}) {
            System.out.println(person);
        }

        Document fishingFast = new Document("Fishing Fast", "This is a book about fishing fast, getting results quickly, doing things expediently, performing in a short time");
        Document fishingSlow = new Document("Fishing Slow", "Fishing is often too fast, so we try to convince you to do it slowly, take your time, don't be hasty");

        Subject fishing = new Subject("Fishing");

        Transaction tx = graphDatabase.beginTx();
        try {

            greg.hasWritten(fishingFast);
            roy.hasWritten(fishingFast);
            craig.hasWritten(fishingSlow);
            roy.hasWritten(fishingSlow);

            authorRepository.save(greg);
            authorRepository.save(roy);
            authorRepository.save(craig);
            documentRepository.save(fishingFast);
            documentRepository.save(fishingSlow);
            tx.success();
        } finally {
            tx.finish();
        }

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        for (Document docToIndex : Arrays.asList(fishingFast, fishingSlow)) {
            Request.Put("http://localhost:9200/neo/document/" + docToIndex.title.replaceAll("\\s", "_"))
                    .bodyString(gson.toJson(docToIndex), ContentType.APPLICATION_JSON)
                    .execute();
        }
    }

    @SuppressWarnings("unchecked")
    private void search() throws IOException {

        String query = "{ \"query\": {\"query_string\" : {" +
                "\"default_field\" : \"title\"," +
                "\"query\" : \"slow\"" +
                "}}}";
        Response response = Request.Post("http://localhost:9200/neo/document/_search")
                .bodyString(query, ContentType.APPLICATION_JSON)
                .execute();
        Gson gson = new GsonBuilder().create();
        Map<String, Object> result = gson.fromJson(response.returnContent().asString(), Map.class);
        List<Object> hits = (List<Object>) ((Map<String, Object>)result.get("hits")).get("hits");
        for (Object hit : hits) {
            Map<String, Object> realhit = (Map<String,Object>)((Map<String, Object>)hit).get("_source");
            Iterable<Author> authors = authorRepository.findByDocumentsTitle(realhit.get("title").toString());
            for (Author author : authors) {
                System.out.println(author);
            }
        }
    }

    public static void main(String[] args) throws Exception {

        SpringApplication.run(App.class, args);
    }

}