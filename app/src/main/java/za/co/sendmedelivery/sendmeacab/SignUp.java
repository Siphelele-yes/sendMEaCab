package za.co.sendmedelivery.sendmeacab;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.lang.*;

public class SignUp extends AppCompatActivity {

    EditText NameET, SurnameET, PhoneET, EmailET, AddressET, PasswordET, PWET;
    Button SignUpButton;
    TextView backToLogin;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        SignUpButton=(Button) findViewById(R.id.btnSignUpDone);
        NameET=(EditText)findViewById(R.id.etNameSignUp);
        SurnameET=(EditText)findViewById(R.id.etSurnameSignUp);
        PhoneET=(EditText)findViewById(R.id.etPhoneSignUp);
        EmailET=(EditText)findViewById(R.id.etEmailSignUp);
        AddressET=(EditText)findViewById(R.id.etAddressSignUp);
        PasswordET=(EditText)findViewById(R.id.etPasswordSignUp);
        PWET=(EditText)findViewById(R.id.etPasswordAgnSignUp);

        backToLogin = (TextView)findViewById(R.id.tv_back_to_login);

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

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
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

        //String specialCharacters=" !#$%&'()*+,-./:;<=>?@[]^_`{|}~";
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
