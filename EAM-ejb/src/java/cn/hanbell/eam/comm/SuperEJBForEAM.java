/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.comm;

import com.lightshell.comm.SuperEJB;
import java.math.BigDecimal;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;
import static org.apache.axis.Constants.XSD_STRING;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

/**
 *
 * @author C0160
 */
public abstract class SuperEJBForEAM<T> extends SuperEJB<T> {

    protected final String url = "http://127.0.0.1:8480/WebService/SHBERPWebService";
    protected final String nameSpace = "http://jws.hanbell.cn/";

    protected String company = "C";

    @PersistenceContext(unitName = "PU-SHBEAM")
    private EntityManager em_shbeam;

    public SuperEJBForEAM(Class<T> entityClass) {
        super(entityClass);
    }

    @Override
    public EntityManager getEntityManager() {
        return em_shbeam;
    }

    public Call createAXISCall(String urlString) throws ServiceException {
        Service service = new Service();
        Call call = (Call) service.createCall();
        call.setTargetEndpointAddress(urlString);
        return call;
    }

    public String formatString(String value, String format) {
        if (value.length() >= format.length()) {
            return value;
        }
        return format.substring(0, format.length() - value.length()) + value;
    }

    public boolean isLessThenInvbal(String facno, String prono, String itnbr, String wareh, BigDecimal qty) throws Exception {
        //建立一个WebServices调用连接
        Call call = createAXISCall(url);
        call.setOperationName(new QName(nameSpace, "isLessThenInvbal"));
        Object[] params = new Object[]{facno, prono, itnbr, wareh, String.valueOf(qty)};
        call.addParameter("facno", XSD_STRING, ParameterMode.IN);
        call.addParameter("prono", XSD_STRING, ParameterMode.IN);
        call.addParameter("itnbr", XSD_STRING, ParameterMode.IN);
        call.addParameter("wareh", XSD_STRING, ParameterMode.IN);
        call.addParameter("qty", XSD_STRING, ParameterMode.IN);
        call.setReturnType(XSD_STRING);
        String ret = call.invoke(params).toString();
        return "200".equals(ret);
    }

    public boolean isLessThenInvbat(String facno, String prono, String itnbr, String wareh, String fixnr, String varnr, BigDecimal qty) throws Exception {
        //建立一个WebServices调用连接
        Call call = createAXISCall(url);
        call.setOperationName(new QName(nameSpace, "isLessThenInvbal"));
        Object[] params = new Object[]{facno, prono, itnbr, wareh, fixnr, varnr, String.valueOf(qty)};
        call.addParameter("facno", XSD_STRING, ParameterMode.IN);
        call.addParameter("prono", XSD_STRING, ParameterMode.IN);
        call.addParameter("itnbr", XSD_STRING, ParameterMode.IN);
        call.addParameter("wareh", XSD_STRING, ParameterMode.IN);
        call.addParameter("fixnr", XSD_STRING, ParameterMode.IN);
        call.addParameter("varnr", XSD_STRING, ParameterMode.IN);
        call.addParameter("qty", XSD_STRING, ParameterMode.IN);
        call.setReturnType(XSD_STRING);
        String ret = call.invoke(params).toString();
        return "200".equals(ret);
    }

}
