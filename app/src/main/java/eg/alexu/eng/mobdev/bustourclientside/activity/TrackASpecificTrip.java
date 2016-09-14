package eg.alexu.eng.mobdev.bustourclientside.activity;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import eg.alexu.eng.mobdev.bustourclientside.R;
import eg.alexu.eng.mobdev.bustourclientside.utilities.Constants;
import eg.alexu.eng.mobdev.bustourclientside.utilities.Extras;

public class TrackASpecificTrip extends FragmentActivity implements OnMapReadyCallback {

    private static final double DISTANCE_RADIUS = 200.0;
    private GoogleMap mMap;
    private String mTripId;
    private String mDriverId;
    private String mLatUserPickUpLocation;
    private String mLongUserPickUpLocation;
    private boolean enableTrackingDriver;
    private Marker marker;
    private String mLatDriverLocation;
    private String mLongDriverLocation;
    private MarkerOptions driverMarkerOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_aspecific_trip);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        driverMarkerOptions = new MarkerOptions();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mLatUserPickUpLocation = getIntent().getStringExtra(Extras.LATITUDE);
        mLongUserPickUpLocation = getIntent().getStringExtra(Extras.LONGITUDE);
        mTripId = getIntent().getStringExtra(Extras.TRIP_ID);
        mDriverId = getIntent().getStringExtra(Extras.DRIVER_ID);
        addListenerToEnableTracking();
        addListenerToDriverLocLat();
        addListenerToDriverLocLong();
        addMarkerToUserPickUp();
    }

    private void addMarkerToUserPickUp() {
        LatLng pickUpLocation = new LatLng(Double.parseDouble(mLatUserPickUpLocation),
                Double.parseDouble(mLongUserPickUpLocation));
        mMap.setTrafficEnabled(true);
        mMap.addCircle(new CircleOptions().center(pickUpLocation).radius(DISTANCE_RADIUS).visible(true).fillColor(0x30000000).strokeColor(Color.RED).strokeWidth(5));
        mMap.addMarker(new MarkerOptions().position(pickUpLocation).title("User Pickup Location"));
        driverMarkerOptions.title("Driver Current Location");
        driverMarkerOptions.position(new LatLng(0.0, 0.0));
        marker = mMap.addMarker(driverMarkerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickUpLocation, 15));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
    }

    private void addListenerToDriverLocLong() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child(Constants.DRIVERS).
                child(mDriverId).
                child(Constants.TRIPS).
                child(mTripId).
                child(Constants.LOC_Y).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mLongDriverLocation = dataSnapshot.getValue(String.class);
                        updateMarkerPosition();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private void addListenerToDriverLocLat() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child(Constants.DRIVERS).
                child(mDriverId).
                child(Constants.TRIPS).
                child(mTripId).
                child(Constants.LOC_X).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mLatDriverLocation = dataSnapshot.getValue(String.class);
                        updateMarkerPosition();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private void updateMarkerPosition() {
        if(mLatDriverLocation != null && mLongDriverLocation != null) {
            LatLng driverLoc = new LatLng(Double.parseDouble(mLatDriverLocation),
                    Double.parseDouble(mLongDriverLocation));
            marker.setPosition(driverLoc);
            if (enableTrackingDriver)
                marker.setVisible(true);
            else
                marker.setVisible(false);
        }
    }

    private void addListenerToEnableTracking() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child(Constants.DRIVERS).
                child(mDriverId).
                child(Constants.TRIPS).
                child(mTripId).
                child(Constants.ENABLE_TRACKING).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        enableTrackingDriver = Boolean.parseBoolean(dataSnapshot.getValue(String.class));
                        updateMarkerPosition();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }
}
