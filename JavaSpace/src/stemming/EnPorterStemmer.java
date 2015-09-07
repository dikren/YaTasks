package stemming;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class aims to be an interpretation of the Porter stemming algorithm for English language, which description can
 * be found right here: http://snowballstem.org/algorithms/english/stemmer.html
 * 
 */
public class EnPorterStemmer {

    static class MutableBoolean {
        public boolean value;
    }

    // vowels
    private static final String VWL_SET = "aeiouy";
    private static final String VWL_YES = "[" + VWL_SET + "]";
    private static final String VWL_NO = "[^" + VWL_SET + "]";

    // double
    private static final String DOUBLE = "(bb|dd|ff|gg|mm|nn|pp|rr|tt)";
    // valid li-ending
    private static final String LI_END_YES = "[cdeghkmnrt]";
    private static final String LI_END_NO = "[^cdeghkmnrt]";

    // short syllable
    private static final String SHORT_SYLLABLE_FIRST = "^" + VWL_YES + VWL_NO;
    private static final String SHORT_SYLLABLE_MIDDLE = VWL_NO + VWL_YES + "[^wxY" + VWL_SET + "]";

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
        // Exceptions
        if (n == 1 && str.matches("^(gener|commun|arsen).*")) {
            Pattern p = Pattern.compile("^(gener|commun|arsen)");
            Matcher m = p.matcher(str);
            m.find();
            return str.substring(m.end());
        }

        // usual rules
        Pattern p = Pattern.compile(VWL_YES + VWL_NO);
        Matcher m = p.matcher(str);
        if (m.find()) {
            if (n > 1) {
                return getR(n - 1, str.substring(m.end()));
            } else {
                return str.substring(m.end());
            }
        }
        return "";
    }

    static boolean isShort(String str) {
        if (getR(1, str).equals("")
                && (str.matches(".*" + SHORT_SYLLABLE_MIDDLE + "$") || str.matches(SHORT_SYLLABLE_FIRST + "$"))) {
            return true;
        }
        return false;
    }

    public static String stem(String str) throws Exception {
        // If the word has two letters or less, leave it as it is
        if (str.length() <= 2) {
            return str;
        }

        // Remove initial ', if present
        if (str.charAt(0) == '\'') {
            str = str.substring(1);
        }

        // exceptions
        switch (str) {
        case "skis":
            return "ski";
        case "skies":
            return "sky";
        case "dying":
            return "die";
        case "lying":
            return "lie";
        case "tying":
            return "tie";
        case "idly":
            return "idl";
        case "gently":
            return "gentl";
        case "ugly":
            return "ugli";
        case "early":
            return "earli";
        case "only":
            return "onli";
        case "singly":
            return "singl";

            // invariants
        case "sky":
        case "news":
        case "howe":
        case "atlas":
        case "cosmos":
        case "bias":
        case "andes":
            return str;
        }

        // Set initial y, or y after a vowel, to Y
        if (str.charAt(0) == 'y') {
            str = str.replaceFirst("y", "Y");
        }
        Pattern p = Pattern.compile(VWL_YES + "y");
        Matcher m;
        boolean isYFound = true;
        while (isYFound) {
            isYFound = false;
            m = p.matcher(str);
            if (m.find()) {
                str = str.substring(0, m.end() - 1) + "Y" + str.substring(m.end());
                isYFound = true;
            }
        }

        // steps
        str = step0(str);

        str = step1a(str);

        // invariants
        switch (str) {
        case "inning":
        case "outing":
        case "canning":
        case "herring":
        case "earring":
        case "proceed":
        case "exceed":
        case "succeed":
            return str;
        }

        str = step1b(str);

        str = step1c(str);

        str = step2(str);

        str = step3(str);

        str = step4(str);

        str = step5(str);

        str = str.replace('Y', 'y');

        return str;

    }

    /**
     * removes suffixes from the specified string or just return it if it has not such suffix
     * 
     * @param str
     *            string to handle
     * @param suffixRegEx
     *            suffixes, e.g.
     *            <p>
     *            suff
     *            <p>
     *            (suff1|suff2|suff3)
     * @return
     */
    static String removeSuffix(String str, String suffixRegEx) {
        Pattern p = Pattern.compile(suffixRegEx + "$");
        Matcher m = p.matcher(str);
        if (m.find()) {
            if (m.start() > 0) {
                return str.substring(0, m.start());
            } else {
                return "";
            }
        }
        return str;
    }

    /**
     * replaces in the specified string current suffix with the needed one. Or just return the string if it has not such
     * suffix
     * 
     * @param str
     * @param crntSuffix
     * @param neededSuffix
     * @return
     */
    static String replaceSuffix(String str, String crntSuffix, String neededSuffix) {
        if (str.endsWith(crntSuffix)) {
            return str.substring(0, str.length() - crntSuffix.length()) + neededSuffix;
        }
        return str;
    }

    /**
     * replaces in the specified string last characters of length of given number with the given suffix.
     */
    static String replaceSuffix(String str, int crntSuffixLen, String neededSuffix) {
        if (str.length() >= crntSuffixLen) {
            return str.substring(0, str.length() - crntSuffixLen) + neededSuffix;
        }
        return str;
    }

    /**
     * this method gets the map of pairs (oldSuffix, newSuffix), sorts it by the length of oldSuffix in descending
     * order, checks one by one if the strToLookThrough (the string for search an old suffix in it) ends by the current
     * old suffix and if it does then replaces in the strForReplace (it is the string in which replacing is needed.) the
     * found old suffix by the one from its pair in the map.
     * <p>
     * If suffix for replacement was found then search is stopped and updated string is returned. in the
     * isSuffixWasReplaced will be true value. If no suffix was found for replacement then the value of
     * isSuffixWasReplaced is false
     * 
     * @param strForReplace
     * @param strToLookThrough
     *            is a substring of strForReplace. If it doesn't the nException is thrown
     * @param replacingPairs
     * @return
     * @throws Exception
     */
    static String replaceSuffix(String strForReplace, String strToLookThrough, Map<String, String> replacingPairs,
            MutableBoolean isSuffixWasReplaced) throws Exception {
        if (!strForReplace.endsWith(strToLookThrough)) {
            throw new Exception("strForReplace: " + strForReplace + " | strToLookThrough: " + strToLookThrough);
        }

        isSuffixWasReplaced.value = false;

        List<String> oldSuffixes = new LinkedList<String>();
        oldSuffixes.addAll(replacingPairs.keySet());
        // sort them by length of the string, so I can go from the longest to the shortest
        oldSuffixes.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o2.length() - o1.length(); // o1 will be to the left from o2 if o2.length < o1.length
            }
        });

        for (Iterator<String> itr = oldSuffixes.iterator(); itr.hasNext();) {
            String oldSuffix = itr.next();
            if (strToLookThrough.endsWith(oldSuffix)) {
                String newSuffix = replacingPairs.get(oldSuffix);
                isSuffixWasReplaced.value = true;
                return replaceSuffix(strForReplace, oldSuffix, newSuffix);
            }
        }
        return strForReplace;
    }

    private static String step0(String str) {
        return removeSuffix(str, "('s'|'s|')");
    }

    private static String step1a(String str) {
        if (str.endsWith("sses")) {
            return replaceSuffix(str, "sses", "ss");
        }
        if (str.endsWith("ied") || str.endsWith("ies")) {
            if (str.length() > 4) {
                return replaceSuffix(str, 3, "i");
            } else {
                return replaceSuffix(str, 3, "ie");
            }
        }
        if (str.endsWith("us") || str.endsWith("ss")) {
            return str;
        }
        if (str.endsWith("s") && str.matches(".*" + VWL_YES + ".+s")) {
            return removeSuffix(str, "s");
        }
        return str;
    }

    private static String step1b(String str) {
        String R1 = getR(1, str);
        if (R1.endsWith("eedly")) {
            return replaceSuffix(str, 5, "ee");
        }
        if (R1.endsWith("eed")) {
            return replaceSuffix(str, 3, "ee");
        }

        if (str.matches(".*" + VWL_YES + ".*(ingly|edly|ing|ed)$")) {
            str = removeSuffix(str, "(ingly|edly|ing|ed)");
            if (str.matches(".*(at|bl|iz)$")) {
                return str += "e";
            }
            if (str.matches(".*" + DOUBLE + "$")) {
                return str.substring(0, str.length() - 1);
            }
            if (isShort(str)) {
                return str += "e";
            }
        }
        return str;
    }

    private static String step1c(String str) {
        if (str.matches(".+" + VWL_NO + "(y|Y)$")) {
            return replaceSuffix(str, 1, "i");
        }
        return str;
    }

    private static String step2(String str) throws Exception {
        String R1 = getR(1, str);

        Map<String, String> replacingSuffixesPairs = new HashMap<String, String>();
        replacingSuffixesPairs.put("fulness", "ful");
        replacingSuffixesPairs.put("ational", "ate");
        replacingSuffixesPairs.put("ization", "ize");
        replacingSuffixesPairs.put("ousness", "ous");
        replacingSuffixesPairs.put("iveness", "ive");

        replacingSuffixesPairs.put("lessli", "less");
        replacingSuffixesPairs.put("biliti", "ble");
        replacingSuffixesPairs.put("tional", "tion");

        replacingSuffixesPairs.put("entli", "ent");
        replacingSuffixesPairs.put("ation", "ate");
        replacingSuffixesPairs.put("alism", "al");
        replacingSuffixesPairs.put("aliti", "al");
        replacingSuffixesPairs.put("ousli", "ous");
        replacingSuffixesPairs.put("iviti", "ive");
        replacingSuffixesPairs.put("fulli", "ful");

        replacingSuffixesPairs.put("enci", "ence");
        replacingSuffixesPairs.put("anci", "ance");
        replacingSuffixesPairs.put("abli", "able");
        replacingSuffixesPairs.put("izer", "ize");
        replacingSuffixesPairs.put("ator", "ate");
        replacingSuffixesPairs.put("alli", "al");

        replacingSuffixesPairs.put("bli", "ble");

        MutableBoolean isSuffixWasReplaced = new MutableBoolean();
        str = replaceSuffix(str, R1, replacingSuffixesPairs, isSuffixWasReplaced);
        if (isSuffixWasReplaced.value) {
            return str;
        }

        // ogi: replace by og if preceded by l
        if (R1.matches(".*logi$")) {
            return replaceSuffix(str, "ogi", "og");
        }

        // li: delete if preceded by a valid li-ending
        if (R1.matches(".*" + LI_END_YES + "li$")) {
            return replaceSuffix(str, "li", "");
        }

        return str;
    }

    private static String step3(String str) throws Exception {
        String R1 = getR(1, str);

        Map<String, String> replacingSuffixesPairs = new HashMap<String, String>();
        replacingSuffixesPairs.put("ational", "ate");
        replacingSuffixesPairs.put("tional", "tion");
        replacingSuffixesPairs.put("alize", "al");
        replacingSuffixesPairs.put("icate", "ic");
        replacingSuffixesPairs.put("iciti", "ic");
        replacingSuffixesPairs.put("ical", "ic");
        replacingSuffixesPairs.put("ness", "");
        replacingSuffixesPairs.put("ful", "");

        MutableBoolean isSuffixWasReplaced = new MutableBoolean();
        str = replaceSuffix(str, R1, replacingSuffixesPairs, isSuffixWasReplaced);
        if (isSuffixWasReplaced.value) {
            return str;
        }

        // ative*: delete if in R2
        String R2 = getR(2, str);
        if (R2.matches(".*ative$")) {
            return replaceSuffix(str, "ative", "");
        }

        return str;
    }

    private static String step4(String str) {
        String R2 = getR(2, str);

        String suffixes = "(ement|ance|ence|ment|able|ible|ant|ent|ism|ate|iti|ous|ive|ize|er|ic|al)";

        if (R2.matches(".*" + suffixes + "$")) {
            return removeSuffix(str, suffixes);
        }
        if (R2.matches(".*[st]ion$")) {
            return removeSuffix(str, "ion");
        }

        return str;
    }

    private static String step5(String str) {
        String R1 = getR(1, str);
        String R2 = getR(2, str);

        if (R2.matches(".*e$")
                || (R1.matches(".*e$") && !str.matches(".*" + SHORT_SYLLABLE_MIDDLE + "e$") && !str
                        .matches(SHORT_SYLLABLE_FIRST + "e$"))) {
            return removeSuffix(str, "e");
        }
        if (R2.matches(".*ll$")) {
            return removeSuffix(str, "l");
        }

        return str;
    }
}
