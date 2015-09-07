import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ListIterator;


public class InputDataParser {
    
    private static List<String> readFile(String filePath) throws IOException {
        Path path = FileSystems.getDefault().getPath(filePath);
        return Files.readAllLines(path);
    }
    
    private static void removeQuotes(List<String> data) {
        for (ListIterator<String> itr = data.listIterator(); itr.hasNext();) {
            itr.set(itr.next().replaceAll("\"", ""));
        }
    }
    
    public static List<String> getData(String filePath) throws IOException {
        List<String> data = readFile(filePath);
        removeQuotes(data);
        return data;
    }

}
