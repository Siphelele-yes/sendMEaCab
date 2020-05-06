package za.co.sendmedelivery.sendmeacab;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        //Final app edit ->
        startActivity((new Intent(MainActivity.this, DefineTripActivity.class)));
        //Final app edit <-
    }

    public void btnSignUp_OnClick(View v){

        startActivity((new Intent(MainActivity.this, SignUp.class)));
    }
}
