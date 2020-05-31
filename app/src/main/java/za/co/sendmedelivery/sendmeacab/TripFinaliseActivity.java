package za.co.sendmedelivery.sendmeacab;

import android.Manifest;
import android.app.Notification;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TripFinaliseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private DrawerLayout drawer; //Drawer Menu
    //Current location for map
    GoogleMap mMap;
    LatLng pickupLatLng, dropoffLatLng;
    SupportMapFragment mapFragment;
    //Route on map
    ArrayList<LatLng> listPoints;

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
        mapFragment.getMapAsync(this);

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

    private String getRequestUrl(LatLng origin, LatLng dest) {
        String str_org = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String param = str_org + "&" + str_dest + "&" + sensor + "&" + mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param +
                "&key=AIzaSyBLCIeprajGmDHwzYDy-pkHPKJtO8juWLo";

        return url;
    }

    private String requestDirections(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        URL url = null;
        try {
            url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
            inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return  responseString;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        listPoints = new ArrayList<>();
        //pickup
        pickupLatLng = new LatLng(-29.139185,26.237474);
        listPoints.add(pickupLatLng);
        MarkerOptions pickupOptions = new MarkerOptions().position(pickupLatLng)
                .title("Pick up point")
                .draggable(false);
        Marker pickupMarker = mMap.addMarker(pickupOptions);
        //dropoff
        dropoffLatLng = new LatLng(-29.093619,26.165049);
        listPoints.add(dropoffLatLng);
        MarkerOptions dropoffOptions = new MarkerOptions().position(dropoffLatLng)
                .title("Drop off point")
                .draggable(false);
        Marker dropoffMarker = mMap.addMarker(dropoffOptions);
        //Routes
        String url = getRequestUrl(listPoints.get(0), listPoints.get(1));
        TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
        taskRequestDirections.execute(url);
        //map
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pickupLatLng, 12));
    }

    public class TaskRequestDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirections(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse json here
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>> >{

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            ArrayList points = null;
            PolylineOptions polylineOptions = null;

            for(List<HashMap<String, String>> path : lists){
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path){
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat, lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }

            if(polylineOptions != null){
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Directions not found", Toast.LENGTH_SHORT).show();
            }
        }
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
}
