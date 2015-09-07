package stemming;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeneralStemmer {
    private static final String UNNECESSARY_CHARS = "[!:+]";

    public static List<String> getStemmedStrings(String str) throws Exception {
        List<String> stemmedWrds = new LinkedList<String>();

        // make first character upper case
        str = makeFirstCharUpperCase(str);

        // removing signs "!:+"
        // I think all cases when they appear can be joined with other words (after removing these chars) rightfully.
        str = str.replaceAll(UNNECESSARY_CHARS, "");

        // replace ёЁ with еЕ
        str = str.replaceAll("ё", "е");
        str = str.replaceAll("Ё", "Е");

        // translit English characters to Russian and vice versa

        // apply Porter's algorithms
        addRuPorterStem(stemmedWrds, str);
        addEnPorterStem(stemmedWrds, str);

        // if no one of Porters algorithms were used then just add string as is
        if (stemmedWrds.isEmpty()) {
            stemmedWrds.add(str);
        }

        return stemmedWrds;
    }

    static String makeFirstCharUpperCase(String str) {
        if (Character.isLowerCase(str.charAt(0))) {
            return Character.toUpperCase(str.charAt(0)) + str.substring(1);
        }
        return str;
    }

    static void addRuPorterStem(List<String> stemmedWrds, String str) {
        /*
         * Because of abbreviations and combined words, only last part of incoming string will be used for Russian
         * porter algorithm. Namely, I will take the substring which can be defined in the next way:
         * 
         * If last character is not a lower case one then don't use Porter algorithm, otherwise, go from the end of the
         * string and take all consecutive lower case characters (if there are no one then don't use Porter algorithm),
         * when you find an upper case character take it and all the consecutive ones. Do this while you don't find
         * another lower case character (you don't need it) or the beginning of the string.
         * 
         * When such substring was found memorize all leading upper case character from it and then convert hole string
         * to lower case because Porter Algorithm realization understands only lower case here.
         * 
         * After the Russian Porter algorithm was applied check if the length of returned string is less then the number
         * of upper case character in the string you applied it to. If it does then add to the original string all upper
         * case character which you memorized because I assume that upper case characters are part of the abbreviation
         * or of something else which is not a suffix. If it doesn't then restore upper case in the Porter result and
         * add it to the shorten original string.
         */

        Pattern p = Pattern.compile("[А-Я]+[а-я]+$");
        Matcher m = p.matcher(str);
        if (m.find()) {
            int startPos = m.start();
            String strTail = m.group();

            // memorize upper case character to restore them afterwards
            p = Pattern.compile("^[А-Я]+");
            m = p.matcher(strTail);
            m.find();
            String strTailUpperCasePart = m.group();

            String potterRes = RuPorterStemmer.stem(strTail.toLowerCase());

            if (potterRes.length() <= strTailUpperCasePart.length()) {
                stemmedWrds.add(str.substring(0, startPos) + strTailUpperCasePart);
            } else {
                stemmedWrds.add(str.substring(0, startPos) + strTailUpperCasePart
                        + potterRes.substring(strTailUpperCasePart.length()));
            }

        }

    }
    
    static void addEnPorterStem(List<String> stemmedWrds, String str) throws Exception {
        /*
         * Same preprocessings as for Russian Porter algorithm but now I additionally left all apostrophes and convert
         * single quotes to it too.
         */
        str = str.replaceAll("[\u2019\u2018\u201B]", "'"); 

        Pattern p = Pattern.compile("[A-Z']+[a-z']+$");
        Matcher m = p.matcher(str);
        if (m.find()) {
            int startPos = m.start();
            String strTail = m.group();

            // memorize upper case character to restore them afterwards
            p = Pattern.compile("^[A-Z']+");
            m = p.matcher(strTail);
            m.find();
            String strTailUpperCasePart = m.group();

            String potterRes = EnPorterStemmer.stem(strTail.toLowerCase());

            if (potterRes.length() <= strTailUpperCasePart.length()) {
                stemmedWrds.add(str.substring(0, startPos) + strTailUpperCasePart);
            } else {
                stemmedWrds.add(str.substring(0, startPos) + strTailUpperCasePart
                        + potterRes.substring(strTailUpperCasePart.length()));
            }

        }

    }

}
