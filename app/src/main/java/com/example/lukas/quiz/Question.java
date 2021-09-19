package com.example.lukas.quiz;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lukas on 07.11.2016.
 */

public class Question implements Parcelable {
    private int id = 0;
    private String question;
    private List<String> answers;
    private boolean isMultipleChoice;
    private List<String> possibleAnswers;

    public Question(String question, List<String> answers, List<String> possibleAnswers, int id) {
        this.question = question;
        this.answers = new ArrayList<>(answers);
        this.possibleAnswers = possibleAnswers;
        if(answers.size() > 1) this.isMultipleChoice = true;
        this.id = id;
    }

    protected Question(Parcel in) {
        id = in.readInt();
        question = in.readString();
        answers = in.createStringArrayList();
        isMultipleChoice = in.readByte() != 0;
        possibleAnswers = in.createStringArrayList();
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public List<String> getPossibleAnswers() { return possibleAnswers; }

    public boolean getIsMultipleChoice() { return isMultipleChoice; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(question);
        parcel.writeStringList(answers);
        parcel.writeByte((byte) (isMultipleChoice ? 1 : 0));
        parcel.writeStringList(possibleAnswers);
    }
}
