package za.co.sendmedelivery.sendmeacab;

import android.os.Bundle;
import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.ChangeBounds;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
//public class TripDetails extends AppCompatActivity {
public class TripDetails extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer; //Drawer Menu
    //Animation variables
    ConstraintLayout tripDetailsLayout;
    LinearLayout bottomTripSpecLayout, topMapLayput, addressOptionsLayout;
    Animation frombottom, fromtop;

    EditText pickupET, dropoffET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        Toolbar toolbar = findViewById(R.id.toolbar_trip_details);
        setSupportActionBar(toolbar);

        //Drawer Menu
        drawer = findViewById(R.id.drawer_layout_trip_details);
        NavigationView navigationView = findViewById(R.id.nav_view_trip_details);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Animation for bottom linear layout with trip details
        bottomTripSpecLayout = (LinearLayout) findViewById(R.id.bottomTripSpecLayout);
        frombottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        bottomTripSpecLayout.setAnimation(frombottom);

        //Animation from top linear layout with map
        topMapLayput = (LinearLayout) findViewById(R.id.topMapLayout);
        fromtop = AnimationUtils.loadAnimation(this, R.anim.fromtop);
        topMapLayput.setAnimation(fromtop);

        tripDetailsLayout = (ConstraintLayout) findViewById(R.id.tripDetailsLayout);
        addressOptionsLayout = (LinearLayout) findViewById(R.id.addressOptionsLayout);
        //On touch of the address text field, implement animation
        pickupET = (EditText) findViewById(R.id.etPickupAddress);
        pickupET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                addressEntryAnim();
                return false;
            }
        });
        //Listener to remove address options if address is being manually inputted
        pickupET.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String pickupValue = pickupET.getText().toString();
                if (!AddressInput(pickupValue)){
                    addressOptionsLayout.setVisibility(View.GONE);
                } else {
                    addressOptionsLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //Once they have stopped typing
        pickupET.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                addressOptionsLayout.setVisibility(View.VISIBLE);
            }
        });

        dropoffET = (EditText) findViewById(R.id.etDropoffAddress);
        //On touch of the address text field, implement animation
        dropoffET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                addressEntryAnim();
                return false;
            }
        });
        //Listener to remove address options if address is being manually inputted
        dropoffET.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String dropoffValue = dropoffET.getText().toString();
                if (!AddressInput(dropoffValue)){
                    addressOptionsLayout.setVisibility(View.GONE);
                } else {
                    addressOptionsLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //Once they have stopped typing
        dropoffET.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                addressOptionsLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    public void btnUseLocation_OnClick(View v){

        //startActivity((new Intent(MainActivity.this, SignUp.class)));
    }
    public void btnDefaultAddress_OnClick(View v){

        //startActivity((new Intent(MainActivity.this, SignUp.class)));
    }
    public void btnSetPickupMap_OnClick(View v){

        //startActivity((new Intent(MainActivity.this, SignUp.class)));
    }

    //Animating the address details layout to go to the top and hide map from screen
    public void addressEntryAnim(){
        //Remove map
        topMapLayput.setVisibility(View.GONE);

        //Take trip specifications layout to the top
        Transition changeBounds = new ChangeBounds();
        changeBounds.setDuration(500);
        //changeBounds.setInterpolator(new BounceInterpolator());
        TransitionManager.beginDelayedTransition(tripDetailsLayout, changeBounds);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(tripDetailsLayout);
        constraintSet.connect(R.id.bottomTripSpecLayout, ConstraintSet.TOP, tripDetailsLayout.getId(), ConstraintSet.TOP, 0);
        constraintSet.clear(R.id.bottomTripSpecLayout, ConstraintSet.BOTTOM);
        constraintSet.applyTo(tripDetailsLayout);
    }

    //Determine if address input text field is empty
    public boolean AddressInput(String addressInput){
        boolean empty = false;
        if (TextUtils.isEmpty(addressInput)) {
            empty = true;
        }
        return empty;
    }

    //Drawer Menu
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_meesage:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_trip_details,
                        new MessageFragment()).commit();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_trip_details,
                        new ProfileFragment()).commit();
                break;
            case R.id.nav_history:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_trip_details,
                        new HistoryFragment()).commit();
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_trip_details,
                        new SettingsFragment()).commit();
                break;
            case R.id.nav_share:
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_send:
                Toast.makeText(this, "Send", Toast.LENGTH_SHORT).show();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void profileView_OnClick(View v){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_trip_details,
                new ProfileFragment()).commit();
        drawer.closeDrawer(GravityCompat.START);
    }
}
