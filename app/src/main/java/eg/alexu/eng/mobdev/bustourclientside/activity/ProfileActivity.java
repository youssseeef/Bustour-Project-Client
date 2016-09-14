package eg.alexu.eng.mobdev.bustourclientside.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;

import eg.alexu.eng.mobdev.bustourclientside.R;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import eg.alexu.eng.mobdev.bustourclientside.model.Model;
import eg.alexu.eng.mobdev.bustourclientside.utilities.Constants;
import eg.alexu.eng.mobdev.bustourclientside.utilities.Extras;

public class ProfileActivity extends AppCompatActivity {

    private RadioGroup mRingingModeRadioGroup;
    private EditText mProfileName;
    private EditText mProfilePhone;
    private ImageView userPhoto;
    private boolean isNewUser;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initializeProfile();
        onClickSaveProfile();
        addListenerToProfileActivity();
    }

    private void addListenerToProfileActivity() {
        if (!isNewUser) {
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
            dbRef.child(Constants.USERS)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            setProfile(dataSnapshot.child(Constants.NAME).getValue(String.class),
                                    dataSnapshot.child(Constants.PHONE).getValue(String.class),
                                    dataSnapshot.child(Constants.RING_MODE).getValue(String.class));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });


        }
    }

    private void initializeProfile() {
        saveButton = (Button) findViewById(R.id.reg_button);
        mProfileName = (EditText) findViewById(R.id.user_name_profile);
        mProfilePhone = (EditText) findViewById(R.id.user_phone_profile);
        mRingingModeRadioGroup = (RadioGroup) findViewById(R.id.ringing_mode_profile);
        userPhoto = (ImageView) findViewById(R.id.user_photo_profile);
        isNewUser = getIntent().getBooleanExtra(Extras.IS_NEW_USER, false);
        Glide.with(userPhoto.getContext())
                .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
                .centerCrop()
                .override(500, 500).
                into(new BitmapImageViewTarget(userPhoto) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(userPhoto.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        userPhoto.setImageDrawable(circularBitmapDrawable);
                    }

                });
        if (!FirebaseAuth.getInstance().getCurrentUser().getDisplayName().equals("") || FirebaseAuth.getInstance().getCurrentUser().getDisplayName() != null) {
            mProfileName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        }
    }

    private void onClickSaveProfile() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean wrongEntry = false;
                if (mProfileName.getText().toString().trim().equals("")) {
                    mProfileName.setError("Required!");
                    wrongEntry = true;
                }

                if (mProfilePhone.getText().toString().trim().length() < 10) {
                    mProfilePhone.setError("Enter a valid phone!");
                    wrongEntry = true;
                }

                if (!wrongEntry) {
                    String url = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() != null ?
                            FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString() : null;
                    Model.getInstance().submitDataFirstTime(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                            mProfileName.getText().toString(),
                            mProfilePhone.getText().toString(),
                            url != null ? url : "error",
                            mRingingModeRadioGroup.getCheckedRadioButtonId() == R.id.call_profile ? "0" : "1",
                            isNewUser);
                    startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                    finish();
                }
            }
        });
    }

    public void setProfile(String fullName, String phone, String ringType) {
        mProfileName.setText(fullName);
        mProfilePhone.setText(phone);
        if (ringType != null && ringType.equals("1")) {
            mRingingModeRadioGroup.check(R.id.notification_profile);
        } else {
            mRingingModeRadioGroup.check(R.id.call_profile);
        }
    }


}
