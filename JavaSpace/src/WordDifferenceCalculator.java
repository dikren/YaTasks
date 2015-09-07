import java.util.ArrayList;
import java.util.List;

import javax.swing.text.Position;


public class WordDifferenceCalculator {
    
    // lists of positions on which changes occur (memorizing only deletions and substitutions positions for each word)
    List<Integer> firstWordPositionChanges = new ArrayList<Integer>();
    List<Integer> secondWordPositionChanges = new ArrayList<Integer>();
    // Levenshtein distance value
    int distVal;
    
    public static Double calcDiff(String first, String second) {
        //Calculation of Levenshtein distance matrix
        WordDifferenceCalculator[][] distanceMatrix =  
                new WordDifferenceCalculator[first.length() + 1][second.length() + 1];
        calcLevenshteinDistance(first, second, first.length(), second.length(), distanceMatrix);
        
        //basing on Levenshtein distance calculate difference measure between words
        return calcDiff(distanceMatrix[first.length()][second.length()], first.length(), second.length());
    }
    
    private static void calcLevenshteinDistance(String first, String second, 
            int indxFrst, int indxScnd, WordDifferenceCalculator[][] distanceMatrix) {
        //if position of interest of distance matrix already was calculated then just return.
        if (distanceMatrix[indxFrst][indxScnd] != null) {
            return;
        }
        
        //initializing distance matrix with start values. Number of row(column) is a the number of character in string.
        // e.g. 0 is position before string (empty), 1 - is first character position, 
        // length() - is the last character in the word
        if (indxFrst == 0) {
            WordDifferenceCalculator wd = new WordDifferenceCalculator();
            wd.distVal = indxScnd;
            for(int i = 1; i <= indxScnd; i++) {
                wd.secondWordPositionChanges.add(i);
            }
            distanceMatrix[indxFrst][indxScnd] = wd;
            return;
        } else if (indxScnd == 0) {
            WordDifferenceCalculator wd = new WordDifferenceCalculator();
            wd.distVal = indxFrst;
            for(int i = 1; i <= indxFrst; i++) {
                wd.firstWordPositionChanges.add(i);
            }
            distanceMatrix[indxFrst][indxScnd] = wd;
            return;
        }
        
        //fill the values for distance matrix with indices numbers less than current indices 
        calcLevenshteinDistance(first, second, indxFrst - 1, indxScnd, distanceMatrix);
        calcLevenshteinDistance(first, second, indxFrst, indxScnd - 1, distanceMatrix);
        calcLevenshteinDistance(first, second, indxFrst - 1, indxScnd - 1, distanceMatrix);
        
        // choose the smallest distance and fill current distance position
        WordDifferenceCalculator wd = new WordDifferenceCalculator();
        distanceMatrix[indxFrst][indxScnd] = wd;
        
        WordDifferenceCalculator leftDelDist = distanceMatrix[indxFrst - 1][indxScnd];
        WordDifferenceCalculator rightDelDist = distanceMatrix[indxFrst][indxScnd - 1];
        WordDifferenceCalculator substitutionDist = distanceMatrix[indxFrst - 1][indxScnd - 1];
        if (leftDelDist.distVal < rightDelDist.distVal &&
                leftDelDist.distVal < substitutionDist.distVal) {
            wd.distVal = leftDelDist.distVal + 1;
            wd.firstWordPositionChanges.addAll(leftDelDist.firstWordPositionChanges);
            wd.secondWordPositionChanges.addAll(leftDelDist.secondWordPositionChanges);
            wd.firstWordPositionChanges.add(indxFrst);
            return;
        } else if (rightDelDist.distVal < substitutionDist.distVal) {
            wd.distVal = rightDelDist.distVal + 1;
            wd.firstWordPositionChanges.addAll(rightDelDist.firstWordPositionChanges);
            wd.secondWordPositionChanges.addAll(rightDelDist.secondWordPositionChanges);
            wd.secondWordPositionChanges.add(indxScnd);
            return;
        } else {
            wd.distVal = substitutionDist.distVal;
            wd.firstWordPositionChanges.addAll(substitutionDist.firstWordPositionChanges);
            wd.secondWordPositionChanges.addAll(substitutionDist.secondWordPositionChanges);
            if (first.charAt(indxFrst - 1) != second.charAt(indxScnd - 1)) {
                wd.distVal++;
                wd.firstWordPositionChanges.add(indxFrst);
                wd.secondWordPositionChanges.add(indxScnd);
            }
            return;
        }
    }
    
    private static Double calcDiff(WordDifferenceCalculator levenshteinDist, int frstWrdLen, int scndWrdLen) {
        //for each word calculate its difference measure
        Double firstWrdDiff = calcDiff(levenshteinDist.firstWordPositionChanges, frstWrdLen);
        Double secondWrdDiff = calcDiff(levenshteinDist.secondWordPositionChanges, scndWrdLen);
        return firstWrdDiff + secondWrdDiff;
    }
    
    /*
     * difference measure for single word is calculated basing on Levenshtein distance and in such way, that
     * prefixes and suffixes get less cost then middle differences in words.
     * To make different costs for different positions I use two linear function here: 
     * y = position. For position < length/2 
     * y = length - position. For positions > length/2
     * After summary of costs for each position of change was calculated I divide this value on length. 
     * This means that if we align words by there middle positions then costs for each changes will be defined by
     * how far this change is from the middle of the word. And position value in new coordinates (where origin is
     * middle of the word) is the same for all words (with any length). 
     */
    private static Double calcDiff(List<Integer> postionsOfChanges, int wordLength) {
        int middlePos = wordLength / 2;
        Double result = new Double(0);
        for (int pos : postionsOfChanges) {
            if (pos <= middlePos) {
                result += pos;
            } else {
                result += wordLength + 1 - pos;
            }
        }
        result /= wordLength;
        return result;
    }

}
