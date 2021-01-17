package com.meghdut.text;

import org.jetbrains.annotations.NotNull;
/*
A Parser for a Document
 */
public interface DocumentParser
{
    /**
     * Parse a Document having raw text into a ParsedDocument
     * each Token of the document has a field pos
     *
     * @param document a unparsed raw document
     * @return  A document which is a Parsed representation of the raw Doc
     */
    ParsedDocument parse(@NotNull Document document);

}
