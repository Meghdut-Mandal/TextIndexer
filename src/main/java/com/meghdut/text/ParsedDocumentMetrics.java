package com.meghdut.text;


import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ParsedDocumentMetrics
{
    private Corpus corpus;
    private ParsedDocument document;
    private Map<String, ParsedDocumentCollection> termsToPostings;

    private Double magnitude;
    private Map<String, Double> tfidfCache;

    public ParsedDocumentMetrics(Corpus corpus, ParsedDocument document,
                                 Map<String, ParsedDocumentCollection> termsToPostings)
    {
        this.corpus = corpus;
        this.document = document;
        this.termsToPostings = termsToPostings;
        this.tfidfCache = getTfidfCache(document);
        getMagnitude();
    }

    @NotNull
    private ConcurrentMap<String, Double> getTfidfCache(@NotNull ParsedDocument document)
    {
        return document.getUniqueTokens()
                .parallelStream()
                .collect(Collectors.toConcurrentMap(Function.identity(), this::calcTfidf));
    }

    public double getTfidf(String word)
    {
        Double retVal = tfidfCache.get(word);
        if (retVal == null) {
            return 0;
        }

        return retVal;
    }

    public double getMagnitude()
    {
        if (magnitude == null) {
            double sumOfSquares = document.getUniqueTokens()
                    .stream()
                    .mapToDouble(this::getTfidf)
                    .map(d -> d * d).sum();

            magnitude = Math.sqrt(sumOfSquares);
        }

        return magnitude;
    }

    public ParsedDocument getDocument()
    {
        return this.document;
    }

    private double calcTfidf(String word)
    {
        int wordFreq = document.getTokenFrequency(word);
        if (wordFreq == 0) {
            return 0;
        }
        return getInverseDocumentFrequency(word) * document.getTokenFrequency(word);
    }

    private double getInverseDocumentFrequency(String word)
    {
        double totalNumDocuments = corpus.size();
        double numDocsWithTerm = numDocumentsTermIsIn(word);
        return Math.log10((totalNumDocuments) / (1 + numDocsWithTerm));
    }

    private int numDocumentsTermIsIn(String term)
    {
        if (!termsToPostings.containsKey(term)) {
            return 0;
        }

        return termsToPostings.get(term).getUniqueDocuments().size();
    }

}
