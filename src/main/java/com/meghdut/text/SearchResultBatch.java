package com.meghdut.text;

import java.util.List;


public class SearchResultBatch
{
    private List<SearchResult> searchResults;

    public SearchResultBatch(List<SearchResult> searchResults)
    {
        this.searchResults = searchResults;
    }

    public List<SearchResult> getSearchResults()
    {
        return searchResults;
    }

    public void setSearchResults(List<SearchResult> searchResults)
    {
        this.searchResults = searchResults;
    }
}
