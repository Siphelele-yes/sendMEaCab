package za.co.sendmedelivery.sendmeacab;

import android.content.Intent;
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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

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

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://sendmedelivery.co.za/aCab/App/login.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "This is a response: " + response, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
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
