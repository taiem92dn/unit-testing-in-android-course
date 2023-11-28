package com.techyourchance.unittestingfundamentals.exercise3;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import com.techyourchance.unittestingfundamentals.example3.Interval;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class IntervalsAdjacencyDetectorTest {

    IntervalsAdjacencyDetector SUT;
    @Before
    public void setUp() throws Exception {
        SUT = new IntervalsAdjacencyDetector();
    }

    // interval 1 before interval 2
    @Test
    public void adjacency_interval1BeforeInterval2_false() {
        Interval interval1 = new Interval(1, 3);
        Interval interval2 = new Interval(5, 7);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    // interval 1 overlap at the start of interval 2
    @Test
    public void adjacency_interval1OverlapAtStartInterval2_false() {
        Interval interval1 = new Interval(1, 5);
        Interval interval2 = new Interval(3, 7);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    // interval 1 overlap and include interval 2
    @Test
    public void adjacency_interval1OverlapAndIncludeInterval2_false() {
        Interval interval1 = new Interval(1, 7);
        Interval interval2 = new Interval(3, 5);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    // interval 1 overlap and is included by interval 2
    @Test
    public void adjacency_interval2OverlapAndIncludeInterval1_false() {
        Interval interval1 = new Interval(1, 3);
        Interval interval2 = new Interval(-1, 5);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    // interval 1 overlap and at the end of interval 2
    @Test
    public void adjacency_interval1OverlapAtTheEndOfInterval2_false() {
        Interval interval1 = new Interval(4, 7);
        Interval interval2 = new Interval(1, 5);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    // interval 1 after interval 2
    @Test
    public void adjacency_interval1AfterInterval2_false() {
        Interval interval1 = new Interval(8, 9);
        Interval interval2 = new Interval(5, 7);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    // interval 1 before and adjacency interval 2
    @Test
    public void adjacency_interval1BeforeAndAdjacencyInterval2_true() {
        Interval interval1 = new Interval(1, 3);
        Interval interval2 = new Interval(3, 7);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(true));
    }

    // interval 1 after and adjacency interval 2
    @Test
    public void adjacency_interval1AfterAndAdjacencyInterval2_true() {
        Interval interval1 = new Interval(7, 9);
        Interval interval2 = new Interval(3, 7);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(true));
    }

    // interval 1 and interval are the same
    @Test
    public void adjacency_interval1AndInterval2AreTheSame_false() {
        Interval interval1 = new Interval(1, 3);
        Interval interval2 = new Interval(1, 3);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }
}