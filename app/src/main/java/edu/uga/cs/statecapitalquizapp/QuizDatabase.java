package edu.uga.cs.statecapitalquizapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QuizDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "quiz.db";
    private static final int DATABASE_VERSION = 1;

    public QuizDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Questions Table
        db.execSQL("CREATE TABLE Questions (" +
                "question_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "state TEXT NOT NULL, " +
                "capital_city TEXT NOT NULL, " +
                "second_city TEXT, " +
                "third_city TEXT, " +
                "statehood_year INTEGER, " +
                "capital_since_year INTEGER, " +
                "capital_rank INTEGER);");

        // Create Quizzes Table
        db.execSQL("CREATE TABLE Quizzes (" +
                "quiz_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "quiz_date TEXT NOT NULL, " +
                "quiz_score INTEGER DEFAULT 0, " +
                "questions_answered INTEGER DEFAULT 0);");

        // Create QuizQuestions Table
        db.execSQL("CREATE TABLE QuizQuestions (" +
                "quiz_question_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "quiz_id INTEGER, " +
                "question_id INTEGER, " +
                "user_answer TEXT, " +
                "FOREIGN KEY (quiz_id) REFERENCES Quizzes (quiz_id), " +
                "FOREIGN KEY (question_id) REFERENCES Questions (question_id));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Questions");
        db.execSQL("DROP TABLE IF EXISTS Quizzes");
        db.execSQL("DROP TABLE IF EXISTS QuizQuestions");
        onCreate(db);
    }

    public List<Integer> getRandomQuestions(int count) {
        List<Integer> questionIds = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT question_id FROM Questions ORDER BY RANDOM() LIMIT ?",
                new String[]{String.valueOf(count)});
        while (cursor.moveToNext()) {
            questionIds.add(cursor.getInt(0));
        }
        cursor.close();
        return questionIds;
    }

    public long createQuiz(List<Integer> questionIds) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues quizValues = new ContentValues();
        quizValues.put("quiz_date", getCurrentDate());
        long quizId = db.insert("Quizzes", null, quizValues);

        for (int questionId : questionIds) {
            ContentValues quizQuestionValues = new ContentValues();
            quizQuestionValues.put("quiz_id", quizId);
            quizQuestionValues.put("question_id", questionId);
            db.insert("QuizQuestions", null, quizQuestionValues);
        }
        return quizId;
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    public void updateQuizProgress(long quizId, int questionsAnswered, int correctAnswers) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("questions_answered", questionsAnswered);
        values.put("quiz_score", correctAnswers);
        db.update("Quizzes", values, "quiz_id = ?", new String[]{String.valueOf(quizId)});
    }

    public void saveUserAnswer(long quizId, long questionId, String answer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quiz_id", quizId);
        values.put("question_id", questionId);
        values.put("user_answer", answer);
        db.insert("QuizQuestions", null, values);
    }

}
