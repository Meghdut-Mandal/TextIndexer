package com.meghdut.text;


import java.util.Objects;

public class DocumentToken
{
    private final String token;
    private long type;
    private long pos;

    /**
     * A simple text only token
     * @param token the raw text of a token
     */
    public DocumentToken(String token)
    {
        this.token = token;
    }

    /**
     * @param token the raw text of a token
     * @param type the type of token, useful for Lexers and Parsers  (Optional)
     * @param pos the position of the
     */
    public DocumentToken(String token, long type, long pos)
    {
        this.token = token;
        this.type = type;
        this.pos = pos;
    }

    public void setType(long type)
    {
        this.type = type;
    }

    public void setPos(long pos)
    {
        this.pos = pos;
    }

    public String getToken()
    {
        return token;
    }

    public long getType()
    {
        return type;
    }

    public long getPos()
    {
        return pos;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentToken that = (DocumentToken) o;
        return type == that.type && pos == that.pos && token.equals(that.token);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(token, type, pos);
    }
}
