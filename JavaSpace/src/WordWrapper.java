import java.util.HashSet;
import java.util.Set;

import stemming.GeneralStemmer;


public class WordWrapper {
    private String originalWord;
    // words which we treat as surely equal to this word (equal means it would belong to the same group if it exists) 
    private Set<String> stemmedForms = new HashSet<String>();
    
    public WordWrapper(String word) {
        originalWord = word;
    }

    public Set<String> getStemmedForms() {
        return stemmedForms;
    }
    
    public String getOriginalWord() {
        return originalWord;
    }
    
    public void findStemmedForms() throws Exception {
        stemmedForms.addAll(GeneralStemmer.getStemmedStrings(originalWord));
    }
    
    public boolean hasSameRootAs(WordWrapper otherWrd) {
        for (String str : stemmedForms) {
            if (otherWrd.stemmedForms.contains(str)) {
                return true;
            }
        }
        return false;
    }
}
