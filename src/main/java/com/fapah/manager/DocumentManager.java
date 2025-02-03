package com.fapah.manager;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
@Slf4j
public class DocumentManager {

    private Map<String, Document> documentsBase = new HashMap<>();

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {

        if (document == null
                || document.getTitle() == null
                || document.getContent() == null
                || document.getAuthor() == null) {
            log.error("Invalid document data provided");
            throw new IllegalArgumentException("Document, title, content, and author must not be null");
        }

        if (document.getId() == null) {
            String newId = UUID.randomUUID().toString();
            document = Document.builder()
                    .id(newId)
                    .title(document.getTitle())
                    .content(document.getContent())
                    .author(document.getAuthor())
                    .created(Instant.now())
                    .build();
            log.info("New document created with ID: {}", newId);
        }

        documentsBase.put(document.getId(), document);

        return document;
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {

        if (request == null) {
            log.error("Search request is null");
            throw new IllegalArgumentException("Search request must not be null");
        }

        log.info("Performing search with request: {}", request);
        List<Document> results = documentsBase.values().stream()
                .filter(doc -> request.getTitlePrefixes() == null || request.getTitlePrefixes().stream().anyMatch(doc.getTitle()::startsWith))
                .filter(doc -> request.getContainsContents() == null || request.getContainsContents().stream().anyMatch(doc.getContent()::contains))
                .filter(doc -> request.getAuthorIds() == null || request.getAuthorIds().contains(doc.getAuthor().getId()))
                .filter(doc -> request.getCreatedFrom() == null || doc.getCreated().isAfter(request.getCreatedFrom()))
                .filter(doc -> request.getCreatedTo() == null || doc.getCreated().isBefore(request.getCreatedTo()))
                .collect(Collectors.toList());
        log.info("Search completed. Found {} documents", results.size());

        return results;
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {

        if (id == null || id.isBlank()) {
            log.error("Invalid document ID provided");
            throw new IllegalArgumentException("Document ID must not be null or blank");
        }

        return Optional.ofNullable(documentsBase.get(id));
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}