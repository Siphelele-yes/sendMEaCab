package za.co.sendmedelivery.sendmeacab;

        import android.content.Context;
        import android.content.SharedPreferences;
        import android.os.Bundle;
        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.fragment.app.Fragment;

        import android.text.TextUtils;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.EditText;
        import android.widget.LinearLayout;
        import android.widget.Toast;

        import static za.co.sendmedelivery.sendmeacab.MainActivity.UserDetails;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private EditText Name,Surname,Phone,Address,Email;
    private String savedName,savedSurname,savedAddress,savedEmail,savedPhone;
    Context context = getActivity();
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

        Name.setText(sharedpreferences.getString("Name",""));
        Surname.setText(sharedpreferences.getString("Surname",""));
        Phone.setText(sharedpreferences.getString("Phone",""));
        Address.setText(sharedpreferences.getString("Address",""));
        Email.setText(sharedpreferences.getString("Email",""));

        return yourFragmentUI;
    }


    @Override
    public void onClick(View v) {
        signUp = new SignUp();
        switch (v.getId()){
            case R.id.btnChangePassword:
                passwordTrigger.setVisibility(v.GONE);
                passwordLayout.setVisibility(v.VISIBLE);
                break;

            case R.id.btnSaveChanges:
                if(!savedName.equals(Name)){
                    if(TextUtils.isEmpty(Name.toString())){
                        Name.setError("Please enter a name");
                        Name.requestFocus();
                    }
                    else
                        editor.putString("Name",Name.toString());
                }
                if(!savedSurname.equals(Surname)){
                    if(TextUtils.isEmpty(Surname.toString())){
                        Surname.setError("Please enter a surname");
                        Surname.requestFocus();
                    }
                    else
                        editor.putString("Surname",Surname.toString());
                }
                if(!savedPhone.equals(Phone)){
                    if(TextUtils.isEmpty(Phone.toString())){
                        Phone.setError("Please enter a phone number");
                        Surname.requestFocus();
                    }else {

                        boolean phoneResult = signUp.PhoneNumberValidation(Phone.toString());
                        if (!phoneResult) {
                            Phone.setError("You have entered and invalid phone number.Try again");
                            Phone.requestFocus();
                        } else
                            editor.putString("Phone", Phone.toString());
                    }

                }
                if(!savedAddress.equals(Address)){
                    if(TextUtils.isEmpty(Address.toString())){
                        Address.setError("Please enter an Address");
                        Address.requestFocus();
                    }
                    else
                        editor.putString("Address",Address.toString());
                }
                if(!savedEmail.equals(Email)){
                    if(!Email.toString().matches(emailPattern)){
                        Email.setError("Enter a valid email");
                        Email.requestFocus();
                    }
                    else
                        editor.putString("Email",Email.toString());

                }
                Toast.makeText(getActivity(), "Your changes are saved.", Toast.LENGTH_LONG).show();
                break;
        }

    }
}
