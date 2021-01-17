package com.meghdut.text;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class InvertedIndex implements TextSearchIndex
{

    private final Corpus corpus;
    private Map<String, ParsedDocumentCollection> termToPostings;
    private Map<ParsedDocument, ParsedDocumentMetrics> docToMetrics;
    private final DocumentParser searchTermParser;

    public InvertedIndex(Corpus corpus)
    {
        this.corpus = corpus;
        searchTermParser = new DefaultDocumentParser();
        init();
    }

    private void init()
    {
        termToPostings = new HashMap<>();
        for (ParsedDocument document : corpus.getParsedDocuments()) {
            document.getDocumentTokens().stream()
                    .map(DocumentToken::getToken)
                    .forEach(word -> termToPostings
                            .computeIfAbsent(word, ParsedDocumentCollection::new).addPosting(document));
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

    /**
     * This narrows down the search to Document which contains the words in the searchDoc
     * @param searchDoc the document whose contents we are looking for
     * @return a Set of Parsed Documents which are relevant to the searchDoc
     */
    private Set<ParsedDocument> getRelevantDocuments(ParsedDocument searchDoc)
    {
        return searchDoc.getUniqueTokens().stream()
                .filter(word -> termToPostings.containsKey(word))
                .flatMap(word -> termToPostings.get(word).getUniqueDocuments().stream())
                .collect(Collectors.toSet());

    }

    /**
     * @param searchTerm the raw text which will be used for searching
     * @return a list of
     */
    @Override
    public List<SearchResult> search(String searchTerm)
    {

        ParsedDocument searchDocument = searchTermParser.parse(new Document(searchTerm, System.currentTimeMillis()));
        Set<ParsedDocument> documentsToScanSet = getRelevantDocuments(searchDocument);

        if (searchDocument.isEmpty() || documentsToScanSet.isEmpty()) {
            // No relevant documents found. Or the search Document docent contain any valid tokens
            return new ArrayList<>();
        }
        final ParsedDocumentMetrics searchDocumentMetrics = new ParsedDocumentMetrics(corpus, searchDocument, termToPostings);


        List<SearchResult> results = documentsToScanSet
                .parallelStream()
                .map(doc -> getSearchResult(searchDocumentMetrics, doc))
                .sorted()
                .collect(Collectors.toList());
        return results;
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


    /**
     * @param searchDocMetrics the document whose content we are searching
     * @param targetDoc the target document
     * @return the cosine similarity of the two documents
     */
    private double computeCosine(ParsedDocumentMetrics searchDocMetrics, ParsedDocument targetDoc)
    {
        double cosine;

        Set<String> wordSet = searchDocMetrics.getDocument().getUniqueTokens();
        if (targetDoc.getUniqueTokens().size() < wordSet.size()) {
            wordSet = targetDoc.getUniqueTokens();
        }

        cosine = wordSet.stream()
                .mapToDouble(word -> ((searchDocMetrics.getTfidf(word)) / searchDocMetrics.getMagnitude()) *
                        ((docToMetrics.get(targetDoc).getTfidf(word)) / docToMetrics.get(targetDoc).getMagnitude()))
                .filter(term -> !Double.isNaN(term)).sum();

        return cosine;
    }

}