package com.meghdut.text;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class ParsedDocument
{
    private List<DocumentToken> documentTokens;
    private ConcurrentMap<String, Integer> tokenFrequencyMap;
    private Set<String> uniqueTokens;
    private long id;

    public ParsedDocument(@NotNull List<DocumentToken> documentTokens, long uniqueId)
    {
        this.id = uniqueId;
        this.documentTokens = documentTokens;
        this.tokenFrequencyMap = getTokenFrequencyMap(documentTokens);
        this.uniqueTokens = tokenFrequencyMap.keySet();
    }

    @NotNull
    private ConcurrentMap<String, Integer> getTokenFrequencyMap(@NotNull List<DocumentToken> documentTokens)
    {
        return documentTokens
                .parallelStream()
                .map(DocumentToken::getToken)
                .collect(Collectors.toConcurrentMap(w -> w, w -> 1, Integer::sum));
    }

    private boolean isEmpty()
    {
        return tokenFrequencyMap.isEmpty();
    }

    public List<DocumentToken> getDocumentTokens()
    {
        return documentTokens;
    }

    public ConcurrentMap<String, Integer> getTokenFrequencyMap()
    {
        return tokenFrequencyMap;
    }

    public Set<String> getUniqueTokens()
    {
        return uniqueTokens;
    }

    public long getId()
    {
        return id;
    }
}
