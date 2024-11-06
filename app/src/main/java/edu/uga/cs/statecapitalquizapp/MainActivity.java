package edu.uga.cs.statecapitalquizapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        // Set up the main layout
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the quiz database helper
        QuizDatabase dbHelper = new QuizDatabase(this);

        // Load CSV data into the database asynchronously
        new CVSLoader(this, dbHelper) {
            @Override
            protected void onPostExecute() {
                // Notify user or proceed with the app setup
                // For example, you can display a message or load the first fragment/activity
            }
        }.execute();
    }
}
