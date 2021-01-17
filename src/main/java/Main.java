import com.meghdut.text.Document;
import com.meghdut.text.SearchIndexFactory;
import com.meghdut.text.SearchResult;
import com.meghdut.text.TextSearchIndex;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main implements IOProvider
{
    Scanner sc;

    public Main()
    {
        sc = new Scanner(System.in);
    }

    public static void main(String[] args) throws IOException
    {
        Main m = new Main();
        m.println("Welcome to Text Indexer !" +
                          "\n This app Indexes Text files and is able to look for documents containing " +
                          "\n similar or exact matching text from the given search phrase.");
        m.println("Input the path to root of text files (Enter for current Directory)");
        String path = m.readLine().trim();
        Path root = new File(path).toPath();
        m.println("Indexing files....");
        FileSearchIndex index = new FileSearchIndex(root);
        m.println("Indexing Complete!");

        while (true) {
            m.println("Enter the Search Phrase. (e to exit)");
            String searchPhrase = m.readLine().trim();
            if (searchPhrase.equalsIgnoreCase("e")) {
                m.println("Thanks !");
                System.exit(0);
            } else if (searchPhrase.isBlank() || searchPhrase.isEmpty()){
                m.println("Sorry Search Phrase cant be blank !");
                continue;
            }
            m.println("Searching...");
            Map<SearchResult, FileDocument> results = index.search(searchPhrase);
            m.println("Total " + results.size() + " files found which contains text similar to \""+searchPhrase+"\"");
            m.println("These File are:-");
            results.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
                File file = entry.getValue().getLocation().toFile();
                m.println("File :" + file.getName() + " Location :" + file.getAbsolutePath() + "  Score :" + (entry.getKey().getRelevanceScore() * 100));
            });
        }


    }


    public static void mainx(String[] args)
    {
        List<Document> documents = new ArrayList<>();
        documents.add(new Document("the total number of documents by the sexy newton of documents containing the term, and then taking the logarithm of that quotient", 1));
        documents.add(new Document("A formula that aims to define the importance of a keyword or phrase within a document or a web page", 2));
        documents.add(new Document("to the more meaningful terms brown newton cow ", 3));
        documents.add(new Document("ccurs in each document; the number of times a term in  keyword a document is called its term frequency.", 4));

        TextSearchIndex index = SearchIndexFactory.buildIndex(documents);

        String searchTerm = "total number of documents";

        var batch = index.search(searchTerm);
        batch.forEach(item -> System.out.println(item.getUniqueIdentifier() + " -- " + item.getRelevanceScore()));

    }

    @Override
    public String readLine()
    {
        return sc.nextLine();
    }

    @Override
    public int readInt()
    {
        return sc.nextInt();
    }

    @Override
    public double readDouble()
    {
        return sc.nextDouble();
    }

    @Override
    public void println(String obj)
    {
        System.out.println(obj);
    }

    @Override
    public void println(Object ob)
    {
        System.out.println(ob.toString());
    }
}
