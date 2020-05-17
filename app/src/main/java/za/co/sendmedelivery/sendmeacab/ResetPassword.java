package za.co.sendmedelivery.sendmeacab;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

public class ResetPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
    }

    public void btnReset_OnClick(View v){
        Toast.makeText(getApplicationContext()
                , "Your Password is reset. Happy riding!",
                Toast.LENGTH_LONG).show();

        //Final app edit ->
        startActivity((new Intent(ResetPassword.this, DefineTripActivity.class)));
        //Final app edit <-
    }

}
