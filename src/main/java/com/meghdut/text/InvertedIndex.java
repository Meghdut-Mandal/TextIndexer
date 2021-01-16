package com.meghdut.text;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.MinMaxPriorityQueue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class InvertedIndex implements TextSearchIndex
{

    private static int THREAD_POOL_SIZE = Math.max(1, Runtime.getRuntime().availableProcessors());

    private Corpus corpus;
    private Map<String, ParsedDocumentCollection> termToPostings;
    private Map<ParsedDocument, ParsedDocumentMetrics> docToMetrics;
    private ExecutorService executorService;
    private DocumentParser searchTermParser;

    public InvertedIndex(Corpus corpus)
    {
        this.corpus = corpus;
        init();
        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        searchTermParser = new DefaultDocumentParser();
    }

    private void init()
    {
        Map<String, ParsedDocumentCollection> termToPostingsMap = new HashMap<>();
        for (ParsedDocument document : corpus.getParsedDocuments()) {
            for (DocumentToken documentToken : document.getDocumentTokens()) {
                final String word = documentToken.getToken();
                if (!termToPostingsMap.containsKey(word)) {
                    termToPostingsMap.put(word, new ParsedDocumentCollection(word));
                }
                termToPostingsMap.get(word).addPosting(document);
            }
        }

        termToPostings = ImmutableMap.copyOf(termToPostingsMap);

        //init metrics cache
        Map<ParsedDocument, ParsedDocumentMetrics> metricsMap = new HashMap<>();
        for (ParsedDocument document : corpus.getParsedDocuments()) {
            metricsMap.put(document, new ParsedDocumentMetrics(corpus, document, termToPostings));
        }
        docToMetrics = ImmutableMap.copyOf(metricsMap);
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
    public SearchResultBatch search(String searchTerm, int maxResults)
    {

        ParsedDocument searchDocument = searchTermParser.parse(new Document(searchTerm, System.currentTimeMillis()));
        Set<ParsedDocument> documentsToScanSet = getRelevantDocuments(searchDocument);

        if (searchDocument.isEmpty() || documentsToScanSet.isEmpty()) {
            return buildResultBatch(new ArrayList<>());
        }

        // do scan
        final Collection<SearchResult> resultsP = new ConcurrentLinkedQueue<>();

        List<ParsedDocument> documentsToScan = new ArrayList<>(documentsToScanSet);
        final ParsedDocumentMetrics pdm = new ParsedDocumentMetrics(corpus, searchDocument, termToPostings);
        List<Future> futures = new ArrayList<>();

        for (final List<ParsedDocument> partition : Lists.partition(documentsToScan, THREAD_POOL_SIZE)) {

            Future future = executorService.submit(new Runnable()
            {
                @Override
                public void run()
                {
                    for (ParsedDocument doc : partition) {
                        double cosine = computeCosine(pdm, doc);

                        SearchResult result = new SearchResult();
                        result.setRelevanceScore(cosine);
                        result.setUniqueIdentifier(doc.getId());
                        resultsP.add(result);
                    }
                }
            });

            futures.add(future);
        }

        for (Future f : futures) {
            try{
                f.get();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        int heapSize = Math.min(resultsP.size(), maxResults);

        MinMaxPriorityQueue<SearchResult> maxHeap = MinMaxPriorityQueue.
                orderedBy((Comparator<SearchResult>) (o1, o2) -> {
                    if (o1.getRelevanceScore() <= o2.getRelevanceScore()) {
                        return 1;
                    } else {
                        return -1;
                    }
                }).
                maximumSize(heapSize).
                expectedSize(heapSize).create(resultsP);


        // return results
        ArrayList<SearchResult> r = new ArrayList<>();
        while (!maxHeap.isEmpty()) {
            SearchResult rs = maxHeap.removeFirst();
            r.add(rs);
        }

        return buildResultBatch(r);
    }

    private SearchResultBatch buildResultBatch(List<SearchResult> results)
    {

        return new SearchResultBatch(results);
    }


    private double computeCosine(ParsedDocumentMetrics searchDocMetrics, ParsedDocument d2)
    {
        double cosine = 0;

        Set<String> wordSet = searchDocMetrics.getDocument().getUniqueTokens();
        if (d2.getUniqueTokens().size() < wordSet.size()) {
            wordSet = d2.getUniqueTokens();
        }

        for (String word : wordSet) {

            double term = ((searchDocMetrics.getTfidf(word)) / searchDocMetrics.getMagnitude()) *
                    ((docToMetrics.get(d2).getTfidf(word)) / docToMetrics.get(d2).getMagnitude());
            if (!Double.isNaN(term)) {
                cosine = cosine + term;
            }
        }

        return cosine;
    }


    public Map<String, ParsedDocumentCollection> getTermToPostings()
    {
        return termToPostings;
    }
}