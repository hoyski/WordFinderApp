package com.hoyski.wordfinder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class WordFinder {

    private static final int MAX_MAX_WORDS_TO_RETURN = 1000;

    private List<String> dictionary;
    private Map<String, BeginEndPos> firstTwoLetterPosMap;

    public WordFinder() {
        this(true);
    }

    public WordFinder(boolean sortDictionaryLengthFirst) {
        loadDictionary(sortDictionaryLengthFirst);
    }

    public FoundWords findWords(String letters, int minimumWordLength, String pattern, int indexOfFirst,
                                int maxToReturn) {
        // Scrub and validate inputs
        if (letters == null) {
            letters = "";
        }

        if (pattern == null) {
            pattern = "";
        }

        if (letters.length() == 0 && pattern.length() == 0) {
            throw new IllegalArgumentException("Must provide letters or a pattern");
        }

        if (letters.length() == 0 || minimumWordLength < 1) {
            minimumWordLength = 1;
        }

        FoundWords foundWords = new FoundWords();

        foundWords.setFoundWords(new ArrayList<String>());
        foundWords.setTotalMatches(0);
        foundWords.setIndexOfFirst(indexOfFirst);


        if (letters.length() > 0 && letters.length() < minimumWordLength) {
            // Nothing can match. Return an empty list
            return foundWords;
        }

        if (maxToReturn < 1 || maxToReturn > MAX_MAX_WORDS_TO_RETURN) {
            maxToReturn = MAX_MAX_WORDS_TO_RETURN;
        }

        validatePattern(pattern, letters);

        if (pattern.length() > 0) {
            pattern = pattern.toUpperCase();
            if (minimumWordLength < pattern.length()) {
                minimumWordLength = pattern.length();
            }
        }

        letters = letters.toUpperCase();

        for (String dictionaryWord : dictionary) {
            if (letters.length() != 0 && dictionaryWord.length() > letters.length()) {
                // Since 'dictionary's words are sorted by length, and the current word is longer
                // than the number of provider letters, no more words in 'dictionary' can match
                break;
            }

            if (dictionaryWord.length() >= minimumWordLength && wordContainedInChars(dictionaryWord, letters)) {
                if (matchesPattern(pattern, dictionaryWord)) {
                    if (foundWords.getTotalMatches() >= indexOfFirst
                            && foundWords.getTotalMatches() < indexOfFirst + maxToReturn) {
                        foundWords.getFoundWords().add(dictionaryWord);
                    }

                    foundWords.setTotalMatches(foundWords.getTotalMatches() + 1);
                }
            }
        }

        return foundWords;
    }

    /**
     * Ensures that the pattern is valid.
     *
     * @throws IllegalArgumentException when the pattern is invalid
     */
    private void validatePattern(String pattern, String characters) {
        if (pattern.length() == 0) {
            // No pattern specified. All good
            return;
        }

        if (characters.length() == 0) {
            // Performing pattern match only. All good
            return;
        }

        pattern = pattern.toUpperCase();

        if (pattern.length() > characters.length()) {
            throw new IllegalArgumentException("Pattern is longer than the number of characters");
        }

        for (int i = 0; i < pattern.length(); ++i) {
            if (pattern.charAt(i) != '_' && characters.indexOf(pattern.charAt(i)) == -1) {
                throw new IllegalArgumentException(
                        "Invalid pattern. Must contain only underscores or letters from the input string");
            }
        }
    }

    /**
     * Returns whether or not <code>candidateWord</code> is an actual word
     */
    public boolean isWord(String candidateWord) {
        return Collections.binarySearch(dictionary, candidateWord.toUpperCase()) >= 0;
    }

    /**
     * Returns whether or not there are words in the dictionary that start
     * with the letters <code>firstLetters</code>
     *
     * @param firstLetters
     * @return
     */
    public boolean wordsExistThatStartWith(String firstLetters) {
        if (firstLetters.length() <= 1) {
            return true;
        }

        firstLetters = firstLetters.toUpperCase();

        BeginEndPos beginEndPos = firstTwoLetterPosMap.get(firstLetters.substring(0, 2));
        if (beginEndPos == null) {
            return false;
        }

        for (int i = beginEndPos.getBeginPos(); i <= beginEndPos.getEndPos(); i++) {
            if (dictionary.get(i).startsWith(firstLetters)) {
                return true;
            }
        }

        return false;

    }

    private boolean matchesPattern(String pattern, String candidateWord) {
        if (pattern.length() == 0) {
            // Not matching on a pattern so everything matches
            return true;
        }

        if (candidateWord.length() != pattern.length()) {
            return false;
        }

        for (int i = 0; i < pattern.length(); ++i) {
            if (pattern.charAt(i) != '_' && pattern.charAt(i) != candidateWord.charAt(i)) {
                return false;
            }
        }

        // If made it here then all of the non-wildcard characters match
        return true;
    }

    private void loadDictionary(boolean sortDictionaryLengthFirst) {
        String word;

        dictionary = new ArrayList<>();
        firstTwoLetterPosMap = new HashMap<>();
        int lineNum = 0;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/words.txt")))) {
            while ((word = br.readLine()) != null) {
                word = word.toUpperCase();
                dictionary.add(word);
                String firstTwo = word.substring(0, 2);
                if (!firstTwoLetterPosMap.containsKey(firstTwo)) {
                    // First occurrence
                    firstTwoLetterPosMap.put(firstTwo, new BeginEndPos(lineNum));
                } else {
                    firstTwoLetterPosMap.get(firstTwo).setEndPos(lineNum);
                }
                lineNum++;

            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading words.txt", e);
        }

        if (sortDictionaryLengthFirst) {
            dictionary.sort(new LengthFirstComparator());
        }
    }

    /**
     * Returns {@code true} if all of the letters in {@code word} are contained in
     * {@code chars}. For repeated letters in {@code word} at least as many
     * occurrences must exist in {@code chars}. Otherwise, return {@code false}.
     *
     * @param word
     * @param chars
     * @return
     */
    public static boolean wordContainedInChars(String word, String chars) {
        if (chars.length() == 0) {
            // Performing a pattern match search so return true
            return true;
        }
        for (int i = 0; i < word.length(); ++i) {
            if (countChars(word, word.charAt(i)) > countChars(chars, word.charAt(i))) {
                return false;
            }
        }

        // If made it this far then word is contained in chars
        return true;
    }

    private static int countChars(String str, char charToCount) {
        int numChars = 0;

        for (int i = 0; i < str.length(); ++i) {
            if (str.charAt(i) == charToCount) {
                numChars++;
            }
        }

        return numChars;
    }

    private static class BeginEndPos {
        private int beginPos;
        private int endPos;

        public BeginEndPos(int beginPos) {
            this.beginPos = beginPos;
            this.endPos = beginPos;
        }

        public int getBeginPos() {
            return beginPos;
        }

        public void setBeginPos(int beginPos) {
            this.beginPos = beginPos;
        }

        public int getEndPos() {
            return endPos;
        }

        public void setEndPos(int endPos) {
            this.endPos = endPos;
        }

    }
}
