package com.techyourchance.unittesting.questions;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.techyourchance.unittesting.common.time.TimeProvider;
import com.techyourchance.unittesting.networking.questions.FetchQuestionDetailsEndpoint;
import com.techyourchance.unittesting.networking.questions.QuestionSchema;
import com.techyourchance.unittesting.testdata.QuestionDetailsTestData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class FetchQuestionDetailsUseCaseTest {

    // region constants
    public static final String QUESTION_ID_1 = QuestionDetailsTestData.getQuestionDetails1().getId();
    public static final QuestionDetails QUESTION_DETAILS_1 = QuestionDetailsTestData.getQuestionDetails1();
    public static final String QUESTION_ID_2 = QuestionDetailsTestData.getQuestionDetails2().getId();

    public static final QuestionDetails QUESTION_DETAILS_2 = QuestionDetailsTestData.getQuestionDetails2();
    public static final long CACHE_TIME_OUT_MS = 60000L;

    // endregion constants

    // region helper fields
    FetchQuestionDetailsEndpointDouble mEndpointTd;

    @Mock
    FetchQuestionDetailsUseCase.Listener mListener1;

    @Mock
    FetchQuestionDetailsUseCase.Listener mListener2;

    @Mock
    TimeProvider mTimeProvider;

    @Captor
    ArgumentCaptor<QuestionDetails> questionDetailsCaptor;

    // endregion helper fields

    FetchQuestionDetailsUseCase SUT;

    @Before
    public void setup() throws Exception {
        mEndpointTd = new FetchQuestionDetailsEndpointDouble();
        SUT = new FetchQuestionDetailsUseCase(mEndpointTd, mTimeProvider);
    }

    // fetchQuestionDetailsAndNotify - success and notify to listeners

    @Test
    public void fetchQuestionDetailsAndNotify_success_notifySuccessToListeners() {
        // Arrange
        SUT.registerListener(mListener1);
        SUT.registerListener(mListener2);
        success();
        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        // Assert
        verify(mListener1).onQuestionDetailsFetched(questionDetailsCaptor.capture());
        verify(mListener2).onQuestionDetailsFetched(questionDetailsCaptor.capture());

        List<QuestionDetails> list = questionDetailsCaptor.getAllValues();

        assertThat(list.get(0), is(QUESTION_DETAILS_1));
        assertThat(list.get(1), is(QUESTION_DETAILS_1));
    }


    // fetchQuestionDetailsAndNotify - failure and notify to listeners
    @Test
    public void fetchQuestionDetailsAndNotify_failure_listenersNotifiedFailures() {
        // Arrange
        SUT.registerListener(mListener1);
        SUT.registerListener(mListener2);
        failure();
        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        // Assert
        verify(mListener1).onQuestionDetailsFetchFailed();
        verify(mListener2).onQuestionDetailsFetchFailed();
    }
    // =============================================================
    // Unit Testing Solution tests with both Listener1 & Listener2
    // Solution only tests the last values of listeners
    // My Unit Tests have less 1 test case than Solution's
    // =============================================================

    //fetchQuestionDetailsAndNotify - second time successful response - called twice and cached value returned
    // Solution test case name: fetchQuestionDetailsAndNotify_secondTimeImmediatelyAfterSuccess_listenersNotifiedWithDataFromCache
    @Test
    public void fetchQuestionDetailsAndNotify_success_notifiedTwiceAndCachedValueNotified() {
        // Arrange
        success();
        SUT.registerListener(mListener1);
        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        // Assert
        assertThat(mEndpointTd.callCount, is(1));
        verify(mListener1, times(2)).onQuestionDetailsFetched(QUESTION_DETAILS_1);
    }

    // Solution name: fetchQuestionDetailsAndNotify_secondTimeWithDifferentIdAfterSuccess_listenersNotifiedWithDataFromEndpoint
    @Test
    public void fetchQuestionDetailsAndNotify_successfulResponse_differentValuesNotified() {
        // Arrange
        success();
        SUT.registerListener(mListener1);
        SUT.registerListener(mListener2);
        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_2);
        // Assert
        assertThat(mEndpointTd.callCount, is(2));
        verify(mListener1).onQuestionDetailsFetched(QUESTION_DETAILS_1);
        verify(mListener2).onQuestionDetailsFetched(QUESTION_DETAILS_1);
        verify(mListener1).onQuestionDetailsFetched(QUESTION_DETAILS_2);
        verify(mListener2).onQuestionDetailsFetched(QUESTION_DETAILS_2);
    }

    // fetchQuestionDetailsAndNotify - second time after caching timeout  - newly fetched value returned
    // Solution name: fetchQuestionDetailsAndNotify_secondTimeRightAfterTimeoutAfterSuccess_listenersNotifiedWithDataFromEndpoint
    // but solution doesn't use different value for the first time
    @Test
    public void fetchQuestionDetailsAndNotify_secondTimeAfterCachingTimeout_newlyFetchValuedNotified() {
        // Arrange
        diffValueFirstTime();
        SUT.registerListener(mListener1);
        when(mTimeProvider.getCurrentTimestamp()).thenReturn(0L);
        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        when(mTimeProvider.getCurrentTimestamp()).thenReturn(CACHE_TIME_OUT_MS);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        // Assert
        assertThat(mEndpointTd.callCount, is(2));
        verify(mListener1).onQuestionDetailsFetched(QUESTION_DETAILS_1);
    }

    // fetchQuestionDetailsAndNotify - second Time before timeout -  cached value returned
    // Solution name: fetchQuestionDetailsAndNotify_secondTimeRightBeforeTimeoutAfterSuccess_listenersNotifiedWithDataFromCache
    @Test
    public void fetchQuestionDetailsAndNotify_secondTimeBeforeTimeout_cachedValueNotified() {
        // Arrange
        SUT.registerListener(mListener1);
        when(mTimeProvider.getCurrentTimestamp()).thenReturn(0L);
        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        when(mTimeProvider.getCurrentTimestamp()).thenReturn(CACHE_TIME_OUT_MS - 1);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        // Assert
        assertThat(mEndpointTd.callCount, is(1));
        verify(mListener1, times(2)).onQuestionDetailsFetched(QUESTION_DETAILS_1);
    }

    // fetchQuestionDetailsAndNotify - second Time before time out - different newly fetched value returned
    // look almost the same with solution but actually is different, solution name:
    // fetchQuestionDetailsAndNotify_afterTwoDifferentQuestionsAtDifferentTimesFirstQuestionRightBeforeTimeout_listenersNotifiedWithDataFromCache
    @Test
    public void fetchQuestionDetailsAndNotify_secondTimeWithFirstQuestionAfterTimeout_differentNewlyFetchedValueNotified() {
        // Arrange
        diffValueFirstTime();
        SUT.registerListener(mListener1);
        when(mTimeProvider.getCurrentTimestamp()).thenReturn(0L);
        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        when(mTimeProvider.getCurrentTimestamp()).thenReturn(CACHE_TIME_OUT_MS/2);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_2);
        when(mTimeProvider.getCurrentTimestamp()).thenReturn(CACHE_TIME_OUT_MS);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        // Assert
        assertThat(mEndpointTd.callCount, is(3));
        verify(mListener1).onQuestionDetailsFetched(new QuestionDetails("id1", "title_", "body_"));
        verify(mListener1).onQuestionDetailsFetched(QuestionDetailsTestData.getQuestionDetails2());
        verify(mListener1).onQuestionDetailsFetched(QUESTION_DETAILS_1);
    }

    // region helper methods
    private void diffValueFirstTime() {
        mEndpointTd.isDiffValueFirstTime = true;
    }

    private void success() {

    }

    private void failure() {
        mEndpointTd.isFailure = true;
    }
    // endregion helper methods

    // region helper classes
    class FetchQuestionDetailsEndpointDouble extends FetchQuestionDetailsEndpoint {


        public boolean isFailure;
        public int callCount;
        public boolean isDiffValueFirstTime;

        public FetchQuestionDetailsEndpointDouble() {
            super(null);
        }

        @Override
        public void fetchQuestionDetails(String questionId, Listener listener) {
            callCount++;

            if (isFailure) {
                listener.onQuestionDetailsFetchFailed();
            } else {
                if (isDiffValueFirstTime && callCount == 1) {
                    listener.onQuestionDetailsFetched(new QuestionSchema("title_", "id1", "body_"));
                }
                else {
                    if (questionId.equals(QUESTION_ID_1)) {
                        listener.onQuestionDetailsFetched(new QuestionSchema("title1", "id1", "body1"));
                    }
                    else if (questionId.equals(QUESTION_ID_2)) {
                        listener.onQuestionDetailsFetched(new QuestionSchema("title2", "id2", "body2"));
                    }
                    else {
                        throw new RuntimeException("Invalid question id");
                    }
                }
            }
        }
    }

    // endregion helper classes
}