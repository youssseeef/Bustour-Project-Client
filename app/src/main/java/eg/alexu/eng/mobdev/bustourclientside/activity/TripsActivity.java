package eg.alexu.eng.mobdev.bustourclientside.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eg.alexu.eng.mobdev.bustourclientside.R;
import eg.alexu.eng.mobdev.bustourclientside.adapter.TripsAdapter;
import eg.alexu.eng.mobdev.bustourclientside.utilities.Constants;

/**
 * Created by Paula B. Bassily on 11/09/2016.
 */
public class TripsActivity extends AppCompatActivity {

    private RecyclerView mTripRecyclerView;
    private TripsAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trips_list);
        initializeRecyclerView();
        listenerForTrip();
    }

    private void listenerForTrip() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child(Constants.USERS).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                child(Constants.TRIPS).
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                modifyTrip(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void modifyTrip(DataSnapshot dataSnapshot) {
        HashMap<String, HashMap<String, String>> map = (HashMap<String, HashMap<String,String>>) dataSnapshot.getValue();
        List<String> tripsId = new ArrayList<>();
        for(String s : map.keySet()) {
            tripsId.add(s);
        }
        mAdapter.updateData(tripsId);
    }

    private void initializeRecyclerView() {
        mTripRecyclerView = (RecyclerView) findViewById(R.id.trip_recycler_view);
        mAdapter = new TripsAdapter(this);
        mTripRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mTripRecyclerView.setAdapter(mAdapter);
    }
}
