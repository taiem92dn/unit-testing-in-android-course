package com.techyourchance.unittestingfundamentals.exercise1;

import static org.hamcrest.CoreMatchers.is;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NegativeNumberValidatorTest {
    NegativeNumberValidator SUT;

    @Before
    public void setup() {
        SUT = new NegativeNumberValidator();
    }

    @Test
    public void test1Positive() {
        boolean result = SUT.isNegative(1);
        Assert.assertThat(result, is(false));
    }

    @Test
    public void test2Zero() {
        boolean result = SUT.isNegative(0);
        Assert.assertThat(result, is(false));
    }

    @Test
    public void test3Negative() {
        boolean result = SUT.isNegative(-1);
        Assert.assertThat(result, is(true));
    }
}