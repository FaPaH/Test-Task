package com.fapah;

import com.fapah.manager.DocumentManager;

import java.util.List;
import java.util.Optional;

public class Main {

    public static void main(String[] args) {
        DocumentManager manager = new DocumentManager();

        DocumentManager.Author author = DocumentManager.Author.builder()
                .id("1")
                .name("John Doe")
                .build();

        DocumentManager.Document doc1 = DocumentManager.Document.builder()
                .title("Test Document")
                .content("This is a test")
                .author(author)
                .build();

        DocumentManager.Document doc2 = DocumentManager.Document.builder()
                .title("Document ABoba")
                .content("This is a second doc")
                .author(author)
                .build();

        DocumentManager.Document savedDoc1 = manager.save(doc1);
        System.out.println("Saved Document: " + savedDoc1);
        DocumentManager.Document savedDoc2 = manager.save(doc2);
        System.out.println("Saved Document: " + savedDoc2);

        Optional<DocumentManager.Document> foundDoc = manager.findById(savedDoc1.getId());
        System.out.println("Found Document: " + foundDoc.orElse(null));

        DocumentManager.SearchRequest request = DocumentManager.SearchRequest.builder()
                .containsContents(List.of("second", "test"))
                .build();

        List<DocumentManager.Document> searchResults = manager.search(request);
        System.out.println("Search Results: " + searchResults);
    }
}