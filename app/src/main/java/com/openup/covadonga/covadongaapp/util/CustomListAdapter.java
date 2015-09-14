package com.openup.covadonga.covadongaapp.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.openup.covadonga.covadongaapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Emilino on 11/09/2015.
 */
public class CustomListAdapter extends ArrayAdapter<Order> {

    ArrayList<Order> orderRes;

    public CustomListAdapter(Context context, ArrayList<Order> orders) {
        super(context, 0, orders);
        this.orderRes = orders;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Order order = this.orderRes.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        // Lookup view for data population
        TextView prod = (TextView) convertView.findViewById(R.id.prodDesc);
        EditText cantFact = (EditText) convertView.findViewById(R.id.cantFact);
        EditText cantEntregado = (EditText) convertView.findViewById(R.id.cantRec);
        // Populate the data into the template view using the data object
        prod.setText(order.getCodigoDesc());
        double Fact = order.getCantFactura();

        cantFact.setText(Double.toString(Fact));
        double Entregado = order.getCantFactura();
        cantEntregado.setText(Double.toString(Entregado));
        // Return the completed view to render on screen
        return convertView;
    }
}
