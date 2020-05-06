package za.co.sendmedelivery.sendmeacab;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    EditText Email,Password;
    String emailText,passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Email = findViewById(R.id.etEmail);
        Password = findViewById(R.id.etPassword);

    }

    public void btnSubmit_OnClick(View v){

        emailText = Email.getText().toString();
        passwordText = Password.getText().toString();

        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute(emailText,passwordText);

        startActivity((new Intent(MainActivity.this, DefineTripActivity.class)));
    }

    class BackgroundTask extends AsyncTask<String, Void, String> {

        String dbURL;
        private ProgressBar progressBar;
        @Override
        protected void onPreExecute() {

            //dbURL=//get URL for db (domain name + /php loginfile name);
        }

        @Override
        protected String doInBackground(String... args) {

            emailText= args[0];
            passwordText=args[1];

            try {
                URL url = new URL(dbURL);
                try {
                    HttpURLConnection httpURLConnection =(HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

                    String details = URLEncoder.encode("email","UTF-8")+ "="+URLEncoder.encode(emailText,"UTF-8")+"&"
                            +URLEncoder.encode("password","UTF-8")+ "="+URLEncoder.encode(passwordText,"UTF-8");

                    bufferedWriter.write(details);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                    String response = "";
                    String line;

                    while ((line = bufferedReader.readLine())!=null) {
                        response += line;
                    }

                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();

                    return response;

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }



        @Override
        protected void onPostExecute(String aVoid) {
            progressBar = findViewById(R.id.progressbar);
            progressBar.setVisibility(View.VISIBLE);
        }
    }
}
