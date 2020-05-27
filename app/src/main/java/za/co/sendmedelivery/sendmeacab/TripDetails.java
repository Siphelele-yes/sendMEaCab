package za.co.sendmedelivery.sendmeacab;

import android.Manifest;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.navigation.NavigationView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.ChangeBounds;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

//public class TripDetails extends AppCompatActivity {
public class TripDetails extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer; //Drawer Menu
    //Current location for map
    GoogleMap googleMap;
    LatLng latLng, newMarkerLatLng;
    SupportMapFragment mapFragment;
    FusedLocationProviderClient client;
    private final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private LocationSettingsRequest.Builder builder;
    private final int REQUEST_CHECK_CODE = 8989;
    //Try to get current location every 2 seconds
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 2*1000; //Delay for 2 seconds.  One second = 1000 milliseconds.
    //get address from coordinates
    Geocoder geocoder;
    List<Address> address, newAddressList;
    String fullCurrentAddress, fullNewAddress;
    boolean currentLocationSet = false;

    //Animation variables
    ConstraintLayout tripDetailsLayout;
    LinearLayout bottomTripSpecLayout, addressOptionsLayout, predTripLayout;
    FrameLayout topMapLayout;
    Animation frombottom, fromtop;

    //Autocomplete
    Button predBtn1, predBtn2, predBtn3, predBtn4, predBtn5, predBtn6, predBtn7, predBtn8
            , currentLocationBtn, defaultAddressBtn, setOnMapBtn;
    View predDiv1, predDiv2, predDiv3, predDiv4, predDiv5, predDiv6, predDiv7, predDiv8
            , currentLocationDiv;
    int predCnt;
    StringBuilder mResult;
    private static final String TAG = TripDetails.class.getSimpleName();
    private AutoCompleteTextView locationSearchActv1, locationSearchActv2, pickupET, dropoffET;// instance of AutoCompleteText View

    EditText queryTxt1, queryTxt2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        Toolbar toolbar = findViewById(R.id.toolbar_trip_details);
        setSupportActionBar(toolbar);

        //Declarations
        currentLocationBtn = (Button) findViewById(R.id.btnUseLocationPickup);
        currentLocationDiv = (View) findViewById(R.id.divider3);
        defaultAddressBtn = (Button) findViewById(R.id.btnDefaultPickup);
        setOnMapBtn = (Button) findViewById(R.id.btnPickupMap);

                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.topMapLayout);
        //mapFragment.getMapAsync(this);
        client = LocationServices .getFusedLocationProviderClient(this);
        if(ActivityCompat.checkSelfPermission(TripDetails.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(TripDetails.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_LOCATION);
        }
        LocationRequest request = new LocationRequest()
                .setFastestInterval(1500)
                .setInterval(3000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(request);

        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    task.getResult(ApiException.class);
                } catch (ApiException e) {
                   switch(e.getStatusCode()){
                       case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                           try {
                               ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                               resolvableApiException.startResolutionForResult(TripDetails.this, REQUEST_CHECK_CODE);
                           } catch (IntentSender.SendIntentException ex) {
                               ex.printStackTrace();
                           } catch (ClassCastException ex){

                           }

                           break;
                       case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:{
                           break;
                       }
                   }
                }
            }
        });



        //Drawer Menu
        drawer = findViewById(R.id.drawer_layout_trip_details);
        NavigationView navigationView = findViewById(R.id.nav_view_trip_details);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Autocomplete
        locationSearchActv1 = findViewById(R.id.etPickupAddress);
        locationSearchActv2 = findViewById(R.id.etDropoffAddress);
        queryTxt1 = findViewById(R.id.etPickupAddress);
        queryTxt2 = findViewById(R.id.etDropoffAddress);
        String apiKey = getString(R.string.api_key);
        predTripLayout = findViewById(R.id.predictionsTripLayout);
        predBtn1 = (Button) findViewById(R.id.pred1);
        predBtn2 = (Button) findViewById(R.id.pred2);
        predBtn3 = (Button) findViewById(R.id.pred3);
        predBtn4 = (Button) findViewById(R.id.pred4);
        predBtn5 = (Button) findViewById(R.id.pred5);
        predBtn6 = (Button) findViewById(R.id.pred6);
        predBtn7 = (Button) findViewById(R.id.pred7);
        predBtn8 = (Button) findViewById(R.id.pred8);
        predDiv1 = (View) findViewById(R.id.predDiv1);
        predDiv2 = (View) findViewById(R.id.predDiv2);
        predDiv3 = (View) findViewById(R.id.predDiv3);
        predDiv4 = (View) findViewById(R.id.predDiv4);
        predDiv5 = (View) findViewById(R.id.predDiv5);
        predDiv6 = (View) findViewById(R.id.predDiv6);
        predDiv7 = (View) findViewById(R.id.predDiv7);
        predDiv8 = (View) findViewById(R.id.predDiv8);

        //Animation for bottom linear layout with trip details
        bottomTripSpecLayout = (LinearLayout) findViewById(R.id.bottomTripSpecLayout);
        frombottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        bottomTripSpecLayout.setAnimation(frombottom);

        //Animation from top linear layout with map
        topMapLayout = (FrameLayout) findViewById(R.id.topMapLayout);
        fromtop = AnimationUtils.loadAnimation(this, R.anim.fromtop);
        topMapLayout.setAnimation(fromtop);

        tripDetailsLayout = (ConstraintLayout) findViewById(R.id.tripDetailsLayout);
        addressOptionsLayout = (LinearLayout) findViewById(R.id.addressOptionsLayout);

        if(!Places.isInitialized()){
            Places.initialize(getApplicationContext(),apiKey);
        }
        PlacesClient places = Places.createClient(this);
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(-33.880490, 151.184363), //dummy lat/lng
                new LatLng(-33.858754, 151.229596));

        pickupET = (AutoCompleteTextView) findViewById(R.id.etPickupAddress);
        locationSearchActv1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String pickupValue = pickupET.getText().toString();
                if (!AddressInput(pickupValue)){
                    addressOptionsLayout.setVisibility(View.GONE);
                    predTripLayout.setVisibility(View.VISIBLE);
                } else {
                    addressOptionsLayout.setVisibility(View.VISIBLE);
                    predTripLayout.setVisibility(View.GONE);
                }

                predCnt = 0;
                predBtn1.setVisibility(View.GONE);
                predBtn2.setVisibility(View.GONE);
                predBtn3.setVisibility(View.GONE);
                predBtn4.setVisibility(View.GONE);
                predBtn5.setVisibility(View.GONE);
                predBtn6.setVisibility(View.GONE);
                predBtn7.setVisibility(View.GONE);
                predBtn8.setVisibility(View.GONE);
                predDiv1.setVisibility(View.GONE);
                predDiv2.setVisibility(View.GONE);
                predDiv3.setVisibility(View.GONE);
                predDiv4.setVisibility(View.GONE);
                predDiv5.setVisibility(View.GONE);
                predDiv6.setVisibility(View.GONE);
                predDiv7.setVisibility(View.GONE);
                predDiv8.setVisibility(View.GONE);

                Notification.MessagingStyle.Message queryText;
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        // Call either setLocationBias() OR setLocationRestriction().
                        .setLocationBias(bounds)
                        .setCountry("za")
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setSessionToken(token)
                        .setQuery(queryTxt1.getText().toString())
                        .build();

                places.findAutocompletePredictions(request).addOnSuccessListener(response -> {
                    mResult = new StringBuilder();


                    for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {

                        mResult.append(prediction.getFullText(null) + "\n");
                        Log.i(TAG, prediction.getPlaceId());
                        Log.i(TAG, prediction.getPrimaryText(null).toString());

                        predCnt++;

                        if (predCnt == 1){
                            predBtn1.setVisibility(View.VISIBLE);
                            predDiv1.setVisibility(View.VISIBLE);
                            predBtn1.setText(prediction.getFullText(null));
                        } else if (predCnt == 2){
                            predBtn2.setVisibility(View.VISIBLE);
                            predDiv2.setVisibility(View.VISIBLE);
                            predBtn2.setText(prediction.getFullText(null));
                        } else if (predCnt == 3){
                            predBtn3.setVisibility(View.VISIBLE);
                            predDiv3.setVisibility(View.VISIBLE);
                            predBtn3.setText(prediction.getFullText(null));
                        } else if (predCnt == 4){
                            predBtn4.setVisibility(View.VISIBLE);
                            predDiv4.setVisibility(View.VISIBLE);
                            predBtn4.setText(prediction.getFullText(null));
                        } else if (predCnt == 5){
                            predBtn5.setVisibility(View.VISIBLE);
                            predDiv5.setVisibility(View.VISIBLE);
                            predBtn5.setText(prediction.getFullText(null));
                        } else if (predCnt == 6){
                            predBtn6.setVisibility(View.VISIBLE);
                            predDiv6.setVisibility(View.VISIBLE);
                            predBtn6.setText(prediction.getFullText(null));
                        } else if (predCnt == 7){
                            predBtn7.setVisibility(View.VISIBLE);
                            predDiv7.setVisibility(View.VISIBLE);
                            predBtn7.setText(prediction.getFullText(null));
                        } else if (predCnt == 8){
                            predBtn8.setVisibility(View.VISIBLE);
                            predDiv8.setVisibility(View.VISIBLE);
                            predBtn8.setText(prediction.getFullText(null));
                        }
                    }
                    predTripLayout.setVisibility(View.VISIBLE);
                    predBtn1.setOnClickListener(v -> {
                        pickupET.setText(predBtn1.getText());
                        predTripLayout.setVisibility(View.GONE);
                    });
                    predBtn2.setOnClickListener(v -> {
                        pickupET.setText(predBtn2.getText());
                        predTripLayout.setVisibility(View.GONE);
                    });
                    predBtn3.setOnClickListener(v -> {
                        pickupET.setText(predBtn3.getText());
                        predTripLayout.setVisibility(View.GONE);
                    });
                    predBtn4.setOnClickListener(v -> {
                        pickupET.setText(predBtn4.getText());
                        predTripLayout.setVisibility(View.GONE);
                    });
                    predBtn5.setOnClickListener(v -> {
                        pickupET.setText(predBtn5.getText());
                        predTripLayout.setVisibility(View.GONE);
                    });
                    predBtn6.setOnClickListener(v -> {
                        pickupET.setText(predBtn6.getText());
                        predTripLayout.setVisibility(View.GONE);
                    });
                    predBtn7.setOnClickListener(v -> {
                        pickupET.setText(predBtn7.getText());
                        predTripLayout.setVisibility(View.GONE);
                    });
                    predBtn8.setOnClickListener(v -> {
                        pickupET.setText(predBtn8.getText());
                        predTripLayout.setVisibility(View.GONE);
                    });
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                    }
                });
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        dropoffET = (AutoCompleteTextView) findViewById(R.id.etDropoffAddress);
        locationSearchActv2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String dropoffValue = dropoffET.getText().toString();
                if (!AddressInput(dropoffValue)){
                    addressOptionsLayout.setVisibility(View.GONE);
                    predTripLayout.setVisibility(View.VISIBLE);
                } else {
                    addressOptionsLayout.setVisibility(View.VISIBLE);
                    predTripLayout.setVisibility(View.GONE);
                }

                predCnt = 0;
                predBtn1.setVisibility(View.GONE);
                predBtn2.setVisibility(View.GONE);
                predBtn3.setVisibility(View.GONE);
                predBtn4.setVisibility(View.GONE);
                predBtn5.setVisibility(View.GONE);
                predBtn6.setVisibility(View.GONE);
                predBtn7.setVisibility(View.GONE);
                predBtn8.setVisibility(View.GONE);
                predDiv1.setVisibility(View.GONE);
                predDiv2.setVisibility(View.GONE);
                predDiv3.setVisibility(View.GONE);
                predDiv4.setVisibility(View.GONE);
                predDiv5.setVisibility(View.GONE);
                predDiv6.setVisibility(View.GONE);
                predDiv7.setVisibility(View.GONE);
                predDiv8.setVisibility(View.GONE);

                Notification.MessagingStyle.Message queryText;
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        // Call either setLocationBias() OR setLocationRestriction().
                        .setLocationBias(bounds)
                        .setCountry("za")
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setSessionToken(token)
                        .setQuery(queryTxt2.getText().toString())
                        .build();

                places.findAutocompletePredictions(request).addOnSuccessListener(response -> {
                    mResult = new StringBuilder();

                    for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {

                        mResult.append(prediction.getFullText(null) + "\n");
                        Log.i(TAG, prediction.getPlaceId());
                        Log.i(TAG, prediction.getPrimaryText(null).toString());

                        predCnt++;

                        if (predCnt == 1){
                            predBtn1.setVisibility(View.VISIBLE);
                            predDiv1.setVisibility(View.VISIBLE);
                            predBtn1.setText(prediction.getFullText(null));
                        } else if (predCnt == 2){
                            predBtn2.setVisibility(View.VISIBLE);
                            predDiv2.setVisibility(View.VISIBLE);
                            predBtn2.setText(prediction.getFullText(null));
                        } else if (predCnt == 3){
                            predBtn3.setVisibility(View.VISIBLE);
                            predDiv3.setVisibility(View.VISIBLE);
                            predBtn3.setText(prediction.getFullText(null));
                        } else if (predCnt == 4){
                            predBtn4.setVisibility(View.VISIBLE);
                            predDiv4.setVisibility(View.VISIBLE);
                            predBtn4.setText(prediction.getFullText(null));
                        } else if (predCnt == 5){
                            predBtn5.setVisibility(View.VISIBLE);
                            predDiv5.setVisibility(View.VISIBLE);
                            predBtn5.setText(prediction.getFullText(null));
                        } else if (predCnt == 6){
                            predBtn6.setVisibility(View.VISIBLE);
                            predDiv6.setVisibility(View.VISIBLE);
                            predBtn6.setText(prediction.getFullText(null));
                        } else if (predCnt == 7){
                            predBtn7.setVisibility(View.VISIBLE);
                            predDiv7.setVisibility(View.VISIBLE);
                            predBtn7.setText(prediction.getFullText(null));
                        } else if (predCnt == 8){
                            predBtn8.setVisibility(View.VISIBLE);
                            predDiv8.setVisibility(View.VISIBLE);
                            predBtn8.setText(prediction.getFullText(null));
                        }
                    }
                    predTripLayout.setVisibility(View.VISIBLE);
                    predBtn1.setOnClickListener(v -> {
                        dropoffET.setText(predBtn1.getText());
                        predTripLayout.setVisibility(View.GONE);
                    });
                    predBtn2.setOnClickListener(v -> {
                        dropoffET.setText(predBtn2.getText());
                        predTripLayout.setVisibility(View.GONE);
                    });
                    predBtn3.setOnClickListener(v -> {
                        dropoffET.setText(predBtn3.getText());
                        predTripLayout.setVisibility(View.GONE);
                    });
                    predBtn4.setOnClickListener(v -> {
                        dropoffET.setText(predBtn4.getText());
                        predTripLayout.setVisibility(View.GONE);
                    });
                    predBtn5.setOnClickListener(v -> {
                        dropoffET.setText(predBtn5.getText());
                        predTripLayout.setVisibility(View.GONE);
                    });
                    predBtn6.setOnClickListener(v -> {
                        dropoffET.setText(predBtn6.getText());
                        predTripLayout.setVisibility(View.GONE);
                    });
                    predBtn7.setOnClickListener(v -> {
                        dropoffET.setText(predBtn7.getText());
                        predTripLayout.setVisibility(View.GONE);
                    });
                    predBtn8.setOnClickListener(v -> {
                        dropoffET.setText(predBtn8.getText());
                        predTripLayout.setVisibility(View.GONE);
                    });
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                    }
                });
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        pickupET.setOnFocusChangeListener((View v, boolean hasFocus) -> {
            if (!hasFocus) {
                predTripLayout.setVisibility(View.GONE);
                addressOptionsLayout.setVisibility(View.VISIBLE);
            } else {
                currentLocationBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pickupET.setText(fullCurrentAddress);
                    }
                });
                defaultAddressBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                setOnMapBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addMarkerOnMap(currentLocationSet, 1, pickupET);
                    }
                });
            }
        });

        //On touch of the address text field, implement animation
        pickupET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //addressEntryAnim();
                return false;
            }
        });
        //On touch of the address text field, implement animation
        dropoffET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //addressEntryAnim();
                return false;
            }
        });
        //Once they have stopped typing
        dropoffET.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                predTripLayout.setVisibility(View.GONE);
                addressOptionsLayout.setVisibility(View.VISIBLE);
            } else {
                currentLocationBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dropoffET.setText(fullCurrentAddress);
                    }
                });
                defaultAddressBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                setOnMapBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addMarkerOnMap(currentLocationSet, 1, dropoffET);
                    }
                });
            }
        });
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
        topMapLayout.setVisibility(View.GONE);

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

    private void getCurrentLocation() {
        geocoder = new Geocoder(this, Locale.getDefault());
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            handler.removeCallbacks(runnable); //stop handler when activity not visible
                            //Layout changes
                            currentLocationBtn.setVisibility(View.VISIBLE);
                            currentLocationDiv.setVisibility(View.VISIBLE);
                            //Map settings
                            latLng = new LatLng(location.getLatitude(),
                                    location.getLongitude());
                            MarkerOptions options = new MarkerOptions().position(latLng)
                                    .title("You are here")
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            googleMap.addMarker(options);
                            //get current address
                            try {
                                address = geocoder.getFromLocation(location.getLatitude(),
                                        location.getLongitude(),1);

                                fullCurrentAddress = address.get(0).getAddressLine(0);
                                currentLocationSet = true;

                                //Toast.makeText(getApplicationContext(), fullCurrentAddress,Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                currentLocationBtn.setVisibility(View.GONE);
                                currentLocationDiv.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    protected void onResume() {
        //start handler as activity become visible
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                getCurrentLocation();
                handler.postDelayed(runnable, delay);
            }
        }, delay);

        super.onResume();
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable); //stop handler when activity not visible
        super.onPause();
    }

    public void addMarkerOnMap(boolean currentLocationSet, int place, EditText etName){
        boolean feedback = false;
        if(!currentLocationSet){
            latLng = new LatLng(-29.0852,26.1596);
        }
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                MarkerOptions pickUpOptions = new MarkerOptions().position(googleMap.getCameraPosition().target)
                        .title("Pick up point")
                        .draggable(false);
                Marker newMarker = googleMap.addMarker(pickUpOptions);
                googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        try {
                            newMarkerLatLng = googleMap.getCameraPosition().target;
                            newAddressList = geocoder.getFromLocation(newMarkerLatLng.latitude,
                                    newMarkerLatLng.longitude,1);
                            fullNewAddress = newAddressList.get(0).getAddressLine(0);
                            etName.setText(fullNewAddress);
                            boolean feedback = true;
                        } catch (IOException e) {
                            currentLocationBtn.setVisibility(View.GONE);
                            currentLocationDiv.setVisibility(View.GONE);
                        }
                    }
                });
                googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {
                        newMarker.setPosition(googleMap.getCameraPosition().target);//to center in map
                    }
                });
            }
        });
    }
}
