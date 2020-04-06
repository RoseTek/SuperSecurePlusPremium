package com.example.supersecurepluspremium;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class Analyze extends AppCompatActivity {
    private static boolean first = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);
        Button clickButton = findViewById(R.id.button);
        clickButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED, returnIntent);
                //prise de screenshot
                onBackPressed();
            }
        });


        ImageView imageView = findViewById(R.id.imageView);
        //remplacer icon par l'illustration de notre choix...
        Glide.with(this).load(R.drawable.before_scan).into(imageView);
        TextView textView = findViewById(R.id.textView);

        //comportement custom si analyse déjà lancée au moins une fois
        if (!first) {
            textView.setText("Aucun problème de sécurité détecté\nAppuyez sur le bouton pour relancer l'analyse");
            Glide.with(this).load(R.drawable.after_scan).into(imageView);
        }
        //premier lancement de l'analyse
        else
            textView.setText("Appuyez sur le bouton pour procéder à l'analyse");

        first = false;
    }
}
