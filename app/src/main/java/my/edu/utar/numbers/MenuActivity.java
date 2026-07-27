package my.edu.utar.numbers;

import android.content.Intent;
import android.os.Bundle;
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

        Button btnCount = findViewById(R.id.btnCount);
        Button btnPlaceValue = findViewById(R.id.btnPlaceValue);
        Button btnWords = findViewById(R.id.btnWords);
        Button btnSequence = findViewById(R.id.btnSequence);

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
}