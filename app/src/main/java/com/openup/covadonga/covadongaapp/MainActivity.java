package com.openup.covadonga.covadongaapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.openup.covadonga.covadongaapp.util.CovadongaDB;
import com.openup.covadonga.covadongaapp.util.Env;
import com.openup.covadonga.covadongaapp.util.InitialLoad;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


public class MainActivity extends ActionBarActivity {

    private Activity mCtx = null;
    private String userIn="";
    private String pswIn="";
    private Button  btnLogIn;
    private Boolean retornoWS=false;
    private ProgressDialog pDialog;
    private String TAG  = "EBP";

    private EditText txtUser;
    private EditText txtPsw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCtx = this;
        getViewElement();
        setActions();

        testDataBase();
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

    public void getViewElement(){
        btnLogIn = (Button) findViewById(R.id.btnLogin);
        txtUser = (EditText) findViewById(R.id.eTxtUserName);
        txtPsw = (EditText) findViewById(R.id.eTxtPassword);
    }

    public void setActions(){
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userIn = txtUser.getText().toString();
                pswIn = txtPsw.getText().toString();
                //if(u.equals("admin") && p.equals("admin")){
                if(isOnline()){
                    if(loginWS(userIn,pswIn)){
//                        startMenuActivity();
                    }else{

                    }
                }else{

                    CharSequence text =  getResources().getString(R.string.noInternet);
                    Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }


    private void startMenuActivity() {
        Intent i = new Intent(this, MenuActivity.class);
        startActivity(i);
    }

    private void testDataBase() {
        // Validate SD
        if(Env.isEnvLoad(this)){
            if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                if(!Env.getDB_PathName(this).equals(CovadongaDB.DB_NAME)){
                    finish();
                }
            }
        } else {
            InitialLoad initData = new InitialLoad(this);
            initData.initialLoad_copyDB();
        }
    }

    private boolean loginWS(String u, String p) {
        final Env e = new Env();
        pDialog = ProgressDialog.show(this, null, "Consultando..", true);
        new Thread(){
            public void run(){
                try{
                    retornoWS = loginWebServer(userIn,pswIn);
                }catch (Exception e){
                    e.getMessage();
                }
                pDialog.dismiss();
                (mCtx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (retornoWS) {
                            if(retornoWS){
                                e.setUser(userIn);
                                e.setPass(pswIn);
                            }
                            startMenuActivity();
                        } else {
                            Context context = getApplicationContext();
                            CharSequence text =  getResources().getString(R.string.user_pws_error);
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    }
                });
            }
        }.start();
        return retornoWS;
    }

    private boolean loginWebServer(String userIn, String pswIn)
    {
        boolean reg = false;

        final String NAMESPACE = Env.NAMESPACE;
        final String URL=Env.URL;
        final String METHOD_NAME = "login";
        final String SOAP_ACTION = "http://3e.pl/ADInterface/ADServicePortType/loginRequest";

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        SoapObject adLoginRequest = new SoapObject(NAMESPACE,"ADLoginRequest");
        PropertyInfo usrPI= new PropertyInfo();
        usrPI.setName("user");
        usrPI.setValue(userIn);
        usrPI.setNamespace(NAMESPACE);
        usrPI.setType(String.class);
        adLoginRequest.addProperty(usrPI);

        PropertyInfo pswPI= new PropertyInfo();
        pswPI.setName("pass");
        pswPI.setValue(pswIn);
        pswPI.setNamespace(NAMESPACE);
        pswPI.setType(String.class);
        adLoginRequest.addProperty(pswPI);

        request.addSoapObject(adLoginRequest);
        SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = false;

        envelope.setOutputSoapObject(request);
        HttpTransportSE transporte = new HttpTransportSE(URL);
        try
        {
            transporte.call(SOAP_ACTION, envelope);
            SoapObject resultado_xml =(SoapObject)envelope.getResponse();
            String status = resultado_xml.getProperty("status").toString();

            //SoapPrimitive resultado_xml =(SoapPrimitive)envelope.getResponse();
            String res = resultado_xml.toString();

            if(status.equals("1000006"))
            {
                Log.d(TAG, "Registrado en mi servidor.");
                reg = true;
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, "Error registro en mi servidor: " + e.getCause() + " || " + e.getMessage());
        }
        return reg;
    }

    private boolean isOnline(){
        Boolean ret = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                ret = true;
            }
        }catch (Exception e){
            e.getMessage();
        }
        return ret;
    }
}
