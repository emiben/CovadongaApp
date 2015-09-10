package com.openup.covadonga.covadongaapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


public class ConfirmarCantidadesActivity extends ActionBarActivity {

    private EditText    facturado;
    private EditText    recibido;
    private Spinner     facturas;
    private Button      cancell;
    private Button      ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_cantidades);

        getViewElements();
        loadInvoices();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_confirmar_cantidades, menu);
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

    public void getViewElements(){
        facturado = (EditText) findViewById(R.id.etInvoiced);
        recibido = (EditText) findViewById(R.id.etReceived);
        facturas = (Spinner) findViewById(R.id.spinInvoices);
        cancell = (Button) findViewById(R.id.btnCancel);
        ok = (Button) findViewById(R.id.btnOK);
    }

    public void setActions(){

    }

    public void loadInvoices(){
        String spinnerArray[] = {"EJ16516","ZX19681","HU619819","JM54615","YU61891"};
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        facturas.setAdapter(spinnerArrayAdapter);
    }

}
