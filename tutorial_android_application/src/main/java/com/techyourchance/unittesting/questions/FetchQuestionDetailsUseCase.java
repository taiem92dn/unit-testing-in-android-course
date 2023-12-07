package com.techyourchance.unittesting.questions;

import com.techyourchance.unittesting.common.BaseObservable;
import com.techyourchance.unittesting.common.time.TimeProvider;
import com.techyourchance.unittesting.networking.questions.FetchQuestionDetailsEndpoint;
import com.techyourchance.unittesting.networking.questions.QuestionSchema;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FetchQuestionDetailsUseCase extends BaseObservable<FetchQuestionDetailsUseCase.Listener> {

    public interface Listener {
        void onQuestionDetailsFetched(QuestionDetails questionDetails);
        void onQuestionDetailsFetchFailed();
    }

    private final int CACHING_TIME_OUT = 60000;
    private final FetchQuestionDetailsEndpoint mFetchQuestionDetailsEndpoint;
    private final TimeProvider mTimeProvider;

    private final Map<String, QuestionSchema> cachedValueMap = new HashMap<>();
    private final Map<String, Long> cachedLastTimeMap = new HashMap<>();

    public FetchQuestionDetailsUseCase(FetchQuestionDetailsEndpoint fetchQuestionDetailsEndpoint,
                                       TimeProvider timeProvider) {
        mFetchQuestionDetailsEndpoint = fetchQuestionDetailsEndpoint;
        mTimeProvider = timeProvider;
    }

    public void fetchQuestionDetailsAndNotify(String questionId) {
        if (isValidCachedValue(questionId)) {
            notifySuccess(cachedValueMap.get(questionId));
        }
        else {
            mFetchQuestionDetailsEndpoint.fetchQuestionDetails(questionId, new FetchQuestionDetailsEndpoint.Listener() {
                @Override
                public void onQuestionDetailsFetched(QuestionSchema question) {
                    cachedLastTimeMap.put(questionId, mTimeProvider.getCurrentTimestamp());
                    cachedValueMap.put(questionId, question);
                    notifySuccess(question);
                }

                @Override
                public void onQuestionDetailsFetchFailed() {
                    notifyFailure();
                }
            });
        }
    }

    private boolean isValidCachedValue(String questionId) {
        return cachedValueMap.containsKey(questionId) &&
                mTimeProvider.getCurrentTimestamp() < Objects.requireNonNull(cachedLastTimeMap.get(questionId)) + CACHING_TIME_OUT;
    }

    private void notifyFailure() {
        for (Listener listener : getListeners()) {
            listener.onQuestionDetailsFetchFailed();
        }
    }

    private void notifySuccess(QuestionSchema questionSchema) {
        for (Listener listener : getListeners()) {
            listener.onQuestionDetailsFetched(
                    new QuestionDetails(
                            questionSchema.getId(),
                            questionSchema.getTitle(),
                            questionSchema.getBody()
                    ));
        }
    }
}
