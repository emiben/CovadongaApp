package com.openup.covadonga.covadongaapp;

import android.app.ProgressDialog;
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
import com.openup.covadonga.covadongaapp.util.WebServices;

import org.ksoap2.serialization.SoapObject;

import java.sql.ResultSet;


public class ListaProveedorActivity extends ActionBarActivity {

    private EditText    etFilter;
    private ListView    lvProv;
    private Button      btnBack;
    private Button      btnOK;
    private TextView    tvEmpresa;
    private ProgressDialog  pDialog;
    private int         recType; // 0 con orden, 1 sin orden
    // Listview Adapter
    private ArrayAdapter<String> adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_proveedor);

        getViewElements();
        getBundleData();
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

    public void getBundleData() {
        Intent in = getIntent();
        Bundle b = in.getExtras();
        recType = b.getInt("recType");
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
        String qry;

        if(recType == 0){
            qry = "Select name from c_bpartner";
        }else{
            qry = "Select name from c_bpartner where isrecieptpo = 'Y'";
        }

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
                if (tvEmpresa.getText().toString() != "") {
                    //startListaOrdenesActivity();
                    getProvOrdersWS();
                } else {
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


    public void getProvOrdersWS() {
        pDialog = ProgressDialog.show(this, null, "Consultando datos...", true);
        new Thread() {
            public void run() {
                try {
                    getProvOrders(getProvId());
                    insertProds();
                    startListaOrdenesActivity();
                } catch (Exception e) {
                    e.getMessage();
                }
                pDialog.dismiss();
            }
        }.start();
    }


    private void getProvOrders(int partnerID){

        WebServices ws = new WebServices();
        String[] columYVal = new String[2];
        SoapObject resultado_xml = null;
        int i = 0;

        columYVal[i++] = "C_BPartner_ID"; //colum
        columYVal[i++] = String.valueOf(partnerID); //val

        resultado_xml = ws.webServiceQry("LoadProvOrders", "VUY_MB_Order", columYVal);
        if(ws.getMessage() == "EOFException"){
            resultado_xml = ws.webServiceQry("LoadProvOrders", "VUY_MB_Order", columYVal);
            insertOrders(resultado_xml);
            insertOrderLines(partnerID);
        }else if(ws.getMessage() == "Error!!"){
            Toast.makeText(getApplicationContext(),
                    "Error! Por favor intente nuevamente!!", Toast.LENGTH_SHORT).show();
        }else{
            insertOrders(resultado_xml);
            insertOrderLines(partnerID);
        }
    }

    private void insertOrders(SoapObject so){
        SoapObject dataResult = (SoapObject)so.getProperty(0);
        int tam = dataResult.getPropertyCount();
        String delims = "[=;]";

        DBHelper db = new DBHelper(this);
        db.openDB(1);

        try{
            if(tam > 0) {
                for (int i = 0; i < tam; i++) {
                    SoapObject dataRow = (SoapObject) dataResult.getProperty(i);
                    String col1[] = dataRow.getProperty(0).toString().split(delims); //C_BPartner_ID--
                    String col2[] = dataRow.getProperty(1).toString().split(delims); //C_Order_ID--
                    String col3[] = dataRow.getProperty(2).toString().split(delims); //DocumentNo

                    String qry = "Insert into C_Order values (";
                    qry = qry + col2[1] + ",'" + col3[1] + "'," + col1[1] + ", 'N','', '0')";

                    db.executeSQL(qry);
                }
            }
        } catch (Exception e){
            System.out.print(e);
        }finally {
            db.close();
        }
    }

    private int getProvId(){
        int provId = 0;
        DBHelper db = null;
        String qry1 = "select c_bpartner_id from c_bpartner where name = '" + tvEmpresa.getText().toString() + "'";

        try {
            db = new DBHelper(getApplicationContext());
            db.openDB(0);
            Cursor rs = db.querySQL(qry1, null);
            rs.moveToFirst();
            provId = rs.getInt(0);

        }catch (Exception e) {
            e.getMessage();
        } finally {
            db.close();
        }
        return provId;
    }

    private void insertOrderLines(int partnerId){
        DBHelper db = null;
        String qry1 = "select c_order_id from c_order where c_bpartner_id = " + partnerId;

        try {
            db = new DBHelper(getApplicationContext());
            db.openDB(0);
            Cursor rs = db.querySQL(qry1, null);

            if(rs.moveToFirst()){
                do{
                    getOrdersLines(rs.getInt(0));
                }while(rs.moveToNext());
            }

        }catch (Exception e) {
            e.getMessage();
        } finally {
            db.close();
        }

    }

    private void getOrdersLines(int ordId){

        WebServices ws = new WebServices();
        String[] columYVal = new String[2];
        SoapObject resultado_xml = null;
        int i = 0;
        columYVal[i++] = "C_Order_ID"; //colum
        columYVal[i++] = String.valueOf(ordId); //val

        resultado_xml = ws.webServiceQry("LoadOrderLines", "VUY_MB_OrderLine", columYVal);
        insertOrdersLinesXML(resultado_xml);

    }

    private void insertOrdersLinesXML(SoapObject so){
        SoapObject dataResult = (SoapObject)so.getProperty(0);
        int tam = dataResult.getPropertyCount();
        String delims = "[=;]";

        DBHelper db = new DBHelper(this);
        db.openDB(1);

        try{
            if(tam > 0) {
                for (int i = 0; i < tam; i++) {
                    SoapObject dataRow = (SoapObject) dataResult.getProperty(i);
                    String col1[] = dataRow.getProperty(0).toString().split(delims); //C_BPartner_ID--
                    String col2[] = dataRow.getProperty(1).toString().split(delims); //C_Order_ID--
                    String col3[] = dataRow.getProperty(2).toString().split(delims); //C_OrderLine_ID--
                    String col4[] = dataRow.getProperty(3).toString().split(delims); //Line--
                    String col5[] = dataRow.getProperty(4).toString().split(delims); //M_Product_ID--
                    String col6[] = dataRow.getProperty(5).toString().split(delims); //qtypend--


                    String qry = "Insert into c_orderline values (";
                    qry = qry + col3[1] + "," + col2[1] + "," + col4[1] + ",";//
                    qry = qry + col1[1] + "," + col5[1] + "," + col6[1] + ",0,0,'')";

                    db.executeSQL(qry);
                }
            }
        } catch (Exception e){
            System.out.print(e);
        }finally {
            db.close();
        }
    }

    private void insertProds(){

        WebServices ws = new WebServices();
        String[] columYVal = new String[4];
        SoapObject resultado_xml = null;
        SoapObject resultado_xml2 = null;


        DBHelper db = new DBHelper(this);
        db.openDB(1);
        //db.executeSQL("DELETE FROM uy_productupc where m_product_id in (select m_product_id FROM M_Product where borrar = 'Y')");
        //db.executeSQL("DELETE FROM M_Product where borrar = 'Y'");
        Cursor rs = db.querySQL("select m_product_id from c_orderline", null);

        if(rs.moveToFirst()){
            do{
                int i = 0;
                columYVal[i++] = "IsActive"; //colum
                columYVal[i++] = "Y"; //val
                columYVal[i++] = "M_Product_ID"; //colum
                columYVal[i++] = String.valueOf(rs.getInt(0)); //val

                resultado_xml = ws.webServiceQry("LoadProducts", "M_Product", columYVal);
                if(ws.getMessage() == "EOFException"){
                    resultado_xml = ws.webServiceQry("LoadProducts", "M_Product", columYVal);
                }else if(ws.getMessage() == "Error!!"){
                    Toast.makeText(getApplicationContext(),
                            "Error! Por favor intente nuevamente!!", Toast.LENGTH_SHORT).show();
                }else{
                    insertProds(resultado_xml);
                }

                resultado_xml2 = ws.webServiceQry("LoadUPC", "UY_ProductUpc", columYVal);
                if(ws.getMessage() == "EOFException"){
                    resultado_xml2 = ws.webServiceQry("LoadUPC", "UY_ProductUpc", columYVal);
                }else if(ws.getMessage() == "Error!!"){
                    Toast.makeText(getApplicationContext(),
                            "Error! Por favor intente nuevamente!!", Toast.LENGTH_SHORT).show();
                }else{
                    insertProdsUPC(resultado_xml2);
                }
            }while(rs.moveToNext());
        }
    }

    private void insertProds(SoapObject so){

        SoapObject dataResult = (SoapObject)so.getProperty(0);

        int tam = dataResult.getPropertyCount();
        String delims = "[=;]";

        DBHelper db = new DBHelper(this);
        db.openDB(1);

        try{
            if(tam > 0) {
                for (int i = 0; i < tam; i++) {
                    SoapObject dataRow = (SoapObject) dataResult.getProperty(i);
                    String col1[] = dataRow.getProperty(0).toString().split(delims); //Description
                    String col2[] = dataRow.getProperty(1).toString().split(delims); //M_Product_ID
                    String col3[] = dataRow.getProperty(2).toString().split(delims); //Name
                    String col4[] = dataRow.getProperty(3).toString().split(delims); //UPC

                    String qry = "Insert into M_Product values (";
                    qry = qry + col2[1] + ",'" + col3[1] + "','" + col1[1] + "','" + col4[1] + "','N')";

                    db.executeSQL(qry);
                }
            }
        } catch (Exception e){
            System.out.print(e);
        }finally {
            db.close();
        }
    }

    private void insertProdsUPC(SoapObject so){

        SoapObject dataResult = (SoapObject)so.getProperty(0);

        int tam = dataResult.getPropertyCount();
        String delims = "[=;]";

        DBHelper db = new DBHelper(this);
        db.openDB(1);

        try{
            if(tam > 0) {
                for (int i = 0; i < tam; i++) {
                    SoapObject dataRow = (SoapObject) dataResult.getProperty(i);
                    String col1[] = dataRow.getProperty(0).toString().split(delims); //M_Product_ID
                    String col2[] = dataRow.getProperty(1).toString().split(delims); //UPC
                    String col3[] = dataRow.getProperty(2).toString().split(delims); //UY_ProductUpc_ID

                    String qry = "Insert into uy_productupc values (";
                    qry = qry + col3[1] + ",'" + col1[1] + "','" + col2[1] + "')";

                    db.executeSQL(qry);
                }
            }
        } catch (Exception e){
            System.out.print(e);
        }finally {
            db.close();
        }
    }

    private void getPriceList(){
        int orderId = 1;
        int documentno = 1;
        int provID = getProvId();
        DBHelper db = null;
        String qry1 = "select max(c_order_id), max(documentno) from c_order where c_order_id < 1000000";

        try {
            db = new DBHelper(getApplicationContext());
            db.openDB(1);
            Cursor rs = db.querySQL(qry1, null);
            if(rs.moveToFirst()){
                orderId = orderId + rs.getInt(0);
                documentno = documentno + rs.getInt(1);
            }

            String qry = "Insert into c_order values ("+orderId+","+documentno+","+provID+",'N','',0)";
            db.executeSQL(qry);


        }catch (Exception e) {
            e.getMessage();
        } finally {
            db.close();
        }

    }
}
