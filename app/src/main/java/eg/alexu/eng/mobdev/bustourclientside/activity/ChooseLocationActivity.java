package eg.alexu.eng.mobdev.bustourclientside.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import eg.alexu.eng.mobdev.bustourclientside.utilities.Constants;
import eg.alexu.eng.mobdev.bustourclientside.utilities.Extras;


public class ChooseLocationActivity extends AppCompatActivity {

    private static final int PLACE_PICKER_REQUEST = 1;
    private String tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tripId = getIntent().getStringExtra(Extras.TRIP_ID);
        PlacePicker.IntentBuilder placePickerBuilder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(placePickerBuilder.build(ChooseLocationActivity.this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                LatLng loc = place.getLatLng();
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                dbRef.child(Constants.USERS).
                        child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                        child(Constants.TRIPS).
                        child(tripId).child(Constants.LOC_X).setValue(""+loc.latitude);
                dbRef.child(Constants.USERS).
                        child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                        child(Constants.TRIPS).
                        child(tripId).child(Constants.LOC_Y).setValue(""+loc.longitude);
                finish();
            }
        }
        finish();
    }
}
