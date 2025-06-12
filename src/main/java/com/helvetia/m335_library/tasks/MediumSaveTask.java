package com.helvetia.m335_library.tasks;

import android.os.AsyncTask;
import android.widget.Toast;

import com.helvetia.m335_library.activities.MediumDetailActivity;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * AsyncTask zum Speichern von Medien (Erstellen oder Bearbeiten)
 * Einfach gehalten für Anfänger
 */
public class MediumSaveTask extends AsyncTask<String, Void, Boolean> {

    private MediumDetailActivity activity;
    private boolean istBearbeiten;
    private Long mediumId;
    private String titel, autor, genre, ean, standort;
    private Integer altersfreigabe;
    private String fehlermeldung = "";

    // Konstruktor - alle Daten mitgeben
    public MediumSaveTask(MediumDetailActivity activity, boolean istBearbeiten, Long mediumId,
                               String titel, String autor, String genre,
                               Integer altersfreigabe, String ean, String standort) {
        this.activity = activity;
        this.istBearbeiten = istBearbeiten;
        this.mediumId = mediumId;
        this.titel = titel;
        this.autor = autor;
        this.genre = genre;
        this.altersfreigabe = altersfreigabe;
        this.ean = ean;
        this.standort = standort;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        HttpURLConnection verbindung = null;
        BufferedWriter writer = null;

        try {
            // URL und Methode festlegen
            String urlString;
            String httpMethode;

            if (istBearbeiten) {
                // PUT für Bearbeiten
                urlString = "http://192.168.1.93:8080/bibliothek/medium/" + mediumId;
                httpMethode = "PUT";
            } else {
                // POST für Erstellen
                urlString = "http://192.168.1.93:8080/bibliothek/medium";
                httpMethode = "POST";
            }

            // Verbindung aufbauen
            URL url = new URL(urlString);
            verbindung = (HttpURLConnection) url.openConnection();
            verbindung.setRequestMethod(httpMethode);
            verbindung.setRequestProperty("Content-Type", "application/json");
            verbindung.setRequestProperty("Accept", "application/json");
            verbindung.setDoOutput(true);

            // JSON-Daten erstellen
            JSONObject jsonDaten = new JSONObject();
            jsonDaten.put("titel", titel);
            jsonDaten.put("autor", autor);

            // Nur setzen wenn nicht leer
            if (genre != null && !genre.isEmpty()) {
                jsonDaten.put("genre", genre);
            }
            if (altersfreigabe != null) {
                jsonDaten.put("altersfreigabe", altersfreigabe);
            }
            if (ean != null && !ean.isEmpty()) {
                jsonDaten.put("ean", ean);
            }
            if (standort != null && !standort.isEmpty()) {
                jsonDaten.put("standort", standort);
            }

            // JSON senden
            writer = new BufferedWriter(
                    new OutputStreamWriter(verbindung.getOutputStream(), StandardCharsets.UTF_8));
            writer.write(jsonDaten.toString());
            writer.flush();

            // Antwort prüfen
            int antwortCode = verbindung.getResponseCode();
            if (antwortCode >= 200 && antwortCode < 300) {
                return true; // Erfolg
            } else {
                fehlermeldung = "Server-Fehler: " + antwortCode;
                return false;
            }

        } catch (Exception e) {
            fehlermeldung = "Fehler: " + e.getMessage();
            return false;
        } finally {
            // Verbindungen schließen
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception ignored) {}
            }
            if (verbindung != null) {
                verbindung.disconnect();
            }
        }
    }

    @Override
    protected void onPostExecute(Boolean erfolgreich) {
        if (erfolgreich) {
            // Erfolg
            String nachricht = istBearbeiten ? "Medium wurde bearbeitet!" : "Medium wurde erstellt!";
            Toast.makeText(activity, nachricht, Toast.LENGTH_SHORT).show();
            activity.activitySchliessen();
        } else {
            // Fehler
            Toast.makeText(activity, fehlermeldung, Toast.LENGTH_LONG).show();
        }
    }
}