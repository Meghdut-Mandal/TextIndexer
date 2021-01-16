package com.meghdut.text;

/**
 * An Unparsed Document
 */
public class Document
{
    private String text;
    private Long id;


    /**
     *
     * @param text - The plain text for this Document
     * @param id  an unique ID for the document
     */
    public Document(String text, Long id)
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
