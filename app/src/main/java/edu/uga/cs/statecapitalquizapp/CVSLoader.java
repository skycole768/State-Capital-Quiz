package edu.uga.cs.statecapitalquizapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class CVSLoader {

    private Context context;
    private QuizDatabase dbHelper;

    public CVSLoader(Context context, QuizDatabase dbHelper) {
        this.context = context;
        this.dbHelper = dbHelper;
    }

    public void execute() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            loadDataInBackground();
            new Handler(Looper.getMainLooper()).post(this::onPostExecute);
        });
    }

    private void loadDataInBackground() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                context.getAssets().open("state_capital.csv")))) {
            String line;
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length < 7) continue;
                    ContentValues values = new ContentValues();
                    values.put("state", parts[0].trim());
                    values.put("capital_city", parts[1].trim());
                    values.put("second_city", parts[2].trim());
                    values.put("third_city", parts[3].trim());
                    values.put("statehood_year", Integer.parseInt(parts[4].trim()));
                    values.put("capital_since_year", Integer.parseInt(parts[5].trim()));
                    values.put("capital_rank", Integer.parseInt(parts[6].trim()));
                    db.insert("Questions", null, values);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract void onPostExecute();
}

