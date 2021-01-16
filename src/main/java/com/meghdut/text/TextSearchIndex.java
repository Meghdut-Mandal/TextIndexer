package com.meghdut.text;

public interface TextSearchIndex
{
    SearchResultBatch search(String searchTerm, int maxResults);

    int numDocuments();

    int termCount();
}
