package com.meghdut.text;

import java.util.List;

public interface TextSearchIndex
{
    List<SearchResult> search(String searchTerm);

    int numDocuments();

    int termCount();
}
