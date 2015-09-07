import stemming.EnPorterStemmerTester;
import stemming.RuPorterStemmerTester;

public class testExecutor {

    public static void main(String[] args) throws Exception {
        RuPorterStemmerTester.test1();
        RuPorterStemmerTester.test2();
        
        IoDataTester.test1();
        
        EnPorterStemmerTester.test1();
        EnPorterStemmerTester.test2();
        EnPorterStemmerTester.test3();
        EnPorterStemmerTester.test4();

    }

}
