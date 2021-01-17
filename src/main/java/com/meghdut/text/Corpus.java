package com.meghdut.text;


import org.jetbrains.annotations.NotNull;
import java.util.List;


/**
 * Collection of Parsed documents
 */
public class Corpus
{
    private List<ParsedDocument> parsedDocuments;

    public Corpus(@NotNull List<ParsedDocument> documents)
    {
        this.parsedDocuments = documents;
    }

    public List<ParsedDocument> getParsedDocuments()
    {
        return this.parsedDocuments;
    }

    public int size()
    {
        return parsedDocuments.size();
    }

}
