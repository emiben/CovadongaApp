package com.openup.covadonga.covadongaapp.util;

import android.util.Log;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.EOFException;
import java.io.IOException;

/**
 * Created by Emilino on 17/09/2015.
 */
public class WebServices {

    private SoapObject response = null;
    private Object respuesta;
    private final String NAMESPACE = "http://3e.pl/ADInterface";
    private final String URL = "http://covadonga.dyndns.org:8273/ADInterface-1.0/services/ModelADService";
    private final String METHOD_NAME = "queryData";
    private final String SOAP_ACTION = "http://3e.pl/ADInterface/ModelADServicePortType/queryDataRequest";
    private final String METHOD_NAME_INSERT = "createData";
    private final String SOAP_ACTION_INSERT = "http://3e.pl/ADInterface/ModelADServicePortType/createDataRequest";
    private String mensajeWS = "";

    public WebServices(){};

    public String getMessage(){
        return mensajeWS;
    }

    public SoapObject getResponse(){
        return response;
    }

    public SoapObject webServiceQry(String method, String table, String[] ColumYVal){

        SoapObject resultado_xml = null;

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        SoapObject ModelCRUDRequest = new SoapObject(NAMESPACE, "ModelCRUDRequest");
        SoapObject ModelCRUD = new SoapObject(NAMESPACE, "ModelCRUD");

        PropertyInfo serviceType = new PropertyInfo();
        serviceType.setName("serviceType");
        serviceType.setValue(method);
        serviceType.setNamespace(NAMESPACE);
        serviceType.setType(String.class);
        ModelCRUD.addProperty(serviceType);
        //ModelCRUD.addProperty("serviceType", "RegisterMobileUser");
        PropertyInfo TableName = new PropertyInfo();
        TableName.setName("TableName");
        TableName.setValue(table);
        TableName.setNamespace(NAMESPACE);
        TableName.setType(String.class);
        ModelCRUD.addProperty(TableName);
        // ModelCRUD.addProperty("TableName", "UY_UserReq");
        PropertyInfo RecordID = new PropertyInfo();
        RecordID.setName("RecordID");
        RecordID.setValue("0");
        RecordID.setNamespace(NAMESPACE);
        RecordID.setType(String.class);
        ModelCRUD.addProperty(RecordID);
        //ModelCRUD.addProperty("RecordID", "1");
        PropertyInfo Action = new PropertyInfo();
        Action.setName("Action");
        Action.setValue("Read");
        Action.setNamespace(NAMESPACE);
        Action.setType(String.class);
        ModelCRUD.addProperty(Action);
        // ModelCRUD.addProperty("Action", "Create");

        SoapObject DataRow = new SoapObject(NAMESPACE, "DataRow");
        SoapObject field;

        if(ColumYVal != null) {
            for (int i = 0; i < ColumYVal.length; i++) {
                field = new SoapObject(NAMESPACE, "field");

                field.addAttribute("column", ColumYVal[i++]);

                PropertyInfo pi2 = new PropertyInfo();
                pi2.setName("val");
                pi2.setValue(ColumYVal[i]);
                pi2.setType(String.class);
                pi2.setNamespace(NAMESPACE);
                field.addProperty(pi2);

                DataRow.addSoapObject(field);
            }
        }

        ModelCRUD.addSoapObject(DataRow);
        ModelCRUDRequest.addSoapObject(ModelCRUD);

        SoapObject ADLoginRequest = new SoapObject(NAMESPACE, "ADLoginRequest");
        PropertyInfo usrPI = new PropertyInfo();
        usrPI.setName("user");
        usrPI.setValue("mobileuser");
        usrPI.setNamespace(NAMESPACE);
        usrPI.setType(String.class);
        ADLoginRequest.addProperty(usrPI);
        PropertyInfo pswPI = new PropertyInfo();
        pswPI.setName("pass");
        pswPI.setValue("mobileuser");
        pswPI.setNamespace(NAMESPACE);
        pswPI.setType(String.class);
        ADLoginRequest.addProperty(pswPI);
        //ADLoginRequest.addProperty("user",  String.valueOf(usr));
        //ADLoginRequest.addProperty("pass", String.valueOf(usr));
        PropertyInfo lang = new PropertyInfo();
        lang.setName("lang");
        lang.setValue("143");
        lang.setNamespace(NAMESPACE);
        lang.setType(String.class);
        ADLoginRequest.addProperty(lang);
        //ADLoginRequest.addProperty("lang","143");
        // ADLoginRequest.addProperty("lang", String.valueOf(Env.getAD_Language(m_Ctx))));
        PropertyInfo cli = new PropertyInfo();
        cli.setName("ClientID");
        cli.setValue("1000006");
        cli.setNamespace(NAMESPACE);
        cli.setType(String.class);
        ADLoginRequest.addProperty(cli);
        //ADLoginRequest.addProperty("ClientID", "1000006");
        //  ADLoginRequest.addProperty("RoleID", String.valueOf(Env.getAD_Role_ID(m_Ctx)));
        PropertyInfo rol = new PropertyInfo();
        rol.setName("RoleID");
        rol.setValue("1000022");
        rol.setNamespace(NAMESPACE);
        rol.setType(String.class);
        ADLoginRequest.addProperty(rol);
        //ADLoginRequest.addProperty("RoleID", "1000022");
        PropertyInfo org = new PropertyInfo();
        org.setName("OrgID");
        org.setValue("1000007");
        org.setNamespace(NAMESPACE);
        org.setType(String.class);
        ADLoginRequest.addProperty(org);
        // ADLoginRequest.addProperty("OrgID", "1000007");
        //   ADLoginRequest.addProperty("WarehouseID",String.valueOf(Env.getM_Warehouse_ID(m_Ctx)));
        PropertyInfo ware = new PropertyInfo();
        ware.setName("WarehouseID");
        ware.setValue("1000053");
        ware.setNamespace(NAMESPACE);
        ware.setType(String.class);
        ADLoginRequest.addProperty(ware);
        //ADLoginRequest.addProperty("WarehouseID","1000052");
        ModelCRUDRequest.addSoapObject(ADLoginRequest);

        request.addSoapObject(ModelCRUDRequest);
        SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = false;

        envelope.setOutputSoapObject(request);
        HttpTransportSE transporte = new HttpTransportSE(URL);
        try {
            transporte.call(SOAP_ACTION, envelope);
            if((SoapObject) envelope.getResponse() != null) {
                resultado_xml = (SoapObject) envelope.getResponse();
            }
            transporte.reset();
        } catch (Exception e) {
            mensajeWS = "Error!!";
            e.getMessage();
            //Log.d(TAG, "Error registro en mi servidor: " + e.getCause() + " || " + e.getMessage());
        }
        return resultado_xml;
    }

    public void webServiceIns(String method, String table, String[] ColumYVal) {

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_INSERT);

        SoapObject ModelCRUDRequest = new SoapObject(NAMESPACE, "ModelCRUDRequest");
        SoapObject ModelCRUD = new SoapObject(NAMESPACE, "ModelCRUD");

        PropertyInfo serviceType = new PropertyInfo();
        serviceType.setName("serviceType");
        serviceType.setValue(method);
        serviceType.setNamespace(NAMESPACE);
        serviceType.setType(String.class);
        ModelCRUD.addProperty(serviceType);
        //ModelCRUD.addProperty("serviceType", "RegisterMobileUser");
        PropertyInfo TableName = new PropertyInfo();
        TableName.setName("TableName");
        TableName.setValue(table);
        TableName.setNamespace(NAMESPACE);
        TableName.setType(String.class);
        ModelCRUD.addProperty(TableName);
        // ModelCRUD.addProperty("TableName", "UY_UserReq");
        PropertyInfo RecordID = new PropertyInfo();
        RecordID.setName("RecordID");
        RecordID.setValue("0");
        RecordID.setNamespace(NAMESPACE);
        RecordID.setType(String.class);
        ModelCRUD.addProperty(RecordID);
        //ModelCRUD.addProperty("RecordID", "1");
        PropertyInfo Action = new PropertyInfo();
        Action.setName("Action");
        Action.setValue("Create");
        Action.setNamespace(NAMESPACE);
        Action.setType(String.class);
        ModelCRUD.addProperty(Action);
        // ModelCRUD.addProperty("Action", "Create");

        SoapObject DataRow = new SoapObject(NAMESPACE, "DataRow");
        SoapObject field;

        if (ColumYVal != null) {
            for (int i = 0; i < ColumYVal.length; i++) {
                field = new SoapObject(NAMESPACE, "field");

                field.addAttribute("column", ColumYVal[i++]);

                PropertyInfo pi2 = new PropertyInfo();
                pi2.setName("val");
                pi2.setValue(ColumYVal[i]);
                pi2.setType(String.class);
                pi2.setNamespace(NAMESPACE);
                field.addProperty(pi2);
                DataRow.addSoapObject(field);
            }
        }

        ModelCRUD.addSoapObject(DataRow);
        ModelCRUDRequest.addSoapObject(ModelCRUD);

        SoapObject ADLoginRequest = new SoapObject(NAMESPACE, "ADLoginRequest");
        PropertyInfo usrPI = new PropertyInfo();
        usrPI.setName("user");
        usrPI.setValue("mobileuser");
        usrPI.setNamespace(NAMESPACE);
        usrPI.setType(String.class);
        ADLoginRequest.addProperty(usrPI);
        PropertyInfo pswPI = new PropertyInfo();
        pswPI.setName("pass");
        pswPI.setValue("mobileuser");
        pswPI.setNamespace(NAMESPACE);
        pswPI.setType(String.class);
        ADLoginRequest.addProperty(pswPI);
        //ADLoginRequest.addProperty("user",  String.valueOf(usr));
        //ADLoginRequest.addProperty("pass", String.valueOf(usr));
        PropertyInfo lang = new PropertyInfo();
        lang.setName("lang");
        lang.setValue("143");
        lang.setNamespace(NAMESPACE);
        lang.setType(String.class);
        ADLoginRequest.addProperty(lang);
        //ADLoginRequest.addProperty("lang","143");
        // ADLoginRequest.addProperty("lang", String.valueOf(Env.getAD_Language(m_Ctx))));
        PropertyInfo cli = new PropertyInfo();
        cli.setName("ClientID");
        cli.setValue("1000006");
        cli.setNamespace(NAMESPACE);
        cli.setType(String.class);
        ADLoginRequest.addProperty(cli);
        //ADLoginRequest.addProperty("ClientID", "1000006");
        //  ADLoginRequest.addProperty("RoleID", String.valueOf(Env.getAD_Role_ID(m_Ctx)));
        PropertyInfo rol = new PropertyInfo();
        rol.setName("RoleID");
        rol.setValue("1000022");
        rol.setNamespace(NAMESPACE);
        rol.setType(String.class);
        ADLoginRequest.addProperty(rol);
        //ADLoginRequest.addProperty("RoleID", "1000022");
        PropertyInfo org = new PropertyInfo();
        org.setName("OrgID");
        org.setValue("1000007");
        org.setNamespace(NAMESPACE);
        org.setType(String.class);
        ADLoginRequest.addProperty(org);
        // ADLoginRequest.addProperty("OrgID", "1000007");
        //   ADLoginRequest.addProperty("WarehouseID",String.valueOf(Env.getM_Warehouse_ID(m_Ctx)));
        PropertyInfo ware = new PropertyInfo();
        ware.setName("WarehouseID");
        ware.setValue("1000053");
        ware.setNamespace(NAMESPACE);
        ware.setType(String.class);
        ADLoginRequest.addProperty(ware);
        //ADLoginRequest.addProperty("WarehouseID","1000052");
        ModelCRUDRequest.addSoapObject(ADLoginRequest);

        request.addSoapObject(ModelCRUDRequest);
        SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = false;

        envelope.setOutputSoapObject(request);
        HttpTransportSE transporte = new HttpTransportSE(URL);
        try {
            transporte.call(SOAP_ACTION_INSERT, envelope);

            if ((SoapObject) envelope.getResponse() != null) {
                response = (SoapObject) envelope.getResponse();
                mensajeWS = "OK";
            }
            transporte.reset();
        } catch (EOFException e1){
            mensajeWS = "EOFException";
        } catch (Exception e) {
            mensajeWS = "ERROR!";
            e.getMessage();
            //Log.d(TAG, "Error registro en mi servidor: " + e.getCause() + " || " + e.getMessage());
        }
    }


    public void SoapCallerInOrder(String _URL, String _NAMESPACE, String _METHOD_NAME, String _SOAP_ACTION, String[] ModelCRUDArguments, OrderTo[] m_orders){
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(_URL);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.encodingStyle = SoapSerializationEnvelope.ENC;

        //http://www.erpcya.com/
        String _METHOD_NAME_DOS = "http://www.erpcya.com/";
        SoapObject request = new SoapObject(_NAMESPACE, _METHOD_NAME); //InOrder
        SoapObject inOrder = new SoapObject(_METHOD_NAME_DOS, "InOrderRT");

        PropertyInfo usrPI= new PropertyInfo();
        usrPI.setName("UserRT");
        usrPI.setValue("mobileuser");
        usrPI.setNamespace(_METHOD_NAME_DOS);
        usrPI.setType(String.class);
        inOrder.addProperty(usrPI);
        //inOrder.addProperty("User",user.getName());
        PropertyInfo pswPI= new PropertyInfo();
        pswPI.setName("PassWordRT");
        pswPI.setValue("mobileuser");
        pswPI.setNamespace(_METHOD_NAME_DOS);
        pswPI.setType(String.class);
        inOrder.addProperty(pswPI);
        //inOrder.addProperty("PassWord", user.getPass());

        SoapObject newOrders = new SoapObject(_METHOD_NAME_DOS, "newOrdersRT"); //OBJETO 1
        SoapObject orders; //OBJETO 2
        for( int i=0;i<1;i++){
            OrderTo unaOrden = m_orders[i];
            String[] datosDeOrden = obtenerDatos(unaOrden);
            orders = new SoapObject(_METHOD_NAME_DOS, "orders");
            //Agrego todos los atributos a la orden
            for(int j = 0;j<datosDeOrden.length;j++){
                PropertyInfo dato= new PropertyInfo();
                dato.setName(datosDeOrden[j++]);
                dato.setValue(datosDeOrden[j]);
                dato.setNamespace(_METHOD_NAME_DOS);
                dato.setType(datosDeOrden[j].getClass());
                orders.addProperty(dato);
                //order.addProperty(datosDeOrden[j++],datosDeOrden[j]);// 0 nomb, 1 val
            }
            //Object que contiene las lineas a la orden
            SoapObject lines = new SoapObject(_METHOD_NAME_DOS, "lines"); //OBJETO 3
            OrderLine[] lineasOrden = new OrderLine[unaOrden.getOLSize()];
            lineasOrden = unaOrden.getoL();
            SoapObject orderlines; //OBJETO 4
            for(int k=0;k<lineasOrden.length;k++){
                OrderLine unaLinea = lineasOrden[k];
                String[] datosLinea = obtenerDatosLinea(unaLinea);
                orderlines = new SoapObject(_METHOD_NAME_DOS,"orderlines");

                for(int l=0;l<datosLinea.length;l++){
                    PropertyInfo dato= new PropertyInfo();
                    dato.setName(datosLinea[l++]);
                    dato.setValue(datosLinea[l]);
                    dato.setNamespace(_METHOD_NAME_DOS);
                    dato.setType(datosLinea[l].getClass());
                    orderlines.addProperty(dato);
                    //orderlines.addProperty(datosLinea[l++],datosDeOrden[l]);
                }
                lines.addSoapObject(orderlines); //Agrego obj4 a objeto3
            }
            orders.addSoapObject(lines); //Agrego objeto 3 a obj 2
            newOrders.addSoapObject(orders); //Agrego Objeto2 a Objeto1
        }
        inOrder.addSoapObject(newOrders);
        request.addSoapObject(inOrder);

        SoapSerializationEnvelope envp = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envp.dotNet = true;
        envp.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(_URL);
        try {
            androidHttpTransport.call(_SOAP_ACTION, envp);
            this.response = (SoapObject)envp.getResponse();
            this.respuesta = envp.getResponse();
        }
        catch (IOException e){
            Log.i("WS Error->", e.toString());
        }
        catch (Exception e) {
            Log.i("WS Error->",e.toString());
        }

    }

    private String[] obtenerDatosLinea(OrderLine unaLinea) {
        // TODO Auto-generated method stub
        String[] ColumYVal = new String[30];
        int i=0;
        if (unaLinea!= null){
            ColumYVal[i++] = "AD_Client_ID"; //colum
            ColumYVal[i++] = String.valueOf(unaLinea.getAdClientId()); //val

            ColumYVal[i++] = "AD_Org_ID"; //colum
            ColumYVal[i++] = String.valueOf(unaLinea.getAdOrgId()); //val

            ColumYVal[i++] = "C_OrderLine_ID"; //colum
            ColumYVal[i++] = String.valueOf(unaLinea.getOrderLineId()); //val

            ColumYVal[i++] = "C_Order_ID"; //colum
            ColumYVal[i++] = String.valueOf(unaLinea.getOrderId()); //val

            ColumYVal[i++] = "M_Product_ID"; //colum
            ColumYVal[i++] = String.valueOf(unaLinea.getProductId()); //val

            ColumYVal[i++] = "QtyInvoiced"; //colum
            ColumYVal[i++] = String.valueOf(unaLinea.getQtyInvoiced()); //val

            ColumYVal[i++] = "QtyDelivered"; //colum
            ColumYVal[i++] = String.valueOf(unaLinea.getQtyDelivered()); //val

            ColumYVal[i++] = "NroFactura"; //colum
            ColumYVal[i++] = String.valueOf(unaLinea.getNroFactura()); //val

            ColumYVal[i++] = "FechaFactura"; //colum
            ColumYVal[i++] = String.valueOf(unaLinea.getFechaFactura()); //val

        }
        return ColumYVal;
    }


    private String[] obtenerDatos(OrderTo inO) {
        // TODO Auto-generated method stub
        String[] ColumYVal = new String[14];
        int i=0;
        if (inO != null){
            ColumYVal[i++] = "AD_Client_ID"; //colum
            ColumYVal[i++] = String.valueOf(inO.getAdClientId()); //val

            ColumYVal[i++] = "AD_Org_ID"; //colum
            ColumYVal[i++] = String.valueOf(inO.getAdOrgId()); //val

            ColumYVal[i++] = "C_Order_ID"; //colum
            ColumYVal[i++] = String.valueOf(inO.getOrderId()); //val

            ColumYVal[i++] = "C_BPartner_ID"; //colum
            ColumYVal[i++] = String.valueOf(inO.getPartnerId()); //val

            ColumYVal[i++] = "MovementDate"; //colum
            ColumYVal[i++] = String.valueOf(inO.getDate()); //val /////Parsear para fecha

            ColumYVal[i++] = "M_Warehouse_ID"; //colum
            ColumYVal[i++] = String.valueOf(inO.getWarehouseId()); //val

            ColumYVal[i++] = "DeviceID"; //colum
            ColumYVal[i++] = String.valueOf(inO.getDeviceId()); //val
        }
        return ColumYVal;
    }



}
