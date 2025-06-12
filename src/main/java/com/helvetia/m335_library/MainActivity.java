package com.helvetia.m335_library;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

import com.helvetia.m335_library.activities.MediumDetailActivity;
import com.helvetia.m335_library.tasks.MediumTask;

/**
 * Hauptbildschirm der Bibliotheks-App
 * Zeigt eine Liste aller Medien an
 */
public class MainActivity extends AppCompatActivity {

    // UI-Elemente (wie im Leitfaden erklärt)
    private TableLayout tabelle;
    private EditText suchfeld;
    private Button suchenButton;
    private Button refreshButton;
    private Button neuButton;
    private Button menuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Alle UI-Elemente finden (findViewById wie im Leitfaden)
        tabelle = findViewById(R.id.tblMedien);
        suchfeld = findViewById(R.id.etSuche);
        suchenButton = findViewById(R.id.btnSuchen);
        refreshButton = findViewById(R.id.btnRefresh);
        neuButton = findViewById(R.id.btnNeu);
        menuButton = findViewById(R.id.btnMenu);

        // Buttons mit Click-Listenern verbinden
        setupButtons();

        // Beim Start alle Medien laden
        alleMedienLaden();
    }

    /**
     * Alle Button-Listener einrichten (wie im AlphaBravoCharlie-Beispiel)
     */
    private void setupButtons() {

        // Refresh-Button: Daten neu laden
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alleMedienLaden();
                Toast.makeText(MainActivity.this, "Lade Daten...", Toast.LENGTH_SHORT).show();
            }
        });

        // Such-Button: Nach Titel suchen
        suchenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String suchtext = suchfeld.getText().toString();

                if (suchtext.isEmpty()) {
                    // Wenn leer, alle anzeigen
                    alleMedienLaden();
                } else {
                    // Sonst suchen
                    medienSuchen(suchtext);
                    Toast.makeText(MainActivity.this, "Suche: " + suchtext, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Neu-Button: Neues Medium erstellen
        neuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Neue Activity öffnen (Intent wie im Leitfaden)
                Intent intent = new Intent(MainActivity.this, MediumDetailActivity.class);
                startActivity(intent);
            }
        });

        // Menü-Button (noch nicht implementiert)
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Menü noch nicht fertig", Toast.LENGTH_SHORT).show();
            }
        });

        // Enter-Taste im Suchfeld (wie im AlphaBravoCharlie-Beispiel)
        suchfeld.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, android.view.KeyEvent event) {
                if (event.getAction() == android.view.KeyEvent.ACTION_DOWN &&
                        keyCode == android.view.KeyEvent.KEYCODE_ENTER) {
                    suchenButton.performClick();
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Alle Medien vom Server laden
     */
    private void alleMedienLaden() {
        // AsyncTask starten (wie im Pendenzen-Beispiel)
        new MediumTask(this, tabelle).execute();
    }

    /**
     * Medien nach Titel suchen
     */
    private void medienSuchen(String suchtext) {
        // AsyncTask mit Suchtext starten
        new MediumTask(this, tabelle, suchtext).execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Wenn wir zurückkommen, Daten aktualisieren
        alleMedienLaden();
    }
}