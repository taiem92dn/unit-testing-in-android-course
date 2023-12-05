package com.techyourchance.unittesting.screens.questiondetails;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

import com.techyourchance.unittesting.questions.FetchQuestionDetailsUseCase;
import com.techyourchance.unittesting.questions.QuestionDetails;
import com.techyourchance.unittesting.screens.common.screensnavigator.ScreensNavigator;
import com.techyourchance.unittesting.screens.common.toastshelper.ToastsHelper;
import com.techyourchance.unittesting.testdata.QuestionDetailsTestData;
import com.techyourchance.unittesting.testdata.QuestionsTestData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class QuestionDetailsControllerSolutionTest {

    // region constants
    public static final QuestionDetails QUESTION_DETAIL = QuestionDetailsTestData.getQuestionDetail();
    public static final String QUESTION_ID = QUESTION_DETAIL.getId();

    // endregion constants

    // region helper fields

    FetchQuestionDetailsUseCaseTd mUseCaseTd;

    @Mock
    ScreensNavigator mScreensNavigator;

    @Mock
    ToastsHelper mToastsHelper;

    @Mock
    QuestionDetailsViewMvc mViewMvc;
    // endregion helper fields

    QuestionDetailsController SUT;

    @Before
    public void setup() throws Exception {
        mUseCaseTd = new FetchQuestionDetailsUseCaseTd();
        SUT = new QuestionDetailsController(mUseCaseTd, mScreensNavigator, mToastsHelper);
        SUT.bindView(mViewMvc);
        SUT.bindQuestionId(QUESTION_ID);
    }

    @Test
    public void onStart_registerListenerCalled() {
        // Arrange
        // Act
        SUT.onStart();
        // Assert
        mUseCaseTd.verifyRegisterListenerCalled(SUT);
        verify(mViewMvc).registerListener(SUT);
    }

    @Test
    public void onStop_unRegisterListenerCalled() {
        // Arrange
        mUseCaseTd.registerListener(SUT);
        // Act
        SUT.onStop();
        // Assert
        mUseCaseTd.verifyUnregisterListenerCalled(SUT);
        verify(mViewMvc).unregisterListener(SUT);
    }


    @Test
    public void onStart_successfulResponse_bindViewCalled() {
        // Arrange
        success();
        // Act
        SUT.onStart();
        // Assert
        assertThat(1, is(mUseCaseTd.fetchCallingCount));
        verify(mViewMvc).bindQuestion(QUESTION_DETAIL);
    }


    @Test
    public void onStart_failureResponse_toastShown() {
        // Arrange
        failure();
        // Act
        SUT.onStart();
        // Assert
        assertThat(1, is(mUseCaseTd.fetchCallingCount));
        verify(mToastsHelper).showUseCaseError();
    }

    @Test
    public void onNavigateUpClicked_screenNavigatorCalled() {
        // Arrange
        // Act
        SUT.onNavigateUpClicked();
        // Assert
        verify(mScreensNavigator).navigateUp();
    }

    @Test
    public void onStart_showProgressIndicationCalled() {
        // Arrange
        // Act
        SUT.onStart();
        // Assert
        verify(mViewMvc).showProgressIndication();
    }

    @Test
    public void onStart_successfulResponse_hideProgressIndicationCalled() {
        // Arrange
        success();
        // Act
        SUT.onStart();
        // Assert
        verify(mViewMvc).hideProgressIndication();
    }

    @Test
    public void onStart_failureResponse_hideProgressIndicationCalled() {
        // Arrange
        failure();
        // Act
        SUT.onStart();
        // Assert
        verify(mViewMvc).hideProgressIndication();
    }

    // region helper methods

    private void success() {
        // no co-op
    }

    private void failure() {
        mUseCaseTd.isFailure = true;
    }
    // endregion helper methods

    // region helper classes

    private class FetchQuestionDetailsUseCaseTd extends FetchQuestionDetailsUseCase {

        public int fetchCallingCount = 0;
        public boolean isFailure;

        public FetchQuestionDetailsUseCaseTd() {
            super(null);
        }

        @Override
        public void fetchQuestionDetailsAndNotify(String questionId) {
            fetchCallingCount++;

            if (!QUESTION_ID.equals(questionId)) {
                throw new RuntimeException("Invalid question id");
            }

            if (isFailure) {
                for (Listener listener : getListeners()) {
                    listener.onQuestionDetailsFetchFailed();
                }
            } else {
                for (Listener listener : getListeners()) {
                    listener.onQuestionDetailsFetched(QUESTION_DETAIL);
                }
            }
        }

        public void verifyRegisterListenerCalled(QuestionDetailsController candidate) {
            for (Listener listener : getListeners()) {
                if (listener == candidate) {
                    return;
                }
            }

            throw new RuntimeException("registerListener is not called");
        }

        public void verifyUnregisterListenerCalled(QuestionDetailsController candidate) {
            for (Listener listener : getListeners()) {
                if (listener == candidate) {
                    throw new RuntimeException("UnregisterListener is not called");
                }
            }
        }
    }
    // endregion helper classes
}