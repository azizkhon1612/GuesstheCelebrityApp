package com.example.guessthecelebrityapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ImageView imageView ;
    LinearLayout gameOver;
    ConstraintLayout game;
    TextView score,gameOverTextView;
    Button option1,option2,option3,option4;
    String result = null,message;
    ArrayList<String> celeburls = new ArrayList<String>();
    ArrayList<String> celebnames = new ArrayList<String>();
    ArrayList<String> answers = new ArrayList<String>();
    Random r =new Random();
    int correctOption;
    int celebIndex;
    int count=0;//counter to count score
    int maxlimit=0;


    public void nextImage(){
        ImageDownloader task = new ImageDownloader();
        Bitmap image;

        try {
            celebIndex = r.nextInt(celebnames.size());
            correctOption = r.nextInt(4)+1;
            image = task.execute(celeburls.get(celebIndex)).get();
            imageView.setImageBitmap(image);
            answers.clear();
            for (int i = 1; i <=4; i++) {
                if (i == correctOption)
                    answers.add(celebnames.get(celebIndex));
                else {
                    int wronganswer = r.nextInt(celebnames.size());
                    while (wronganswer == celebIndex) {
                        wronganswer = r.nextInt(celebnames.size());

                    }
                    answers.add(celebnames.get(wronganswer));
                }
            }

            option1.setText(answers.get(0));
            option2.setText(answers.get(1));
            option3.setText(answers.get(2));
            option4.setText(answers.get(3));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void onClick(View view) {
        maxlimit+=1;
        if(view.getTag().equals(Integer.toString(correctOption))) {
            message = "Correct";
            count += 1;
            score.setText(Integer.toString(count) + "/10");
        }
        else{
            message = "oh! it's " + celebnames.get(celebIndex);
        }
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
        if (maxlimit==10){
            gameOver.setVisibility(View.VISIBLE);
            score.setVisibility(View.INVISIBLE);
           gameOverTextView.setText("Game Over\nYour Score is "+Integer.toString(count)+" out of 10\n");
           option1.setVisibility(View.INVISIBLE);
           option2.setVisibility(View.INVISIBLE);
           option3.setVisibility(View.INVISIBLE);
           option4.setVisibility(View.INVISIBLE);
           imageView.setVisibility(View.INVISIBLE);
        }
        else {
            nextImage();
        }
    }
    public void Continue(View view){
        score.setVisibility(View.VISIBLE);
        gameOver.setVisibility(View.INVISIBLE);
        option1.setVisibility(View.VISIBLE);
        option2.setVisibility(View.VISIBLE);
        option3.setVisibility(View.VISIBLE);
        option4.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);
        maxlimit=0;
        count=0;
        score.setText("0/10");
        nextImage();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        option1 = findViewById(R.id.button1);
        option2 = findViewById(R.id.button2);
        option3 = findViewById(R.id.button3);
        option4 = findViewById(R.id.button4);
        score=findViewById(R.id.ScoreTextView);
        gameOver=findViewById(R.id.gameOverScreen);
        game=findViewById(R.id.Game);
        gameOverTextView=findViewById(R.id.gameOverTextView);

        DownloadTask task = new DownloadTask();
        try {
            result=task.execute("https://www.imdb.com/list/ls052283250/").get();

            Pattern p = Pattern.compile("src=\"(.*?).jpg\"");
            Matcher m = p.matcher(result);
            while(m.find()){
                celeburls.add(m.group(1)+".jpg");
            }
            p=Pattern.compile("<img alt=\"(.*?)\"");
            m=p.matcher(result);
            while(m.find()){
                celebnames.add(m.group(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        nextImage();
        // Log.i("result",result);
    }


    public class ImageDownloader extends AsyncTask<String,Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {

            URL url ;
            HttpURLConnection connection = null;

            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {

            String content ="";
            URL url ;
            HttpURLConnection connection = null;
            StringBuilder stringBuilder = new StringBuilder();
            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data != -1){
                    char current = (char) data;
                    stringBuilder.append(current);
                    data = reader.read();
                }
                content = stringBuilder.toString();
                return content;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}