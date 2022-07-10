package com.hoyski.wordfinder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class WordFinderTest {

    WordFinder wordFinder;

    @Before
    public void setup() {
        wordFinder = new WordFinder(false);
    }

    @Test
    public void computer_is_a_word() {
        Assert.assertTrue(wordFinder.isWord("computer"));
    }

    @Test
    public void words_exist_that_start_with_com() {
        Assert.assertTrue(wordFinder.wordsExistThatStartWith("com"));
    }

    @Test
    public void words_exist_that_start_with_hu() {
        Assert.assertTrue(wordFinder.wordsExistThatStartWith("HU"));
    }

    @Test
    public void words_exist_that_start_with_ho() {
        Assert.assertTrue(wordFinder.wordsExistThatStartWith("ho"));
    }
}
