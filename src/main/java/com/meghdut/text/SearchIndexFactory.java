package com.meghdut.text;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class SearchIndexFactory
{



    public static TextSearchIndex buildIndex(Collection<Document> documents)
    {

        final DocumentParser parser = new DefaultDocumentParser();
        List<ParsedDocument> parsedDocuments = documents.parallelStream().map(parser::parse).collect(Collectors.toList());

        Corpus corpus = new Corpus(parsedDocuments);

        return new InvertedIndex(corpus);
    }
}
