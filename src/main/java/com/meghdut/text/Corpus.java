package com.meghdut.text;


import org.jetbrains.annotations.NotNull;
import java.util.List;


/**
 * Collection of Parsed documents
 * This can be extended to store some Meta-data about the documents
 *
 */
public class Corpus
{
    private final List<ParsedDocument> parsedDocuments;

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
