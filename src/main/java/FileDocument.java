import com.meghdut.text.Document;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

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
}
