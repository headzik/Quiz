package com.example.lukas.quiz;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends FragmentActivity {

    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(db == null) {
            db = new DatabaseHandler(this);
            deleteQuestions();
            putQuestionsToDataBase();
        }

        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create an instance of ExampleFragment
            WelcomeFragment welcomeFragment = new WelcomeFragment();

            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            //firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, welcomeFragment).commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        deleteQuestions();
    }

    public void deleteQuestions() {
        List<Question> questions = db.getAllQuestions();

        for(Question q : questions) {
            db.deleteQuestion(q);
        }
    }

    public void checkAnswer(View v) {
        QuizFragment quizFragment = (QuizFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if(quizFragment != null) {
            quizFragment.checkAnswer();
        }
    }

    public void showResult() {
        QuizFragment quizFragment = (QuizFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("SCORE", quizFragment.score);
        intent.putExtra("POINTS_AVAILABLE", quizFragment.pointsAvailable);
        startActivity(intent);
        finish();
    }

    public void start(View v) {
        QuizFragment quizFragment = new QuizFragment();

        getSupportFragmentManager().beginTransaction().
                replace(R.id.fragment_container, quizFragment).commit();
    }

    public void putQuestionsToDataBase() {
        String content1 = "At which year the  Battle of Vienna took place?";
        String content2 = "Which animals are mammals?";
        String content3 = "Which countries are neighbours of Poland?";
        String content4 = "How many wheels are in the car?";

        String[] answersArray1 = {"1683"};
        String[] answersArray2 = {"Dog", "Cow", "Dolphin", "Monkey"};
        String[] answersArray3 = {"Germany", "Czech Republic", "Slovakia", "Belarus"};
        String[] answersArray4 = {"4"};

        List<String> answers1 = Arrays.asList(answersArray1);
        List<String> answers2 = Arrays.asList(answersArray2);
        List<String> answers3 = Arrays.asList(answersArray3);
        List<String> answers4 = Arrays.asList(answersArray4);

        String[] possibleAnswersArray1 = {"1234", "464", "2010"};
        String[] possibleAnswersArray2 = {"Herring", "Sparrow", "Shark"};
        String[] possibleAnswersArray3 = {"USA", "China", "France"};
        String[] possibleAnswersArray4 = {"1", "2", "3"};

        List<String> possibleAnswers1 = Arrays.asList(possibleAnswersArray1);
        List<String> possibleAnswers2 = Arrays.asList(possibleAnswersArray2);
        List<String> possibleAnswers3 = Arrays.asList(possibleAnswersArray3);
        List<String> possibleAnswers4 = Arrays.asList(possibleAnswersArray4);

        Question question1 = new Question(content1, answers1, possibleAnswers1, 0);
        Question question2 = new Question(content2, answers2, possibleAnswers2, 1);
        Question question3 = new Question(content3, answers3, possibleAnswers3, 2);
        Question question4 = new Question(content4, answers4, possibleAnswers4, 3);

        db.addQuestion(question1);
        db.addQuestion(question2);
        db.addQuestion(question3);
        db.addQuestion(question4);

    }

}

