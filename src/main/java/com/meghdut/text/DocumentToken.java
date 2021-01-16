package com.meghdut.text;

import com.google.common.base.Objects;

public class DocumentToken
{
    private String token;
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
     * @param type the Type of
     * @param pos
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
        return type == that.type && pos == that.pos && Objects.equal(token, that.token);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(token, type, pos);
    }
}
