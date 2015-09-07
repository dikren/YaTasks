import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class IoDataTester {
    private static final String OUTPUT_FILE;

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
        OUTPUT_FILE = properties.getProperty("IoDataTester_OUTPUT_FILE");

    }

    public static void test1() throws Exception {
        LinkedList<Group> groups = new LinkedList<Group>();
        groups.add(new Group(new WordWrapper("qwer")));
        groups.add(new Group(Arrays.asList(new WordWrapper("Привет"), new WordWrapper("zxczxczxc"))));
        
        OutputDataCreator.writeData(groups, OUTPUT_FILE);
    }

}
