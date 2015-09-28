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
                            int resu = db.deleteSQL("uy_productupc","uy_productupc_id = "+rs.getInt(0), null);
                        } else {
                            Toast.makeText(CustomApplication.getCustomAppContext(), "El UPC " + rs.getInt(0) +
                                    " no se pudo sincronizar!", Toast.LENGTH_SHORT).show();
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

    public void sendOrders(){
        Env env = new Env();
        DBHelper db = null;
        Cursor ordCurs;
        OrderTo ord = null;
        OrderTo[] orders;

        ////Selecciono las ordenes marcadas como finalizadas
        try{
            db = new DBHelper(CustomApplication.getCustomAppContext());
            db.openDB(1);
            SoapObject resp = new SoapObject();
            ws = new WebServices();
            int i = 0;
            String qryOrd = "select c_order_id, c_bpartner_id, fecha from c_order where finalizado = 'Y'";
            ordCurs = db.querySQL(qryOrd, null);
            orders = new OrderTo[ordCurs.getCount()];
            ord = new OrderTo();
            if(ordCurs.moveToFirst()) {
                do {
                    ord.setAdClientId(env.getAdUsr());
                    ord.setAdOrgId("1000007");
                    ord.setOrderId(ordCurs.getString(0));
                    ord.setPartnerId(ordCurs.getString(1));
                    ord.setDate(ordCurs.getString(2));
                    ord.setWarehouseId("1000053");
                    ord.setDeviceId("dev 1");
                    addOrderLines(ord, ordCurs.getString(0));
                    orders[i] = ord;
                } while (ordCurs.moveToNext());
                ///////llamar al WS pasandole order
                ws.SoapCallerInOrder(orders);
                ////Con la respuesta llamar a la func que elimina los datos de las ordenes
                resp = ws.getResponse();
            }
        }catch (Exception e) {
            e.getMessage();
        } finally {
            db.close();
        }
    }

    private void addOrderLines(OrderTo ord, String ordId){
        Env env = new Env();
        DBHelper db = null;
        Cursor ordLineCurs;
        OrderLine ordLine = null;
        OrderLine[] orderLines;

        try {
            db = new DBHelper(CustomApplication.getCustomAppContext());
            db.openDB(0);
            int i = 0;
            String qryOrdLine = "select l.c_orderline_id, l.c_order_id, l.m_product_id," +
                    " l.qtyinvoiced, l.qtydelivered, l.factura_id, f.fecha" +
                    " from c_orderline l join factura f" +
                    " on l.factura_id = f.factura_id" +
                    " where l.c_order_id = " + ordId;
            ordLineCurs = db.querySQL(qryOrdLine, null);
            orderLines = new OrderLine[ordLineCurs.getCount()];
            ordLine = new OrderLine();
            if (ordLineCurs.moveToFirst()) {
                do {
                    ordLine.setAdClientId(env.getAdUsr());
                    ordLine.setAdOrgId("1000007");
                    ordLine.setOrderLineId(ordLineCurs.getString(0));
                    ordLine.setOrderId(ordLineCurs.getString(1));
                    ordLine.setProductId(ordLineCurs.getString(2));
                    ordLine.setQtyInvoiced(ordLineCurs.getString(3));
                    ordLine.setQtyDelivered(ordLineCurs.getString(4));
                    ordLine.setNroFactura(ordLineCurs.getString(5));
                    ordLine.setFechaFactura(ordLineCurs.getString(6));
                    orderLines[i] = ordLine;
                    i++;
                } while (ordLineCurs.moveToNext());
                ord.setoL(orderLines);
            }
        }catch (Exception e) {
            e.getMessage();
        } finally {
        }
    }

    public void delSyncOrders(int[] orders){
        int tam = orders.length;
        int ordId;
        String qryProdId;

        for(int i=0; i<tam; i++){
            DBHelper db = null;
            ordId = orders[i];
            qryProdId = "select distinct m_product_id from c_orderline where c_order_id = " + ordId;

            try{
                db = new DBHelper(CustomApplication.getCustomAppContext());
                db.openDB(1);
                Cursor rsProds = db.querySQL(qryProdId, null);
                String whereProd = "";
                String whereOrd = " c_order_id = " + ordId;
                if(rsProds.moveToFirst()) {
                    do {
                        whereProd = "where m_product_id = "+ rsProds.getInt(0) +" and m_product_id not in" +
                        " (select distinct m_product_id from c_orderline where c_order_id <> "+ ordId +")";

                        int resuUpc = db.deleteSQL("uy_productupc", whereProd, null);
                        int resuProd = db.deleteSQL("m_product", whereProd, null);

                    } while (rsProds.moveToNext());
                }
                int resuFac = db.deleteSQL("factura", whereOrd, null);
                int resuOL = db.deleteSQL("c_orderline", whereOrd, null);
                int resuOrd = db.deleteSQL("c_order", whereOrd, null);

            }catch (Exception e) {
                e.getMessage();
            } finally {
                db.close();
            }
        }
    }



}
