package eg.alexu.eng.mobdev.bustourclientside.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import eg.alexu.eng.mobdev.bustourclientside.R;

public class HomeActivity extends AppCompatActivity  {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);
    }

    public void onClickTrips(View v){
        Intent intent= new Intent(HomeActivity.this,TripsActivity.class);
        startActivity(intent);
    }

    public void onClickProfile(View v){
        Intent intent= new Intent(HomeActivity.this,ProfileActivity.class);
        startActivity(intent);
    }
}
