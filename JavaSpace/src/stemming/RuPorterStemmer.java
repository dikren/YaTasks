package stemming;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class aims to be an interpretation of the Porter stemming algorithm for Russian language, which description can
 * be found right here: http://snowballstem.org/algorithms/russian/stemmer.html
 * 
 */
public class RuPorterStemmer {

    static class MutableBoolean {
        public boolean value;
    }

    private static Set<Character> vowels;
    static {
        vowels = new TreeSet<Character>();
        vowels.addAll(Arrays.asList(new Character[] { 'а', 'е', 'и', 'о', 'у', 'ы', 'э', 'ю', 'я' }));
    }

    // i-suffixes
    private final static String PERFECTIVE_GERUND_GRP_1_LEN_1 = ".*[ая]в$";
    private final static String PERFECTIVE_GERUND_GRP_1_LEN_3 = ".*[ая]вши$";
    private final static String PERFECTIVE_GERUND_GRP_1_LEN_5 = ".*[ая]вшись$";
    private final static String PERFECTIVE_GERUND_GRP_2_LEN_2 = ".*(ив|ыв)$";
    private final static String PERFECTIVE_GERUND_GRP_2_LEN_4 = ".*(ивши|ывши)$";
    private final static String PERFECTIVE_GERUND_GRP_2_LEN_6 = ".*(ившись|ывшись)$";

    private final static String ADJECTIVE_LEN_2 = ".*(ее|ие|ые|ое|ей|ий|ый|ой|ем|им|ым|ом|их|ых|ую|юю|ая|яя|ою|ею)$";
    private final static String ADJECTIVE_LEN_3 = ".*(ими|ыми|его|ого|ему|ому)$";

    private final static String PARTICIPLE_GRP_1_LEN_1 = ".*[ая]щ$";
    private final static String PARTICIPLE_GRP_1_LEN_2 = ".*[ая](ем|нн|вш|ющ)$";
    private final static String PARTICIPLE_GRP_2_LEN_3 = ".*(ивш|ывш|ующ)$";

    private final static String REFLEXIVE_LEN_2 = ".*(ся|сь)$";

    private final static String VERB_GRP_1_LEN_1 = ".*[ая](й|л|н)$";
    private final static String VERB_GRP_1_LEN_2 = ".*[ая](ла|на|ли|ем|ло|но|ет|ют|ны|ть)$";
    private final static String VERB_GRP_1_LEN_3 = ".*[ая](ете|йте|ешь|нно)$";
    private final static String VERB_GRP_2_LEN_1 = ".*(ю)$";
    private final static String VERB_GRP_2_LEN_2 = ".*(ей|уй|ил|ыл|им|ым|ен|ят|ит|ыт|ую)$";
    private final static String VERB_GRP_2_LEN_3 = ".*(ила|ыла|ена|ите|или|ыли|ило|ыло|ено|ует|уют|ены|ить|ыть|ишь)$";
    private final static String VERB_GRP_2_LEN_4 = ".*(ейте|уйте)$";

    private final static String NOUN_LEN_1 = ".*(а|е|и|й|о|у|ы|ь|ю|я)$";
    private final static String NOUN_LEN_2 = ".*(ев|ов|ие|ье|еи|ии|ей|ой|ий|ям|ем|ам|ом|ах|ях|ию|ью|ия|ья)$";
    private final static String NOUN_LEN_3 = ".*(ями|ами|ией|иям|ием|иях)$";
    private final static String NOUN_LEN_4 = ".*(иями)$";

    private final static String SUPERLATIVE_LEN_3 = ".*(ейш)$";
    private final static String SUPERLATIVE_LEN_4 = ".*(ейше)$";

    // d-suffixes
    private final static String DERIVATIONAL_LEN_3 = ".*(ост)$";
    private final static String DERIVATIONAL_LEN_4 = ".*(ость)$";

    // Define an ADJECTIVAL ending as an ADJECTIVE ending optionally preceded by a PARTICIPLE ending

    // other
    private final static String DOUBLE_N = ".*нн$";
    private final static String SIGN_I = ".*и$";
    private final static String SOFT_SIGN = ".*ь$";

    /**
     * In any word, RV is the region after the first vowel, or the end of the word if it contains no vowel.
     * 
     * @param str
     * @return
     */
    static String getRv(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (vowels.contains(str.charAt(i)) && i + 1 < str.length()) {
                return str.substring(i + 1);
            }
        }
        return "";
    }

    /**
     * Returns Rn of the specified string, e.g. for n = 2 it is R2.
     * <p>
     * R1 is the region after the first non-vowel following a vowel, or the end of the word if there is no such
     * non-vowel.
     * <p>
     * R2 is the region after the first non-vowel following a vowel in R1, or the end of the word if there is no such
     * non-vowel
     * 
     * @param str
     *            a string from which Rn is built
     * @param n
     *            the number of Rn
     */
    static String getR(int n, String str) {
        boolean vowelFound = false;
        for (int i = 0; i < str.length(); i++) {
            if (!vowelFound && vowels.contains(str.charAt(i))) {
                vowelFound = true;
            } else if (vowelFound && !vowels.contains(str.charAt(i)) && i + 1 < str.length()) {
                if (n == 1) {
                    return str.substring(i + 1);
                } else {
                    return getR(n - 1, str.substring(i + 1));
                }
            }
        }
        return "";
    }

    public static String stem(String str) {
        /**
         * All tests take place in the RV part of the word.
         * <p>
         * Do each of steps 1, 2, 3 and 4.
         * <p>
         * Step 1: Search for a PERFECTIVE GERUND ending. If one is found remove it, and that is then the end of step 1.
         * Otherwise try and remove a REFLEXIVE ending, and then search in turn for (1) an ADJECTIVAL, (2) a VERB or (3)
         * a NOUN ending. As soon as one of the endings (1) to (3) is found remove it, and terminate step 1.
         * <p>
         * Step 2: If the word ends with и (i), remove it.
         * <p>
         * Step 3: Search for a DERIVATIONAL ending in R2 (i.e. the entire ending must lie in R2), and if one is found,
         * remove it.
         * <p>
         * Step 4: (1) Undouble н (n), or, (2) if the word ends with a SUPERLATIVE ending, remove it and undouble н (n),
         * or (3) if the word ends ь (') (soft sign) remove it.
         */

        String workingStr = getRv(str);
        int wrkStrLen = workingStr.length(); // memorize RV length for find easy its start position in the str latter.

        workingStr = step1(workingStr);

        workingStr = step2(workingStr);

        int stemmedSuffixesLen = wrkStrLen - workingStr.length();
        str = str.substring(0, str.length() - stemmedSuffixesLen); // get original string without suffixes of steps 1, 2

        workingStr = getR(2, str);
        wrkStrLen = workingStr.length();

        workingStr = step3(workingStr);

        stemmedSuffixesLen = wrkStrLen - workingStr.length();
        str = str.substring(0, str.length() - stemmedSuffixesLen);

        return step4(str);
    }

    static String step1(String str) {
        /**
         * Step 1: Search for a PERFECTIVE GERUND ending. If one is found remove it, and that is then the end of step 1.
         * Otherwise try and remove a REFLEXIVE ending, and then search in turn for (1) an ADJECTIVAL, (2) a VERB or (3)
         * a NOUN ending. As soon as one of the endings (1) to (3) is found remove it, and terminate step 1.
         */

        MutableBoolean isTimeToReturn = new MutableBoolean();
        isTimeToReturn.value = false;

        str = stemPerfectiveGerund(str, isTimeToReturn);
        if (isTimeToReturn.value) {
            return str;
        }

        str = stemReflexive(str);

        str = stemAdjectival(str, isTimeToReturn);
        if (isTimeToReturn.value) {
            return str;
        }

        str = stemVerb(str, isTimeToReturn);
        if (isTimeToReturn.value) {
            return str;
        }

        str = stemNoun(str, isTimeToReturn);
        /*
         * if (isTimeToReturn.value) { return str; }
         */

        return str;
    }

    private static String stemPerfectiveGerund(String str, MutableBoolean isTimeToReturn) {
        isTimeToReturn.value = false;
        if (str.matches(PERFECTIVE_GERUND_GRP_2_LEN_6)) {
            isTimeToReturn.value = true;
            return str.substring(0, str.length() - 6);
        }
        if (str.matches(PERFECTIVE_GERUND_GRP_1_LEN_5)) {
            isTimeToReturn.value = true;
            return str.substring(0, str.length() - 5);
        }
        if (str.matches(PERFECTIVE_GERUND_GRP_2_LEN_4)) {
            isTimeToReturn.value = true;
            return str.substring(0, str.length() - 4);
        }
        if (str.matches(PERFECTIVE_GERUND_GRP_1_LEN_3)) {
            isTimeToReturn.value = true;
            return str.substring(0, str.length() - 3);
        }
        if (str.matches(PERFECTIVE_GERUND_GRP_2_LEN_2)) {
            isTimeToReturn.value = true;
            return str.substring(0, str.length() - 2);
        }
        if (str.matches(PERFECTIVE_GERUND_GRP_1_LEN_1)) {
            isTimeToReturn.value = true;
            return str.substring(0, str.length() - 1);
        }
        return str;
    }

    private static String stemReflexive(String str) {
        if (str.matches(REFLEXIVE_LEN_2)) {
            return str.substring(0, str.length() - 2);
        }
        return str;
    }

    private static String stemAdjectival(String str, MutableBoolean isTimeToReturn) {
        str = stemAdjective(str, isTimeToReturn);
        if (isTimeToReturn.value) {
            str = stemParticiple(str);
        }
        return str;
    }

    private static String stemAdjective(String str, MutableBoolean isTimeToReturn) {
        isTimeToReturn.value = false;
        if (str.matches(ADJECTIVE_LEN_3)) {
            isTimeToReturn.value = true;
            return str.substring(0, str.length() - 3);
        }
        if (str.matches(ADJECTIVE_LEN_2)) {
            isTimeToReturn.value = true;
            return str.substring(0, str.length() - 2);
        }
        return str;
    }

    private static String stemParticiple(String str) {
        if (str.matches(PARTICIPLE_GRP_2_LEN_3)) {
            return str.substring(0, str.length() - 3);
        }
        if (str.matches(PARTICIPLE_GRP_1_LEN_2)) {
            return str.substring(0, str.length() - 2);
        }
        if (str.matches(PARTICIPLE_GRP_1_LEN_1)) {
            return str.substring(0, str.length() - 1);
        }
        return str;
    }

    private static String stemVerb(String str, MutableBoolean isTimeToReturn) {
        isTimeToReturn.value = false;
        if (str.matches(VERB_GRP_2_LEN_4)) {
            isTimeToReturn.value = true;
            return str.substring(0, str.length() - 4);
        }
        if (str.matches(VERB_GRP_1_LEN_3) || str.matches(VERB_GRP_2_LEN_3)) {
            isTimeToReturn.value = true;
            return str.substring(0, str.length() - 3);
        }
        if (str.matches(VERB_GRP_1_LEN_2) || str.matches(VERB_GRP_2_LEN_2)) {
            isTimeToReturn.value = true;
            return str.substring(0, str.length() - 2);
        }
        if (str.matches(VERB_GRP_1_LEN_1) || str.matches(VERB_GRP_2_LEN_1)) {
            isTimeToReturn.value = true;
            return str.substring(0, str.length() - 1);
        }
        return str;
    }

    private static String stemNoun(String str, MutableBoolean isTimeToReturn) {
        isTimeToReturn.value = false;
        if (str.matches(NOUN_LEN_4)) {
            isTimeToReturn.value = true;
            return str.substring(0, str.length() - 4);
        }
        if (str.matches(NOUN_LEN_3)) {
            isTimeToReturn.value = true;
            return str.substring(0, str.length() - 3);
        }
        if (str.matches(NOUN_LEN_2)) {
            isTimeToReturn.value = true;
            return str.substring(0, str.length() - 2);
        }
        if (str.matches(NOUN_LEN_1)) {
            isTimeToReturn.value = true;
            return str.substring(0, str.length() - 1);
        }
        return str;
    }

    private static String step2(String str) {
        /**
         * Step 2: If the word ends with и (i), remove it.
         */

        if (str.matches(SIGN_I)) {
            return str.substring(0, str.length() - 1);
        }
        return str;
    }

    private static String step3(String str) {
        /**
         * Step 3: Search for a DERIVATIONAL ending in R2 (i.e. the entire ending must lie in R2), and if one is found,
         * remove it.
         */
        return stemDerivational(str);
    }

    private static String stemDerivational(String str) {
        if (str.matches(DERIVATIONAL_LEN_4)) {
            return str.substring(0, str.length() - 4);
        }
        if (str.matches(DERIVATIONAL_LEN_3)) {
            return str.substring(0, str.length() - 3);
        }
        return str;
    }

    private static String step4(String str) {
        /**
         * Step 4: (1) Undouble н (n), or, (2) if the word ends with a SUPERLATIVE ending, remove it and undouble н (n),
         * or (3) if the word ends ь (') (soft sign) remove it.
         */
        if (str.matches(SOFT_SIGN)) {
            return str.substring(0, str.length() - 1);
        }
        str = stemSuperlative(str);
        if (str.matches(DOUBLE_N)) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    private static String stemSuperlative(String str) {
        if (str.matches(SUPERLATIVE_LEN_4)) {
            return str.substring(0, str.length() - 4);
        }
        if (str.matches(SUPERLATIVE_LEN_3)) {
            return str.substring(0, str.length() - 3);
        }
        return str;
    }
}
