package com.example.wordseach;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private AwesomeValidation mAwesomeValidation;
    private TextView[][] textViews = new TextView[10][10];
    private Set<Integer> textPositions = new HashSet<>();
    private String word = "",plName="";
    private int playerPoints,clicks=10;
    private long startTime = 0;
    private TextView tvPoints,clicksLeft,playerName,timer;
    private Set<String> indexPositions = new HashSet<>();
    private List<String> wordsFound = new ArrayList<>();
    private Map<String, String> wordLocations = new HashMap<>();
    private EditText input;
    private Handler handler = new Handler();

    String[][] letters = new String[][]{{"A", "B", "C", "S", "W", "I", "F", "T", "G", "R"},
            {"K", "J", "M", "P", "R", "U", "L", "E", "K", "L"},
            {"O", "Q", "A", "S", "T", "V", "E", "L", "C", "F"},
            {"T", "I", "K", "V", "X", "M", "L", "B", "G", "O"},
            {"L", "J", "L", "D", "A", "N", "B", "A", "U", "V"},
            {"I", "Q", "E", "A", "O", "P", "A", "I", "X", "Y"},
            {"N", "M", "O", "B", "I", "L", "E", "R", "W", "O"},
            {"Z", "C", "D", "F", "H", "J", "R", "A", "V", "X"},
            {"Y", "M", "O", "P", "R", "X", "E", "V", "R", "T"},
            {"O", "B", "J", "E", "C", "T", "I", "V", "E", "C"}};

//    private Runnable timerRunnable = new Runnable() {
//        @Override
//        public void run() {
//            long mills = System.currentTimeMillis() - startTime;
//            int seconds = (int) (mills/1000);
//            int minutes = seconds/60;
//            seconds = seconds % 60;
//
//            timer.setText(String.format("%d:%"));
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();
        if (wordLocations.isEmpty()) {
            getWordLocations();
        }
        if(input == null)
        input = new EditText(MainActivity.this);
        setName();
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, ((TextView) v).getText().toString(), Toast.LENGTH_SHORT).show();
        v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        if(!textPositions.contains(v.getId())) {
            textPositions.add(v.getId());
            word +=((TextView) v).getText().toString();
            getClicksLeft();
        }

        if (word.length() < 10) {
            findWord();
        }

        if(word.length() == 10 && !findWord()){
            resetBackgroundColor();
            Toasty.error(this,"not a valid word, try again",Toast.LENGTH_SHORT,true).show();
            textPositions.clear();
            word= "";
        }
    }

    private void setName() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.getContext().setTheme(R.style.AlertDialogTheme);
        alertDialog.setTitle("Enter Your Name");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        if(mAwesomeValidation == null) {
            mAwesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
            mAwesomeValidation.addValidation(input, RegexTemplate.NOT_EMPTY, "Required");
        }

        alertDialog.setIcon(R.drawable.ic_person);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                plName = input.getText().toString();
                if(mAwesomeValidation.validate()){
                    playerName.setText(String.format("%s%s", "Welcome ", plName));
                } else
                    if(input.getParent() != null){
                        ((ViewGroup)input.getParent()).removeView(input);
                         setName();
                    }
            }
        });
        alertDialog.show();
    }

    private void getClicksLeft(){
        clicks--;
        if(clicks == 0)
            clicks = 10;
        clicksLeft.setText(String.valueOf(clicks));
    }

    private boolean findWord() {
            if (verifyWord(textViews)) {
                word = "";
                textPositions.clear();
                incrementPlayerScore();
                return true;
            }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private boolean verifyWord(TextView[][] textViews) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (textPositions.contains(textViews[i][j].getId())) {
                    Log.d("i,j", i + "," + j);
                    indexPositions.add(i+""+j);
                    String wordPos = concatenateIndexPositions(indexPositions);
                    if (wordLocations.containsKey(wordPos)) {
                        Log.d("found", "true");
                        clicks = 10;
                        clicksLeft.setText(String.valueOf(clicks));
                        Toasty.success(this, Objects.requireNonNull(String.format("%s found", wordLocations.get(wordPos).toUpperCase())),Toast.LENGTH_SHORT).show();
                        indexPositions.clear();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String concatenateIndexPositions(Set<String> indexPositions) {
        String indexes = "";
        for(String index : indexPositions){
            indexes += index;
        }
        return indexes;
    }

    private void resetBackgroundColor(){
        for(int i=0;i<10;i++){
            for(int j=0;j<10;j++){
                if(indexPositions.contains(i+""+j)){
                    textViews[i][j].setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }
            }
        }
        indexPositions.clear();
    }

    private void incrementPlayerScore() {
        playerPoints++;
        tvPoints.setText(String.valueOf(playerPoints));
        if(playerPoints == 6){
            congratulatePlayer();
        }
    }

    private void congratulatePlayer() {
        // Alert Dialog builder instance
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.getContext().setTheme(R.style.AlertDialogTheme);

        // Linear Layout View Group
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final TextView cr = new TextView(MainActivity.this);
        cr.setText("Congratulations");
        cr.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        cr.setTextColor(getResources().getColor(R.color.colorPrimary));
        cr.setTextSize(22);
        cr.setTypeface(Typeface.DEFAULT_BOLD);

        final TextView text = new TextView(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(32,8,8,8);
        text.setLayoutParams(lp);
        text.setText(String.format("Way to go %s, you completed the game in %s", plName, timer.getText().toString()));
        text.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        text.setTextColor(getResources().getColor(R.color.colorPrimary));
        text.setTextSize(22);

        layout.addView(cr);
        layout.addView(text);

        alertDialog.setView(layout);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = input.getText().toString();
                if(mAwesomeValidation.validate()){
                    playerName.setText(String.format("%s%s", "Welcome ", name));
                } else
                if(input.getParent() != null){
                    ((ViewGroup)input.getParent()).removeView(input);
                    setName();
                }
            }
        });

        alertDialog.show();
    }

    private void initializeComponents() {
        tvPoints = findViewById(R.id.score);
        clicksLeft = findViewById(R.id.clicksLeft);
        playerName = findViewById(R.id.playerName);
        timer = findViewById(R.id.timer);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                String textID = "txt" + i + j;
                int resID = getResources().getIdentifier(textID, "id", getPackageName());
                textViews[i][j] = findViewById(resID);
                textViews[i][j].setText(letters[i][j]);
                textViews[i][j].setOnClickListener(this);
            }
        }
    }

    private void getWordLocations() {
        wordLocations.put("0304050607", "swift");
        wordLocations.put("605040302010", "kotlin");
        wordLocations.put("666162636465", "mobile");
        wordLocations.put("99909192939495969798", "objectivec");
        wordLocations.put("7767574737271787", "variable");
        wordLocations.put("11223344", "java");
    }

    private void reset(){
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                textViews[i][j].setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.reset){
            word = "";
            clicks = 10;
            playerPoints = 0;
            tvPoints.setText("0");
            clicksLeft.setText("10");
            indexPositions.clear();
            reset();
        }
        return super.onOptionsItemSelected(item);
    }
}
