import com.meghdut.text.Document;
import com.meghdut.text.SearchIndexFactory;
import com.meghdut.text.SearchResult;
import com.meghdut.text.TextSearchIndex;

import java.util.ArrayList;
import java.util.List;

public class InMemoryTest
{
    public static void main(String[] args)
    {
        List<Document> documents = new ArrayList<>();
        documents.add(new Document("the total number of documents by the sexy newton of documents containing the term, and then taking the logarithm of that quotient", 1));
        documents.add(new Document("A formula that aims to define the importance of a keyword or phrase within a document or a web page", 2));
        documents.add(new Document("to the more meaningful terms brown newton cow ", 3));
        documents.add(new Document("ccurs in each document; the number of times a term in  keyword a document is called its term frequency.", 4));

        TextSearchIndex index = SearchIndexFactory.buildIndex(documents);

        String searchTerm = "total number of documents";

        List<SearchResult> batch = index.search(searchTerm);
        // print the
        batch.forEach(item -> System.out.println(item.getUniqueIdentifier() + " -- " + item.getRelevanceScore()));

    }
}
