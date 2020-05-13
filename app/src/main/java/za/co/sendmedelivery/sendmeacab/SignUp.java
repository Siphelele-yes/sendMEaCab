package za.co.sendmedelivery.sendmeacab;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);



        NameET=(EditText)findViewById(R.id.etNameSignUp);
        SurnameET=(EditText)findViewById(R.id.etSurnameSignUp);
        PhoneET=(EditText)findViewById(R.id.etPhoneSignUp);
        EmailET=(EditText)findViewById(R.id.etEmailSignUp);
        AddressET=(EditText)findViewById(R.id.etAddressSignUp);
        PasswordET=(EditText)findViewById(R.id.etPasswordSignUp);
        PWET=(EditText)findViewById(R.id.etPasswordAgnSignUp);

        backToLogin = (TextView)findViewById(R.id.tv_back_to_login);

        SignUpButton=(Button) findViewById(R.id.btnSignUpDone);
    }

    public void btnSignUpDone_OnClick(View v){
        final String fname = NameET.getText().toString();
        final String sname = SurnameET.getText().toString();
        final String phone_num = PhoneET.getText().toString();
        final String email = EmailET.getText().toString();
        final String HomeAdd = AddressET.getText().toString();
        final String passwordEntry1 = PasswordET.getText().toString();
        final String passwordConfirm = PWET.getText().toString();
        final String psw_repeat;
        final int response1=1,response0=0;

        if(passwordEntry1.equals(passwordConfirm)){
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
                                Toast.makeText(getApplicationContext(), "Welcome to sendME. Happy riding! ", Toast.LENGTH_LONG).show();
                                startActivity((new Intent(SignUp.this, TripDetails.class)));
                            }
                            else if(intResponse==3){
                                //Toast.makeText(getApplicationContext(), "There have been a problem signing you up. Please try again! ", Toast.LENGTH_LONG).show();
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
        else {
            Toast.makeText(getApplicationContext(), "Passwords don't match. Re-enter again!" , Toast.LENGTH_LONG).show();
        }
    }

}