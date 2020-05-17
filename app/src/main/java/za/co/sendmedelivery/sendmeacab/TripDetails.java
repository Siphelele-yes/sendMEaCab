package za.co.sendmedelivery.sendmeacab;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class TripDetails extends AppCompatActivity {
    //Animation variables
    LinearLayout bottomTripSpecLayout, topMapLayput;
    Animation frombottom, fromtop;

    EditText pickupET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        //Animation for bottom linear layout with trip details
        bottomTripSpecLayout = (LinearLayout) findViewById(R.id.bottomTripSpecLayout);
        frombottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        bottomTripSpecLayout.setAnimation(frombottom);

        //Animation from top linear layout with map
        topMapLayput = (LinearLayout) findViewById(R.id.topMapLayout);
        fromtop = AnimationUtils.loadAnimation(this, R.anim.fromtop);
        topMapLayput.setAnimation(fromtop);

        pickupET = (EditText) findViewById(R.id.etPickupAddress);
        //pickupET.addTextChangedListener(filterTextWatcher);
        pickupET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ViewGroup.LayoutParams params = topMapLayput.getLayoutParams();
                params.height = 1;
                params.width = 1;
                topMapLayput.setLayoutParams(params);

                return false;
            }
        });
    }

    /*private TextWatcher filterTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            ViewGroup.LayoutParams params = topMapLayput.getLayoutParams();
            params.height = 1;
            params.width = 1;
            topMapLayput.setLayoutParams(params);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };*/


    public void btnUseLocation_OnClick(View v){

        //startActivity((new Intent(MainActivity.this, SignUp.class)));
    }
    public void btnDefaultAddress_OnClick(View v){

        //startActivity((new Intent(MainActivity.this, SignUp.class)));
    }
    public void btnSetPickupMap_OnClick(View v){

        //startActivity((new Intent(MainActivity.this, SignUp.class)));
    }


}
