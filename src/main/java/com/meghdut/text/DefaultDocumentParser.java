package com.meghdut.text;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A default Implementation of DocumentParser which splits by space
 */
public class DefaultDocumentParser implements DocumentParser
{
    public DefaultDocumentParser()
    {
    }

    @Override
    public ParsedDocument parse(@NotNull Document document)
    {
        String rawText = document.getText();
        List<DocumentToken> documentTokens = rawTextToTermList(rawText);
        return new ParsedDocument(documentTokens, document.getId());
    }

    private List<DocumentToken> rawTextToTermList(String rawText)
    {
        return Arrays.stream(rawText.split(" "))
                .filter(it -> !it.isEmpty()) // remove Empty Strings
                .map(DocumentToken::new)
                .collect(Collectors.toList());
    }
}
