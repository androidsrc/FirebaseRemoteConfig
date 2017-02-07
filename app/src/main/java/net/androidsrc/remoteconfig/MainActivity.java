package net.androidsrc.remoteconfig;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private TextView textViewDataUrl;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;


    private static final String DISPLAY_MESSAGE = "display_message";
    private static final String DATA_URL = "data_url";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        //initialize view objects
        textView = (TextView) findViewById(R.id.textView);
        textViewDataUrl = (TextView) findViewById(R.id.textViewDataUrl);

        //firebase initialization
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        //load defaults from xml if not available from cache
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        fetchRemoteConfig();

    }

    private void fetchRemoteConfig() {
        //display current config data
        displayCurrentData();

        long cacheExpiration = 3600; // 1 hour in seconds.
        // If in developer mode cacheExpiration is set to 0 so each fetch will retrieve values from
        // the server.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        // [START fetch_config_with_callback]
        // cacheExpirationSeconds is set to cacheExpiration here, indicating that any previously
        // fetched and cached config would be considered expired because it would have been fetched
        // more than cacheExpiration seconds ago. Thus the next fetch would go to the server unless
        // throttling is in progress. The default expiration duration is 43200 (12 hours).
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Fetch Succeeded", Toast.LENGTH_SHORT).show();

                            // Once the config is successfully fetched it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            Toast.makeText(MainActivity.this, "Fetch Failed", Toast.LENGTH_SHORT).show();
                        }
                        displayCurrentData();
                    }
                });
    }

    //display active data in text view
    private void displayCurrentData() {
        textViewDataUrl.setText(mFirebaseRemoteConfig.getString(DATA_URL));
        textView.setText(mFirebaseRemoteConfig.getString(DISPLAY_MESSAGE));
    }
}
