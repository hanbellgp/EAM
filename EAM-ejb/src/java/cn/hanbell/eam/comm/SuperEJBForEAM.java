/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.comm;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.lightshell.comm.SuperEJB;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;
import static org.apache.axis.Constants.XSD_STRING;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.code128.Code128Constants;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

/**
 *
 * @author C0160
 */
public abstract class SuperEJBForEAM<T> extends SuperEJB<T> {

    protected final String url = "http://127.0.0.1:8480/WebService/SHBERPWebService";
    protected final String nameSpace = "http://jws.hanbell.cn/";

    protected final Logger log4j = LogManager.getLogger();

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

    public void generateCode128(String content, float widthSize, double height, String fullFileName) {
        try {
            Code128Bean bean = new Code128Bean();
            //设置解析度
            final int dpi = 150;
            //Configure the barcode generator
            bean.setModuleWidth(UnitConv.in2mm(widthSize / dpi)); //makes the narrow bar
            bean.setBarHeight(height);
            bean.setCodeset(Code128Constants.CODESET_A);
            bean.doQuietZone(false);
            //产生Code128文件
            File outputFile = new File(fullFileName);
            OutputStream out = new FileOutputStream(outputFile);
            try {
                //Set up the canvas provider
                BitmapCanvasProvider canvas = new BitmapCanvasProvider(
                        out, "image/png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
                //Generate the barcode
                bean.generateBarcode(canvas, content);
                //Signal end of generation
                canvas.finish();
            } finally {
                out.close();
            }
        } catch (Exception ex) {
            log4j.error(ex);
        }
    }

    public void generateQRCode(String content, int width, int height, String filePath, String fileName) {
        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            Map hints = new HashMap();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            Path path = FileSystems.getDefault().getPath(filePath, fileName);
            MatrixToImageWriter.writeToPath(bitMatrix, "png", path);
        } catch (WriterException | IOException ex) {
            log4j.error(ex);
        }
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
