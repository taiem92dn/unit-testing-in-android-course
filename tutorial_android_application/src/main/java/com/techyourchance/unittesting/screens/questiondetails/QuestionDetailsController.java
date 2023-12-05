package com.techyourchance.unittesting.screens.questiondetails;

import com.techyourchance.unittesting.questions.FetchQuestionDetailsUseCase;
import com.techyourchance.unittesting.questions.QuestionDetails;
import com.techyourchance.unittesting.screens.common.screensnavigator.ScreensNavigator;
import com.techyourchance.unittesting.screens.common.toastshelper.ToastsHelper;

public class QuestionDetailsController implements FetchQuestionDetailsUseCase.Listener, QuestionDetailsViewMvc.Listener {

    private final FetchQuestionDetailsUseCase mFetchQuestionDetailsUseCase;
    private final ScreensNavigator mScreensNavigator;
    private final ToastsHelper mToastsHelper;

    private String mQuestionId;
    private QuestionDetailsViewMvc mViewMvc;

    public QuestionDetailsController(FetchQuestionDetailsUseCase fetchQuestionDetailsUseCase,
                                     ScreensNavigator screensNavigator,
                                     ToastsHelper toastsHelper) {
        mFetchQuestionDetailsUseCase = fetchQuestionDetailsUseCase;
        mScreensNavigator = screensNavigator;
        mToastsHelper = toastsHelper;
    }

    public void bindQuestionId(String questionId) {
        mQuestionId = questionId;
    }

    public void bindView(QuestionDetailsViewMvc viewMvc) {
        mViewMvc = viewMvc;
    }

    public void onStart() {
        mFetchQuestionDetailsUseCase.registerListener(this);
        mViewMvc.registerListener(this);

        mViewMvc.showProgressIndication();
        mFetchQuestionDetailsUseCase.fetchQuestionDetailsAndNotify(mQuestionId);
    }

    public void onStop() {
        mFetchQuestionDetailsUseCase.unregisterListener(this);
        mViewMvc.unregisterListener(this);
    }

    @Override
    public void onQuestionDetailsFetched(QuestionDetails questionDetails) {
        mViewMvc.hideProgressIndication();
        mViewMvc.bindQuestion(questionDetails);
    }

    @Override
    public void onQuestionDetailsFetchFailed() {
        mViewMvc.hideProgressIndication();
        mToastsHelper.showUseCaseError();
    }

    @Override
    public void onNavigateUpClicked() {
        mScreensNavigator.navigateUp();
    }

}
