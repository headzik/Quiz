package com.example.lukas.quiz;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class QuizFragment extends Fragment {

    private final int MAX_ANSWERS = 4;

    //for saving in the InstanceState
    String STATE_CHECKED_ANSWERS = "checkedAnswers";
    String STATE_SHOWN_ANSWERS = "shownAnswers";
    String STATE_CURRENT_QUESTION = "currentQuestion";
    String STATE_IS_ANSWERED = "isAnswered";
    String STATE_SCORE = "score";
    String STATE_POINTS_AVAILABLE = "pointsAvailable";
    String STATE_ROUND = "round";
    String STATE_QUESTIONS = "questions";

    //global values that are being saved/read from InstanceState
    List<String> shownAnswers;
    List<Integer> checkedAnswers;
    Question currentQuestion;
    boolean isAnswered;
    int score;
    int pointsAvailable;
    int round;

    List<Question> questions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            questions = savedInstanceState.getParcelableArrayList(STATE_QUESTIONS);
            shownAnswers = savedInstanceState.getStringArrayList(STATE_SHOWN_ANSWERS);
            checkedAnswers = savedInstanceState.getIntegerArrayList(STATE_CHECKED_ANSWERS);
            isAnswered = savedInstanceState.getBoolean(STATE_IS_ANSWERED);
            currentQuestion = savedInstanceState.getParcelable(STATE_CURRENT_QUESTION);
            score = savedInstanceState.getInt(STATE_SCORE);
            pointsAvailable = savedInstanceState.getInt(STATE_POINTS_AVAILABLE);
            round = savedInstanceState.getInt(STATE_ROUND);
        } else {
            getQuestions();
            cleanData();
            getNextQuestion(false);
            setQuestionsToDisplay();
            score = 0;
            pointsAvailable = 0;
            round = 0;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        cleanScreen();
        setDisplay();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        RadioGroup radioGroup = (RadioGroup) getActivity().findViewById(R.id.radio_group_id);

        checkedAnswers.clear();

        for(int i = 0; i < MAX_ANSWERS; i++) {
            if(((Checkable)radioGroup.getChildAt(i)).isChecked()) {
                checkedAnswers.add(i);
            }
        }

        savedInstanceState.putIntegerArrayList(STATE_CHECKED_ANSWERS, new ArrayList<>(checkedAnswers));
        savedInstanceState.putStringArrayList(STATE_SHOWN_ANSWERS, new ArrayList<>(shownAnswers));
        savedInstanceState.putParcelable(STATE_CURRENT_QUESTION, currentQuestion);
        savedInstanceState.putBoolean(STATE_IS_ANSWERED, isAnswered);
        savedInstanceState.putInt(STATE_SCORE, score);
        savedInstanceState.putInt(STATE_POINTS_AVAILABLE, pointsAvailable);
        savedInstanceState.putInt(STATE_ROUND, round);
        savedInstanceState.putParcelableArrayList(STATE_QUESTIONS, new ArrayList<>(questions));

        super.onSaveInstanceState(savedInstanceState);
    }

    private void displayQuestion() {
        TextView questionTextView = (TextView) getActivity().findViewById(R.id.question_text_view);

        questionTextView.setText(currentQuestion.getQuestion());

        RelativeLayout quizLayout = (RelativeLayout) getActivity().findViewById(R.id.quiz);

        if(quizLayout != null) {
            RadioGroup radioGroup = new RadioGroup(this.getContext());
            radioGroup.setId(R.id.radio_group_id);

            int orientation = getActivity().getResources().getConfiguration().orientation == 1 ?
                    LinearLayout.VERTICAL : LinearLayout.HORIZONTAL;
            radioGroup.setOrientation(orientation);

            if(currentQuestion.getIsMultipleChoice()) {
                createAnswers(radioGroup, Switch.class);
            } else {
                createAnswers(radioGroup, RadioButton.class);
            }

            RelativeLayout.LayoutParams rgLayoutParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            rgLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

            quizLayout.addView(radioGroup, rgLayoutParams);
        }
    }

    private void setDisplay() {
        displayQuestion();
        createClearButton();
        if(isAnswered) {
            checkAnswer();
        }
    }

    private void getQuestions() {
        questions = ((MainActivity) getActivity()).db.getAllQuestions();
    }

    private void createAnswers(RadioGroup radioGroup, Class viewClass) {
        for(int i = 0; i < MAX_ANSWERS; i++) {
            View answer = viewClass == Switch.class ?
                    new Switch(this.getContext()) : new RadioButton(this.getContext());

            answer.setId(i);
            ((TextView) answer).setText(shownAnswers.get(i));

            if(checkedAnswers.contains(i)) {
                ((Checkable) answer).setChecked(true);
            }
            radioGroup.addView(answer);
        }
    }

    private void setQuestionsToDisplay() {
        int rightAnswersAmount = currentQuestion.getIsMultipleChoice() ?
                ThreadLocalRandom.current().nextInt(1, MAX_ANSWERS + 1) : 1;

        List<String> shuffledAnswers = shuffleArray(
                currentQuestion.getAnswers().toArray(new String[rightAnswersAmount]));
        List<String> shuffledPossibleAnswers = shuffleArray(
                currentQuestion.getPossibleAnswers().toArray(new String[MAX_ANSWERS - rightAnswersAmount]));

        int counter = 0;

        for (String correctAnswer : shuffledAnswers) {
            shownAnswers.add(correctAnswer);
            if (++counter == rightAnswersAmount) break;
        }
        counter = 0;
        for (String wrongAnswer : shuffledPossibleAnswers) {
            shownAnswers.add(wrongAnswer);
            if (++counter == (MAX_ANSWERS - rightAnswersAmount)) break;
        }

        shownAnswers = shuffleArray(shownAnswers.toArray(new String[MAX_ANSWERS]));
    }

    public void checkAnswer() {
        RadioGroup radioGroup = (RadioGroup) getActivity().findViewById(R.id.radio_group_id);

        if(radioGroup != null) {
            List<String> currentAnswers = currentQuestion.getAnswers();
            for (int i = 0; i < MAX_ANSWERS; i++) {
                TextView answer = ((TextView) radioGroup.getChildAt(i));
                if (currentAnswers.contains(answer.getText())) {
                    answer.setTextColor(getResources().getColor(R.color.colorCorrect));
                }
                answer.setClickable(false);
            }
            if (!isAnswered) {
                managePoints(radioGroup);
                isAnswered = true;
            }
        }

        Button clearButton = (Button)  getActivity().findViewById(R.id.clear_button_id);
        clearButton.setVisibility(View.INVISIBLE);

        if(questions.size() > 1) {
            createNextQuestionButton();
        } else {
            createSeeResultButton();
        }
    }

    private void createSeeResultButton() {
        Button seeResultBtn = new Button(this.getContext());
        seeResultBtn.setText("See Result");

        seeResultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).showResult();
            }
        });

        RelativeLayout.LayoutParams relativeLayoutParams = (RelativeLayout.LayoutParams) getActivity().
                findViewById(R.id.check_answer_button).getLayoutParams();

        ((RelativeLayout) getActivity().findViewById(R.id.quiz)).addView(seeResultBtn, relativeLayoutParams);
    }

    private void createNextQuestionButton() {
        Button nextQuestionBtn = new Button(this.getContext());
        nextQuestionBtn.setText("Next");
        nextQuestionBtn.setId(R.id.next_button_id);

        nextQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cleanData();
                cleanScreen();
                getNextQuestion(true);
                setQuestionsToDisplay();
                displayQuestion();
                createClearButton();

            }
        });

        RelativeLayout.LayoutParams relativeLayoutParams = (RelativeLayout.LayoutParams) getActivity().
                findViewById(R.id.check_answer_button).getLayoutParams();

        ((RelativeLayout) getActivity().findViewById(R.id.quiz)).addView(nextQuestionBtn, relativeLayoutParams);
    }

    private void cleanData() {
        shownAnswers = new ArrayList<>();
        checkedAnswers = new ArrayList<>();
        currentQuestion = null;
        isAnswered = false;
    }

    private void cleanScreen() {

        RelativeLayout rl = (RelativeLayout) getActivity().findViewById(R.id.quiz);
        rl.removeView(getActivity().findViewById(R.id.radio_group_id));
        rl.removeView(getActivity().findViewById(R.id.next_button_id));
        rl.removeView(getActivity().findViewById(R.id.feedback_text_id));
        rl.removeView(getActivity().findViewById(R.id.clear_button_id));
        rl.removeView(getActivity().findViewById(R.id.score_textview_id));
    }

    private List<String> shuffleArray(String [] array) {

        Random random = new Random();

        int count = array.length;
        for (int i = count; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i));
        }

        return new ArrayList<>(Arrays.asList(array));
    }

    private void swap(String[] array, int i, int j) {
        String temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    private void clearAnswers() {
        RadioGroup radioGroup = (RadioGroup) getActivity().findViewById(R.id.radio_group_id);

        if(radioGroup != null) {
            for(int i = 0; i < MAX_ANSWERS; i++) {
                ((Checkable) radioGroup.getChildAt(i)).setChecked(false);
            }
        }
    }

    private void createClearButton() {
        RelativeLayout quizLayout = (RelativeLayout) getActivity().findViewById(R.id.quiz);

        if(quizLayout != null) {
            Button clearButton = new Button(getContext());
            clearButton.setText("Clear");
            clearButton.setId(R.id.clear_button_id);

            RelativeLayout.LayoutParams cbLayoutParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            cbLayoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.radio_group_id);
            cbLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);

            clearButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clearAnswers();
                }
            });

            quizLayout.addView(clearButton, cbLayoutParams);
        }
    }

    private void getNextQuestion(boolean shouldRemove) {
        if(currentQuestion == null) {
            int questionID = ThreadLocalRandom.current().nextInt(0, MAX_ANSWERS+1 - ++round);
            currentQuestion = questions.get(questionID);
            if(shouldRemove) { questions.remove(questionID); }
        }
    }

    private void managePoints(RadioGroup radioGroup) {
        List<String> currentAnswers = currentQuestion.getAnswers();
        for (int i = 0; i < MAX_ANSWERS; i++) {
            TextView answer = ((TextView) radioGroup.getChildAt(i));
            if (currentAnswers.contains(answer.getText())) {
                pointsAvailable++;
                if(((Checkable) answer).isChecked()) {
                    score++;
                }
            } else {
                if(((Checkable) answer).isChecked()) {
                    score--;
                }
            }
        }
    }
}
