import com.meghdut.text.DefaultDocumentParser;
import com.meghdut.text.Document;
import com.meghdut.text.DocumentParser;
import com.meghdut.text.SearchIndexFactory;
import com.meghdut.text.SearchResult;
import com.meghdut.text.TextSearchIndex;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FileSearchIndex
{
    private static final FileDocument EMPTY_DOC;

    static {
        EMPTY_DOC = new FileDocument(Path.of(""), "", 34);
    }

    private final DocumentParser parser;
    private final TextSearchIndex searchIndex;
    private final AtomicInteger count;
    private final Map<Long, FileDocument> documentMap;


    /**
     * Create a Files Index with default word based Parser
     * @param path the root path of the files
     * @throws IOException
     */
    public FileSearchIndex(@NotNull Path path) throws IOException
    {
        this(path, new DefaultDocumentParser());
    }

    public FileSearchIndex(@NotNull Path path, @NotNull DocumentParser parser) throws IOException
    {
        this.parser = parser;
        count = new AtomicInteger(0);
        documentMap = readFiles(path);
        searchIndex = SearchIndexFactory.buildIndex(documentMap.values());
    }

    private Map<Long, FileDocument> readFiles(Path path) throws IOException
    {
        return Files.walk(path)
                .parallel()
                .filter(Files::isRegularFile)
                .filter(Utils::isTextFile)
                .map(this::readDocument)
                .collect(Collectors.toMap(Document::getId, Function.identity()));

    }

    @NotNull
    private FileDocument readDocument(Path path)
    {
        try{
            String rawText = Files.readString(path);
            int id = count.getAndIncrement();
            return new FileDocument(path, rawText, id);
        } catch (IOException e) {
            System.err.println("Error on path " + path.toAbsolutePath().toString());
            e.printStackTrace();
        }
        return EMPTY_DOC;
    }


    public Map<SearchResult, FileDocument> search(String searchTerm, int maxResults)
    {
        return searchIndex.search(searchTerm, maxResults).stream()
                .collect(Collectors.toMap(Function.identity(), it -> documentMap.get(it.getUniqueIdentifier())));
    }

    public DocumentParser getParser()
    {
        return parser;
    }
}
