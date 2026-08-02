package my.edu.utar.numbers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.menu), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        View btnCount = findViewById(R.id.btnCount);
        View btnPlaceValue = findViewById(R.id.btnPlaceValue);
        View btnWords = findViewById(R.id.btnWords);
        View btnSequence = findViewById(R.id.btnSequence);

        setupButtonAnimations(btnCount, btnPlaceValue, btnWords, btnSequence);

        btnCount.setOnClickListener(v -> startGame("COUNTING"));
        btnPlaceValue.setOnClickListener(v -> startGame("PLACE_VALUE"));
        btnWords.setOnClickListener(v -> startGame("WORDS"));
        btnSequence.setOnClickListener(v -> startGame("SEQUENCE"));
    }

    private void startGame(String topicMode) {
        Intent intent = new Intent(MenuActivity.this, GameActivity.class);
        //forward msg to next activity
        intent.putExtra("TOPIC_MODE", topicMode);
        startActivity(intent);
    }

    private void setupButtonAnimations(android.view.View... views) {
        for (int i = 0; i < views.length; i++) {
            android.view.View currentView = views[i];

            currentView.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case android.view.MotionEvent.ACTION_DOWN:
                        v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).start();
                        break;
                    case android.view.MotionEvent.ACTION_UP:
                    case android.view.MotionEvent.ACTION_CANCEL:
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                        break;
                }
                return false;
            });
        }
    }
}