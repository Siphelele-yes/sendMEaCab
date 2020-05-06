package za.co.sendmedelivery.sendmeacab;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    public void btnSignUpDone_OnClick(View v){
        //Final app edit ->
        Toast.makeText(getApplicationContext(),"Welcome to sendME. Happy riding!",
                Toast.LENGTH_LONG);
        startActivity((new Intent(SignUp.this, DefineTripActivity.class)));
        //Final app edit <-
    }

}
