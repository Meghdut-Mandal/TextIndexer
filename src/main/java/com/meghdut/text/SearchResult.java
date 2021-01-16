package com.meghdut.text;

/**
 * Created by brad on 6/7/15.
 */
public class SearchResult
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
}
