import com.meghdut.text.Document;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Objects;

public class FileDocument extends Document
{

    private final Path location;

    /**
     * @param text - The plain text for this Document
     * @param id   an unique ID for the document
     * @param location the Location of the file
     */
    public FileDocument(@NotNull Path location, @NotNull String text, long id)
    {
        super(text, id);
        this.location = location;
    }

    public Path getLocation()
    {
        return location;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof FileDocument)) return false;
        if (!super.equals(o)) return false;
        FileDocument that = (FileDocument) o;
        return Objects.equals(location, that.location);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), location);
    }
}
