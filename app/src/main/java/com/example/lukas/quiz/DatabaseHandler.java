package com.example.lukas.quiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Lukas on 01/12/2016.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "contactsManager";

    private static final String TABLE_QUESTIONS = "questions";

    private static final String KEY_ID = "id";
    private static final String KEY_QUESTION = "question";
    private static final String KEY_ANSWERS = "answers";
    private static final String KEY_POSSIBLE_ANSWERS = "possibleAnswers";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_QUESTIONS_TABLE = "CREATE TABLE " + TABLE_QUESTIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_QUESTION + " TEXT,"
                + KEY_ANSWERS + " TEXT," + KEY_POSSIBLE_ANSWERS
                + " TEXT" + ")";
        db.execSQL(CREATE_QUESTIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);

        onCreate(db);
    }

    public void addQuestion(Question question) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_QUESTION, question.getQuestion());
        values.put(KEY_ANSWERS, question.getAnswers().toString());
        values.put(KEY_POSSIBLE_ANSWERS, question.getPossibleAnswers().toString());

        db.insert(TABLE_QUESTIONS, null, values);
        db.close();
    }

    private List<String> getListFromString(String string) {
        String tempString = string.replace("[","");
        tempString = tempString.replace("]","");
        String [] answers = tempString.split(", ");
        return new ArrayList<>(Arrays.asList(answers));
    }

    public List<Question> getAllQuestions() {
        List<Question> questions = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_QUESTIONS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Question question = new Question(cursor.getString(1), getListFromString(cursor.getString(2)),
                        getListFromString(cursor.getString(3)), Integer.parseInt(cursor.getString(0)));
                questions.add(question);
            } while (cursor.moveToNext());
        }

        return questions;
    }

    public void deleteQuestion(Question question) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_QUESTIONS, KEY_ID + " = ?",
                new String[] { String.valueOf(question.getId()) });
        db.close();
    }
}