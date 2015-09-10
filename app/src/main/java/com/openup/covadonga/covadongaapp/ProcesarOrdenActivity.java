package com.openup.covadonga.covadongaapp;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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
            return PlaceholderFragment.newInstance(position + 1);
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
        private EditText    etFilter;
        private Button      scan;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_procesar_orden, container, false);
            //loadProducts(Integer.toString(tam));
            lvProducts = (ListView) rootView.findViewById(R.id.listViewFragment);
            etFilter = (EditText) rootView.findViewById(R.id.editTextFragBucar);
            scan = (Button) rootView.findViewById(R.id.btnScan);
            setActions();
            loadProducts();

            return rootView;
        }


        public void loadProducts(){
//            String[] distri = {"Prod a", "Prod b", "Prod c", "Prod d", "Prod 1", "Prod 2", "Prod 3", "Prod 4", "Prod 5"};
//            adaptador = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_list_item_1, distri);
//            lvProducts.setAdapter(adaptador);
            ArrayList<Order> orderResults = GetSearchResults();
            lvProducts.setAdapter(new OrderListAdapter(getActivity().getBaseContext(), orderResults));
        }

        private ArrayList<Order> GetSearchResults(){
            ArrayList<Order> results = new ArrayList<Order>();

            Order sr1 = new Order();
            sr1.setCodigoDesc("Prod 1");
            sr1.setCantOrdenada(10);
            sr1.setCantFactura(10);
            sr1.setCantRecibida(10);
            results.add(sr1);

            sr1 = new Order();
            sr1.setCodigoDesc("Prod 2");
            sr1.setCantOrdenada(9);
            sr1.setCantFactura(9);
            sr1.setCantRecibida(9);
            results.add(sr1);

            sr1 = new Order();
            sr1.setCodigoDesc("Prod 3");
            sr1.setCantOrdenada(5);
            sr1.setCantFactura(5);
            sr1.setCantRecibida(5);
            results.add(sr1);

            sr1 = new Order();
            sr1.setCodigoDesc("Prod 4");
            sr1.setCantOrdenada(15);
            sr1.setCantFactura(15);
            sr1.setCantRecibida(15);
            results.add(sr1);

            return results;
        }

        public void setActions() {
            etFilter.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    // When user changed the Text
                    PlaceholderFragment.this.adaptador.getFilter().filter(cs);
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

            scan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startConfirmarCantidadesActivity();
                }
            });
        }

        public void startConfirmarCantidadesActivity(){
            Intent i = new Intent(getActivity().getBaseContext(), ConfirmarCantidadesActivity.class);
            startActivity(i);
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

    public void newOrderProcess(){
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
        Intent i = new Intent(this, ListaClienteActivity.class);
        startActivity(i);
    }

}
