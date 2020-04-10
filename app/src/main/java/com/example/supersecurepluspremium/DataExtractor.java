package com.example.supersecurepluspremium;

import android.util.Log;

public class DataExtractor {
    //Classe effectuant les tâches en fond d'extraction applicative de données
    //Prototype général des méthodes :
    //void extractData(boolean sendToVPS)
    //sendToVPS = true implique l'utilisation de snitch pour envoyer les données

    public DataExtractor(){
        Log.v("secme","Data extractor created");
    }

    public void extractClipboard(boolean b) {
        Log.v("secme","Data extractor test background task");
        for (int i=0;i<30;i++){
            Log.v("secme","Background extracting task");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
