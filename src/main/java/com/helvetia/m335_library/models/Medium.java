package com.helvetia.m335_library.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Einfache Klasse für ein Medium (Buch, DVD, etc.)
 * Orientiert sich am Pendenzen-Beispiel aus dem Leitfaden
 */
public class Medium {

    // Einfache Variablen für die Medium-Daten
    private Long id;
    private String titel;
    private String autor;
    private String genre;
    private Integer altersfreigabe;
    private String ean;
    private String standort;

    /**
     * Konstruktor - erstellt Medium aus JSON-Objekt
     * (wie im Leitfaden gezeigt)
     */
    public Medium(JSONObject object) {
        try {
            // ID lesen
            if (object.has("id")) {
                this.id = object.getLong("id");
            }

            // Titel lesen
            if (object.has("titel")) {
                this.titel = object.getString("titel");
            }

            // Autor lesen
            if (object.has("autor")) {
                this.autor = object.getString("autor");
            }

            // Genre lesen (kann leer sein)
            if (object.has("genre")) {
                this.genre = object.getString("genre");
            }

            // Altersfreigabe lesen (kann leer sein)
            if (object.has("altersfreigabe")) {
                this.altersfreigabe = object.getInt("altersfreigabe");
            }

            // EAN lesen (kann leer sein)
            if (object.has("ean")) {
                this.ean = object.getString("ean");
            }

            // Standort lesen (kann leer sein)
            if (object.has("standort")) {
                this.standort = object.getString("standort");
            }

        } catch (JSONException e) {
            // Bei Fehler einfach ignorieren
        }
    }

    /**
     * JSON Array zu Liste umwandeln (wie im Pendenzen-Beispiel)
     */
    public static List<Medium> parseJsonArray(String jsonString) {
        List<Medium> liste = new ArrayList<>();

        if (jsonString != null) {
            try {
                JSONArray array = new JSONArray(jsonString);

                // Jedes Element durchgehen
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    Medium medium = new Medium(obj);
                    liste.add(medium);
                }

            } catch (JSONException e) {
                // Bei Fehler leere Liste zurückgeben
            }
        }

        return liste;
    }

    // Einfache Getter-Methoden
    public Long getId() {
        return id;
    }

    public String getTitel() {
        return titel;
    }

    public String getAutor() {
        return autor;
    }

    public String getGenre() {
        return genre;
    }

    public Integer getAltersfreigabe() {
        return altersfreigabe;
    }

    public String getEan() {
        return ean;
    }

    public String getStandort() {
        return standort;
    }
}