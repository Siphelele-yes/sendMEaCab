package za.co.sendmedelivery.sendmeacab;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ForgotPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        TextView tv_Resend = findViewById(R.id.tv_resend);
        String string_Resend = "Resend Email";
        SpannableString ss_Resend = new SpannableString(string_Resend);
        ClickableSpan clickableSpan_Resend = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity((new Intent(ForgotPassword.this, ForgotPassword.class)));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
                ds.setUnderlineText(false);
            }
        };
        ss_Resend.setSpan(clickableSpan_Resend, 0, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_Resend.setText(ss_Resend);
        tv_Resend.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void btnRecoverPassword_OnClick(View v){

        Toast.makeText(getApplicationContext()
                , "Click on the link in the sent email to reset your password",
                Toast.LENGTH_LONG).show();

        TextView tv_Resend = findViewById(R.id.tv_resend);
        tv_Resend.setVisibility(View.VISIBLE);

        //Final app edit ->
        startActivity((new Intent(ForgotPassword.this, ResetPassword.class)));
        //Final app edit <-
    }

}