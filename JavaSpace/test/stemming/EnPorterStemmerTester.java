package stemming;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.List;
import java.util.Properties;

public class EnPorterStemmerTester {
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
        INPUT_DATA = properties.getProperty("EnStemmer_INPUT_DATA");
        OUTPUT_DATA = properties.getProperty("EnStemmer_OUTPUT_DATA");
        
    }
    
    public static void test1() throws Exception {
        String str = "beautiful";
        if (!"iful".equals(EnPorterStemmer.getR(1, str))) {
            throw new Exception(str);
        }
        if (!"ul".equals(EnPorterStemmer.getR(2, str))) {
            throw new Exception(str);
        }
        
        str = "beauty";
        if (!"y".equals(EnPorterStemmer.getR(1, str))) {
            throw new Exception(str);
        }
        if (!"".equals(EnPorterStemmer.getR(2, str))) {
            throw new Exception(str);
        }
        
        str = "beau";
        if (!"".equals(EnPorterStemmer.getR(1, str))) {
            throw new Exception(str);
        }
        if (!"".equals(EnPorterStemmer.getR(2, str))) {
            throw new Exception(str);
        }
    }
    
    public static void test2() throws Exception {
        if (!EnPorterStemmer.isShort("bed") || !EnPorterStemmer.isShort("shed") || !EnPorterStemmer.isShort("shred")) {
            throw new Exception("must be short");
        }
        if (EnPorterStemmer.isShort("bead") || EnPorterStemmer.isShort("embed") || EnPorterStemmer.isShort("beds")) {
            throw new Exception("must not be short");
        }
    }
    
    public static void test3() throws Exception {
        if (!"qwe".equals(EnPorterStemmer.removeSuffix("qwe's'", "('s'|'s|')"))) {
            throw new Exception();
        }
        if (!"qweNew".equals(EnPorterStemmer.replaceSuffix("qweOld", "Old", "New"))) {
            throw new Exception();
        }
    }
    

    public static void test4() throws Exception {
        List<String> input = Files.readAllLines(FileSystems.getDefault().getPath(System.getProperty("user.dir"), INPUT_DATA));
        List<String> output = Files.readAllLines(FileSystems.getDefault().getPath(OUTPUT_DATA));
        for (int i = 0; i < input.size(); i++) {
            if (!EnPorterStemmer.stem(input.get(i)).equals(output.get(i))) {
                throw new Exception("i: " + i +  " | input: " + input.get(i) + " | output: " + output.get(i));
            }
        }
    }

}
