package com.openup.covadonga.covadongaapp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.openup.covadonga.covadongaapp.util.CustomApplication;
import com.openup.covadonga.covadongaapp.util.DBHelper;
import com.openup.covadonga.covadongaapp.util.CustomListAdapter;
import com.openup.covadonga.covadongaapp.util.Env;
import com.openup.covadonga.covadongaapp.util.Order;


public class ProcesarOrdenActivity extends ActionBarActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager   mViewPager;
    private String[]    ordenes;
    private int         tam;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_procesar_orden);

        getTabsInfo();

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 1; i <= tam; i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.

            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_procesar_orden, menu);

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
            newOrderProcess();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            PlaceholderFragment ph = new PlaceholderFragment();
            return ph.newInstance(position + 1, ordenes[position]);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return tam;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            return ordenes[position-1].toUpperCase(l);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        private ListView    lvProducts;
        private ArrayAdapter<String> adaptador;
        private CustomListAdapter adapter;
        private Button      scan;
        private Button      guardar;
        private Button      finalizar;
        private int         docID;
        private int         ordId;
        private long        barCode;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public PlaceholderFragment newInstance(int sectionNumber, String docuID) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putInt("docID", Integer.parseInt(docuID));
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        public int getShownDoc() {
            return getArguments().getInt("docID", 0);
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_procesar_orden, container, false);
//            Intent in = getShownIndex();
//            Bundle b = in.getExtras();
//            if (null != b) {
                docID = getShownDoc();
//            }
            lvProducts = (ListView) rootView.findViewById(R.id.listViewFragment);
            scan = (Button) rootView.findViewById(R.id.btnScan);
            guardar = (Button) rootView.findViewById(R.id.btnSave);
            finalizar = (Button) rootView.findViewById(R.id.btnEnd);

            setActions();
            loadProducts();

            return rootView;
        }

        @Override
        public void onResume()
        {
            super.onResume();
            loadProducts();
        }



        public void loadProducts(){
            ArrayList<Order> orderResults = GetSearchResults();
            adapter = new CustomListAdapter(getActivity().getBaseContext(), orderResults);
            lvProducts.setAdapter(adapter);
        }

        private ArrayList<Order> GetSearchResults(){
            DBHelper db = null;
            ArrayList<Order> results = new ArrayList<Order>();
            Cursor rs = null;
            String qry = "select p.name, ol.qtyordered, ol.qtyinvoiced, ol.qtydelivered" +
                           " from c_orderline ol JOIN m_product p" +
                           " ON ol.m_product_id = p.m_product_id" +
                           " where ol.c_order_id = ";

            try {
                db = new DBHelper(CustomApplication.getCustomAppContext());
                db.openDB(0);
                String qryAux = "select c_order_id from c_order where documentno = " + docID + "";
                Cursor rsAux = db.querySQL(qryAux, null);
                rsAux.moveToFirst();
                ordId = rsAux.getInt(0);
                qry = qry + ordId;

                rs = db.querySQL(qry, null);

                if(rs.moveToFirst()){
                    do{
                        Order sr1 = new Order();
                        sr1.setCodigoDesc(rs.getString(0));
                        sr1.setCantOrdenada(rs.getFloat(1));
                        sr1.setCantFactura(rs.getFloat(2));
                        sr1.setCantRecibida(rs.getFloat(3));
                        results.add(sr1);
                    }while(rs.moveToNext());
                }

            }catch (Exception e) {
                e.getMessage();
            } finally {
                db.close();
            }

            return results;
        }

        public void setActions() {

            scan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    insertUPC();
                }
            });

            guardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });

            finalizar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Finalizar Orden?");
                    // Add the buttons
                    builder.setPositiveButton(R.string.txtOK, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finalizeOrder();
                        }
                    });
                    builder.setNegativeButton(R.string.txtCancell, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }

        public void insertUPC(){
            android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(getActivity());
            final EditText input = new EditText(getActivity());
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder1.setView(input);
            builder1.setTitle("Ingrese el Codigo de Barras");
            builder1.setCancelable(true);
            builder1.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            barCode = Long.valueOf(input.getText().toString());
                            startConfirmarCantidadesActivity(barCode);
                        }
                    });
            builder1.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            android.app.AlertDialog alert11 = builder1.create();
            alert11.show();
        }


        public void startConfirmarCantidadesActivity(long barCode){
            Intent i = new Intent(getActivity().getBaseContext(), ConfirmarCantidadesActivity.class);
            Bundle b = new Bundle();
            b.putInt("c_order_id", ordId);
            b.putLong("barcode", barCode);
            i.putExtras(b);
            startActivity(i);
        }

        public void finalizeOrder(){
            DBHelper db = null;
            int res;
            String date;

            String where = " c_order_id = " + ordId;
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            String mon = String.valueOf(month+1);
            if((month+1) < 10){
                mon = "0"+mon;
            }
            date = year+"-"+mon+"-"+day+" 00:00:00";

            try {
                db = new DBHelper(CustomApplication.getCustomAppContext());
                db.openDB(1);
                ContentValues cv = new ContentValues();
                cv.put("finalizado", "Y");
                cv.put("fecha", date);

                res = db.updateSQL("c_order", cv, where, null);
                if(res > 0){
                    Toast.makeText(getActivity().getBaseContext(), "Orden Finalizada!",
                            Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }else{
                    Toast.makeText(getActivity().getBaseContext(), "Error al finalizar orden!",
                            Toast.LENGTH_SHORT).show();
                }

            }catch (Exception e) {
                e.getMessage();
            } finally {
                db.close();
            }
        }
    }



    public void getTabsInfo() {
        // get the Intent that started this Activity
        Intent in = getIntent();
        // get the Bundle that stores the data of this Activity
        Bundle b = in.getExtras();
            if (null != b) {
                ordenes = b.getString("Ordenes").split(";");
                tam = ordenes.length;
            }
    }

        public void newOrderProcess() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.new_supplier);
            builder.setMessage(R.string.process_new_order)
                .setPositiveButton(R.string.txtOK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startListaClienteActivity();
                    }
                })
                    .setNegativeButton(R.string.txtCancell, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }

    private void startListaClienteActivity() {
        Intent i = new Intent(this, ListaProveedorActivity.class);
        startActivity(i);
    }

}
