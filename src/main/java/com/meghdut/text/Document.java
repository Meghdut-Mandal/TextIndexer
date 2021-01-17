package com.meghdut.text;

import org.jetbrains.annotations.NotNull;

/**
 * An Unparsed Document
 */
public class Document
{
    private final String text;
    private final Long id;

    /**
     *
     * @param text - The plain text for this Document
     * @param id  an unique ID for the document
     */
    public Document(@NotNull String text, long id)
    {
        this.text = text;
        this.id = id;
    }


    public String getText()
    {
        return text;
    }

    public Long getId()
    {
        return id;
    }
}
