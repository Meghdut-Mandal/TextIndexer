package com.meghdut.text;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class ParsedDocument
{
    private final List<DocumentToken> documentTokens;
    private final ConcurrentMap<String, Integer> tokenFrequencyMap;
    private final long id;

    public ParsedDocument(@NotNull List<DocumentToken> documentTokens, long uniqueId)
    {
        this.id = uniqueId;
        this.documentTokens = documentTokens;
        this.tokenFrequencyMap = getTokenFrequencyMap(documentTokens);
    }

    @NotNull
    private ConcurrentMap<String, Integer> getTokenFrequencyMap(@NotNull List<DocumentToken> documentTokens)
    {
        return documentTokens
                .parallelStream()
                .map(DocumentToken::getToken)
                .collect(Collectors.toConcurrentMap(w -> w, w -> 1, Integer::sum));
    }

    public boolean isEmpty()
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
        return tokenFrequencyMap.keySet();
    }

    public long getId()
    {
        return id;
    }

    public int getTokenFrequency(String word) {
        if (!tokenFrequencyMap.containsKey(word)) {
            return 0;
        }

        return tokenFrequencyMap.get(word);
    }


}
