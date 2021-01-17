package com.meghdut.text;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return Objects.equals(text, document.text) && Objects.equals(id, document.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(text, id);
    }
}
