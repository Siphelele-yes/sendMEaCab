package za.co.sendmedelivery.sendmeacab;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;


import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;

import com.android.volley.Response;
import com.android.volley.VolleyError;
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
    public static  Boolean SaveDetails = false;
    //boolean saveLogin;

    SharedPreferences sharedPreferences;

    private EditText UsernameET,PasswordET;
    private TextView tv_fgt_password;
    private CheckBox rememberMeCheckBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get Reference to variables
        sharedPreferences = getSharedPreferences(UserDetails,
                Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();

        UsernameET = (EditText)findViewById(R.id.etEmail);
        PasswordET = (EditText) findViewById(R.id.etPassword);
        tv_fgt_password = (TextView)findViewById(R.id.tv_fgt_password);
        //rememberMeCheckBox = (CheckBox) findViewById(R.id.saveLoginCheckBox);


        String string_fgt_password = "I forgot my password";
        SpannableString ss_fgt_password = new SpannableString(string_fgt_password);
        ClickableSpan clickableSpan_fgt_password = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity((new Intent(MainActivity.this, SignUp.class)));
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

        boolean loginEntries = true;

        if (TextUtils.isEmpty(username)) {
            UsernameET.requestFocus();
            loginEntries = false;

        }
        if (TextUtils.isEmpty(password)) {
            PasswordET.requestFocus();
            loginEntries = false;

        }
        if (loginEntries==true){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://sendmedelivery.co.za/aCab/App/login.php",
                    response -> {
                        switch(response){
                            case "0":
                            case "1":
                                Toast.makeText(getApplicationContext(), "Incorrect Username or Password:  " + response, Toast.LENGTH_LONG).show();
                                break;

                            case "2":
                                Toast.makeText(getApplicationContext(), "Post issue :  " + response, Toast.LENGTH_LONG).show();
                                break;

                            case "3":
                                Toast.makeText(getApplicationContext(), "DB Connection issue " + response, Toast.LENGTH_LONG).show();
                                break;

                            default:
                                SaveUserDetails(response);
                                /*if(rememberMeCheckBox.isChecked())
                                    RememberMe();*/
                                startActivity((new Intent(MainActivity.this, TripDetails.class)));
                        }
                    }, error -> Toast.makeText(MainActivity.this.getApplicationContext(), "Error", Toast.LENGTH_LONG).show()){
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> params = new HashMap<>();
                    params.put("username",username);
                    params.put("password",password);
                    return params;
                }
            };
            Volley.newRequestQueue(this).add(stringRequest);
        }
        else{
            UsernameET.setError("Please enter a Username and a Password");
            Toast.makeText(MainActivity.this, "Please enter a Username and a Password", Toast.LENGTH_LONG).show();

        }
    }
    public void btnSignUp_OnClick(View v){

        startActivity((new Intent(MainActivity.this, SignUp.class)));
    }
    public void SaveUserDetails(String userDetails){
        String [] tokenDetails = userDetails.split("~");
        SharedPreferences.Editor editor = sharedPreferences.edit();


        editor.putString(Id,tokenDetails[0]);
        editor.putString(Name,tokenDetails[1]);
        editor.putString(Surname,tokenDetails[2]);
        editor.putString(Phone,tokenDetails[3]);
        editor.putString(Address,tokenDetails[4]);
        editor.putString(Email,tokenDetails[5]);

        editor.commit();

        Toast.makeText(MainActivity.this, "Welcome : " + sharedPreferences.getString(Name, "") + " " + sharedPreferences.getString(Surname, ""), Toast.LENGTH_LONG).show();

    }
    public void RememberMe() {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(String.valueOf(SaveDetails), true);
    }

}
