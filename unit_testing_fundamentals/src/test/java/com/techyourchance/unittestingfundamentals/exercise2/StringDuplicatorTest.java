package com.techyourchance.unittestingfundamentals.exercise2;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StringDuplicatorTest {

    StringDuplicator SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new StringDuplicator();
    }

    @Test
    public void duplicator_emptyStr_emptyStrReturned() {
        String result = SUT.duplicate("");
        assertThat(result, is(""));
    }

    @Test
    public void duplicator_singleCharacter_doubleSingleCharacterReturned() {
        String result = SUT.duplicate("a");
        assertThat(result, is("aa"));
    }

    @Test
    public void duplicator_longStr_doubleLongStrReturned() {
        String result = SUT.duplicate("abc");
        assertThat(result, is("abcabc"));
    }
}