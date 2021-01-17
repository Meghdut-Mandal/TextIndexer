import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Utils
{


    static boolean isTextFile(Path p){
        return isTextFile(p.toFile());
    }


    static boolean isTextFile(File f)
    {
        String type = null;
        try{
            type = Files.probeContentType(f.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //type isn't text
        if (type == null) {
            //type couldn't be determined, assume binary
            return false;
        } else return type.startsWith("text");
    }
}
