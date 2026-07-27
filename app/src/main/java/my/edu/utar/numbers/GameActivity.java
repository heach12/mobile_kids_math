package my.edu.utar.numbers;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private TextView tvTitle;
    private TextView tvQuestion;
    private Button btnOption1, btnOption2, btnOption3;
    private String currentMode;
    private int correctAnswer;
    private int currentStreak = 0;
    private ProgressBar streak;
    private TextView tvCombo;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);
        streak = findViewById(R.id.streak);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.game), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvTitle = findViewById(R.id.tvTitle);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvCombo = findViewById(R.id.tvCombo);
        btnOption1 = findViewById(R.id.btnOption1);
        btnOption2 = findViewById(R.id.btnOption2);
        btnOption3 = findViewById(R.id.btnOption3);

        currentMode = getIntent().getStringExtra("TOPIC_MODE");

        btnOption1.setOnClickListener(v -> checkAnswer(btnOption1.getText().toString()));
        btnOption2.setOnClickListener(v -> checkAnswer(btnOption2.getText().toString()));
        btnOption3.setOnClickListener(v -> checkAnswer(btnOption3.getText().toString()));

        generateNextQuestion();
    }
    private void generateNextQuestion() {
        if (currentMode != null) {
            switch (currentMode) {
                case "COUNTING":
                    generateCountingQuestion();
                    break;
                case "PLACE_VALUE":
                    generatePlaceValueQuestion();
                    break;
                case "WORDS":
                    generateWordsQuestion();
                    break;
                case "SEQUENCE":
                    generateSequenceQuestion();
                    break;
            }
        }
    }

    // Game 1: Count Objects (bound 1-9)
    private void generateCountingQuestion() {
        String[] emojis = {"🍎", "🐶", "⭐", "🚗", "🎈", "🐱", "🍓"};
        String selectedEmoji = emojis[random.nextInt(emojis.length)];

        tvTitle.setText("How many " + selectedEmoji + " ?");

        correctAnswer = random.nextInt(9) + 1;

        StringBuilder objects = new StringBuilder();
        for (int i = 0; i < correctAnswer; i++) {
            objects.append(selectedEmoji).append(" ");
        }
        tvQuestion.setText(objects.toString());

        int wrong1,wrong2;
        do {
            wrong1 = random.nextInt(9) + 1;
            wrong2 = random.nextInt(9) + 1;
        }
        while (wrong1 == correctAnswer || wrong2 == correctAnswer || wrong1 == wrong2);

        //add all the 3 options into list for shuffle later on
        shuffleAndSetOptions(correctAnswer, wrong1, wrong2);
    }

    // Game 2: Place Value (bound 10-99)
    private void generatePlaceValueQuestion() {
        tvTitle.setText("What is the number?");

        int tens = random.nextInt(9) + 1;   // 1–9
        int ones = random.nextInt(10);      // 0–9

        correctAnswer = (tens * 10) + ones;

        if (random.nextBoolean()) {
            tvQuestion.setText(tens + " Ten(s)\n" + ones + " One(s)");
        } else {
            tvQuestion.setText(ones + " One(s)\n" + tens + " Ten(s)");
        }
        tvQuestion.setTextSize(40);

        int wrong1, wrong2;

        if (tens != ones) {
            // Swapped-digit trap (e.g. 35 -> 53)
            wrong1 = (ones * 10) + tens;
        } else {
            // tens == ones (e.g. 44): swapping gives back the same number
            // to a random distractor — but make sure it doesn't accidentally match the answer
            do {
                wrong1 = random.nextInt(90) + 10;
            } while (wrong1 == correctAnswer);
        }

        do {
            wrong2 = random.nextInt(90) + 10;
        } while (wrong2 == correctAnswer || wrong2 == wrong1);

        shuffleAndSetOptions(correctAnswer, wrong1, wrong2);
    }

    //Game 3 : Words vs Numbers (bound 1-10)
    private void generateWordsQuestion() {
        tvTitle.setText("Which number is this?");

        String[] words = {"Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten"};

        correctAnswer = random.nextInt(10) + 1;

        tvQuestion.setText(words[correctAnswer]);
        tvQuestion.setTextSize(60);

        int wrong1, wrong2;
        do {
            wrong1 = random.nextInt(10) + 1;
            wrong2 = random.nextInt(10) + 1;
        } while (wrong1 == correctAnswer || wrong2 == correctAnswer || wrong1 == wrong2);

        shuffleAndSetOptions(correctAnswer, wrong1, wrong2);
    }

    //Game 4 : Sequence (Bound +5/-5)
    private void generateSequenceQuestion() {
        tvTitle.setText("What is missing?");

        int step = random.nextInt(3) + 1;
        if (step == 3) step = 5;

        int start = random.nextInt(10) + 1;

        int[] sequence = new int[4];
        for (int i = 0; i < 4; i++) {
            sequence[i] = start + (i * step);
        }

        int missingIndex = random.nextInt(4);
        correctAnswer = sequence[missingIndex];

        StringBuilder questionText = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            if (i == missingIndex) {
                questionText.append("_");
            } else {
                questionText.append(sequence[i]);
            }

            if (i < 3) {
                questionText.append(", ");
            }
        }
        tvQuestion.setText(questionText.toString());
        tvQuestion.setTextSize(50);

        int wrong1, wrong2;
        do {
            wrong1 = correctAnswer + (random.nextInt(11) - 5);
            wrong2 = correctAnswer + (random.nextInt(11) - 5);

        } while (wrong1 == correctAnswer || wrong2 == correctAnswer || wrong1 == wrong2 || wrong1 <= 0 || wrong2 <= 0);

        shuffleAndSetOptions(correctAnswer, wrong1, wrong2);
    }

    private void checkAnswer(String selectedText) {
        int selectedNumber = Integer.parseInt(selectedText);

        if (selectedNumber == correctAnswer) {
            currentStreak++;

            // max = 5
            int targetProgress = Math.min(currentStreak, 5);
            // more fluent
            ObjectAnimator.ofInt(streak, "progress", streak.getProgress(), targetProgress)
                    .setDuration(400).start();

            String barColor = "#2196F3";
            switch (targetProgress) {
                case 1: barColor = "#2196F3"; break;
                case 2: barColor = "#4CAF50"; break;
                case 3: barColor = "#FFC107"; break;
                case 4: barColor = "#FF9800"; break;
                case 5: barColor = "#F44336"; break;
            }
            streak.setProgressTintList(android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor(barColor)
            ));

            if (currentStreak >= 5) {
                tvCombo.setText("UNSTOPPABLE! 👑 " + currentStreak + " COMBO!");
                playBounceAnimation(tvCombo);
                playBounceAnimation(tvTitle);
            } else if (currentStreak >= 3) {
                tvCombo.setText("ON FIRE! 🔥 " + currentStreak + " Combo!");
                playBounceAnimation(tvCombo);
            } else if (currentStreak == 2) {
                tvCombo.setText("Awesome! 🌟 2 Combo!");
            } else {
                tvCombo.setText("Correct! 🎉");
            }

            tvCombo.setTextColor(android.graphics.Color.parseColor("#2E7D32"));

            generateNextQuestion();

        } else {
            if (currentStreak > 2) {
                tvCombo.setText("Oh no! Combo broken. 💔");
            } else {
                tvCombo.setText("Oops! Try again. 😢");
            }

            tvCombo.setTextColor(android.graphics.Color.parseColor("#C62828"));

            currentStreak = 0;

            ObjectAnimator.ofInt(streak, "progress", streak.getProgress(), 0)
                    .setDuration(300).start();

            streak.setProgressTintList(android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#BDBDBD")
            ));

            playShakeAnimation(tvQuestion);
        }
    }
    private void shuffleAndSetOptions(int correct, int w1, int w2) {
        ArrayList<Integer> options = new ArrayList<>();
        options.add(correct);
        options.add(w1);
        options.add(w2);

        Collections.shuffle(options);

        btnOption1.setText(String.valueOf(options.get(0)));
        btnOption2.setText(String.valueOf(options.get(1)));
        btnOption3.setText(String.valueOf(options.get(2)));
    }
    private void playBounceAnimation(View view) {
        // effect that zoom and bounce back to ori (when correct)
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.5f, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.5f, 1f);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY);
        animator.setDuration(400);
        animator.start();
    }
    // effect, swing when wrong answer
    private void playShakeAnimation(View view) {
        //control coordinate X,mimic the shaking head action
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", 0f, 20f, -20f, 20f, -20f, 10f, -10f, 0f);
        animator.setDuration(300);
        animator.start();
    }
}
