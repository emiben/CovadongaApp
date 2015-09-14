package com.openup.covadonga.covadongaapp;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.openup.covadonga.covadongaapp.util.DBHelper;


public class ListaProveedorActivity extends ActionBarActivity {

    private EditText    etFilter;
    private ListView    lvProv;
    private Button      btnBack;
    private Button      btnOK;
    private TextView    tvEmpresa;
    // Listview Adapter
    private ArrayAdapter<String> adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_proveedor);

        getViewElements();
        loadClients();
        setActions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lista_proveedor, menu);
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
        etFilter = (EditText) findViewById(R.id.editTextFilter);
        lvProv = (ListView) findViewById(R.id.listViewClientes);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnOK = (Button) findViewById(R.id.btnOK);
        tvEmpresa = (TextView) findViewById(R.id.textViewEmp);
    }

    public void loadClients(){
        DBHelper db = null;
        String qry = "Select name from c_bpartner";

        try {
            db = new DBHelper(getApplicationContext());
            db.openDB(0);
            Cursor rs = db.querySQL(qry, null);
            int tam = rs.getCount();
            String[] prov = new String[tam];
            rs.moveToFirst();
            for(int i = 0; i < tam; i++){
                prov[i] = rs.getString(0);
                rs.moveToNext();
            }
            adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, prov);
            lvProv.setAdapter(adaptador);

        }catch (Exception e) {
            e.getMessage();
        } finally {
            db.close();
        }

    }

    public void setActions(){
        lvProv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                // TODO Auto-generated method stub
                tvEmpresa.setText(arg0.getItemAtPosition(position).toString());
            }
        });

        etFilter.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                ListaProveedorActivity.this.adaptador.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tvEmpresa.getText().toString() != ""){
                    startListaOrdenesActivity();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Por favor elija un proveedor!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void startListaOrdenesActivity() {
        Intent i = new Intent(this, ListaOrdenesActivity.class);
        Bundle b = new Bundle();
        b.putString("Prov", tvEmpresa.getText().toString());

        i.putExtras(b);
        this.finish();
        startActivity(i);
    }
}
