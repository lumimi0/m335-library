package com.helvetia.m335_library.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.helvetia.m335_library.R;
import com.helvetia.m335_library.tasks.MediumDeleteTask;
import com.helvetia.m335_library.tasks.MediumSaveTask;

/**
 * Bildschirm zum Erstellen oder Bearbeiten von Medien
 * Einfach gehalten für Anfänger
 */
public class MediumDetailActivity extends AppCompatActivity {

    // UI-Elemente
    private TextView titelText;
    private EditText titelFeld, autorFeld, genreFeld, altersfreigabeFeld, eanFeld, standortFeld;
    private Button speichernButton, abbrechenButton, loeschenButton;

    // Daten
    private Long mediumId = null;
    private boolean istBearbeitenModus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medium_detail);

        // UI-Elemente finden
        findeAlleElemente();

        // Daten aus Intent laden
        ladeDatenAusIntent();

        // Buttons einrichten
        setupButtons();
    }

    /**
     * Alle UI-Elemente mit findViewById finden
     */
    private void findeAlleElemente() {
        titelText = findViewById(R.id.tvTitel);
        titelFeld = findViewById(R.id.etTitel);
        autorFeld = findViewById(R.id.etAutor);
        genreFeld = findViewById(R.id.etGenre);
        altersfreigabeFeld = findViewById(R.id.etAltersfreigabe);
        eanFeld = findViewById(R.id.etEan);
        standortFeld = findViewById(R.id.etStandort);
        speichernButton = findViewById(R.id.btnSpeichern);
        abbrechenButton = findViewById(R.id.btnAbbrechen);
        loeschenButton = findViewById(R.id.btnLoeschen);
    }

    /**
     * Daten aus dem Intent lesen (wie im Leitfaden gezeigt)
     */
    private void ladeDatenAusIntent() {
        Intent intent = getIntent();

        // Prüfen ob Medium-ID vorhanden (dann Bearbeiten, sonst Neu)
        if (intent.hasExtra("medium_id")) {
            // Bearbeiten-Modus
            istBearbeitenModus = true;
            mediumId = intent.getLongExtra("medium_id", 0);
            titelText.setText("Medium bearbeiten");
            loeschenButton.setVisibility(View.VISIBLE);

            // Felder mit Daten füllen
            titelFeld.setText(intent.getStringExtra("medium_titel"));
            autorFeld.setText(intent.getStringExtra("medium_autor"));
            genreFeld.setText(intent.getStringExtra("medium_genre"));

            int altersfreigabe = intent.getIntExtra("medium_altersfreigabe", -1);
            if (altersfreigabe >= 0) {
                altersfreigabeFeld.setText(String.valueOf(altersfreigabe));
            }

            eanFeld.setText(intent.getStringExtra("medium_ean"));
            standortFeld.setText(intent.getStringExtra("medium_standort"));

        } else {
            // Neu erstellen-Modus
            istBearbeitenModus = false;
            titelText.setText("Neues Medium erstellen");
            loeschenButton.setVisibility(View.GONE);
        }
    }

    /**
     * Button Click-Listener einrichten
     */
    private void setupButtons() {

        // Speichern-Button
        speichernButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediumSpeichern();
            }
        });

        // Abbrechen-Button
        abbrechenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Activity schließen
            }
        });

        // Löschen-Button
        loeschenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediumLoeschen();
            }
        });
    }

    /**
     * Medium speichern (erstellen oder bearbeiten)
     */
    private void mediumSpeichern() {
        // Eingaben lesen
        String titel = titelFeld.getText().toString().trim();
        String autor = autorFeld.getText().toString().trim();

        // Pflichtfelder prüfen
        if (titel.isEmpty()) {
            Toast.makeText(this, "Titel muss ausgefüllt werden!", Toast.LENGTH_SHORT).show();
            titelFeld.requestFocus();
            return;
        }

        if (autor.isEmpty()) {
            Toast.makeText(this, "Autor muss ausgefüllt werden!", Toast.LENGTH_SHORT).show();
            autorFeld.requestFocus();
            return;
        }

        // Optionale Felder lesen
        String genre = genreFeld.getText().toString().trim();
        String altersfreigabeText = altersfreigabeFeld.getText().toString().trim();
        String ean = eanFeld.getText().toString().trim();
        String standort = standortFeld.getText().toString().trim();

        // Altersfreigabe prüfen (muss Zahl sein)
        Integer altersfreigabe = null;
        if (!altersfreigabeText.isEmpty()) {
            try {
                altersfreigabe = Integer.parseInt(altersfreigabeText);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Altersfreigabe muss eine Zahl sein!", Toast.LENGTH_SHORT).show();
                altersfreigabeFeld.requestFocus();
                return;
            }
        }

        // AsyncTask starten
        if (istBearbeitenModus) {
            // Medium bearbeiten (PUT)
            new MediumSaveTask(this, true, mediumId, titel, autor, genre, altersfreigabe, ean, standort).execute();
        } else {
            // Neues Medium erstellen (POST)
            new MediumSaveTask(this, false, null, titel, autor, genre, altersfreigabe, ean, standort).execute();
        }
    }

    /**
     * Medium löschen
     */
    private void mediumLoeschen() {
        if (mediumId != null) {
            // Bestätigung fragen
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Medium löschen")
                    .setMessage("Soll dieses Medium wirklich gelöscht werden?")
                    .setPositiveButton("Ja, löschen", new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(android.content.DialogInterface dialog, int which) {
                            new MediumDeleteTask(MediumDetailActivity.this, mediumId).execute();
                        }
                    })
                    .setNegativeButton("Abbrechen", null)
                    .show();
        }
    }

    /**
     * Activity schließen (wird von AsyncTasks aufgerufen)
     */
    public void activitySchliessen() {
        finish();
    }
}