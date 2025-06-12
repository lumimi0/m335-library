package com.helvetia.m335_library.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.helvetia.m335_library.activities.MediumDetailActivity;
import com.helvetia.m335_library.models.Medium;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * AsyncTask für das Laden von Medien vom Server
 * Orientiert sich am PendenzenTask aus dem Leitfaden
 */
public class MediumTask extends AsyncTask<String, Integer, String> {

    private Context context;
    private TableLayout tabelle;
    private String suchtext;

    // Konstruktor für alle Medien laden
    public MediumTask(Context context, TableLayout tabelle) {
        this.context = context;
        this.tabelle = tabelle;
        this.suchtext = null;
    }

    // Konstruktor für Medien suchen
    public MediumTask(Context context, TableLayout tabelle, String suchtext) {
        this.context = context;
        this.tabelle = tabelle;
        this.suchtext = suchtext;
    }

    @Override
    protected String doInBackground(String... params) {
        StringBuilder response = new StringBuilder();
        String line;

        // Verbindung erstellen (wie im Pendenzen-Beispiel)
        HttpURLConnection con = null;
        BufferedReader br = null;

        try {
            // URL zusammenbauen
            String urlString = "http://192.168.1.93:8080/bibliothek/medium";

            // Wenn Suchtext vorhanden, an URL anhängen
            if (suchtext != null && !suchtext.isEmpty()) {
                urlString = urlString + "?titel=" + suchtext;
            }

            URL url = new URL(urlString);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");

            // Antwort lesen
            br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));

            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }

        } catch (Exception e) {
            // Bei Fehler eine Fehler-JSON zurückgeben
            response = new StringBuilder("[{\"id\":0,\"titel\":\"Fehler\",\"autor\":\"");
            response.append(e.getMessage()).append("\"}]");
        } finally {
            // Verbindung schließen
            if (br != null) {
                try {
                    br.close();
                } catch (Exception ignored) {}
            }
            if (con != null) {
                con.disconnect();
            }
        }

        return response.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        // Tabelle leeren (außer Header-Zeile)
        int anzahlZeilen = tabelle.getChildCount();
        if (anzahlZeilen > 1) {
            tabelle.removeViews(1, anzahlZeilen - 1);
        }

        // JSON zu Medium-Liste umwandeln
        List<Medium> medienListe = Medium.parseJsonArray(result);

        // Jedes Medium als Tabellenzeile hinzufügen
        for (int i = 0; i < medienListe.size(); i++) {
            Medium medium = medienListe.get(i);

            // Neue Tabellenzeile erstellen
            TableRow zeile = new TableRow(context);

            // ID-Spalte
            TextView idText = new TextView(context);
            idText.setText(String.valueOf(medium.getId()));
            idText.setPadding(8, 8, 8, 8);
            zeile.addView(idText);

            // Titel-Spalte
            TextView titelText = new TextView(context);
            titelText.setText(medium.getTitel());
            titelText.setPadding(8, 8, 8, 8);
            zeile.addView(titelText);

            // Autor-Spalte
            TextView autorText = new TextView(context);
            autorText.setText(medium.getAutor());
            autorText.setPadding(8, 8, 8, 8);
            zeile.addView(autorText);

            // Genre-Spalte
            TextView genreText = new TextView(context);
            String genre = medium.getGenre();
            if (genre == null || genre.equals("null")) {
                genre = "";
            }
            genreText.setText(genre);
            genreText.setPadding(8, 8, 8, 8);
            zeile.addView(genreText);

            // Zeile anklickbar machen (für Bearbeiten)
            final Medium dasJetzigeGMedium = medium;
            zeile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Bearbeiten-Activity öffnen mit Daten
                    Intent intent = new Intent(context, MediumDetailActivity.class);

                    // Medium-Daten an Intent anhängen
                    intent.putExtra("medium_id", dasJetzigeGMedium.getId());
                    intent.putExtra("medium_titel", dasJetzigeGMedium.getTitel());
                    intent.putExtra("medium_autor", dasJetzigeGMedium.getAutor());
                    intent.putExtra("medium_genre", dasJetzigeGMedium.getGenre());

                    if (dasJetzigeGMedium.getAltersfreigabe() != null) {
                        intent.putExtra("medium_altersfreigabe", dasJetzigeGMedium.getAltersfreigabe());
                    }

                    intent.putExtra("medium_ean", dasJetzigeGMedium.getEan());
                    intent.putExtra("medium_standort", dasJetzigeGMedium.getStandort());

                    context.startActivity(intent);
                }
            });

            // Zeile hervorheben wenn man draufklickt
            zeile.setBackgroundResource(android.R.drawable.list_selector_background);

            // Zeile zur Tabelle hinzufügen
            tabelle.addView(zeile);
        }
    }
}