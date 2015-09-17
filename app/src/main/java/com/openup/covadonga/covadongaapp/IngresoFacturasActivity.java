package com.openup.covadonga.covadongaapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.openup.covadonga.covadongaapp.util.CustomApplication;
import com.openup.covadonga.covadongaapp.util.DBHelper;

import java.util.Calendar;


public class IngresoFacturasActivity extends ActionBarActivity {

    private String      strOrdenes;
    private String[]    ordenes;
    private int         tam;
    private Spinner     spinOrders;
    private EditText    noFact;
    private EditText    fecFact;
    private Button      btnInsert;
    private Button      btnAtras;
    private Button      btnSig;
    private int         day;
    private int         month;
    private int         year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso_facturas);


        getViewItems();
        getOrdersInfo();
        setActions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ingreso_facturas, menu);
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

    public void getViewItems(){
        spinOrders = (Spinner) findViewById(R.id.spinOrders);
        noFact = (EditText) findViewById(R.id.etNoFact);
        fecFact = (EditText) findViewById(R.id.etFecFact);
        btnInsert = (Button) findViewById(R.id.btnInsertFact);
        btnAtras = (Button) findViewById(R.id.btnBackFact);
        btnSig = (Button) findViewById(R.id.btnOkFact);

    }

    public void setActions(){
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertFact();
            }
        });

        fecFact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(1);
            }
        });

        btnSig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProcesarOrdenActivity();
            }
        });
    }

    public void getOrdersInfo() {
        // get the Intent that started this Activity
        Intent in = getIntent();
        // get the Bundle that stores the data of this Activity
        Bundle b = in.getExtras();
        if (null != b) {
            strOrdenes = b.getString("Ordenes");
            ordenes = b.getString("Ordenes").split(";");
            tam = ordenes.length;
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ordenes);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinOrders.setAdapter(spinnerArrayAdapter);
    }

    public void insertFact(){
        if(noFact.getText().toString().equals("") || fecFact.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Por favor ingrese cantidad Nro. Factura y Fecha!",
                    Toast.LENGTH_SHORT).show();
        }else{
            DBHelper db = null;
            String docId = spinOrders.getSelectedItem().toString();
            int ordId;
            String factId = noFact.getText().toString();
            String[] parsFecha = fecFact.getText().toString().split("/");
            String fecha = parsFecha[2] + "-" + parsFecha[1] + "-" + parsFecha[0];

            try{
                db = new DBHelper(CustomApplication.getCustomAppContext());
                db.openDB(1);
                String qryAux = "select c_order_id from c_order where documentno = " + docId;
                Cursor rsAux = db.querySQL(qryAux, null);
                ContentValues cv = new ContentValues();
                rsAux.moveToFirst();
                ordId = rsAux.getInt(0);
                cv.put("factura_id", factId);
                cv.put("c_order_id", ordId);
                cv.put("fecha", fecha);

                if(db.insertSQL("factura", null, cv) == -1){
                    Toast.makeText(getApplicationContext(), "Error al guardar la Factura!!",
                            Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Factura guradada!",
                            Toast.LENGTH_SHORT).show();
                }
                noFact.setText("");
                fecFact.setText("");

            }catch (Exception e) {
                e.getMessage();
            } finally {
                db.close();
            }

        }

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(this, datePickerListener,
                year, month,day);
    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            final Calendar c = Calendar.getInstance();
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // set selected date into textview
            fecFact.setText(day+"/"+(month+1)+"/"+year);
        }
    };

    private void startProcesarOrdenActivity() {
        Intent i = new Intent(this, ProcesarOrdenActivity.class);
        Bundle b = new Bundle();
        b.putString("Ordenes", strOrdenes);
        i.putExtras(b);
        this.finish();
        startActivity(i);
    }

}
