package za.co.sendmedelivery.sendmeacab;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity {

    public static final String UserDetails = "UserDetails" ;
    public static  String Name = "Name";
    public static  String Phone = "Phone";
    public static  String Email = "Email";
    public static  String Surname = "Surname";
    public static  String Address = "Address";
    public static  String Id = "Id";

    SharedPreferences sharedpreferences;

    private EditText UsernameET,PasswordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get Reference to variables
        UsernameET = findViewById(R.id.etEmail);
        PasswordET = findViewById(R.id.etPassword);

        TextView tv_fgt_password = findViewById(R.id.tv_fgt_password);
        String string_fgt_password = "I forgot my password";
        SpannableString ss_fgt_password = new SpannableString(string_fgt_password);
        ClickableSpan clickableSpan_fgt_password = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity((new Intent(MainActivity.this, ForgotPassword.class)));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
                ds.setUnderlineText(false);
            }
        };
        ss_fgt_password.setSpan(clickableSpan_fgt_password, 0, 20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_fgt_password.setText(ss_fgt_password);
        tv_fgt_password.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void btnLogin_OnClick(View v){


        // Get text from email and password field
        final String username = UsernameET.getText().toString();
        final String password = PasswordET.getText().toString();
        sharedpreferences = getSharedPreferences(UserDetails, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://sendmedelivery.co.za/aCab/App/login.php",
                (String response) -> {
                    switch (response){
                        case "0":
                                makeText(getApplicationContext(), "Incorrect username / password combination! " + response, Toast.LENGTH_LONG).show();
                                break;
                        case "1":
                                Toast.makeText(getApplicationContext(), "Username not unique, database return issue.", Toast.LENGTH_LONG).show();
                                break;
                        case "2":
                            Toast.makeText(getApplicationContext(), "Java / PHP POST issue", Toast.LENGTH_LONG).show();
                            break;
                        default:{
                            String [] userDetailsToken = response.split(",");
                            ArrayList<String> tokenList = new ArrayList<>
                                    (Arrays.asList(userDetailsToken));
                            String tokenID = tokenList.get(0);
                            String tokenName = tokenList.get(1);
                            String tokenSurname = tokenList.get(2);
                            String tokenPhoneNo = tokenList.get(3);
                            String tokenAddress = tokenList.get(4);
                            String tokenEmail = tokenList.get(5);

                            editor.putString("Id", tokenID);
                            editor.putString("Name", tokenName);
                            editor.putString("Surname", tokenSurname);
                            editor.putString("Phone", tokenPhoneNo);
                            editor.putString("Address", tokenAddress);
                            editor.putString("Email", tokenEmail);
                            editor.commit();

                            Toast.makeText(getApplicationContext(), "Login Successful: " + sharedpreferences.getString("Name","") + " " + sharedpreferences.getString("Surname",""), Toast.LENGTH_LONG).show();
                            //startActivity((new Intent(MainActivity.this, TripDetails.class)));
                        }
                    }

                   /* if(response == "0"){
                        Toast.makeText(getApplicationContext(), "Incorrect username / password combination! " + response, Toast.LENGTH_LONG).show();
                    }
                    else if (response == "1"){
                    Toast.makeText(getApplicationContext(), "Username not unique, database return issue.", Toast.LENGTH_LONG).show();
                    }
                    else if (response == "2"){
                        Toast.makeText(getApplicationContext(), "Java / PHP POST issue", Toast.LENGTH_LONG).show();
                    }
                    else{
                        String [] userDetailsToken = response.split(",");
                        ArrayList<String> tokenList = new ArrayList<>
                                (Arrays.asList(userDetailsToken));
                        String tokenID = tokenList.get(0);
                        String tokenName = tokenList.get(1);
                        String tokenSurname = tokenList.get(2);
                        String tokenPhoneNo = tokenList.get(3);
                        String tokenAddress = tokenList.get(4);
                        String tokenEmail = tokenList.get(5);

                        editor.putString(Id, tokenID);
                        editor.putString(Name, tokenName);
                        editor.putString(Surname, tokenSurname);
                        editor.putString(Phone, tokenPhoneNo);
                        editor.putString(Address, tokenAddress);
                        editor.putString(Email, tokenEmail);
                        editor.commit();

                        Toast.makeText(getApplicationContext(), "Login Successful: " + Name + " " + Surname, Toast.LENGTH_LONG).show();
                    }*/


                }, error -> {

                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("username",username);
                params.put("password",password);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);

    }

    public void btnSignUp_OnClick(View v){

        startActivity((new Intent(MainActivity.this, SignUp.class)));
    }

}
