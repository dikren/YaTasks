import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;


public class Task3Handler {
    
    private static final String INPUT_FILE;
    private static final String OUTPUT_FILE;
    
    static {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("src/.properties"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        INPUT_FILE = properties.getProperty("INPUT_FILE_PATH");
        OUTPUT_FILE = properties.getProperty("OUTPUT_FILE_PATH");
        
    }
    
    public static void execute() throws Exception {
        // read all words without quotes from the given file
        List<String> data = InputDataParser.getData(INPUT_FILE);
        
        // creating word wrapper and group for each word
        WordSet ws = new WordSet(data);
        
        // stemming words in each group to find there root representation 
        ws.stemAll();
        
        // uniting groups basing on equality of roots at least for one word pair in groups
        ws.uniteGroups();
        
        // printing result in a html file
        OutputDataCreator.writeData(ws.getGroups(), OUTPUT_FILE);
    }

}
