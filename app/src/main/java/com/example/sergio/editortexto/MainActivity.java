package com.example.sergio.editortexto;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;


public class MainActivity extends ActionBarActivity {
    private EditText et;
    private String url;
    private AlertDialog alert;
    private Boolean isDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et = (EditText) findViewById(R.id.editText);
        isDialog = false;
        leerArchivo();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isDialog = savedInstanceState.getBoolean("isDialog");
        if(isDialog) {
            guardar(null);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isDialog", isDialog);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(alert != null){
            alert.dismiss();
        }
    }
    public void leerArchivo(){
        Intent intent = getIntent();
        Uri data = intent.getData();
        if(data!=null) {
            URI texto = null;
            try {
                texto = new URI(data.toString());
            } catch (URISyntaxException e) {
                Toast.makeText(this, getString(R.string.errorAlAbrir), Toast.LENGTH_SHORT).show();
            }
            if (texto != null) {
               mostrarTexto(texto);
            }
        }
    }
    public void mostrarTexto(URI uri){
        url = uri.getPath();
        try {
            FileInputStream fs = new FileInputStream(url);
            InputStreamReader archivo = new InputStreamReader(fs, "UTF-8");
            BufferedReader br = new BufferedReader(archivo);
            String line = br.readLine();
            StringBuilder textofinal = new StringBuilder("");
            while (line != null) {
                textofinal.append(line + "\n");
                line = br.readLine();
            }
            br.close();
            archivo.close();
            et.setText(textofinal);

        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.errorAlMostrar), Toast.LENGTH_SHORT).show();
        }
    }
    public void guardar(View v){
        String text = et.getText().toString();
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle(getString(R.string.tituloDialogo));
        dialogo.setMessage(getString(R.string.infoDialogo));
        dialogo.setCancelable(true); // PARA CANCELAR EL DIALOGO!
        dialogo.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo, int id) {
                FileWriter fw;
                PrintWriter pw;
                try {
                    fw = new FileWriter(url);
                    pw = new PrintWriter(fw);
                    pw.println(MainActivity.this.et.getText().toString());
                    pw.close();
                    fw.close();
                    MainActivity.this.isDialog = false;
                    finish();
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, getString(R.string.errorGuardar), Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialogo.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo, int id) {
                MainActivity.this.isDialog = false;
                dialogo.dismiss();
            }
        });
        alert = dialogo.create();
        this.isDialog = true;
        alert.show();
    }
}
