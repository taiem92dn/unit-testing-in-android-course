package com.techyourchance.unittesting.questions;

import androidx.annotation.Nullable;

import java.util.Objects;

public class QuestionDetails {

    private final String mId;

    private final String mTitle;

    private final String mBody;

    public QuestionDetails(String id, String title, String body) {
        mId = id;
        mTitle = title;
        mBody = body;
    }

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getBody() {
        return mBody;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        QuestionDetails questionDetails = (QuestionDetails) obj;
        return Objects.equals(this.mId, questionDetails.mId)
                && Objects.equals(this.mTitle, questionDetails.mTitle)
                && Objects.equals(this.mBody, questionDetails.mBody);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mId, mTitle, mBody);
    }
}
