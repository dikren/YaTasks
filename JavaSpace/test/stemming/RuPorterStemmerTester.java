package stemming;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.List;
import java.util.Properties;


public class RuPorterStemmerTester {
    
    private static final String INPUT_DATA;
    private static final String OUTPUT_DATA;
    
    static {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("test/.properties"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        INPUT_DATA = properties.getProperty("RuStemmer_INPUT_DATA");
        OUTPUT_DATA = properties.getProperty("RuStemmer_OUTPUT_DATA");
        
    }
    
    public static void test1() throws Exception {
        String str = "противоестественном";
        if (!"тивоестественном".equals(RuPorterStemmer.getRv(str))) {
            throw new Exception("Rv is not equal");
        }
        if (!"оестественном".equals(RuPorterStemmer.getR(2, str))) {
            throw new Exception("R2 is not equal");
        }
    }
    
    public static void test2() throws Exception {
        List<String> input = Files.readAllLines(FileSystems.getDefault().getPath(System.getProperty("user.dir"), INPUT_DATA));
        List<String> output = Files.readAllLines(FileSystems.getDefault().getPath(OUTPUT_DATA));
        for (int i = 0; i < input.size(); i++) {
            if (!RuPorterStemmer.stem(input.get(i)).equals(output.get(i))) {
                throw new Exception("i: " + i +  " | input: " + input.get(i) + " | output: " + output.get(i));
            }
        }
    }

}
