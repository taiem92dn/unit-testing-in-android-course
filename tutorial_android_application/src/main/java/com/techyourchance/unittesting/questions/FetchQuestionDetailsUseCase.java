package com.techyourchance.unittesting.questions;

import androidx.annotation.NonNull;

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

    private final Map<String, QuestionDetails> cachedValueMap = new HashMap<>();
    private final Map<String, Long> cachedLastTimeMap = new HashMap<>();

    public FetchQuestionDetailsUseCase(FetchQuestionDetailsEndpoint fetchQuestionDetailsEndpoint,
                                       TimeProvider timeProvider) {
        mFetchQuestionDetailsEndpoint = fetchQuestionDetailsEndpoint;
        mTimeProvider = timeProvider;
    }

    public void fetchQuestionDetailsAndNotify(String questionId) {
        QuestionDetails cachedData = getValidCachedData(questionId);
        if (cachedData != null) {
            notifySuccess(cachedData);
        } else {
            mFetchQuestionDetailsEndpoint.fetchQuestionDetails(questionId, new FetchQuestionDetailsEndpoint.Listener() {
                @Override
                public void onQuestionDetailsFetched(QuestionSchema question) {
                    cachedLastTimeMap.put(questionId, mTimeProvider.getCurrentTimestamp());
                    QuestionDetails questionDetails = schemaToQuestionDetails(question);
                    cachedValueMap.put(questionId, questionDetails);
                    notifySuccess(questionDetails);
                }

                @Override
                public void onQuestionDetailsFetchFailed() {
                    notifyFailure();
                }
            });
        }
    }

    private QuestionDetails getValidCachedData(String questionId) {
        QuestionDetails result = cachedValueMap.get(questionId);
        if (result != null &&
                mTimeProvider.getCurrentTimestamp() < Objects.requireNonNull(cachedLastTimeMap.get(questionId)) + CACHING_TIME_OUT)
            return result;

        return null;
    }

    private void notifyFailure() {
        for (Listener listener : getListeners()) {
            listener.onQuestionDetailsFetchFailed();
        }
    }

    private void notifySuccess(QuestionDetails question) {
        for (Listener listener : getListeners()) {
            listener.onQuestionDetailsFetched(question);
        }
    }

    @NonNull
    private static QuestionDetails schemaToQuestionDetails(QuestionSchema questionSchema) {
        return new QuestionDetails(
                questionSchema.getId(),
                questionSchema.getTitle(),
                questionSchema.getBody()
        );
    }
}
