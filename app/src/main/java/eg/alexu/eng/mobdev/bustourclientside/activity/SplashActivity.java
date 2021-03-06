package eg.alexu.eng.mobdev.bustourclientside.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import eg.alexu.eng.mobdev.bustourclientside.R;
import eg.alexu.eng.mobdev.bustourclientside.model.Model;
import eg.alexu.eng.mobdev.bustourclientside.utilities.Extras;


public class SplashActivity extends AppCompatActivity {
    private final long SPLASH_DISPLAY_LENGTH = 4000;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        imageView = (ImageView) findViewById(R.id.image_splash);
        Model.getInstance().isANewUser(FirebaseAuth.getInstance().getCurrentUser().getUid());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkIsANewUser();
            }
        }, SPLASH_DISPLAY_LENGTH);
        TextView tx = (TextView) findViewById(R.id.text_view_title);

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/main_font.ttf");


        assert tx != null;
        tx.setTypeface(custom_font);
        assert getSupportActionBar() != null;
        getSupportActionBar().hide();


    }

    private void checkIsANewUser() {
        boolean newUser = Model.getInstance().isNewUser();
        if (newUser) {
            Intent intent = new Intent(SplashActivity.this, ProfileActivity.class);
            intent.putExtra(Extras.IS_NEW_USER, true);
            startActivity(intent);
            finish();
        } else {
            startTrans();
            finish();
        }
    }

    private void startTrans() {
        Intent intent = new Intent(this, HomeActivity.class);
        if (Build.VERSION.SDK_INT >= 21) {

            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this, imageView, "driver_icon");
            if (options != null)
                startActivity(intent, options.toBundle());
            else
                startActivity(intent);
        } else {
            startActivity(intent);
        }
        finish();
    }


}
