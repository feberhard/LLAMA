package org.llama.llama.map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Looper;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.androidmapsextensions.OnMapReadyCallback;
import com.androidmapsextensions.SupportMapFragment;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.llama.llama.MyApp;
import org.llama.llama.PermissionRequests;
import org.llama.llama.R;
import org.llama.llama.services.IChatService;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ValueEventListener, GoogleMap.OnMarkerClickListener {

    private final static Double COORDINATES_OFFSET = 1.008;
    private final static long TIMER_PERIOD = 5000;

    DatabaseReference usersRef;
    DatabaseReference myLocationRef;

    @Inject
    IChatService chatService;

    private GoogleMap mMap;
    private LatLng myLatLng;

    private Timer timer;
    private TimerTask timerTask;
    private boolean timerActive;

    private String lastUser;

    @Override
    protected void onResume() {
        super.onResume();

        timerActive = true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        timerActive = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApp) getApplication()).getServiceComponent().inject(this);

        this.lastUser = "";

        // is access granted?
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PermissionRequests.LOCATION_FINE);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                PermissionRequests.LOCATION_COARSE);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getExtendedMapAsync(this);

        // setting up db
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        myLocationRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("location");

        // setting up the timer
        timerTask = new TimerTask() {
            @Override
            public void run() {

                if(timerActive) {

                    // only run, if there is already a nown position of ourselves
                    if (MapsActivity.this.myLatLng != null) {
                        // filter for latitude at server side, longitude later
                        Query query = usersRef.orderByChild("location/0").startAt(myLatLng.latitude - COORDINATES_OFFSET).endAt(myLatLng.latitude + COORDINATES_OFFSET);
                        query.addListenerForSingleValueEvent(MapsActivity.this);
                    }
                }
            }
        };

        timer = new Timer();
        timerActive = true;
        timer.schedule(timerTask, 0, TIMER_PERIOD);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // setting up the map
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, getResources().getText(R.string.location_not_enabled), Toast.LENGTH_LONG).show();
            this.finish();
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                return false;
            }
        });
        mMap.setOnMarkerClickListener(this);

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria providerCriteria = new Criteria();
        providerCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        providerCriteria.setSpeedRequired(false);

        String bestProvider = lm.getBestProvider(providerCriteria, true);

        lm.requestSingleUpdate(providerCriteria, new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);

                // store new location in db
                myLocationRef.child("0").setValue(myLatLng.latitude);
                myLocationRef.child("1").setValue(myLatLng.longitude);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        }, null);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Log.d("MAP", "Users nearby:");

        for(DataSnapshot user : dataSnapshot.getChildren()) {
            // skip ourselves!
            if (user.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                continue;
            }

            // check longitude
            Double longitude = user.child("location/1").getValue(Double.class);
            if (longitude.compareTo(myLatLng.longitude - COORDINATES_OFFSET) < 1 || longitude.compareTo(myLatLng.longitude + COORDINATES_OFFSET) > 1)
                continue;

            Log.d("MAP", user.child("username").getValue(String.class) != null ? user.child("username").getValue(String.class) : "anonymous");

            Double latitude = user.child("location/0").getValue(Double.class);
            String username = user.child("username").getValue(String.class);
            String userId = user.getKey();
            String country = user.child("country").getValue(String.class);
            String mood = user.child("mood").getValue(String.class);
            String defaultLanguage = user.child("defaultLanguage").getValue(String.class);

            MarkerOptions mo = new MarkerOptions()
                    .data(username)
                    .draggable(false)
                    .position(new LatLng(latitude, longitude))
                    .title(username)
                    .snippet(country + " | \"" + mood + "\"");


            // read a flag from the assets folder
            SVG svg = null;
            try {
                svg = SVG.getFromAsset(MyApp.getAppContext().getAssets(), "flags/" + defaultLanguage + ".svg");
            } catch (SVGParseException | IOException e) {
                Log.d("MAP", "Error loading svg for default language " + defaultLanguage);
            }
            // create a canvas to draw onto
            if (svg.getDocumentWidth() != -1) {
                Bitmap bitmap = Bitmap.createBitmap(100, 75, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                canvas.drawPicture(svg.renderToPicture(), new Rect(0, 0, 100, 75));

                mo.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
            }

            mMap.addMarker(mo);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String username = marker.getData();
        if (this.lastUser.equals(username)) {
            chatService.createDialogChat(username);
            this.lastUser = "";
            Toast.makeText(this, "A new chat was created in your Chatlist.", Toast.LENGTH_LONG).show();
        } else {
            this.lastUser = username;
            Toast.makeText(this, "Click again to create a new dialog in the chat list.", Toast.LENGTH_LONG).show();
        }

        return false;
    }
}
