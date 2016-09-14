package eg.alexu.eng.mobdev.bustourclientside.activity;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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
import eg.alexu.eng.mobdev.bustourclientside.adapter.TripsAdapter;
import eg.alexu.eng.mobdev.bustourclientside.utilities.Constants;
import eg.alexu.eng.mobdev.bustourclientside.utilities.Extras;

public class FakeCallActivity extends AppCompatActivity {

    private ImageView mDriverImage;
    private TextView mDriverName;
    private TextView mDriverPhone;
    private String mDriverId;
    private ImageView mAcceptPhone;
    private ImageView mRefusePhone;
    private MediaPlayer mp;
    private Animation slideLeft;
    private Animation slideRight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        TripsAdapter.activeFakeCall = true;
        playMusic();
        setContentView(R.layout.activity_fake_call);
        mDriverId = getIntent().getStringExtra(Extras.DRIVER_ID);
        mDriverImage = (ImageView) findViewById(R.id.user_photo_profile_fake_call);
        mDriverName = (TextView) findViewById(R.id.driver_name_fake_call);
        mDriverPhone = (TextView) findViewById(R.id.driver_phone_fake_call);
        mAcceptPhone = (ImageView) findViewById(R.id.accept_phone);
        mRefusePhone = (ImageView) findViewById(R.id.reject_phone);
        slideLeft = AnimationUtils.loadAnimation(this, R.anim.slide_left);
        slideRight = AnimationUtils.loadAnimation(this, R.anim.slide_right);
        mAcceptPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideRight();
                finish();
            }
        });
        mRefusePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideLeft();
                finish();
            }
        });
        setDriverName();
        setDriverPhone();
        setDriverImage();

    }

    private void slideRight() {
        mAcceptPhone.clearAnimation();
        mRefusePhone.clearAnimation();
        mAcceptPhone.startAnimation(slideRight);
    }

    private void slideLeft() {
        mAcceptPhone.clearAnimation();
        mRefusePhone.clearAnimation();
        mRefusePhone.startAnimation(slideLeft);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setDriverImage() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child(Constants.DRIVERS).
                child(mDriverId).
                child(Constants.USER_PHOTO).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String urlString = dataSnapshot.getValue(String.class);
                        if (urlString != null) {
                            Glide.with(mDriverImage.getContext())
                                    .load(urlString)
                                    .asBitmap()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .placeholder(R.drawable.user)
                                    .error(R.drawable.user)
                                    .centerCrop()
                                    .into(new BitmapImageViewTarget(mDriverImage) {
                                        @Override
                                        protected void setResource(Bitmap resource) {
                                            RoundedBitmapDrawable circularBitmapDrawable =
                                                    RoundedBitmapDrawableFactory.create(mDriverImage.getResources(), resource);
                                            circularBitmapDrawable.setCircular(true);
                                            mDriverImage.setImageDrawable(circularBitmapDrawable);
                                        }

                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void finish() {
        clearMusic();
        TripsAdapter.activeFakeCall = false;
        super.finish();
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

    private void clearMusic() {
        if (mp != null) {
            mp.stop();
            mp.reset();
            mp.release();
            mp = null;
        }
    }

    private void playMusic() {
        if (mp != null) {
            clearMusic();
        }
        mp = MediaPlayer.create(this, R.raw.ringphone);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                clearMusic();
            }
        });
        mp.start();
    }

}
