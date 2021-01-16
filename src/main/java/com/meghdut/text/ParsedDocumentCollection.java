package com.meghdut.text;

import java.util.HashSet;

public class ParsedDocumentCollection
{

    private String word;
    private HashSet<ParsedDocument> uniqueDocuments;

    public ParsedDocumentCollection(String word) {
        this.word = word;
        this.uniqueDocuments = new HashSet<>();
    }

    public void addPosting( ParsedDocument doc) {
        uniqueDocuments.add(doc);
    }

    public HashSet<ParsedDocument> getUniqueDocuments() {
        return uniqueDocuments;
    }
}
