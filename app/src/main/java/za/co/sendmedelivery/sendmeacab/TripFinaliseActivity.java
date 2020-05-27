package za.co.sendmedelivery.sendmeacab;

import android.Manifest;
import android.app.Notification;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class TripFinaliseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer; //Drawer Menu
    //Current location for map
    GoogleMap googleMap;
    LatLng latLng, newMarkerLatLng;
    SupportMapFragment mapFragment;
    FusedLocationProviderClient client;
    private final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private LocationSettingsRequest.Builder builder;
    private final int REQUEST_CHECK_CODE = 8989;
    int delay = 2*1000; //Delay for 2 seconds.  One second = 1000 milliseconds.
    //get address from coordinates
    Geocoder geocoder;
    List<Address> address, newAddressList;
    String fullCurrentAddress, fullNewAddress;
    boolean currentLocationSet = false;

    //Animation variables
    ConstraintLayout tripDetailsLayout;
    LinearLayout bottomTripSpecLayout, addressOptionsLayout;
    FrameLayout topMapLayout;
    Animation frombottom, fromtop;

    Button currentLocationBtn, defaultAddressBtn, setOnMapBtn;
    View currentLocationDiv;
    private static final String TAG = TripDetails.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_finalise);

        Toolbar toolbar = findViewById(R.id.toolbar_trip_finalise_activity);
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

        //Drawer Menu
        drawer = findViewById(R.id.drawer_layout_trip_finalise_activity);
        NavigationView navigationView = findViewById(R.id.nav_view_trip_finalise_activity);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        String apiKey = getString(R.string.api_key);

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

    //Drawer Menu
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_meesage:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_trip_finalise_activity,
                        new MessageFragment()).commit();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_trip_finalise_activity,
                        new ProfileFragment()).commit();
                break;
            case R.id.nav_history:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_trip_finalise_activity,
                        new HistoryFragment()).commit();
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_trip_finalise_activity,
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
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_trip_finalise_activity,
                new ProfileFragment()).commit();
        drawer.closeDrawer(GravityCompat.START);
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
