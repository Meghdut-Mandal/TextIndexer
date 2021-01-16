package com.meghdut.text;

import org.jetbrains.annotations.NotNull;

/**
 * Created by brad on 6/7/15.
 */
public class SearchResult implements Comparable<SearchResult>
{

    private double relevanceScore;
    private long uniqueIdentifier;

    public long getUniqueIdentifier()
    {
        return uniqueIdentifier;
    }

    public void setUniqueIdentifier(long uniqueIdentifier)
    {
        this.uniqueIdentifier = uniqueIdentifier;
    }

    public double getRelevanceScore()
    {
        return relevanceScore;
    }

    public void setRelevanceScore(double relevanceScore)
    {
        this.relevanceScore = relevanceScore;
    }

    @Override
    public int compareTo(@NotNull SearchResult o)
    {
        if (getRelevanceScore() <= o.getRelevanceScore()) {
            return 1;
        } else {
            return -1;
        }
    }
}
