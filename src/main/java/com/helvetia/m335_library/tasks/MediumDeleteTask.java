package com.helvetia.m335_library.tasks;

import android.os.AsyncTask;
import android.widget.Toast;

import com.helvetia.m335_library.activities.MediumDetailActivity;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * AsyncTask zum Löschen von Medien
 * Einfach gehalten für Anfänger
 */
public class MediumDeleteTask extends AsyncTask<String, Void, Boolean> {

    private MediumDetailActivity activity;
    private Long mediumId;
    private String fehlermeldung = "";

    // Konstruktor
    public MediumDeleteTask(MediumDetailActivity activity, Long mediumId) {
        this.activity = activity;
        this.mediumId = mediumId;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        HttpURLConnection verbindung = null;

        try {
            // URL für DELETE-Request
            String urlString = "http://192.168.1.93:8080/bibliothek/medium/" + mediumId;

            // Verbindung aufbauen
            URL url = new URL(urlString);
            verbindung = (HttpURLConnection) url.openConnection();
            verbindung.setRequestMethod("DELETE");
            verbindung.setRequestProperty("Accept", "application/json");

            // Antwort prüfen
            int antwortCode = verbindung.getResponseCode();
            if (antwortCode >= 200 && antwortCode < 300) {
                return true; // Erfolg
            } else {
                fehlermeldung = "Server-Fehler: " + antwortCode;
                return false;
            }

        } catch (Exception e) {
            fehlermeldung = "Fehler beim Löschen: " + e.getMessage();
            return false;
        } finally {
            // Verbindung schließen
            if (verbindung != null) {
                verbindung.disconnect();
            }
        }
    }

    @Override
    protected void onPostExecute(Boolean erfolgreich) {
        if (erfolgreich) {
            // Erfolg
            Toast.makeText(activity, "Medium wurde gelöscht!", Toast.LENGTH_SHORT).show();
            activity.activitySchliessen();
        } else {
            // Fehler
            Toast.makeText(activity, fehlermeldung, Toast.LENGTH_LONG).show();
        }
    }
}