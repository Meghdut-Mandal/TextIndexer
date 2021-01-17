import com.meghdut.text.DefaultDocumentParser;
import com.meghdut.text.Document;
import com.meghdut.text.DocumentParser;
import com.meghdut.text.SearchIndexFactory;
import com.meghdut.text.SearchResult;
import com.meghdut.text.TextSearchIndex;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
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
    private final AtomicLong count;
    private final Map<Long, FileDocument> documentMap;
    private IndexingProgressListener listener;


    /**
     * Create a Files Index with default word based Parser
     *
     * @param path the root path of the files
     * @throws IOException if there's an issue with reading the files
     */
    public FileSearchIndex(@NotNull Path path) throws IOException
    {
        this(path, new DefaultDocumentParser());
    }

    public FileSearchIndex(@NotNull Path path, @NotNull DocumentParser parser) throws IOException
    {
        this.parser = parser;
        count = new AtomicLong(0);
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
                .filter(it -> !EMPTY_DOC.equals(it))
                .peek(this::updateProgress)
                .collect(Collectors.toMap(Document::getId, Function.identity(), (e, p) -> e));

    }

    private void updateProgress(FileDocument document)
    {
        if (listener != null) {
            long current = count.incrementAndGet();
            listener.update(document, current);
        }
    }

    @NotNull
    private FileDocument readDocument(Path path)
    {
        try{
            String rawText = Files.readString(path);
            long id = System.nanoTime();
            return new FileDocument(path, rawText, id);
        } catch (MalformedInputException ignored) {

        } catch (Exception e) {
            System.err.println("Error on path " + path.toAbsolutePath().toString());
            e.printStackTrace();
        }
        return EMPTY_DOC;
    }

    public void setListener(IndexingProgressListener listener)
    {
        this.listener = listener;
    }

    public Map<SearchResult, FileDocument> search(String searchTerm)
    {
        return searchIndex.search(searchTerm).stream()
                .collect(Collectors.toMap(Function.identity(), it -> documentMap.get(it.getUniqueIdentifier())));
    }

    public DocumentParser getParser()
    {
        return parser;
    }
}
