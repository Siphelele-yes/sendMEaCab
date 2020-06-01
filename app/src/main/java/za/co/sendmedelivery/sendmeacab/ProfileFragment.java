package za.co.sendmedelivery.sendmeacab;

    import android.content.Context;
    import android.content.SharedPreferences;
    import android.os.Bundle;
    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.fragment.app.Fragment;

    import android.preference.PreferenceManager;
    import android.text.Editable;
    import android.text.TextUtils;
    import android.text.TextWatcher;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.LinearLayout;
    import android.widget.Toast;

    import com.android.volley.Request;
    import com.android.volley.toolbox.StringRequest;
    import com.android.volley.toolbox.Volley;

    import java.util.HashMap;
    import java.util.Map;

    import static za.co.sendmedelivery.sendmeacab.MainActivity.UserDetails;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private EditText Name,Surname,Phone,Address,Email;
    private String savedName,savedSurname,savedAddress,savedEmail,savedPhone;
    boolean changes = false;

    public SharedPreferences sharedpreferences;
    public SharedPreferences.Editor editor;
    SignUp signUp;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

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

        Button btnChangePassword = (Button) yourFragmentUI.findViewById(R.id.btnChangePassword);
        btnChangePassword.setOnClickListener(this);
        Button btnSaveChanges = (Button) yourFragmentUI.findViewById(R.id.btnSaveChanges);
        btnSaveChanges.setOnClickListener(this);
        passwordTrigger = (LinearLayout) yourFragmentUI.findViewById(R.id.passwordTriggerLayout);
        passwordLayout = (LinearLayout) yourFragmentUI.findViewById(R.id.passwordLayout);

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

        return yourFragmentUI;
    }


    @Override
    public void onClick(View v) {
        signUp = new SignUp();
        switch (v.getId()) {
            case R.id.btnChangePassword:
                passwordTrigger.setVisibility(v.GONE);
                passwordLayout.setVisibility(v.VISIBLE);
                break;

            case R.id.btnSaveChanges:
                boolean changes = TrackChanges();
                if(changes){
                    if(savedName.equals(Name.getText().toString())||savedSurname.equals(Surname.getText().toString())||savedPhone.equals(Phone.getText().toString())
                            ||savedAddress.equals(Address.getText().toString())||savedEmail.equals(Email.getText().toString())){
                        boolean validationResult = FieldValidation();
                        if(validationResult){
                            //PostChanges();
                            Toast.makeText(getActivity(), "Your changes are saved.", Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                        Toast.makeText(getActivity(), "No changes have been made.", Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(getActivity(), "No changes have been made to the fields.", Toast.LENGTH_LONG).show();
                break;
        }
    }

    public boolean TrackChanges(){

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                changes=true;

            }

            @Override
            public void afterTextChanged(Editable s) {

                changes=true;

            }
        };
        Name.addTextChangedListener(textWatcher);
        Surname.addTextChangedListener(textWatcher);
        Phone.addTextChangedListener(textWatcher);
        Email.addTextChangedListener(textWatcher);
        Address.addTextChangedListener(textWatcher);
        return changes;
    }
    public void PostChanges(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://sendmedelivery.co.za/aCab/App/EditPersonalDetails.php",
                response -> {
                    int intResponse = Integer.parseInt(response);
                    switch(intResponse){
                        /*case 0:
                            Toast.makeText(getContext(), "An account with your Email address or Phone number already exists. " +
                                    "Go back to Login or try again.", Toast.LENGTH_LONG).show();
                            break;
                        case 2:
                            Toast.makeText(getContext(), "Could not sign up. Please try again. ", Toast.LENGTH_LONG).show();
                            break;

                        case 3:
                            Toast.makeText(getContext(), "Database connection problem ", Toast.LENGTH_LONG).show();
                            break;
*/

                    }
                }, error -> {

        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("fname",Name.getText().toString());
                params.put("sname",Surname.getText().toString());
                params.put("HomeAdd",Address.getText().toString());
                params.put("email",Email.getText().toString());
                params.put("phone_num",Phone.getText().toString());
                //params.put("psw_repeat",psw_repeat);
                return params;
            }
        };
        Volley.newRequestQueue(getContext()).add(stringRequest);
        Toast.makeText(getActivity(), "Your changes are saved.", Toast.LENGTH_LONG).show();
    }

    public boolean FieldValidation(){

        boolean fieldValid = true;

        if (TextUtils.isEmpty(Name.getText().toString())) {
            Name.setError("Please enter a name");
            Name.requestFocus();
            fieldValid=false;
        }

        if (TextUtils.isEmpty(Surname.getText().toString())) {
            Surname.setError("Please enter a surname");
            Surname.requestFocus();
            fieldValid=false;
        }

        if (TextUtils.isEmpty(Phone.getText().toString())) {
            Phone.setError("Please enter a phone number");
            Surname.requestFocus();
            fieldValid=false;
        } else {

            boolean phoneResult = signUp.PhoneNumberValidation(Phone.getText().toString());
            if (!phoneResult) {
                Phone.setError("You have entered and invalid phone number.Try again");
                Phone.requestFocus();
                fieldValid=false;
            }
        }

        if (TextUtils.isEmpty(Address.getText().toString())) {
            Address.setError("Please enter an Address");
            Address.requestFocus();
            fieldValid=false;
        }

        if (!Email.getText().toString().matches(emailPattern)) {
            Email.setError("Enter a valid email");
            Email.requestFocus();
            fieldValid=false;
        }

        return fieldValid;

    }



}
