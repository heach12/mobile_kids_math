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
    private int currentTotal = 0;
    private int correctAnswer;
    private int currentStreak = 0;
    private ProgressBar streak;
    private TextView tvCombo;
    private Random random = new Random();
    TextView tvTotal;
    Button btnAdd10, btnAdd1;

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
        tvTotal = findViewById(R.id.tvTotal);
        btnAdd10= findViewById(R.id.btnAdd10);
        btnAdd1 = findViewById(R.id.btnAdd1);

        setupButtonAnimations(btnOption1, btnOption2, btnOption3);

        currentMode = getIntent().getStringExtra("TOPIC_MODE");

        btnOption1.setOnClickListener(v -> checkAnswer(btnOption1.getText().toString()));
        btnOption2.setOnClickListener(v -> checkAnswer(btnOption2.getText().toString()));
        btnOption3.setOnClickListener(v -> checkAnswer(btnOption3.getText().toString()));
        btnAdd10.setOnClickListener(v -> addPlaceValue(10));
        btnAdd1.setOnClickListener(v -> addPlaceValue(1));

        generateNextQuestion();
    }
    private void generateNextQuestion() {
        currentTotal = 0;
        TextView tvFeedbackMessage = findViewById(R.id.tvFeedbackMessage);
        if (tvFeedbackMessage != null) {
            tvFeedbackMessage.setText("");
        }

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

        // Show this game's own buttons, hide the multiple-choice ones
        btnOption1.setVisibility(View.GONE);
        btnOption2.setVisibility(View.GONE);
        btnOption3.setVisibility(View.GONE);
        tvTotal.setVisibility(View.VISIBLE);
        btnAdd10.setVisibility(View.VISIBLE);
        btnAdd1.setVisibility(View.VISIBLE);

        correctAnswer = random.nextInt(90) + 1; // 1 to 99
        currentTotal = 0;
        tvQuestion.setText(" " + correctAnswer);
        tvTotal.setText("Your total: " + currentTotal);
    }

    //Game 3 : Words vs Numbers (bound 1-10)
    private void generateWordsQuestion() {
        tvTitle.setText("Which number is this?");

        // 1. Generate a random number between 1 and 99
        correctAnswer = random.nextInt(99) + 1;

        // 2. Turn the number into a word
        tvQuestion.setText(numberToWord(correctAnswer));
        tvQuestion.setTextSize(50);

        int wrong1 = -1;
        int wrong2 = -1;

        // 3. The Trick Logic (50% chance to happen)
        boolean useTrick = random.nextBoolean();
        int trickNumber = -1;

        if (useTrick) {
            // Find the tens and units (e.g., for 68: tens = 6, units = 8)
            int tens = correctAnswer / 10;
            int units = correctAnswer % 10;

            // Swap them (e.g., 8 * 10 + 6 = 86)
            trickNumber = (units * 10) + tens;

            // If the reversed number is the same (like 22, 55) or invalid (like 0), turn the trick off
            if (trickNumber == correctAnswer || trickNumber == 0) {
                useTrick = false;
            }
        }

        // 4. Generate the wrong answers
        do {
            if (useTrick) {
                wrong1 = trickNumber; // Put the tricky 86 here!
            } else {
                wrong1 = random.nextInt(99) + 1; // Or just use a random number
            }

            wrong2 = random.nextInt(99) + 1; // The second wrong answer is always random

            // Make sure no buttons have duplicate numbers!
        } while (wrong1 == correctAnswer || wrong2 == correctAnswer || wrong1 == wrong2);

        // 5. Shuffle and set the buttons
        shuffleAndSetOptions(correctAnswer, wrong1, wrong2);
    }

    private String numberToWord(int number) {
        if (number == 0) return "Zero";

        // Arrays for the unique words
        String[] ones = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
                "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
        String[] tens = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};

        // If it's less than 20, just grab it from the ones array
        if (number < 20) {
            return ones[number];
        } else {
            // Calculate the tens place (e.g., 45 / 10 = 4 -> Forty)
            int tenDigit = number / 10;
            // Calculate the ones place (e.g., 45 % 10 = 5 -> Five)
            int unitDigit = number % 10;

            // If it's a perfect ten (like 20, 30), don't add a dash
            if (unitDigit == 0) {
                return tens[tenDigit];
            } else {
                // Combine them with a dash (e.g., Forty-five)
                return tens[tenDigit] + "-" + ones[unitDigit].toLowerCase();
            }
        }
    }

    //Game 4 : Sequence (Bound +5/-5)
    private void generateSequenceQuestion() {
        tvTitle.setText("What is missing?");

        int step = random.nextInt(3) + 1;
        if (step == 3) step = 5;

        int start;
        boolean isAscending = random.nextBoolean();

        if (isAscending) {
            start = random.nextInt(10) + 1;
        } else {
            start = random.nextInt(10) + (3 * step) + 1;
            step = -step;
        }

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
        tvQuestion.setTextSize(30);

        int wrong1, wrong2;
        do {
            wrong1 = correctAnswer + (random.nextInt(11) - 5);
            wrong2 = correctAnswer + (random.nextInt(11) - 5);
        } while (wrong1 == correctAnswer || wrong2 == correctAnswer || wrong1 == wrong2 || wrong1 <= 0 || wrong2 <= 0);

        shuffleAndSetOptions(correctAnswer, wrong1, wrong2);
    }

    private void checkAnswer(String selectedText) {
        int selectedNumber = Integer.parseInt(selectedText);
        TextView tvFeedbackMessage = findViewById(R.id.tvFeedbackMessage);

        // 1.prevent duplicate click
        btnOption1.setEnabled(false);
        btnOption2.setEnabled(false);
        btnOption3.setEnabled(false);
        btnAdd10.setEnabled(false);
        btnAdd1.setEnabled(false);

        if (selectedNumber == correctAnswer) {
            currentStreak++;

            // progress bar
            int targetProgress = Math.min(currentStreak, 5);
            ObjectAnimator.ofInt(streak, "progress", streak.getProgress(), targetProgress).setDuration(400).start();

            String barColor = "#2196F3";
            switch (targetProgress) {
                case 1: barColor = "#2196F3"; break;
                case 2: barColor = "#4CAF50"; break;
                case 3: barColor = "#FFC107"; break;
                case 4: barColor = "#FF9800"; break;
                case 5: barColor = "#F44336"; break;
            }
            streak.setProgressTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor(barColor)));

            // Combo
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

            tvFeedbackMessage.setAlpha(1f);
            tvFeedbackMessage.setText("Amazing! 🎉");
            tvFeedbackMessage.setTextColor(android.graphics.Color.parseColor("#4CAF50"));

            // badge
            if (currentStreak == 3 || currentStreak == 10) {
                View badgeLayout = findViewById(R.id.layoutBadgeReward);
                Button btnCloseBadge = findViewById(R.id.btnCloseBadge);
                TextView badgeTitle = findViewById(R.id.badgeTitle);
                TextView badgeSubtitle = findViewById(R.id.badgeSubtitle);

                if (badgeLayout != null) {
                    // Change text based on the streak!
                    if (currentStreak == 3) {
                        badgeTitle.setText("🌟 Super Star! 🌟");
                        badgeSubtitle.setText("3 Combo Badge Unlocked!");
                    } else if (currentStreak == 10) {
                        badgeTitle.setText("🏆 Math Genius! 🏆");
                        badgeSubtitle.setText("10 Combo Badge Unlocked!\nTry to challenge other game?");
                    }

                    badgeLayout.setVisibility(View.VISIBLE);
                    badgeLayout.setScaleX(0f);
                    badgeLayout.setScaleY(0f);
                    badgeLayout.animate().scaleX(1f).scaleY(1f).setDuration(300).start();
                }

                if (btnCloseBadge != null) {
                    btnCloseBadge.setOnClickListener(v -> {
                        if (badgeLayout != null) {
                            badgeLayout.setVisibility(View.GONE);
                        }
                        // close badge then proceed to next ques
                        btnOption1.setEnabled(true);
                        btnOption2.setEnabled(true);
                        btnOption3.setEnabled(true);
                        btnAdd10.setEnabled(true);
                        btnAdd1.setEnabled(true);
                        generateNextQuestion();
                    });
                }
            } else {
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                    btnOption1.setEnabled(true);
                    btnOption2.setEnabled(true);
                    btnOption3.setEnabled(true);
                    btnAdd10.setEnabled(true);
                    btnAdd1.setEnabled(true);
                    generateNextQuestion();
                }, 1750);
            }

        } else {
            // Check if player has the shield (streak of 5 or more)
            boolean hasExtraLife = (currentStreak >= 5);

            if (hasExtraLife) {
                // Shield logic: Give them a second chance without changing the question
                tvCombo.setText("Shield Activated! 🛡️ Try again!");
                tvCombo.setTextColor(android.graphics.Color.parseColor("#FF9800")); // Orange warning color

                // Consume the shield by reducing the streak to 4 instead of resetting to 0
                currentStreak = 4;
                ObjectAnimator.ofInt(streak, "progress", streak.getProgress(), currentStreak).setDuration(300).start();

                playShakeAnimation(tvQuestion);

                tvFeedbackMessage.setAlpha(1f);
                tvFeedbackMessage.setText("Saved by Shield! Try again! 💡");
                tvFeedbackMessage.setTextColor(android.graphics.Color.parseColor("#FF9800"));
                tvFeedbackMessage.setScaleX(0.8f);
                tvFeedbackMessage.setScaleY(0.8f);
                tvFeedbackMessage.animate().scaleX(1f).scaleY(1f).setDuration(200).start();

                // buffer time for shield: unlock buttons but DO NOT generate next question
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                    btnOption1.setEnabled(true);
                    btnOption2.setEnabled(true);
                    btnOption3.setEnabled(true);
                    btnAdd10.setEnabled(true);
                    btnAdd1.setEnabled(true);
                }, 1750);

            } else {
                // wrong logic (Original behavior)
                if (currentStreak > 2) {
                    tvCombo.setText("Oh no! Combo broken. 💔");
                } else {
                    tvCombo.setText("Oops! Try again. 😢");
                }
                tvCombo.setTextColor(android.graphics.Color.parseColor("#C62828"));

                currentStreak = 0;

                // streak progress reset
                ObjectAnimator.ofInt(streak, "progress", streak.getProgress(), 0).setDuration(300).start();
                streak.setProgressTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#BDBDBD")));

                playShakeAnimation(tvQuestion);

                // wrong notification
                tvFeedbackMessage.setAlpha(1f);
                if (currentMode != null && currentMode.equals("PLACE_VALUE")) {
                    tvFeedbackMessage.setText("Oops! You went over! 💥");
                } else {
                    tvFeedbackMessage.setText("Oops! The answer was " + correctAnswer + " 💡");
                }
                tvFeedbackMessage.setTextColor(android.graphics.Color.parseColor("#FF5722"));
                tvFeedbackMessage.setScaleX(0.8f);
                tvFeedbackMessage.setScaleY(0.8f);
                tvFeedbackMessage.animate().scaleX(1f).scaleY(1f).setDuration(200).start();

                // buffer time 3s (Kept your comment, but using 1750 as in your code)
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                    btnOption1.setEnabled(true);
                    btnOption2.setEnabled(true);
                    btnOption3.setEnabled(true);
                    btnAdd10.setEnabled(true);
                    btnAdd1.setEnabled(true);
                    generateNextQuestion();
                }, 1750);
            }
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
    private void addPlaceValue(int amount) {

        if (!btnAdd10.isEnabled()) {
            return;
        }
        // 1. Add to the current total
        currentTotal += amount;

        // 2. Update the text on the screen
        TextView tvTotal = findViewById(R.id.tvTotal);
        tvTotal.setText("Your total: " + currentTotal);

        // 3. Check if they won or lost!
        if (currentTotal == correctAnswer) {
            btnAdd10.setEnabled(false);
            btnAdd1.setEnabled(false);
            // CORRECT! They hit the exact number.
            // We pass the correct answer to your existing check method
            checkAnswer(String.valueOf(correctAnswer));

        } else if (currentTotal > correctAnswer) {
            btnAdd10.setEnabled(false);
            btnAdd1.setEnabled(false);
            // WRONG! They went over the target number.
            // We pass a fake wrong answer (like -1) to trigger your "Oops!" logic
            checkAnswer("-1");
        }
        // If currentTotal is LESS than the correctAnswer, we do nothing and let them keep clicking!
    }
}
