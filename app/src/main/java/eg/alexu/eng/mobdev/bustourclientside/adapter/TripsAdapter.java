package eg.alexu.eng.mobdev.bustourclientside.adapter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import eg.alexu.eng.mobdev.bustourclientside.R;
import eg.alexu.eng.mobdev.bustourclientside.activity.FakeCallActivity;
import eg.alexu.eng.mobdev.bustourclientside.activity.TripInfoActivity;
import eg.alexu.eng.mobdev.bustourclientside.activity.ChooseLocationActivity;
import eg.alexu.eng.mobdev.bustourclientside.activity.TrackASpecificTrip;
import eg.alexu.eng.mobdev.bustourclientside.utilities.Constants;
import eg.alexu.eng.mobdev.bustourclientside.utilities.Extras;

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.ViewHolder> {

    private List<String> mTripsId;
    private Context mContext;
    private ValueEventListener arrivedValueListener;

    public TripsAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String tripId = mTripsId.get(position);
        initializeView(holder, tripId);
    }

    private void checkOnOff(final ViewHolder holder, final String tripId, final String driverId) {
        arrivedValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                boolean arrived = Boolean.parseBoolean(dataSnapshot.getValue(String.class));
                if (arrived) {
                    dbRef.child(Constants.USERS).
                            child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                            child(Constants.RING_MODE).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String ringType = dataSnapshot.getValue(String.class);
                            if (ringType.equals("0")) {
                                Intent intent = new Intent(mContext, FakeCallActivity.class);
                                intent.putExtra(Extras.DRIVER_ID, driverId);
                                intent.putExtra(Extras.TRIP_ID, tripId);
                                if(!FakeCallActivity.active)
                                    mContext.startActivity(intent);
                            }else {
                                notifyUser();
                            }
                            dbRef.child(Constants.USERS).
                                    child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                                    child(Constants.TRIPS).
                                    child(tripId).
                                    child(Constants.ENABLED).
                                    setValue("false");
                            holder.mOnOff.setSelected(false);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        Log.d("lolo", holder.mOnOff.isSelected()+"");
        if(holder.mOnOff.isEnabled() && holder.mOnOff.isSelected()){
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
            dbRef.child(Constants.USERS).
                    child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                    child(Constants.TRIPS).
                    child(tripId).
                    child(Constants.ARRIVED).addValueEventListener(arrivedValueListener);
        }

    }

    @Override
    public int getItemCount() {
        return mTripsId != null ? mTripsId.size() : 0;
    }

    public void updateData(List<String> tripsId) {
        mTripsId = tripsId;
        notifyDataSetChanged();
    }

    private void initializeView(final ViewHolder holder, final String tripId) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child(Constants.USERS).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                child(Constants.TRIPS).
                child(tripId).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String locX = dataSnapshot.child(Constants.LOC_X).getValue(String.class);
                        String locY = dataSnapshot.child(Constants.LOC_Y).getValue(String.class);
                        String driverId = dataSnapshot.child(Constants.DRIVER_ID).getValue(String.class);
                        boolean enableTrip = Boolean.parseBoolean(dataSnapshot.child(Constants.ENABLED).getValue(String.class));
                        if (!locX.equals(Constants.NO_VALUE) && !locY.equals(Constants.NO_VALUE)) {
                            setTripEnabled(holder, enableTrip);
                        } else if (locX.equals(Constants.NO_VALUE) || locY.equals(Constants.NO_VALUE)) {
                            setTripDisabled(holder);
                        }
                        setTripName(holder, tripId, driverId);
                        setTripDescription(holder, tripId, driverId);
                        checkOnOff(holder, tripId, driverId);
                        addOnClickListenerToOnOff(holder, tripId);
                        addListenerForSetLocation(holder, tripId);
                        addOnClickListenerForInfo(holder, tripId, driverId);
                        addOnClickListenerForTracking(holder, tripId, driverId, locX, locY);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void addOnClickListenerForTracking(final ViewHolder holder, final String tripId, final String driverId, final String latitude, final String longitude) {
        holder.mTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.mTrack.isEnabled()) {
                    Intent intent = new Intent(v.getContext(), TrackASpecificTrip.class);
                    intent.putExtra(Extras.TRIP_ID, tripId);
                    intent.putExtra(Extras.DRIVER_ID, driverId);
                    intent.putExtra(Extras.LATITUDE, latitude);
                    intent.putExtra(Extras.LONGITUDE, longitude);
                    v.getContext().startActivity(intent);
                }
            }
        });
    }

    private void addOnClickListenerForInfo(final ViewHolder holder, final String tripId, final String driverId) {
        holder.mInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), TripInfoActivity.class);
                intent.putExtra(Extras.TRIP_ID, tripId);
                intent.putExtra(Extras.DRIVER_ID, driverId);
                v.getContext().startActivity(intent);
            }
        });
    }

    private void addOnClickListenerToOnOff(final ViewHolder holder, final String tripId) {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        holder.mOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.mOnOff.isSelected() && holder.mOnOff.isEnabled()) {
                    holder.mOnOff.setSelected(false);
                    dbRef.child(Constants.USERS).
                            child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                            child(Constants.TRIPS).
                            child(tripId).
                            child(Constants.ENABLED).
                            setValue("false");
                    dbRef.child(Constants.USERS).
                            child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                            child(Constants.TRIPS).
                            child(tripId).
                            child(Constants.ARRIVED).removeEventListener(arrivedValueListener);
                    Toast.makeText(v.getContext(), R.string.trip_disabled, Toast.LENGTH_LONG).show();
                } else if (!holder.mOnOff.isSelected() && holder.mOnOff.isEnabled()) {
                    holder.mOnOff.setSelected(true);
                    dbRef.child(Constants.USERS).
                            child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                            child(Constants.TRIPS).
                            child(tripId).
                            child(Constants.ENABLED).
                            setValue("true");
                    dbRef.child(Constants.USERS).
                            child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                            child(Constants.TRIPS).
                            child(tripId).
                            child(Constants.ARRIVED).addValueEventListener(arrivedValueListener);
                    Toast.makeText(v.getContext(), R.string.trip_enabled, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void addListenerForSetLocation(final ViewHolder holder, final String tripId) {
        holder.mSetLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ChooseLocationActivity.class);
                intent.putExtra(Extras.TRIP_ID, tripId);
                v.getContext().startActivity(intent);
            }
        });
    }

    private void setTripDisabled(ViewHolder holder) {
        holder.mTrack.setEnabled(false);
        holder.mOnOff.setEnabled(false);
        holder.mSetLoc.setSelected(false);
    }

    private void setTripEnabled(ViewHolder holder, boolean enableTrip) {
        holder.mSetLoc.setSelected(true);
        holder.mTrack.setEnabled(true);
        holder.mOnOff.setEnabled(true);
        if (enableTrip)
            holder.mOnOff.setSelected(true);
        else if (!enableTrip)
            holder.mOnOff.setSelected(false);
    }

    private void setTripDescription(final ViewHolder holder, String tripId, String driverId) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child(Constants.DRIVERS).
                child(driverId).
                child(Constants.TRIPS).
                child(tripId).
                child(Constants.DESCRIPTION).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        holder.mTripDescription.setText(dataSnapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setTripName(final ViewHolder holder, String tripId, String driverId) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child(Constants.DRIVERS).
                child(driverId).
                child(Constants.TRIPS).
                child(tripId).
                child(Constants.NAME).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        holder.mTripName.setText(dataSnapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void notifyUser() {

        Intent intent = new Intent(mContext, TripsAdapter.class);
        PendingIntent pIntent = PendingIntent.getActivity(mContext, (int) System.currentTimeMillis(), intent, 0);

        Notification n  = new Notification.Builder(mContext)
                .setContentTitle("Bus Tour")
                .setContentText("Driver is near")
                .setSmallIcon(R.drawable.bus_logo)
                .setContentIntent(pIntent)
                .setAutoCancel(true).build();

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
        notificationManager.notify(0, n);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTripName;
        private TextView mTripDescription;
        private ImageView mOnOff;
        private ImageView mSetLoc;
        private ImageView mTrack;
        private ImageView mInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            mTripName = (TextView) itemView.findViewById(R.id.trip_name);
            mTripDescription = (TextView) itemView.findViewById(R.id.trip_description);
            mOnOff = (ImageView) itemView.findViewById(R.id.on_off);
            mInfo = (ImageView) itemView.findViewById(R.id.info);
            mTrack = (ImageView) itemView.findViewById(R.id.loc);
            mSetLoc = (ImageView) itemView.findViewById(R.id.set_loc);
        }
    }
}
