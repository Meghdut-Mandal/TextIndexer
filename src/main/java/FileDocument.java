import com.meghdut.text.Document;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class FileDocument extends Document
{

    private Path location;

    /**
     * @param text - The plain text for this Document
     * @param id   an unique ID for the document
     */
    public FileDocument(@NotNull Path location, @NotNull String text, long id)
    {
        super(text, id);
        this.location = location;
    }
}
