package za.co.sendmedelivery.sendmeacab;

    import android.content.Context;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.os.Bundle;

    import androidx.annotation.ContentView;
    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.fragment.app.Fragment;

    import android.preference.PreferenceManager;
    import android.text.Editable;
    import android.text.TextUtils;
    import android.text.TextWatcher;
    import android.text.method.BaseKeyListener;
    import android.util.Log;
    import android.view.KeyEvent;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
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

    import java.util.HashMap;
    import java.util.Map;

    import static za.co.sendmedelivery.sendmeacab.MainActivity.Id;
    import static za.co.sendmedelivery.sendmeacab.MainActivity.UserDetails;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private EditText Name,Surname,Phone,Email,CurrentPassword,NewPassword,NewPasswordReEntry,queryText;
    private TextView EnterNewPassword,ConfirmNewPassword,EnterCurrentPassword;
    private String savedName,savedSurname,savedAddress,savedEmail,savedPhone,savedID;
    private AutoCompleteTextView Address;
    LinearLayout PredictionLayout;
    StringBuilder mResult;

    Button predictionBtn1, predictionBtn2, predictionBtn3, predictionBtn4,
            predictionBtn5, predictionBtn6;
    int predictionCnt;


    String fname,sname, phone_num,email, HomeAdd, passwordEntry1, passwordConfirm ;

    boolean changes;

    public SharedPreferences sharedpreferences;
    public SharedPreferences.Editor editor;
    SignUp signUp;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    PlacesClient places;
    RectangularBounds bounds;
    AutocompleteSessionToken token;
    private final String TAG = ProfileFragment.class.getSimpleName();

    LinearLayout passwordLayout, passwordTrigger;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View yourFragmentUI = inflater.inflate(R.layout.fragment_profile, container, false);

        Name = yourFragmentUI.findViewById(R.id.etNameEdit);
        Surname = yourFragmentUI.findViewById(R.id.etSurnameEdit);
        Phone = yourFragmentUI.findViewById(R.id.etPhoneEdit);
        Address = yourFragmentUI.findViewById(R.id.etAddressEdit);
        Email = yourFragmentUI.findViewById(R.id.etEmailEdit);
        EnterNewPassword = yourFragmentUI.findViewById(R.id.tvNewPasswordEdit);
        ConfirmNewPassword = yourFragmentUI.findViewById(R.id.tvNewPasswordAgnEdit);
        EnterCurrentPassword = yourFragmentUI.findViewById(R.id.tvCurrentPasswordEdit);
        NewPassword = yourFragmentUI.findViewById(R.id.etNewPasswordEdit);
        NewPasswordReEntry = yourFragmentUI.findViewById(R.id.etNewPasswordAgnEdit);
        CurrentPassword = yourFragmentUI.findViewById(R.id.etPasswordEdit);

        PredictionLayout = yourFragmentUI.findViewById(R.id.predictionsLayout);
        //Autocomplete predictions
        predictionBtn1 = (Button) yourFragmentUI.findViewById(R.id.prediction1);
        predictionBtn2 = (Button) yourFragmentUI.findViewById(R.id.prediction2);
        predictionBtn3 = (Button) yourFragmentUI.findViewById(R.id.prediction3);
        predictionBtn4 = (Button) yourFragmentUI.findViewById(R.id.prediction4);
        predictionBtn5 = (Button) yourFragmentUI.findViewById(R.id.prediction5);
        predictionBtn6 = (Button) yourFragmentUI.findViewById(R.id.prediction6);

        queryText = yourFragmentUI.findViewById(R.id.etAddressEdit);
        String apiKey = getString(R.string.api_key);


        Button btnSaveChanges = (Button) yourFragmentUI.findViewById(R.id.btnSaveChanges);
        btnSaveChanges.setOnClickListener(this);
        passwordTrigger = (LinearLayout) yourFragmentUI.findViewById(R.id.passwordTriggerLayout);
        passwordLayout = (LinearLayout) yourFragmentUI.findViewById(R.id.passwordLayout);

        Button btnChangePassword = (Button) yourFragmentUI.findViewById(R.id.btnChangePassword);
        btnChangePassword.setOnClickListener(v -> {
            passwordLayout.setVisibility(View.VISIBLE);


            CurrentPassword.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        Toast.makeText(getContext(), "Current password is being validated.  ", Toast.LENGTH_LONG).show();
                        PostCurrentPassword();
                        return true;
                    }
                    return false;
                }
            });
        });

        sharedpreferences = getContext().getSharedPreferences(UserDetails,
                Context.MODE_PRIVATE);

        Name.setText(sharedpreferences.getString("Name",""));
        Surname.setText(sharedpreferences.getString("Surname",""));
        Phone.setText(sharedpreferences.getString("Phone",""));
        Address.setText(sharedpreferences.getString("Address",""));
        Email.setText(sharedpreferences.getString("Email",""));

        savedName = sharedpreferences.getString("Name","");
        savedSurname = sharedpreferences.getString("Surname","");
        savedPhone =sharedpreferences.getString("Phone","");
        savedAddress = sharedpreferences.getString("Address","");
        savedEmail = sharedpreferences.getString("Email","");
        savedID = sharedpreferences.getString("Id", "");

        places = Places.createClient(getContext());
        token = AutocompleteSessionToken.newInstance();
        bounds = RectangularBounds.newInstance(
                new LatLng(-33.880490, 151.184363), //dummy lat/lng
                new LatLng(-33.858754, 151.229596));

        if(!Places.isInitialized()){
            Places.initialize(getContext(),apiKey);
        }

        changes = TrackPersonalDetailsChanged();

        return yourFragmentUI;
    }

    @Override
    public void onClick(View v) {

        fname = Name.getText().toString();
        sname = Surname.getText().toString();
        phone_num = Phone.getText().toString();
        email = Email.getText().toString();
        HomeAdd = Address.getText().toString();
        passwordEntry1 = NewPassword.getText().toString();
        passwordConfirm = NewPasswordReEntry.getText().toString();

        signUp = new SignUp();
        switch (v.getId()) {
            case R.id.btnChangePassword:
                passwordTrigger.setVisibility(v.GONE);
                passwordLayout.setVisibility(v.VISIBLE);
                break;

            case R.id.btnSaveChanges:

                    if(changes==true){
                        boolean valid = InputValidation(fname,sname,phone_num,email);
                        if(valid==true){
                            PostChanges();
                            saveDetails();
                        }
                    }
                    else
                        Toast.makeText(getContext(), " No changes made to any of the fields. ", Toast.LENGTH_LONG).show();

                if(!TextUtils.isEmpty(passwordEntry1)){
                        boolean passwordvalid = InputPasswordValidation(passwordEntry1,passwordConfirm);
                        if(passwordvalid){
                            PostPassword();
                        }
                }
        }
    }

    public boolean TrackPersonalDetailsChanged(){

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changes = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                changes = true;
            }
        };
        Name.addTextChangedListener(textWatcher);
        Surname.addTextChangedListener(textWatcher);
        Email.addTextChangedListener(textWatcher);
        Phone.addTextChangedListener(textWatcher);

        Address.addTextChangedListener(new TextWatcher() {
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
                        Address.setText(predictionBtn1.getText());
                        PredictionLayout.setVisibility(View.GONE);
                    });
                    predictionBtn2.setOnClickListener(v -> {
                        Address.setText(predictionBtn2.getText());
                        PredictionLayout.setVisibility(View.GONE);
                    });
                    predictionBtn3.setOnClickListener(v -> {
                        Address.setText(predictionBtn3.getText());
                        PredictionLayout.setVisibility(View.GONE);
                    });
                    predictionBtn4.setOnClickListener(v -> {
                        Address.setText(predictionBtn4.getText());
                        PredictionLayout.setVisibility(View.GONE);
                    });
                    predictionBtn5.setOnClickListener(v -> {
                        Address.setText(predictionBtn5.getText());
                        PredictionLayout.setVisibility(View.GONE);
                    });
                    predictionBtn6.setOnClickListener(v -> {
                        Address.setText(predictionBtn6.getText());
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
        Address.setOnFocusChangeListener((View v, boolean hasFocus) -> {
            if (!hasFocus) {
                PredictionLayout.setVisibility(View.GONE);
            }
        });

        return changes;

    }

    public void PostChanges(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://sendmedelivery.co.za/aCab/App/edit_profile.php",
                response -> {
                    int intResponse = Integer.parseInt(response);
                    switch(intResponse){
                        case 0:
                            Toast.makeText(getContext(), "Database connection issue. ", Toast.LENGTH_LONG).show();
                            break;
                        case 2:
                            Toast.makeText(getContext(), "Your personal details have been successfully updated. ", Toast.LENGTH_LONG).show();
                            break;

                        case 3:
                            Toast.makeText(getContext(), "Your personal details have not been updated. Please try again. ", Toast.LENGTH_LONG).show();
                            break;
                        case 10:
                            Toast.makeText(getContext(), "Data posting issue. ", Toast.LENGTH_LONG).show();
                            break;
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
                params.put("userId",savedID);
                return params;
            }
        };
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }

    public void PostPassword(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://sendmedelivery.co.za/aCab/App/edit_profile.php",
                response -> {
                    int intResponse = Integer.parseInt(response);
                    switch(intResponse){
                        case 0:
                            Toast.makeText(getContext(), "Database connection issue. ", Toast.LENGTH_LONG).show();
                            break;

                        case 4:
                            Toast.makeText(getContext(), "Your personal details and password have been successfully updated. ", Toast.LENGTH_LONG).show();
                            break;

                        case 5:
                        case 8:
                            Toast.makeText(getContext(), "Your personal details and password have not been updated. Please try again. ", Toast.LENGTH_LONG).show();
                            break;

                        case 6:
                            Toast.makeText(getContext(), "Your password have been successfully updated. ", Toast.LENGTH_LONG).show();
                            passwordLayout.setVisibility(View.GONE);
                            CurrentPassword.getText().clear();
                            NewPassword.getText().clear();
                            NewPasswordReEntry.getText().clear();
                            passwordTrigger.setVisibility(View.VISIBLE);

                            break;

                        case 7:
                            Toast.makeText(getContext(), "We've only successfully updated your personal details. Please try updating your password again. ", Toast.LENGTH_LONG).show();
                            break;

                        case 9:
                            Toast.makeText(getContext(), "Your password has not been updated. Please try again. ", Toast.LENGTH_LONG).show();
                            break;

                        case 11:
                            Toast.makeText(getContext(), "Data posting issue. ", Toast.LENGTH_LONG).show();
                            break;
                    }
                }, error -> {
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("psw_repeat",passwordConfirm);
                params.put("userId",savedID);
                return params;
            }
        };
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }

    public void PostCurrentPassword(){
        final String currentpass = CurrentPassword.getText().toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://sendmedelivery.co.za/aCab/App/edit_profile.php",
                    response -> {
                        int intResponse = Integer.parseInt(response);
                        switch(intResponse){
                            case 100:
                                Toast.makeText(getContext(), "Current password is correct.  " + response, Toast.LENGTH_LONG).show();
                                EnterNewPassword.setVisibility(View.VISIBLE);
                                NewPassword.setVisibility(View.VISIBLE);

                                ConfirmNewPassword.setVisibility(View.VISIBLE);
                                NewPasswordReEntry.setVisibility(View.VISIBLE);

                                EnterCurrentPassword.setVisibility(View.GONE);
                                CurrentPassword.setVisibility(View.GONE);
                                break;
                            case 99:
                                Toast.makeText(getContext(), "Enter your current password and press the enter key  " + response, Toast.LENGTH_LONG).show();
                                break;
                        }
                    }, error -> Toast.makeText(ProfileFragment.this.getContext(), "Error", Toast.LENGTH_LONG).show()){
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> params = new HashMap<>();
                    params.put("curr_pswd",currentpass);
                    params.put("userId",savedID);
                    return params;
                }
            };
            Volley.newRequestQueue(this.getContext()).add(stringRequest);
    }

    public boolean InputValidation(String fname, String sname, String phone_num,String email){
        boolean flag=true;

        if (TextUtils.isEmpty(fname)) {
            Name.setError("Please enter a name");
            Name.requestFocus();
            flag=false;
        }
        if (TextUtils.isEmpty(sname)) {
            Surname.setError("Please enter a surname");
            Surname.requestFocus();
            flag=false;;
        }
        if (TextUtils.isEmpty(phone_num)) {
            Phone.setError("Please enter a phone number");
            Phone.requestFocus();
            flag=false;
        }
        else{
            boolean phoneResult= PhoneNumberValidation(phone_num);
            if(!phoneResult){
                Phone.setError("You have entered and invalid phone number.Try again");
                Phone.requestFocus();
            }
        }
        if (TextUtils.isEmpty(email)){
            Email.setError("Please enter your email");
            Email.requestFocus();
            flag=false;;
        }
        else{

            if(!email.matches(emailPattern)){
                Email.setError("Enter a valid email");
                Email.requestFocus();
                flag=false;
            }
        }

        return flag;

    }
    public boolean InputPasswordValidation(String passwordEntry1, String passwordConfirm){

        boolean flag =true;

        if (TextUtils.isEmpty(passwordEntry1)) {
            NewPassword.setError("Enter a password");
            NewPassword.requestFocus();
            flag=false;
        }
        else{
            boolean passwordResult = signUp.PasswordValidation(passwordEntry1);
            if(!passwordResult){
                NewPassword.setError("Your password must be at least 8 characters and include alphabets" +
                        " and numbers with no special characters");
                flag=false;
            }
        }
        if (TextUtils.isEmpty(passwordConfirm)) {
            NewPasswordReEntry.setError("Please enter a matching password ");
            NewPasswordReEntry.requestFocus();
            flag=false;
        }
        else if(!passwordEntry1.equals(passwordConfirm)){
            NewPasswordReEntry.setError("Please enter a matching password ");
            NewPasswordReEntry.requestFocus();
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
    public void saveDetails(){

        SharedPreferences.Editor editor = sharedpreferences.edit();
        if(savedName.equals(fname))
            editor.putString("Name",fname);
        if(savedSurname.equals(sname))
            editor.putString("Surname",sname);
        if(savedEmail.equals(email))
            editor.putString("Email",email);
        if(savedPhone.equals(phone_num))
        editor.putString("Phone",phone_num);
        if(savedAddress.equals(HomeAdd))
            editor.putString("Phone",HomeAdd);

        editor.commit();
        Toast.makeText(getContext(), "Changes made to your shared preference file. ", Toast.LENGTH_LONG).show();

    }


}




