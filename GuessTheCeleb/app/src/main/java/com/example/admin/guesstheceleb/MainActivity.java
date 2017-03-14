package com.example.admin.guesstheceleb;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class MainActivity extends ActionBarActivity {

    ArrayList<String> celeburls = new ArrayList<String>();

    ArrayList<String> celebnames  = new ArrayList<String>();

    int chosenCeleb = 0;

    ImageView imageView;

    int locationofcorrectanswer = 0; //0 to 3

    String[] answer = new String[4]; //4 answers

    Button button1;
    Button button2;
    Button button3;
    Button button4;


    public void celebChose(View view) throws ExecutionException, InterruptedException {

        if(view.getTag().toString().equals(Integer.toString(locationofcorrectanswer))) {
            //guessed the correct celeb

            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_LONG).show();
        }
        else
            Toast.makeText(getApplicationContext(),"Wrong it was" + celebnames.get(chosenCeleb),Toast.LENGTH_LONG).show();

        newquestion();
        }



    public class ImageDownloader  extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String...urls) {
            URL url;


            try {
                url = new URL(urls[0]);
                HttpURLConnection connection =  (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream is = connection.getInputStream();

                Bitmap mybitmap = BitmapFactory.decodeStream(is); //to get Bitmap

                return mybitmap;

            }
            catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    //download content from website


    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String...urls) {

            String result = ""; //result string

            URL url;
            HttpURLConnection urlConnection = null;


            try{
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while(data != -1){
                    char current  = (char) data;
                    result = result + current;

                    data = reader.read();
                }

              return result;
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    public void newquestion() throws ExecutionException, InterruptedException {
        Random random = new Random();

        chosenCeleb = random.nextInt(celeburls.size()); // gives between 0 and 1 less than size of celeb images.

        ImageDownloader imagetask  = new ImageDownloader();

        Bitmap celebimage;


        celebimage = imagetask.execute(celeburls.get(chosenCeleb)).get(); //gets a random celebrity image from the list of celeb urls

        imageView.setImageBitmap(celebimage); //sets the bitmap to the imageview on screen

        locationofcorrectanswer = random.nextInt(4);

        int incorrectanswer;

        for(int i = 0; i < 4; i++){
            if (i == locationofcorrectanswer)
                answer[i]  = celebnames.get(chosenCeleb);

            else {
                incorrectanswer = random.nextInt(celeburls.size());

                while(incorrectanswer != chosenCeleb) {

                    incorrectanswer = random.nextInt(celeburls.size());
                }

                answer[i]  = celebnames.get(incorrectanswer);

            }//else ends
        }
        button1.setText(answer[0]);
        button2.setText(answer[1]);
        button3.setText(answer[2]);
        button4.setText(answer[4]);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadTask task = new DownloadTask();
        String result = null;

        imageView = (ImageView) findViewById(R.id.imageView);

        //Buttons
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);

        try {
            result = task.execute("http://www.posh24.com/celebrities").get();

            String[] splitResult = result.split("<div class = \"sidebarContainer\">"); //splits the html page code at that point

            Pattern p = Pattern.compile("<img src=\"(.*?)\""); //gives image url of celeb
            Matcher m = p.matcher(splitResult[0]); //splitresult[0] is the first part since we broke result into two parts

            while(m.find()){
                celeburls.add(m.group(1)); //adds to url list
            }
            p = Pattern.compile("alt=\"(.*?)\""); //regex expressions for manipulating strings giving celeb name
            m = p.matcher(splitResult[0]);

            while(m.find()){
                celebnames.add(m.group(1)); //adds to names list
            }

        }

        catch (Exception e){
            e.printStackTrace();
        }

        try {
            newquestion();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
