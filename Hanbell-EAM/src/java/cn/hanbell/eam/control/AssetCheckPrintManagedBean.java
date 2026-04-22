/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.entity.AssetCheck;
import com.lightshell.comm.BaseLib;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfReader;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "assetCheckPrintManagedBean")
@SessionScoped
public class AssetCheckPrintManagedBean extends AssetCheckManagedBean {

    /**
     * Creates a new instance of AssetCheckPrintManagedBean
     */
    public AssetCheckPrintManagedBean() {
    }

//    批量导出选择的PDF
    public void batchPrint() throws Exception {
        if (currentPrgGrant == null || currentPrgGrant.getSysprg().getRptclazz() == null) {
            showErrorMsg("Error", "系统配置错误无法打印");
            return;
        }
        if (entityList == null || entityList.isEmpty()) {
            showErrorMsg("Error", "没有可打印数据");
            return;
        }
        String reportName, outputName, reportFormat;
        //设置导出文件名称
        fileName = "AssetCheck" + BaseLib.formatDate("yyyyMMddHHmmss", getDate()) + ".pdf";
        String zipFileName = "AssetCheck" + BaseLib.formatDate("yyyyMMddHHmmss", getDate());

        String folderPath = reportOutputPath + zipFileName;
        // 创建File对象
        File folder = new File(folderPath);
        // 创建文件夹
        folder.mkdir();

        reportClassLoader = Class.forName(this.currentPrgGrant.getSysprg().getRptclazz()).getClassLoader();

        HashMap<String, Object> reportParams = new HashMap<>();
        ByteArrayOutputStream baos;
        for (AssetCheck c : entityList) {
            outputName = folderPath + "/" + c.getFormid() + "_" + c.getDeptno() + ".pdf";
            OutputStream os = new FileOutputStream(outputName);
            PdfCopyFields pdfCopy = new PdfCopyFields(os);
            //设置报表名称
            if (c.getCategory().getNoauto()) {
                reportName = reportPath + currentPrgGrant.getSysprg().getRptdesign();
            } else {
                reportName = reportPath + "assetcheckB.rptdesign";
            }
            //设置报表参数
            baos = new ByteArrayOutputStream();
            reportParams.put("company", userManagedBean.getCurrentCompany().getName());
            reportParams.put("companyFullName", userManagedBean.getCurrentCompany().getFullname());
            reportParams.put("id", c.getId());
            reportParams.put("formid", c.getFormid());
            reportParams.put("JNDIName", currentPrgGrant.getSysprg().getRptjndi());
            try {
                //初始配置
                this.reportInitAndConfig();
                //生成报表
                this.reportRunAndOutput(reportName, reportParams, null, "pdf", baos);
            } catch (Exception ex) {
                throw ex;
            } finally {
                reportParams.clear();
            }
            pdfCopy.addDocument(new PdfReader(baos.toByteArray()));
            pdfCopy.close();
        }

        String finalFilePath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        int index = finalFilePath.indexOf("WEB-INF");
        String path = finalFilePath.substring(1, index) + "rpt/output/" + zipFileName + ".zip";

        File folderZip = new File(folderPath);
        try {
            FileOutputStream fos = new FileOutputStream(folderPath + ".zip");
            ZipOutputStream zos = new ZipOutputStream(fos);
            addFolderToZip(folderZip, "", zos);
            this.reportViewPath = reportViewContext + zipFileName + ".zip";
            this.preview();
            zos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            deleteFolder(folderZip);//删除新增的文件夹

        }
    }

    public static boolean deleteFolder(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteFolder(file); // 递归删除子文件夹
                    } else {
                        file.delete(); // 删除文件
                    }
                }
            }
        }
        return dir.delete(); // 删除空文件夹本身
    }

    public static void addFolderToZip(File folder, String parentFolder, ZipOutputStream zos) throws IOException {
        byte[] buffer = new byte[1024];

        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                addFolderToZip(file, parentFolder + file.getName() + "/", zos);
            } else {
                FileInputStream fis = new FileInputStream(file);

                zos.putNextEntry(new ZipEntry(parentFolder + file.getName()));

                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }

                fis.close();
            }
        }
    }

    @Override
    public void print() throws Exception {
        if (currentPrgGrant == null || currentPrgGrant.getSysprg().getRptclazz() == null) {
            showErrorMsg("Error", "系统配置错误无法打印");
            return;
        }
        if (entityList == null || entityList.isEmpty()) {
            showErrorMsg("Error", "没有可打印数据");
            return;
        }
        String reportName, outputName, reportFormat;
        //设置导出文件名称
        fileName = "AssetCheck" + BaseLib.formatDate("yyyyMMddHHmmss", getDate()) + ".pdf";
        outputName = reportOutputPath + fileName;
        OutputStream os = new FileOutputStream(outputName);
        reportClassLoader = Class.forName(this.currentPrgGrant.getSysprg().getRptclazz()).getClassLoader();
        PdfCopyFields pdfCopy = new PdfCopyFields(os);
        HashMap<String, Object> reportParams = new HashMap<>();
        ByteArrayOutputStream baos;
        for (AssetCheck c : entityList) {
            //设置报表名称
            if (c.getCategory().getNoauto()) {
                reportName = reportPath + currentPrgGrant.getSysprg().getRptdesign();
            } else {
                reportName = reportPath + "assetcheckB.rptdesign";
            }
            //设置报表参数
            baos = new ByteArrayOutputStream();
            reportParams.put("company", userManagedBean.getCurrentCompany().getName());
            reportParams.put("companyFullName", userManagedBean.getCurrentCompany().getFullname());
            reportParams.put("id", c.getId());
            reportParams.put("formid", c.getFormid());
            reportParams.put("JNDIName", currentPrgGrant.getSysprg().getRptjndi());
            try {
                //初始配置
                this.reportInitAndConfig();
                //生成报表
                this.reportRunAndOutput(reportName, reportParams, null, "pdf", baos);
            } catch (Exception ex) {
                throw ex;
            } finally {
                reportParams.clear();
            }
            pdfCopy.addDocument(new PdfReader(baos.toByteArray()));
        }
        pdfCopy.close();
        this.reportViewPath = reportViewContext + fileName;
        this.preview();
    }

    public void print(String rptclazz, String rptdesign, String reportFormat) throws Exception {
        if (currentPrgGrant != null && currentPrgGrant.getDoprt()) {
            HashMap<String, Object> reportParams = new HashMap<>();
            reportParams.put("company", userManagedBean.getCurrentCompany().getName());
            reportParams.put("companyFullName", userManagedBean.getCurrentCompany().getFullname());
            reportParams.put("JNDIName", this.currentPrgGrant.getSysprg().getRptjndi());
            if (!this.model.getFilterFields().isEmpty()) {
                reportParams.put("filterFields", BaseLib.convertMapToStringWithClass(this.model.getFilterFields()));
            } else {
                reportParams.put("filterFields", "");
            }
            if (!this.model.getSortFields().isEmpty()) {
                reportParams.put("sortFields", BaseLib.convertMapToString(this.model.getSortFields()));
            } else {
                reportParams.put("sortFields", "");
            }
            this.fileName = this.currentPrgGrant.getSysprg().getApi() + BaseLib.formatDate("yyyyMMddHHss", this.getDate()) + "." + reportFormat;
            String reportName = reportPath + rptdesign;
            String outputName = reportOutputPath + this.fileName;
            this.reportViewPath = reportViewContext + this.fileName;
            try {
                if (this.currentPrgGrant != null && rptclazz != null) {
                    reportClassLoader = Class.forName(rptclazz).getClassLoader();
                }
                //初始配置
                this.reportInitAndConfig();
                //生成报表
                this.reportRunAndOutput(reportName, reportParams, outputName, reportFormat, null);
                //预览报表
                this.preview();
            } catch (Exception ex) {
                throw ex;
            }
        }
    }

    @Override
    public void verify() {
        if (entityList == null || entityList.isEmpty()) {
            showInfoMsg("Info", "没有可审核资料");
            return;
        }
        try {
            entityList.stream().forEach((ac) -> {
                AssetCheck t = assetCheckBean.findById(ac.getId());
                if (t != null && "N".equals(t.getStatus())) {
                    ac.setStatus("V");
                    ac.setCfmuser(userManagedBean.getCurrentUser().getUsername());
                    ac.setCfmdateToNow();
                    assetCheckBean.verify(ac);
                } else {
                    showWarnMsg("Warn", ac.getFormid() + "已审核或不存在");
                }
            });
            showInfoMsg("Info", "批量审核成功");
        } catch (Exception ex) {
            showErrorMsg("Error", ex.toString());
        }
    }

}
