package com.techyourchance.unittesting.questions;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

import com.techyourchance.unittesting.networking.questions.FetchQuestionDetailsEndpoint;
import com.techyourchance.unittesting.networking.questions.QuestionSchema;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class FetchQuestionDetailsUseCaseSolutionTest {

    // region constants
    public static final String QUESTION_ID = "id";

    // endregion constants

    // region helper fields
    FetchQuestionDetailsEndpointDouble mEndpointTd;

    @Mock
    FetchQuestionDetailsUseCase.Listener mListener1;

    @Mock
    FetchQuestionDetailsUseCase.Listener mListener2;

    @Captor
    ArgumentCaptor<QuestionDetails> questionDetailsCaptor;

    // endregion helper fields

    FetchQuestionDetailsUseCase SUT;

    @Before
    public void setup() throws Exception {
        mEndpointTd = new FetchQuestionDetailsEndpointDouble();
        SUT = new FetchQuestionDetailsUseCase(mEndpointTd);
    }

    // fetchQuestionDetailsAndNotify - success and notify to listeners

    @Test
    public void fetchQuestionDetailsAndNotify_success_notifySuccessToListeners() {
        // Arrange
        SUT.registerListener(mListener1);
        SUT.registerListener(mListener2);
        success();
        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);
        // Assert
        verify(mListener1).onQuestionDetailsFetched(questionDetailsCaptor.capture());
        verify(mListener2).onQuestionDetailsFetched(questionDetailsCaptor.capture());

        List<QuestionDetails> list = questionDetailsCaptor.getAllValues();

        assertThat(list.get(0), is(getExpectedQuestionDetail()));
        assertThat(list.get(1), is(getExpectedQuestionDetail()));
    }


    // fetchQuestionDetailsAndNotify - failure and notify to listeners
    @Test
    public void fetchQuestionDetailsAndNotify_failure_listenersNotifiedFailures() {
        // Arrange
        SUT.registerListener(mListener1);
        SUT.registerListener(mListener2);
        failure();
        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);
        // Assert
        verify(mListener1).onQuestionDetailsFetchFailed();
        verify(mListener2).onQuestionDetailsFetchFailed();
    }

    // region helper methods
    private QuestionDetails getExpectedQuestionDetail() {
        return new QuestionDetails("id", "title", "body");
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

        public FetchQuestionDetailsEndpointDouble() {
            super(null);
        }

        @Override
        public void fetchQuestionDetails(String questionId, Listener listener) {
            if (isFailure) {
                listener.onQuestionDetailsFetchFailed();
            } else {
                listener.onQuestionDetailsFetched(new QuestionSchema("title", "id", "body"));
            }
        }
    }

    // endregion helper classes
}