package com.techyourchance.unittesting.testdata;

import com.techyourchance.unittesting.questions.QuestionDetails;

public class QuestionDetailsTestData {

    public static QuestionDetails getQuestionDetail() {
        return new QuestionDetails("id", "title", "body");
    }
}
