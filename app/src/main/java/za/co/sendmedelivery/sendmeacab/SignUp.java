package za.co.sendmedelivery.sendmeacab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.preference.PreferenceManager;
import android.text.Editable;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;

import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.lang.*;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;

import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;


import java.util.HashMap;
import java.util.Map;

import static za.co.sendmedelivery.sendmeacab.MainActivity.UserDetails;

import static android.widget.Toast.makeText;


public class SignUp extends AppCompatActivity {


    EditText NameET, SurnameET, PhoneET, EmailET, AddressET, PasswordET, PWET;
    Button predictionBtn1, predictionBtn2, predictionBtn3, predictionBtn4,
            predictionBtn5, predictionBtn6,SignUp;
    int predictionCnt;
    TextView backToLogin;
    private EditText queryText;
    StringBuilder mResult;
    LinearLayout PredictionLayout;

    public static  String Name = "Name";
    public static  String Phone = "Phone";
    public static  String Email = "Email";
    public static  String Surname = "Surname";
    public static  String Address = "Address";
    public static  String Id = "Id";
    public static  Boolean SaveDetails = false;

    private final String TAG = SignUp.class.getSimpleName();
    private AutoCompleteTextView locationSearchActv;// instance of AutoCompleteText View

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        NameET = findViewById(R.id.etNameSignUp);
        SurnameET = findViewById(R.id.etSurnameSignUp);
        PhoneET = findViewById(R.id.etPhoneSignUp);
        PasswordET = findViewById(R.id.etPasswordSignUp);
        PWET = findViewById(R.id.etPasswordAgnSignUp);
        EmailET = findViewById(R.id.etEmailSignUp);
        SignUp = findViewById(R.id.btnSignUpDone);
        backToLogin = (TextView) findViewById(R.id.tv_back_to_login);
        BackToLogin();
        AddressET = findViewById(R.id.autocomplete_address);
        locationSearchActv = findViewById(R.id.autocomplete_address);

        PredictionLayout = findViewById(R.id.predictionsLayout);
        //Autocomplete predictions
        predictionBtn1 = (Button) findViewById(R.id.prediction1);
        predictionBtn2 = (Button) findViewById(R.id.prediction2);
        predictionBtn3 = (Button) findViewById(R.id.prediction3);
        predictionBtn4 = (Button) findViewById(R.id.prediction4);
        predictionBtn5 = (Button) findViewById(R.id.prediction5);
        predictionBtn6 = (Button) findViewById(R.id.prediction6);

        queryText = findViewById(R.id.autocomplete_address);

        String apiKey = getString(R.string.api_key);

        if(!Places.isInitialized()){
            Places.initialize(getApplicationContext(),apiKey);
        }
        PlacesClient places = Places.createClient(this);
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(-33.880490, 151.184363), //dummy lat/lng
                new LatLng(-33.858754, 151.229596));

        locationSearchActv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                predictionCnt = 0;
                predictionBtn1.setVisibility(View.GONE);
                predictionBtn2.setVisibility(View.GONE);
                predictionBtn3.setVisibility(View.GONE);
                predictionBtn4.setVisibility(View.GONE);
                predictionBtn5.setVisibility(View.GONE);
                predictionBtn6.setVisibility(View.GONE);

                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()

                        .setLocationBias(bounds)
                        .setCountry("za")
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setSessionToken(token)
                        .setQuery(queryText.getText().toString())
                        .build();

                places.findAutocompletePredictions(request).addOnSuccessListener(response -> {
                    mResult = new StringBuilder();


                    for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {

                        mResult.append(prediction.getFullText(null) + "\n");
                        Log.i(TAG, prediction.getPlaceId());
                        Log.i(TAG, prediction.getPrimaryText(null).toString());

                        predictionCnt++;

                        if (predictionCnt == 1){
                            predictionBtn1.setVisibility(View.VISIBLE);
                            predictionBtn1.setText(prediction.getFullText(null));
                        } else if (predictionCnt == 2){
                            predictionBtn2.setVisibility(View.VISIBLE);
                            predictionBtn2.setText(prediction.getFullText(null));
                        } else if (predictionCnt == 3){
                            predictionBtn3.setVisibility(View.VISIBLE);
                            predictionBtn3.setText(prediction.getFullText(null));
                        } else if (predictionCnt == 4){
                            predictionBtn4.setVisibility(View.VISIBLE);
                            predictionBtn4.setText(prediction.getFullText(null));
                        } else if (predictionCnt == 5){
                            predictionBtn5.setVisibility(View.VISIBLE);
                            predictionBtn5.setText(prediction.getFullText(null));
                        } else if (predictionCnt == 6){
                            predictionBtn6.setVisibility(View.VISIBLE);
                            predictionBtn6.setText(prediction.getFullText(null));
                        }

                    }
                    PredictionLayout.setVisibility(View.VISIBLE);
                    predictionBtn1.setOnClickListener(v -> {
                        AddressET.setText(predictionBtn1.getText());
                        PredictionLayout.setVisibility(View.GONE);
                    });
                    predictionBtn2.setOnClickListener(v -> {
                        AddressET.setText(predictionBtn2.getText());
                        PredictionLayout.setVisibility(View.GONE);
                    });
                    predictionBtn3.setOnClickListener(v -> {
                        AddressET.setText(predictionBtn3.getText());
                        PredictionLayout.setVisibility(View.GONE);
                    });
                    predictionBtn4.setOnClickListener(v -> {
                        AddressET.setText(predictionBtn4.getText());
                        PredictionLayout.setVisibility(View.GONE);
                    });
                    predictionBtn5.setOnClickListener(v -> {
                        AddressET.setText(predictionBtn5.getText());
                        PredictionLayout.setVisibility(View.GONE);
                    });
                    predictionBtn6.setOnClickListener(v -> {
                        AddressET.setText(predictionBtn6.getText());
                        PredictionLayout.setVisibility(View.GONE);
                    });
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                    }
                });
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        AddressET.setOnFocusChangeListener((View v, boolean hasFocus) -> {
            if (!hasFocus) {
                PredictionLayout.setVisibility(View.GONE);
            }
        });
    }

    public void btnSignUpDone_OnClick(View v) {
        //Final app edit ->
        final String fname = NameET.getText().toString();
        final String sname = SurnameET.getText().toString();
        final String phone_num = PhoneET.getText().toString();
        final String email = EmailET.getText().toString();
        final String HomeAdd = AddressET.getText().toString();
        final String passwordEntry1 = PasswordET.getText().toString();
        final String passwordConfirm = PWET.getText().toString();
        final String psw_repeat;

        final int response1 = 1, response0 = 0;

        boolean flagResult=InputValidation(fname,sname,phone_num,email,passwordEntry1,passwordConfirm);
        if(flagResult&&passwordEntry1.equals(passwordConfirm)){
            psw_repeat = passwordConfirm;

            StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://sendmedelivery.co.za/aCab/App/SignUp.php",
                    response -> {
                        int intResponse = Integer.parseInt(response);
                        switch(intResponse){
                            case 0:
                                Toast.makeText(getApplicationContext(), "An account with your Email address or Phone number already exists. " +
                                        "Go back to Login or try again.", Toast.LENGTH_LONG).show();
                                break;
                            case 2:
                                Toast.makeText(getApplicationContext(), "Could not sign up. Please try again. ", Toast.LENGTH_LONG).show();
                                break;

                            case 3:
                                Toast.makeText(getApplicationContext(), "Database connection problem ", Toast.LENGTH_LONG).show();
                                break;

                            default:

                                saveDetails(response);
                                //startActivity((new Intent(SignUp.this, TripDetails.class)));
                        }
                    }, error -> {

            }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> params = new HashMap<>();
                    params.put("fname",fname);
                    params.put("sname",sname);
                    params.put("HomeAdd",HomeAdd);
                    params.put("email",email);
                    params.put("phone_num",phone_num);
                    params.put("psw_repeat",psw_repeat);
                    return params;
                }
            };
            Volley.newRequestQueue(this).add(stringRequest);
        }
        else
            Toast.makeText(getApplicationContext(), "There is a problem.", Toast.LENGTH_LONG).show();

    }

    public boolean InputValidation(String fname, String sname, String phone_num,String email, String passwordEntry1, String passwordConfirm){
        boolean flag=true;

        if (TextUtils.isEmpty(fname)) {
            NameET.setError("Please enter a name");
            NameET.requestFocus();
            flag=false;
        }
        if (TextUtils.isEmpty(sname)) {
            SurnameET.setError("Please enter a surname");
            SurnameET.requestFocus();
            flag=false;;
        }
        if (TextUtils.isEmpty(phone_num)) {
            PhoneET.setError("Please enter a phone number");
            PhoneET.requestFocus();
            flag=false;
        }
        else{
            boolean phoneResult= PhoneNumberValidation(phone_num);
            if(!phoneResult){
                PhoneET.setError("You have entered and invalid phone number.Try again");
                PhoneET.requestFocus();
            }
        }
        if (TextUtils.isEmpty(email)){
            EmailET.setError("Please enter your email");
            EmailET.requestFocus();
            flag=false;;
        }
        else{

            if(!email.matches(emailPattern)){
            EmailET.setError("Enter a valid email");
            EmailET.requestFocus();
            flag=false;
            }
        }
        if (TextUtils.isEmpty(passwordEntry1)) {
            PasswordET.setError("Enter a password");
            PasswordET.requestFocus();
            flag=false;
        }
        else{
            boolean passwordResult = PasswordValidation(passwordEntry1);
            if(!passwordResult){
                PasswordET.setError("Your password must be at least 8 characters and include alphabets" +
                        " and numbers with no special characters");
                flag=false;
            }
        }
        if (TextUtils.isEmpty(passwordConfirm)) {
            PWET.setError("Please enter a matching password ");
            PWET.requestFocus();
            flag=false;
        }
        else if(!passwordEntry1.equals(passwordConfirm)){
            PWET.setError("Please enter a matching password ");
            PWET.requestFocus();
            flag=false;
        }

        return flag;

    }
    public boolean PhoneNumberValidation(String phone_no){
        boolean phoneValid = true;

        if(phone_no.length()>12||phone_no.length()<10){
            phoneValid = false;
        }
        else{
            if(phone_no.length()==12){
                if (phone_no.charAt(0)!='+'){
                    phoneValid = false;
                }
                else {
                    if(phone_no.charAt(1)!='2'&&phone_no.charAt(2)!='7'){
                        phoneValid = false;
                    }
                }
            }
            else{
                if(phone_no.length()==10){
                    if(phone_no.charAt(0)!='0'){
                        phoneValid = false;;
                    }
                }
                if (phone_no.length()==11){
                    if(phone_no.charAt(0)!='2'&&phone_no.charAt(1)!='7'){
                        phoneValid = false;
                    }
                }
            }
        }
        return phoneValid;
    }

    public boolean PasswordValidation(String password1){
        int letterCnt = 0, digitCnt = 0;
        boolean passwordFlag=true;
        char c[] = password1.toCharArray();

        if(password1.length()< 9){
            passwordFlag=false;
        }
        else {
            for (int x=0;x<c.length;x++){
                if(Character.isLetterOrDigit(c[x])){
                    if(Character.isLetter(c[x])){
                        letterCnt++;
                    }
                    if(Character.isDigit(c[x])){
                        digitCnt++;
                    }
                }
                else
                    passwordFlag=false;
            }
            if(letterCnt==0 || digitCnt==0) {
                passwordFlag = false;
            }
        }
        return passwordFlag;
    }
    public void saveDetails(String ID){

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(Id,ID);
        editor.putString(Name,NameET.getText().toString());
        editor.putString(Surname,SurnameET.getText().toString());
        editor.putString(Phone,PhoneET.getText().toString());
        editor.putString(Address,AddressET.getText().toString());
        editor.putString(Email,EmailET.getText().toString());
        editor.commit();

        Toast.makeText(getApplicationContext(), sharedPreferences.getString(Name,"")+" have successfully signed up! ", Toast.LENGTH_LONG).show();
    }
    public void BackToLogin(){
        String string_back_to_login = "Go Back to Login";
        SpannableString ss_back_to_login = new SpannableString(string_back_to_login);
        ClickableSpan clickableSpan_back_to_login = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity((new Intent(SignUp.this, MainActivity.class)));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
                ds.setUnderlineText(false);
            }
        };
        ss_back_to_login.setSpan(clickableSpan_back_to_login, 0, 16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        backToLogin.setText(ss_back_to_login);
        backToLogin.setMovementMethod(LinkMovementMethod.getInstance());

    }


}