package com.openup.covadonga.covadongaapp.util;

import android.database.Cursor;
import android.widget.Toast;

import org.ksoap2.serialization.SoapObject;

/**
 * Created by Emilino on 22/09/2015.
 */
public class SincronizeData {

    private WebServices ws;

    public void sendUPC(){
        ws = new WebServices();
        String[] columYVal = new String[6];
        SoapObject resultado_xml = null;
        DBHelper db = null;
        String qryUPC = "select * from uy_productupc where uy_productupc_id < 1000";

        try{
            db = new DBHelper(CustomApplication.getCustomAppContext());
            db.openDB(1);
            Cursor rs = db.querySQL(qryUPC, null);

            if(rs.moveToFirst()){
                do{
                    int i = 0;
                    columYVal[i++] = "UPC"; //colum
                    columYVal[i++] = String.valueOf(rs.getLong(2)); //val
                    columYVal[i++] = "M_Product_ID"; //colum
                    columYVal[i++] = String.valueOf(rs.getInt(1)); //val
                    columYVal[i++] = "IsMobile"; //colum
                    columYVal[i++] = "Y"; //val
                    ws.webServiceIns("CreateProductUPC", "UY_ProductUpc", columYVal);///cambiar
                    if(ws.getMessage() == "EOFException"){
                        ws.webServiceIns("CreateProductUPC", "UY_ProductUpc", columYVal);///cambiar
                    }
                    if(ws.getResponse() != null) {
                        String res = (String) ws.getResponse().getAttribute(0);
                        if (Integer.valueOf(res) > 0) {
//                            String qryDelUPC = "delete from uy_productupc where uy_productupc_id = " + rs.getInt(0);
//                            Cursor cur = db.querySQL(qryDelUPC, null);
                            int resu = db.deleteSQL("uy_productupc","uy_productupc_id = "+rs.getInt(0), null);
                        } else {
                            Toast.makeText(CustomApplication.getCustomAppContext(), "El UPC " + rs.getInt(0) + " no se pudo sincronizar!"
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                }while(rs.moveToNext());
            }
        }catch (Exception e) {
            e.getMessage();
        } finally {
            db.close();
        }
    }
}
