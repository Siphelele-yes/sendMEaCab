package za.co.sendmedelivery.sendmeacab;

import android.content.Intent;
import android.os.Bundle;
<<<<<<< HEAD
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
=======
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
>>>>>>> master
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

<<<<<<< HEAD
import java.util.HashMap;
import java.util.Map;
=======
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    EditText NameET, SurnameET, PhoneET, EmailET, AddressET, PasswordET, PWET;
    Button SignUpButton;
    TextView backToLogin;
>>>>>>> master

public class SignUp extends AppCompatActivity {
    EditText NameET, SurnameET, PhoneET, EmailET, AddressET, PasswordET, PWET;
    Button SignUpButton, Search, predictionBtn1, predictionBtn2, predictionBtn3, predictionBtn4,
            predictionBtn5, predictionBtn6;
    int predictionCnt;
    TextView backToLogin;
    private EditText queryText;
    StringBuilder mResult;
    LinearLayout PredictionLayout;
    private static final String TAG = SignUp.class.getSimpleName();
    private AutoCompleteTextView locationSearchActv;// instance of AutoCompleteText View
    private TextView addressTv, locationDataTv; // TextViews Used to display the adddress selected by the user
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        SignUpButton=(Button) findViewById(R.id.btnSignUpDone);
        NameET=(EditText)findViewById(R.id.etNameSignUp);
        SurnameET=(EditText)findViewById(R.id.etSurnameSignUp);
        PhoneET=(EditText)findViewById(R.id.etPhoneSignUp);
        EmailET=(EditText)findViewById(R.id.etEmailSignUp);
        PasswordET=(EditText)findViewById(R.id.etPasswordSignUp);
        PWET=(EditText)findViewById(R.id.etPasswordAgnSignUp);
        backToLogin = (TextView)findViewById(R.id.tv_back_to_login);
<<<<<<< HEAD

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
                        // Call either setLocationBias() OR setLocationRestriction().
                        .setLocationBias(bounds)
                        .setCountry("za")//Nigeria
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
=======
>>>>>>> master
    }

    public void btnSignUpDone_OnClick(View v){
        //Final app edit ->
        final String fname = NameET.getText().toString();
        final String sname = SurnameET.getText().toString();
        final String phone_num = PhoneET.getText().toString();
        final String email = EmailET.getText().toString();
        final String HomeAdd = AddressET.getText().toString();
        final String passwordEntry1 = PasswordET.getText().toString();
        final String passwordConfirm = PWET.getText().toString();
        final String psw_repeat;
        final int response1=1,response0=0;
<<<<<<< HEAD

        boolean flagResult=InputValidation(fname,sname,phone_num,email,passwordEntry1,passwordConfirm);
        if(flagResult&&passwordEntry1.equals(passwordConfirm)){
            psw_repeat = passwordConfirm;
=======
      
            boolean flagResult=InputValidation(fname,sname,phone_num,email,passwordEntry1,passwordConfirm);
            if(flagResult&&passwordEntry1.equals(passwordConfirm)){
                psw_repeat = passwordConfirm;

                StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://sendmedelivery.co.za/aCab/App/SignUp.php",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                int intResponse = Integer.parseInt(response);
                                if(intResponse==0){
                                    Toast.makeText(getApplicationContext(), "An account with your Email address or Phone number already exists. " +
                                            "Go back to Login or try again.", Toast.LENGTH_LONG).show();
                                    backToLogin.setVisibility(View.VISIBLE);
                                }
                                else if(intResponse==1){
                                    startActivity((new Intent(SignUp.this, DefineTripActivity.class)));
                                }
                                else if(intResponse==3){
                                    Toast.makeText(getApplicationContext(), "Database connection problem ", Toast.LENGTH_LONG).show();

                                }
                                else
                                    Toast.makeText(getApplicationContext(), "There is a database query problem.", Toast.LENGTH_LONG).show();

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
>>>>>>> master

            StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://sendmedelivery.co.za/aCab/App/SignUp.php",
                    response -> {
                        int intResponse = Integer.parseInt(response);
                        if(intResponse==0){
                            Toast.makeText(getApplicationContext(), "An account with your Email address or Phone number already exists. " +
                                    "Go back to Login or try again.", Toast.LENGTH_LONG).show();
                            backToLogin.setVisibility(View.VISIBLE);
                        }
                        else if(intResponse==1){
                            startActivity((new Intent(SignUp.this, TripDetails.class)));
                        }
                        else if(intResponse==3){
                            Toast.makeText(getApplicationContext(), "Database connection problem ", Toast.LENGTH_LONG).show();

                        }
                        else
                            Toast.makeText(getApplicationContext(), "There is a database query problem.", Toast.LENGTH_LONG).show();

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
            PhoneNumberValidation(phone_num);
        }
        if (TextUtils.isEmpty(email)){
            EmailET.setError("Please enter your email");
            EmailET.requestFocus();
            flag=false;;
        }
        else if(!email.matches(emailPattern)){
            EmailET.setError("Enter a valid email");
            EmailET.requestFocus();
            flag=false;;
        }
        if (TextUtils.isEmpty(passwordEntry1)) {
            PasswordET.setError("Enter a password");
            PasswordET.requestFocus();
            flag=false;
        }
        else if (!PasswordValidation(passwordEntry1)) {
            PasswordET.setError("Your password must be at least 8 characters and include alphabets" +
                    " and numbers with no special characters");
            flag=false;
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
    public void PhoneNumberValidation(String phone_no){

        if(phone_no.length()>12||phone_no.length()<10){
            PhoneET.setError("You have entered and invalid phone number.Try again");
            PhoneET.requestFocus();
            return;
        }
        else{
            if(phone_no.length()==12){
                if (phone_no.charAt(0)!='+'){
                    PhoneET.setError("You have entered and invalid phone number.Try again");
                    PhoneET.requestFocus();
                    return;
                }
                else {
                    if(phone_no.charAt(1)!='2'&&phone_no.charAt(2)!='7'){
                        PhoneET.setError("You have entered and invalid phone number.Try again");
                        PhoneET.requestFocus();
                        return;
                    }
                }
            }
            else{
                if(phone_no.length()==10){
                    if(phone_no.charAt(0)!='0'){
                        PhoneET.setError("You have entered and invalid phone number.Try again");
                        PhoneET.requestFocus();
                        return;
                    }
                }
                if (phone_no.length()==11){
                    if(phone_no.charAt(0)!='2'&&phone_no.charAt(1)!='7'){
                        PhoneET.setError("You have entered and invalid phone number.Try again");
                        PhoneET.requestFocus();
                        return;
                    }
                }
            }
        }
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

}