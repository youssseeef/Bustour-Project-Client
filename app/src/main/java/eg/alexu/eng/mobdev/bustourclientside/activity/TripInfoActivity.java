package eg.alexu.eng.mobdev.bustourclientside.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import eg.alexu.eng.mobdev.bustourclientside.R;
import eg.alexu.eng.mobdev.bustourclientside.utilities.Constants;
import eg.alexu.eng.mobdev.bustourclientside.utilities.Extras;

public class TripInfoActivity extends AppCompatActivity {

    private TextView mDriverName;
    private TextView mDriverPhone;
    private TextView mTripName;
    private TextView mTripDescription;
    private ImageView mDriverImage;
    private String mDriverId;
    private String mTripId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_info);
        initializeFields();
        setDriverName();
        setDriverPhone();
        setTripName();
        setTripDescription();
        setDriverImage();
    }

    private void setDriverImage() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child(Constants.DRIVERS).
                child(mDriverId).
                child(Constants.USER_PHOTO).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String url = dataSnapshot.getValue(String.class);
                        if (url != null)
                        Glide.with(mDriverImage.getContext())
                                .load(url)
                                .asBitmap()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.user)
                                .error(R.drawable.user)
                                .centerCrop()
                                .override(500, 500).
                                into(new BitmapImageViewTarget(mDriverImage) {
                                    @Override
                                    protected void setResource(Bitmap resource) {
                                        RoundedBitmapDrawable circularBitmapDrawable =
                                                RoundedBitmapDrawableFactory.create(mDriverImage.getResources(), resource);
                                        circularBitmapDrawable.setCircular(true);
                                        mDriverImage.setImageDrawable(circularBitmapDrawable);
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setTripDescription() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child(Constants.DRIVERS).
                child(mDriverId).
                child(Constants.TRIPS).
                child(mTripId).
                child(Constants.DESCRIPTION).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mTripDescription.setText(dataSnapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setTripName() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child(Constants.DRIVERS).
                child(mDriverId).
                child(Constants.TRIPS).
                child(mTripId).
                child(Constants.NAME).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mTripName.setText(dataSnapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setDriverPhone() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child(Constants.DRIVERS).
                child(mDriverId).
                child(Constants.PHONE).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mDriverPhone.setText(dataSnapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setDriverName() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child(Constants.DRIVERS).
                child(mDriverId).
                child(Constants.NAME).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mDriverName.setText(dataSnapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void initializeFields() {
        mDriverId = getIntent().getStringExtra(Extras.DRIVER_ID);
        mTripId = getIntent().getStringExtra(Extras.TRIP_ID);
        mDriverName = (TextView) findViewById(R.id.driver_name_trip_info);
        mDriverPhone = (TextView) findViewById(R.id.driver_phone_trip_info);
        mTripName = (TextView) findViewById(R.id.trip_name_trip_info);
        mTripDescription = (TextView) findViewById(R.id.trip_description_trip_info);
        mDriverImage = (ImageView) findViewById(R.id.driver_image);
    }

}
