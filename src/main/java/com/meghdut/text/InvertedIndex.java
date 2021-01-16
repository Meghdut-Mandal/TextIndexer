package com.meghdut.text;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class InvertedIndex implements TextSearchIndex
{

    private Corpus corpus;
    private Map<String, ParsedDocumentCollection> termToPostings;
    private Map<ParsedDocument, ParsedDocumentMetrics> docToMetrics;
    private DocumentParser searchTermParser;

    public InvertedIndex(Corpus corpus)
    {
        this.corpus = corpus;
        init();
        searchTermParser = new DefaultDocumentParser();
    }

    private void init()
    {
        termToPostings = new HashMap<>();
        for (ParsedDocument document : corpus.getParsedDocuments()) {
            for (DocumentToken documentToken : document.getDocumentTokens()) {
                final String word = documentToken.getToken();
                termToPostings.computeIfAbsent(word, ParsedDocumentCollection::new).addPosting(document);
            }
        }


        docToMetrics = getDocToMetrics();
    }

    @NotNull
    private Map<ParsedDocument, ParsedDocumentMetrics> getDocToMetrics()
    {
        return corpus.getParsedDocuments().stream()
                .collect(Collectors.toMap(document -> document, document ->
                        new ParsedDocumentMetrics(corpus, document, termToPostings), (a, b) -> b));
    }

    public int numDocuments()
    {
        return corpus.size();
    }

    public int termCount()
    {
        return termToPostings.keySet().size();
    }

    private Set<ParsedDocument> getRelevantDocuments(ParsedDocument searchDoc)
    {

        Set<ParsedDocument> retVal = new HashSet<>();
        for (String word : searchDoc.getUniqueTokens()) {
            if (termToPostings.containsKey(word)) {
                retVal.addAll(termToPostings.get(word).getUniqueDocuments());
            }
        }

        return retVal;
    }

    @Override
    public List<SearchResult> search(String searchTerm, int maxResults)
    {

        ParsedDocument searchDocument = searchTermParser.parse(new Document(searchTerm, System.currentTimeMillis()));
        Set<ParsedDocument> documentsToScanSet = getRelevantDocuments(searchDocument);

        if (searchDocument.isEmpty() || documentsToScanSet.isEmpty()) {

            return new ArrayList<>();
        }
        final ParsedDocumentMetrics searchDocumentMetrics = new ParsedDocumentMetrics(corpus, searchDocument, termToPostings);
        List<SearchResult> resultsP = documentsToScanSet
                .parallelStream()
                .map(doc -> getSearchResult(searchDocumentMetrics, doc))
                .sorted()
                .collect(Collectors.toList());


        return (resultsP);
    }

    @NotNull
    private SearchResult getSearchResult(ParsedDocumentMetrics searchDocumentMetrics, ParsedDocument doc)
    {
        double cosine = computeCosine(searchDocumentMetrics, doc);
        SearchResult result = new SearchResult();
        result.setRelevanceScore(cosine);
        result.setUniqueIdentifier(doc.getId());
        return result;
    }


    private double computeCosine(ParsedDocumentMetrics searchDocMetrics, ParsedDocument d2)
    {
        double cosine;

        Set<String> wordSet = searchDocMetrics.getDocument().getUniqueTokens();
        if (d2.getUniqueTokens().size() < wordSet.size()) {
            wordSet = d2.getUniqueTokens();
        }

        cosine = wordSet.stream()
                .mapToDouble(word ->
                      ((searchDocMetrics.getTfidf(word)) / searchDocMetrics.getMagnitude()) *
                ((docToMetrics.get(d2).getTfidf(word)) / docToMetrics.get(d2).getMagnitude()))
                .filter(term -> !Double.isNaN(term)).sum();

        return cosine;
    }


    public Map<String, ParsedDocumentCollection> getTermToPostings()
    {
        return termToPostings;
    }
}