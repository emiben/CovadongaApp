package com.openup.covadonga.covadongaapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.openup.covadonga.covadongaapp.util.DBHelper;
import com.openup.covadonga.covadongaapp.util.SincronizeData;
import com.openup.covadonga.covadongaapp.util.WebServices;

import org.ksoap2.serialization.SoapObject;


public class MenuActivity extends ActionBarActivity {

    private Button          btnProcessPO;
    private Button          btnSincProv;
    private Button          btnSincUPC;
    private Button          btnSincOrders;
    private ProgressDialog  pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        getViewElements();
        setActions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
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
        btnProcessPO = (Button) findViewById(R.id.btnProcessPO);
        btnSincProv = (Button) findViewById(R.id.btnSincProv);
        btnSincUPC = (Button) findViewById(R.id.btnSincUPC);
        btnSincOrders = (Button) findViewById(R.id.btnSincOrders);
    }

    public void setActions(){
        btnProcessPO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListaClienteActivity();
            }
        });

        btnSincProv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sincronizarWS();
            }
        });

        btnSincUPC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sincronizarUPC();
            }
        });

        btnSincOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sincronizarOrders();
            }
        });

    }

    private void startListaClienteActivity() {
        Intent i = new Intent(this, ListaProveedorActivity.class);
        startActivity(i);
    }

    public void sincronizarWS() {
        pDialog = ProgressDialog.show(this, null, "Consultando datos...", true);
        new Thread() {
            public void run() {
                try {
                    sincronizar();
                } catch (Exception e) {
                    e.getMessage();
                }
                pDialog.dismiss();
            }
        }.start();
    }

    public void sincronizarUPC(){
        pDialog = ProgressDialog.show(this, null, "Enviando datos...", true);
        final SincronizeData sd = new SincronizeData();
        new Thread() {
            public void run() {
                try {
                    sd.sendUPC();
                } catch (Exception e) {
                    e.getMessage();
                }
                pDialog.dismiss();
            }
        }.start();
    }

    private void sincronizar(){

        WebServices ws = new WebServices();
        String[] columYVal = new String[4];
        SoapObject resultado_xml = null;
        int i = 0;
        columYVal[i++] = "IsVendor"; //colum
        columYVal[i++] = "Y"; //val

        columYVal[i++] = "IsActive"; //colum
        columYVal[i++] = "Y"; //val

        resultado_xml = ws.webServiceQry("QueryCBPartner", "C_BPartner", columYVal);
        insertVendors(resultado_xml);

    }

    private void insertVendors(SoapObject so){

        SoapObject dataResult = (SoapObject)so.getProperty(0);

        int tam = dataResult.getPropertyCount();
        String delims = "[=;]";

        DBHelper db = new DBHelper(this);
        db.openDB(1);
        db.executeSQL("DELETE FROM c_bpartner");

        try{
            if(tam > 0) {
                for (int i = 0; i < tam; i++) {
                    SoapObject dataRow = (SoapObject) dataResult.getProperty(i);
                    String col1[] = dataRow.getProperty(0).toString().split(delims); //C_BPartner_ID--
                    String col2[] = dataRow.getProperty(1).toString().split(delims); //Created--
                    String col3[] = dataRow.getProperty(2).toString().split(delims); //Name--
                    String col4[] = dataRow.getProperty(3).toString().split(delims); //Name2--
                    String col5[] = dataRow.getProperty(4).toString().split(delims); //Updated--

                    String qry = "Insert into c_bpartner values (";
                    qry = qry + col1[1] + ",'" + col2[1] + "','" + col5[1] + "','";
                    qry = qry + col3[1] + "','" + col4[1] + "'" +")";

                    db.executeSQL(qry);
                }
            }
        } catch (Exception e){
            System.out.print(e);
        }finally {
            db.close();
        }
    }

    public void sincronizarOrders(){
        pDialog = ProgressDialog.show(this, null, "Enviando datos...", true);
        final SincronizeData sd = new SincronizeData();
        new Thread() {
            public void run() {
                try {
                    sd.sendOrders();
                } catch (Exception e) {
                    e.getMessage();
                }
                pDialog.dismiss();
            }
        }.start();
    }



}
