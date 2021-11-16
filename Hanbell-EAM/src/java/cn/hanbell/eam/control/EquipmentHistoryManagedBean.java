/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.EquipmentRepairBean;
import cn.hanbell.eam.ejb.EquipmentRepairFileBean;
import cn.hanbell.eam.ejb.EquipmentRepairHelpersBean;
import cn.hanbell.eam.ejb.EquipmentRepairHisBean;
import cn.hanbell.eam.ejb.EquipmentRepairSpareBean;
import cn.hanbell.eam.ejb.EquipmentSpareRecodeDtaBean;
import cn.hanbell.eam.ejb.EquipmentTroubleBean;
import cn.hanbell.eam.ejb.SysCodeBean;
import cn.hanbell.eam.entity.EquipmentRepair;
import cn.hanbell.eam.entity.EquipmentRepairFile;
import cn.hanbell.eam.entity.EquipmentRepairHelpers;
import cn.hanbell.eam.entity.EquipmentRepairHis;
import cn.hanbell.eam.entity.EquipmentRepairSpare;
import cn.hanbell.eam.entity.EquipmentSpareRecodeDta;
import cn.hanbell.eam.entity.EquipmentTrouble;
import cn.hanbell.eam.entity.SysCode;
import cn.hanbell.eam.lazy.EquipmentRepairModel;
import cn.hanbell.eam.web.FormMulti3Bean;
import cn.hanbell.eap.ejb.SystemUserBean;
import cn.hanbell.eap.entity.SystemUser;
import com.lightshell.comm.BaseLib;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 *
 * @author C2079
 */
@ManagedBean(name = "equipmentHistoryManagedBean")
@SessionScoped
public class EquipmentHistoryManagedBean extends FormMulti3Bean<EquipmentRepair, EquipmentRepairFile, EquipmentRepairSpare, EquipmentRepairHis> {

    @EJB
    protected EquipmentRepairBean equipmentRepairBean;
    @EJB
    protected EquipmentRepairFileBean equipmentRepairFileBean;
    @EJB
    protected EquipmentRepairSpareBean equipmentRepairSpareBean;
    @EJB
    protected EquipmentRepairHisBean equipmentRepairHisBean;
    @EJB
    private SystemUserBean systemUserBean;
    @EJB
    private EquipmentTroubleBean equipmentTroubleBean;
    @EJB
    private SysCodeBean sysCodeBean;
    @EJB
    private EquipmentRepairHelpersBean equipmentRepairHelpersBean;
    @EJB
    private EquipmentSpareRecodeDtaBean equipmentSpareRecodeDtaBean;
    private String queryEquipmentName;
    private String imageName;
    private String maintenanceSupervisor;
    private String queryServiceuser;
    private String queryDeptname;
    private double maintenanceCosts;
    private double totalCost;
    private String queryRepairuser;
    private String queryHitchsort1;
    private String queryHitchdesc;
    private String queryHitchreason;
    private String queryRepairprocess;
    private String queryHitchalarm;
    private String queryFaultTime;
    private String queryArchive;
    private List<SysCode> hitchurgencyList;
    private List<SysCode> abrasehitchList;
    private List<EquipmentTrouble> equipmentTroubleList;
    protected List<EquipmentRepairHelpers> detailList4;
    private EquipmentRepairHelpers currentDetail4;
    private List<EquipmentSpareRecodeDta> eDtaList;

    public EquipmentHistoryManagedBean() {
        super(EquipmentRepair.class, EquipmentRepairFile.class, EquipmentRepairSpare.class, EquipmentRepairHis.class);
    }

    //初始化数据筛选
    @Override
    public void init() {
        openParams = new HashMap<>();
        superEJB = equipmentRepairBean;
        model = new EquipmentRepairModel(equipmentRepairBean, userManagedBean);
        detailEJB = equipmentRepairFileBean;
        detailEJB2 = equipmentRepairSpareBean;
        detailEJB3 = equipmentRepairHisBean;
        queryState = "95";
        equipmentTroubleList = equipmentTroubleBean.findAll();
        model.getFilterFields().put("rstatus", queryState);
        model.getFilterFields().put("company", userManagedBean.getCompany());
        model.getSortFields().put("hitchtime", "DESC");
        super.init();
    }

    @Override
    public String view(String path) {
        if (currentEntity.getServicearrivetime() != null) {
            //获取联络时间
            currentEntity.setContactTime(this.getTimeDifference(currentEntity.getServicearrivetime(), currentEntity.getHitchtime(), 0));
        }
        if (currentEntity.getCompletetime() != null && currentEntity.getServicearrivetime() != null && currentEntity.getExcepttime() != null) {
            //获取维修时间
            currentEntity.setMaintenanceTime(this.getTimeDifference(currentEntity.getCompletetime(), currentEntity.getServicearrivetime(), currentEntity.getExcepttime()));
        }
        //获取总的停机时间
        if (currentEntity.getExcepttime() != null) {
            currentEntity.setDowntime(this.getTimeDifference(currentEntity.getCompletetime(), currentEntity.getHitchtime(), currentEntity.getExcepttime()));
        }
        String deptno = sysCodeBean.findBySyskindAndCode(userManagedBean.getCompany(),"RD", "repairleaders").getCvalue();
        maintenanceSupervisor = systemUserBean.findByUserId(deptno).getUsername();
        detailList4 = equipmentRepairHelpersBean.findByPId(currentEntity.getFormid());
        getPartsCost();
        hitchurgencyList = sysCodeBean.getTroubleNameList(userManagedBean.getCompany(), "RD", "hitchurgency");
        //获取故障责任原因
        abrasehitchList = sysCodeBean.getTroubleNameList(userManagedBean.getCompany(), "RD", "dutycause");
        eDtaList = equipmentSpareRecodeDtaBean.getEquipmentSpareRecodeDtaList(currentEntity.getFormid());
        calculateTotalCost();
        return super.view(path); //To change body of generated methods, choose Tools | Templates.
    }
//获取停机时间

    public void getDowntimes() {
        if (currentEntity.getExcepttime() != null) {
            currentEntity.setDowntime(this.getTimeDifference(currentEntity.getCompletetime(), currentEntity.getHitchtime(), currentEntity.getExcepttime()));
        }
    }

//获取零件费用
    public Double getPartsCost() {
        maintenanceCosts = 0;
        detailList2.forEach(equipmentrepair -> {
            BigDecimal price = equipmentrepair.getUprice();
            maintenanceCosts += equipmentrepair.getQty().doubleValue() * price.doubleValue();
        });
        return maintenanceCosts;
    }

    /**
     * 查询数据条件
     */
    @Override
    public void query() {
        if (model != null) {
            this.model.getFilterFields().clear();

            if (queryDateBegin != null) {
                model.getFilterFields().put("formdateBegin", queryDateBegin);
            }

            if (queryDateEnd != null) {
                model.getFilterFields().put("formdateEnd", queryDateEnd);
            }
            if (queryFormId != null && !"".equals(queryFormId)) {
                model.getFilterFields().put("formid", queryFormId);
            }
            if (queryName != null && !"".equals(queryName)) {
                model.getFilterFields().put("assetno.formid", queryName);
            }
            if (queryEquipmentName != null && !"".equals(queryEquipmentName)) {
                if (queryEquipmentName.equals("其") || queryEquipmentName.equals("他") || queryEquipmentName.equals("其他")) {
                    model.getFilterFields().put("itemno =", "9");
                } else {
                    model.getFilterFields().put("assetno.assetDesc", queryEquipmentName);
                }
            }
            if (queryDeptname != null && !"".equals(queryDeptname)) {
                model.getFilterFields().put("repairdeptname", queryDeptname);
            }
            if (queryServiceuser != null && !"".equals(queryServiceuser)) {
                model.getFilterFields().put("serviceusername", queryServiceuser);
            }
            if (queryRepairuser != null && !"".equals(queryRepairuser)) {
                model.getFilterFields().put("repairusername", queryRepairuser);
            }
            if (queryHitchdesc != null && !"".equals(queryHitchdesc)) {
                model.getFilterFields().put("hitchdesc", queryHitchdesc);
            }
            if (queryHitchreason != null && !"".equals(queryHitchreason)) {
                model.getFilterFields().put("hitchreason", queryHitchreason);
            }
            if (queryHitchalarm != null && !"".equals(queryHitchalarm)) {
                model.getFilterFields().put("hitchalarm", queryHitchalarm);
            }
            if (queryRepairprocess != null && !"".equals(queryRepairprocess)) {
                model.getFilterFields().put("repairprocess", queryRepairprocess);
            }

            if (queryHitchsort1 != null && !"ALL".equals(queryHitchsort1)) {
                this.model.getFilterFields().put("hitchsort1", queryHitchsort1);
            }
            model.getFilterFields().put("company", userManagedBean.getCompany());
            if (!"NULL".equals(queryState)) {
                model.getFilterFields().put("rstatus", queryState);
            } else {
                model.getFilterFields().put("rstatus <=", "95");
            }
            if (!"NULL".equals(queryArchive)) {
                model.getFilterFields().put("repairarchive", queryArchive);
            }
            model.getSortFields().put("hitchtime", "DESC");

        }
    }

    //计算总费用
    public void calculateTotalCost() {
        totalCost = 0;
        if (currentEntity.getRepaircost() != null) {
            totalCost += currentEntity.getRepaircost().doubleValue();
        }
        if (currentEntity.getLaborcosts() != null) {
            totalCost += currentEntity.getLaborcosts().doubleValue();
        }
        if (currentEntity.getSparecost() != null) {
            totalCost += currentEntity.getSparecost().doubleValue();
        }
    }

    //获取故障来源
    public String getTroubleName(String cValue) {
        SysCode sysCode = sysCodeBean.getTroubleName("RD", "faultType", cValue);
        String troubleName = "";
        if (sysCode == null) {
            return troubleName;
        }
        troubleName = sysCode.getCdesc();
        return troubleName;
    }

    //获取显示的进度
    public String getStateName(String str) {
        switch (str) {
            case "10":
                return "已报修";
            case "15":
                return "已受理";
            case "20":
                return "维修到达";
            case "25":
                return "维修中";
            case "28":
                return "维修暂停";
            case "30":
                return "维修完成";
            case "40":
                return "维修验收";
            case "50":
                return "责任回复";
            case "60":
                return "课长审核";
            case "70":
                return "经理审核";
            case "95":
                return "报修结案";
            case "98":
                return "已作废";
            default:
                return "";
        }
    }
    //获取故障紧急度

    public String getHitchurgency(String cValue) {
        SysCode sysCode = sysCodeBean.getTroubleName("RD", "hitchurgency", cValue);
        String troubleName = "";
        if (sysCode == null) {
            return troubleName;
        }
        troubleName = sysCode.getCdesc();
        return troubleName;
    }
//处理俩时间显示的格式

    public String getTimeDifference(Date strDate, Date endDate, int downTimes) {
        long l = strDate.getTime() - endDate.getTime();
        long day = l / (24 * 60 * 60 * 1000);
        long hour = (l / (60 * 60 * 1000) - day * 24);
        long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
        if (downTimes != 0) {
            int days = downTimes / (60 * 24);
            downTimes -= days * 60 * 24;
            int hours = (int) Math.floor(downTimes / 60);
            downTimes -= days * 60;
            int minute = (int) (downTimes % 60);
            day = day - days;

            hour = hour - hours;
            if (hour < 0) {
                day = day - 1;
                hour = hour + 24;
            }
            min = min - minute;
            if (min < 0) {
                hour = hour - 1;
                min = min + 60;
            }
            if (day < 0) {
                hour = 0;
                min = 0;
                day = 0;

            }
            if (hour < 0) {
                hour = 0;
                min = 0;
                day = 0;
            }

        }
        if (day > 0) {
            hour += 24 * day;
        }
        return hour *60+ min + "分";
    }
//导出界面的EXCEL数据处理

    @Override
    public void print() throws ParseException {

        fileName = "维修履历表" + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
        String fileFullName = reportOutputPath + fileName;
        HSSFWorkbook workbook = new HSSFWorkbook();
        //获得表格样式
        Map<String, CellStyle> style = createStyles(workbook);
        // 生成一个表格
        HSSFSheet sheet1 = workbook.createSheet("Sheet1");
        // 设置表格宽度
        int[] wt1 = getInventoryWidth();

        //创建标题行
        Row row;
        Row row2;
        Row row3;
        String[] title1 = getInventoryTitle();
        String[] title2 = getInventoryTitle2();
        row = sheet1.createRow(1);
        row2 = sheet1.createRow(2);
        row3 = sheet1.createRow(0);
        for (int k = 0; k < title2.length; k++) {
            sheet1.autoSizeColumn(k);
        }

        sheet1.getRow(1).setHeightInPoints(20);

        for (int i = 0; i < 6; i++) {
            sheet1.setColumnWidth(i, 12 * 256);
        }

        for (int i = 0; i < title1.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(style.get("head"));
            cell.setCellValue(title1[i]);
        }
        for (int i = 0; i < title2.length; i++) {
            Cell cell = row2.createCell(i);
            cell.setCellStyle(style.get("head"));
            cell.setCellValue(title2[i]);
        }
        sheet1.addMergedRegion(new CellRangeAddress(0, 0, 0, 25));
        Cell cellTitle = row3.createCell(0);
        cellTitle.setCellStyle(style.get("title"));
        SimpleDateFormat date = new SimpleDateFormat("yyyy年MM月dd日");
        if (queryDateBegin != null && queryDateEnd != null) {
            cellTitle.setCellValue(date.format(queryDateBegin) + "-----" + date.format(queryDateEnd) + "  维修履历表");
        } else {
            cellTitle.setCellValue("维修履历表");
        }

        //合并单元格
        for (int i = 0; i < 8; i++) {
            sheet1.addMergedRegion(new CellRangeAddress(1, 2, i, i));
        }
        sheet1.addMergedRegion(new CellRangeAddress(1, 1, 8, 13));
        sheet1.addMergedRegion(new CellRangeAddress(1, 1, 14, 17));
        sheet1.addMergedRegion(new CellRangeAddress(1, 1, 18, 20));
        sheet1.addMergedRegion(new CellRangeAddress(1, 1, 21, 25));
        //设置宽度
        for (int i = 0; i < wt1.length; i++) {
            sheet1.setColumnWidth(i, wt1[i] * 256);
        }
        //设置第二行行高
        row2.setHeight((short) (18 * 40));
        List<EquipmentRepair> equipmentrepairList = equipmentRepairBean.getEquipmentRepairList(model.getFilterFields(), model.getSortFields());
        int j = 3;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (EquipmentRepair equipmentrepair : equipmentrepairList) {
            if (!queryFaultTime.equals("NULL")) {
                //从对象中拿到时间
                long hitchtime = sdf.parse(sdf.format(equipmentrepair.getHitchtime())).getTime();
                long completetime = sdf.parse(sdf.format(equipmentrepair.getCompletetime())).getTime();
                long diff = (completetime - hitchtime) / 1000 / 60;
                if (queryFaultTime.equals("1")) {
                    if (diff <= 10) {
                        continue;
                    }
                } else if (queryFaultTime.equals("0")) {
                    if (diff > 10) {
                        continue;
                    }
                }
            }
            row = sheet1.createRow(j);
            j++;
            row.setHeight((short) 400);
            Cell cell0 = row.createCell(0);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(equipmentrepair.getFormid());
            Cell cell1 = row.createCell(1);
            cell1.setCellStyle(style.get("cell"));
            if (equipmentrepair.getAssetno() != null) {
                cell1.setCellValue(equipmentrepair.getAssetno().getFormid());
            }
            Cell cell2 = row.createCell(2);
            cell2.setCellStyle(style.get("cell"));
            cell2.setCellValue(equipmentrepair.getItemno());
            Cell cell3 = row.createCell(3);
            cell3.setCellStyle(style.get("cell"));

            cell3.setCellValue(equipmentrepair.getAssetno() == null ? "其他" : equipmentrepair.getAssetno().getAssetDesc());
            Cell cell4 = row.createCell(4);
            cell4.setCellStyle(style.get("cell"));
            cell4.setCellValue(equipmentrepair.getRepairusername());
            Cell cell5 = row.createCell(5);
            cell5.setCellStyle(style.get("cell"));
            cell5.setCellValue(equipmentrepair.getAssetno() == null ? equipmentrepair.getRepairdeptname() : equipmentrepair.getAssetno().getDeptname());

            Cell cell6 = row.createCell(6);
            cell6.setCellStyle(style.get("cell"));
            cell6.setCellValue(getStateName(equipmentrepair.getRstatus()));

            Cell cell7 = row.createCell(7);
            cell7.setCellStyle(style.get("cell"));
            cell7.setCellValue(equipmentrepair.getServiceusername());
            String credate = sdf.format(equipmentrepair.getHitchtime().getTime());

            Cell cell8 = row.createCell(8);
            cell8.setCellStyle(style.get("cell"));
            cell8.setCellValue(credate);

            Cell cell9 = row.createCell(9);
            cell9.setCellStyle(style.get("cell"));
            if (equipmentrepair.getServicearrivetime() != null) {
                String servicearrivetime = sdf.format(equipmentrepair.getServicearrivetime().getTime());
                cell9.setCellValue(servicearrivetime);
            }

            Cell cell10 = row.createCell(10);
            cell10.setCellStyle(style.get("cell"));
            if (equipmentrepair.getCompletetime() != null) {
                String completetime = sdf.format(equipmentrepair.getCompletetime().getTime());
                cell10.setCellValue(completetime);
            }
            Cell cell11 = row.createCell(11);
            cell11.setCellStyle(style.get("cell"));
            if (equipmentrepair.getExcepttime() != null) {
                cell11.setCellValue(equipmentrepair.getExcepttime());
            }
            Cell cell12 = row.createCell(12);
            cell12.setCellStyle(style.get("cell"));
            if (equipmentrepair.getStopworktime() != null) {
                cell12.setCellValue(equipmentrepair.getStopworktime());
            }

            Cell cell13 = row.createCell(13);
            cell13.setCellStyle(style.get("cell"));
            cell13.setCellStyle(style.get("right"));
            if (equipmentrepair.getCompletetime() != null && equipmentrepair.getServicearrivetime() != null) {
                cell13.setCellValue(getTimeDifference(equipmentrepair.getCompletetime(), equipmentrepair.getServicearrivetime(), 0));
            }
            Cell cell14 = row.createCell(14);
            cell14.setCellStyle(style.get("cell"));
            cell14.setCellValue(getTroubleName(equipmentrepair.getTroublefrom()));

            Cell cell15 = row.createCell(15);
            cell15.setCellStyle(style.get("left"));
            cell15.setCellValue(equipmentrepair.getHitchalarm());

            Cell cell16 = row.createCell(16);
            cell16.setCellStyle(style.get("cell"));
            if (equipmentrepair.getHitchurgency() != null && !equipmentrepair.getHitchurgency().equals("NULL")) {
                String hitchtype = sysCodeBean.getTroubleName("RD", "hitchurgency", equipmentrepair.getHitchurgency()).getCdesc();
                cell16.setCellValue(hitchtype);
            }

            Cell cell17 = row.createCell(17);
            cell17.setCellStyle(style.get("cell"));
            if (equipmentrepair.getHitchsort1() != null) {
                String trouble = equipmentTroubleBean.findByTroubleid(equipmentrepair.getHitchsort1()).getTroublename();
                cell17.setCellValue(trouble);
            }

            Cell cell18 = row.createCell(19);
            cell18.setCellStyle(style.get("left"));
            cell18.setCellValue(equipmentrepair.getRepairmethod());

            Cell cell19 = row.createCell(18);
            cell19.setCellStyle(style.get("left"));
            cell19.setCellValue(equipmentrepair.getHitchreason());

            Cell cell20 = row.createCell(20);
            cell20.setCellStyle(style.get("left"));
            cell20.setCellValue(equipmentrepair.getRepairprocess());
            detailList2 = equipmentRepairSpareBean.findByPId(equipmentrepair.getFormid());
            maintenanceCosts = 0;
            detailList2.forEach(equipmentrepair1 -> {
                BigDecimal price = equipmentrepair1.getUprice();
                maintenanceCosts += equipmentrepair1.getQty().doubleValue() * price.doubleValue();
            });
            Cell cell21 = row.createCell(21);
            cell21.setCellStyle(style.get("cell"));
            cell21.setCellValue(maintenanceCosts);

            Cell cell22 = row.createCell(22);
            cell22.setCellStyle(style.get("cell"));
            double laborcost = 0;

            if (equipmentrepair.getLaborcosts() != null) {
                laborcost = equipmentrepair.getLaborcosts().doubleValue();
            }
            cell22.setCellValue(laborcost);

            Cell cell23 = row.createCell(23);
            cell23.setCellStyle(style.get("cell"));
            double repaircost = 0;
            if (equipmentrepair.getRepaircost() != null) {
                repaircost = equipmentrepair.getRepaircost().doubleValue();
            }
            cell23.setCellValue(repaircost);
            double totalCost = maintenanceCosts + repaircost + laborcost;
            Cell cell24 = row.createCell(24);
            cell24.setCellStyle(style.get("cell"));
            double cost = maintenanceCosts + repaircost;
            cell24.setCellValue(cost);

            Cell cell25 = row.createCell(25);
            cell25.setCellStyle(style.get("cell"));
            cell25.setCellValue(totalCost);
        }
        OutputStream os = null;
        try {
            os = new FileOutputStream(fileFullName);
            workbook.write(os);
            this.reportViewPath = reportViewContext + fileName;
            this.preview();
        } catch (Exception ex) {
            showErrorMsg("Error", ex.getMessage());
        } finally {
            try {
                if (null != os) {
                    os.flush();
                    os.close();
                }
            } catch (IOException ex) {
                showErrorMsg("Error", ex.getMessage());
            }
        }
    }
//导出单台设备数据

    public void printSingleData() throws ParseException {
        String finalFilePath = "";
        try {
            finalFilePath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            int index = finalFilePath.indexOf("WEB-INF");

            InputStream is = new FileInputStream(finalFilePath.substring(1, index) + "rpt/设备故障单台履历模板.xls");
            Workbook workbook = WorkbookFactory.create(is);
            //获得表格样式
            Map<String, CellStyle> style = createStyles(workbook);
            Sheet sheet;
            sheet = workbook.getSheetAt(0);
            SimpleDateFormat date = new SimpleDateFormat("yyyy年MM月dd日");
            SimpleDateFormat date2 = new SimpleDateFormat("yyyy/MM/dd HH:ss");
            if (currentEntity == null) {
                showErrorMsg("Error", "请先选择数据！！！");
                return;
            }
            Cell cellTitle = sheet.getRow(3).getCell(6);
            cellTitle.setCellValue(date.format(getDate()));
            cellTitle = sheet.getRow(4).getCell(2);
            cellTitle.setCellValue(currentEntity.getItemno().equals("9") ? "其他" : currentEntity.getAssetno().getAssetDesc());
            cellTitle = sheet.getRow(4).getCell(11);
            cellTitle.setCellValue(currentEntity.getAssetno().getDeptname());
            cellTitle = sheet.getRow(4).getCell(6);
            cellTitle.setCellValue(currentEntity.getAssetno().getFormid());
            cellTitle = sheet.getRow(5).getCell(2);
            cellTitle.setCellValue(date2.format(currentEntity.getHitchtime()));
            cellTitle = sheet.getRow(5).getCell(9);
            cellTitle.setCellValue(date2.format(currentEntity.getCompletetime()));

            if (currentEntity.getHitchalarm() != null && !"".equals(currentEntity.getHitchalarm())) {
                cellTitle = sheet.getRow(6).getCell(1);
                cellTitle.setCellValue("故障内容: " + currentEntity.getHitchalarm());
            }
            if (currentEntity.getHitchdesc() != null && !"".equals(currentEntity.getHitchdesc())) {
                cellTitle = sheet.getRow(8).getCell(1);
                cellTitle.setCellValue("故障详情:" + currentEntity.getHitchdesc());
            }

            Drawing patriarch = sheet.createDrawingPatriarch();
            List<EquipmentRepairFile> equipmentRepairFiles = equipmentRepairFileBean.findByPId(currentEntity.getFormid());
            FileInputStream fileInputStream = null;
            int a = 0;
            int b = 0;
            int repairSize = 0;
            int serviceSize = 0;
            //得道维修图片和报修图片的数量
            for (EquipmentRepairFile equipmentRepairFile : equipmentRepairFiles) {
                if (equipmentRepairFile.getFilefrom().equals("报修图片")) {
                    //清空文字
                    cellTitle = sheet.getRow(9).getCell(1);
                    cellTitle.setCellValue(" ");
                    repairSize++;
                } else if (equipmentRepairFile.getFilefrom().equals("维修图片")) {
                    //清空文字
                    cellTitle = sheet.getRow(19).getCell(1);
                    cellTitle.setCellValue(" ");
                    serviceSize++;
                }
            }
            for (int i = 0; i < equipmentRepairFiles.size(); i++) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                String file = equipmentRepairFiles.get(i).getFilename();
                file = file.substring(file.length() - 3, file.length());//获取文件格式
                if (!file.equals("jpg") && !file.equals("png")) {
                    continue;
                }
                int index2 = equipmentRepairFiles.get(i).getFilepath().indexOf("res/");
                //获取完整文件路径
                String path = finalFilePath.substring(1, index) + "resources/app/" + equipmentRepairFiles.get(i).getFilepath().substring(index2, equipmentRepairFiles.get(i).getFilepath().length());
                File files = new File(path);
                //判断图片是否存着，不存在则进行下次循环
                if (!files.exists() && !files.isDirectory()) {
                    continue;
                }
                fileInputStream = new FileInputStream(files);//输入文件流
                byte[] data = new byte[1024];
                int len = 0;
                while ((len = fileInputStream.read(data)) != -1) {
                    outputStream.write(data, 0, len);//读取文件流
                }

                // anchor主要用于设置图片的属性
                HSSFClientAnchor anchor2 = new HSSFClientAnchor(0, 125, 1023, 255, (short) 10, 1 + i * 10, (short) (80), (8 + i * 10));
                if (equipmentRepairFiles.get(i).getFilefrom().equals("报修图片")) {
                    if (repairSize > 3) {
                        anchor2.setCol1(a * 2 + 1);
                        anchor2.setCol2(2 * a + 2);
                        anchor2.setRow1((9));
                        anchor2.setRow2((11));
                    } else {
                        anchor2.setCol1(a * 3 + 1);
                        anchor2.setCol2(3 * a + 3);
                        anchor2.setRow1((9));
                        anchor2.setRow2((11));
                    }
                    a++;
                } else if (equipmentRepairFiles.get(i).getFilefrom().equals("维修图片")) {
                    if (serviceSize > 3) {
                        anchor2.setCol1(b * 2 + 1);
                        anchor2.setCol2(2 * b + 2);
                        anchor2.setRow1((19));
                        anchor2.setRow2((22));
                    } else {
                        anchor2.setCol1(b * 3 + 1);
                        anchor2.setCol2(3 * b + 3);
                        anchor2.setRow1((19));
                        anchor2.setRow2((22));
                    }

                    b++;
                }
                patriarch.createPicture(anchor2, workbook.addPicture(outputStream.toByteArray(), HSSFWorkbook.PICTURE_TYPE_JPEG));
                outputStream.close();
            }
            if (currentEntity.getAssetno().getUsername() != null) {
                cellTitle = sheet.getRow(11).getCell(12);
                cellTitle.setCellValue(currentEntity.getRepairusername());
            }
            cellTitle = sheet.getRow(9).getCell(12);
            cellTitle.setCellValue(currentEntity.getAssetno().getUsername());
            if (currentEntity.getRepairmethod() != null && !"".equals(currentEntity.getRepairmethod())) {
                cellTitle = sheet.getRow(12).getCell(1);
                cellTitle.setCellValue("维修作业方式:" + currentEntity.getRepairmethod());
            }
            if (currentEntity.getHitchreason() != null && !"".equals(currentEntity.getHitchreason())) {
                cellTitle = sheet.getRow(14).getCell(1);
                cellTitle.setCellValue("故障现状过程及原因:" + currentEntity.getHitchreason());
            }
            if (currentEntity.getRepairprocess() != null && !"".equals(currentEntity.getRepairprocess())) {
                cellTitle = sheet.getRow(15).getCell(1);
                cellTitle.setCellValue("再发防止对策:" + currentEntity.getRepairprocess());
            }
            if (currentEntity.getMeasure() != null && !"".equals(currentEntity.getMeasure())) {
                cellTitle = sheet.getRow(16).getCell(1);
                cellTitle.setCellValue("预防措施:" + currentEntity.getMeasure());
            }
            cellTitle = sheet.getRow(19).getCell(12);
            cellTitle.setCellValue(currentEntity.getStopworktime());
            cellTitle = sheet.getRow(20).getCell(12);
            cellTitle.setCellValue(currentEntity.getMaintenanceTime());
            cellTitle = sheet.getRow(21).getCell(12);
            cellTitle.setCellValue(totalCost);
            cellTitle = sheet.getRow(22).getCell(12);
            cellTitle.setCellValue(currentEntity.getServiceusername());
            deletedDetailList3 = equipmentRepairHisBean.findByPId(currentEntity.getFormid());
            for (EquipmentRepairHis equipmentRepairHis : deletedDetailList3) {
                if (equipmentRepairHis.getCurnode().equals("课长审核")) {
                    cellTitle = sheet.getRow(23).getCell(1);
                    cellTitle.setCellValue(equipmentRepairHis.getNote());
                    cellTitle = sheet.getRow(23).getCell(12);
                    cellTitle.setCellValue(getUserName(equipmentRepairHis.getUserno()));
                }
                if (equipmentRepairHis.getCurnode().equals("经理审核")) {
                    if (totalCost > 5000) {
                        cellTitle = sheet.getRow(27).getCell(1);
                        cellTitle.setCellValue(equipmentRepairHis.getNote());
                        cellTitle = sheet.getRow(27).getCell(12);
                        cellTitle.setCellValue(getUserName(equipmentRepairHis.getUserno()));
                    }
                }
            }

            OutputStream os = null;
            fileName = "设备故障单台履历表" + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
            String fileFullName = reportOutputPath + fileName;
            try {
                os = new FileOutputStream(fileFullName);
                workbook.write(os);
                this.reportViewPath = reportViewContext + fileName;
                this.preview();
            } catch (Exception ex) {
                showErrorMsg("Error", ex.getMessage());
            } finally {
                try {
                    if (null != os) {
                        os.flush();
                        os.close();
                    }
                } catch (IOException ex) {
                    showErrorMsg("Error", ex.getMessage());
                }
            }
        } catch (IOException | InvalidFormatException e) {
            showErrorMsg("Error", e.toString());
        }
    }

    /**
     * 设置表头名称字段
     */
    private String[] getInventoryTitle() {

        return new String[]{"报修单号", "资产编号", "资产件号", "资产名称", "报修人", "使用部门", "进度", "维修人", "故障/停机时间", "", "", "", "", "", "故障", "", "", "", "原因及改善对策", "", "", "费用", "", "", "", ""};
    }

    /**
     * 设置表头名称字段2
     */
    private String[] getInventoryTitle2() {

        return new String[]{"", "", "", "", "", "", "", "", "报修时间", "维修到达时间", "维修完成时间", "非工作时间(分)", "停工时间(分)", "维修时间", "故障来源", "故障内容", "故障类型", "故障分类", "故障现状过程及原因", "维修作业方式", "再发防止对策", "备件费用", "人工成本", "其他费用", "总维修费用<不含人工费>", "总维修费用"};
    }

    /**
     * 设置单元格宽度
     */
    private int[] getInventoryWidth() {
        return new int[]{15, 20, 15, 15, 10, 15, 10, 10, 20, 20, 20, 10, 10, 10, 10, 20, 10, 10, 25, 25, 25, 10, 10, 10, 15, 10};
    }

    /**
     * 设置导出EXCEL表格样式
     */
    private Map<String, CellStyle> createStyles(Workbook wb) {
        Map<String, CellStyle> styles = new LinkedHashMap<>();
        // 文件头样式
        CellStyle headStyle = wb.createCellStyle();
        headStyle.setWrapText(true);//设置自动换行
        headStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        headStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        headStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());//单元格背景颜色
        headStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        headStyle.setBorderRight(CellStyle.BORDER_THIN);
        headStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        headStyle.setBorderLeft(CellStyle.BORDER_THIN);
        headStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        headStyle.setBorderTop(CellStyle.BORDER_THIN);
        headStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        headStyle.setBorderBottom(CellStyle.BORDER_THIN);
        headStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        Font headFont = wb.createFont();
        headFont.setFontHeightInPoints((short) 11);
        headFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        headStyle.setFont(headFont);
        styles.put("head", headStyle);

        // 正文样式
        CellStyle cellStyle = wb.createCellStyle();
        Font cellFont = wb.createFont();
        cellFont.setFontHeightInPoints((short) 10);
        cellStyle.setFont(cellFont);
        cellStyle.setWrapText(true);//设置自动换行
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());//单元格背景颜色
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderTop(CellStyle.BORDER_THIN);
        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        styles.put("cell", cellStyle);

        CellStyle leftStyle = wb.createCellStyle();
        leftStyle.setWrapText(true);//设置自动换行
        leftStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        leftStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        leftStyle.setBorderLeft(CellStyle.BORDER_THIN);
        leftStyle.setBorderBottom(CellStyle.BORDER_THIN);
        leftStyle.setBorderRight(CellStyle.BORDER_THIN);
        styles.put("left", leftStyle);

        CellStyle rightStyle = wb.createCellStyle();
        rightStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        rightStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        rightStyle.setBorderRight(CellStyle.BORDER_THIN);
        rightStyle.setBorderBottom(CellStyle.BORDER_THIN);
        styles.put("right", rightStyle);

        CellStyle titleStyle = wb.createCellStyle();
        titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        Font headFont2 = wb.createFont();
        headFont2.setFontHeightInPoints((short) 20);
        headFont2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        titleStyle.setFont(headFont2);
        styles.put("title", titleStyle);

        CellStyle dateStyle = wb.createCellStyle();
        dateStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        dateStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        Font dateFont = wb.createFont();
        dateFont.setFontHeightInPoints((short) 11);
        dateFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        dateStyle.setFont(headFont);
        styles.put("date", dateStyle);
        return styles;
    }

    //根据用户ID获取用户姓名
    public String getUserName(String userId) {
        SystemUser s = systemUserBean.findByUserId(userId);
        return s.getUsername();
    }

    public String getQueryEquipmentName() {
        return queryEquipmentName;
    }

    public void setQueryEquipmentName(String queryEquipmentName) {
        this.queryEquipmentName = queryEquipmentName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getMaintenanceSupervisor() {
        return maintenanceSupervisor;
    }

    public void setMaintenanceSupervisor(String maintenanceSupervisor) {
        this.maintenanceSupervisor = maintenanceSupervisor;
    }

    public String getQueryServiceuser() {
        return queryServiceuser;
    }

    public void setQueryServiceuser(String queryServiceuser) {
        this.queryServiceuser = queryServiceuser;
    }

    public String getQueryDeptname() {
        return queryDeptname;
    }

    public void setQueryDeptname(String queryDeptname) {
        this.queryDeptname = queryDeptname;
    }

    public List<EquipmentTrouble> getEquipmentTroubleList() {
        return equipmentTroubleList;
    }

    public void setEquipmentTroubleList(List<EquipmentTrouble> equipmentTroubleList) {
        this.equipmentTroubleList = equipmentTroubleList;
    }

    public double getMaintenanceCosts() {
        return maintenanceCosts;
    }

    public void setMaintenanceCosts(double maintenanceCosts) {
        this.maintenanceCosts = maintenanceCosts;
    }

    public String getQueryRepairuser() {
        return queryRepairuser;
    }

    public void setQueryRepairuser(String queryRepairuser) {
        this.queryRepairuser = queryRepairuser;
    }

    public String getQueryHitchsort1() {
        return queryHitchsort1;
    }

    public void setQueryHitchsort1(String queryHitchsort1) {
        this.queryHitchsort1 = queryHitchsort1;
    }

    public String getQueryHitchdesc() {
        return queryHitchdesc;
    }

    public void setQueryHitchdesc(String queryHitchdesc) {
        this.queryHitchdesc = queryHitchdesc;
    }

    public String getQueryHitchreason() {
        return queryHitchreason;
    }

    public void setQueryHitchreason(String queryHitchreason) {
        this.queryHitchreason = queryHitchreason;
    }

    public String getQueryRepairprocess() {
        return queryRepairprocess;
    }

    public void setQueryRepairprocess(String queryRepairprocess) {
        this.queryRepairprocess = queryRepairprocess;
    }

    public String getQueryHitchalarm() {
        return queryHitchalarm;
    }

    public void setQueryHitchalarm(String queryHitchalarm) {
        this.queryHitchalarm = queryHitchalarm;
    }

    public List<SysCode> getHitchurgencyList() {
        return hitchurgencyList;
    }

    public void setHitchurgencyList(List<SysCode> hitchurgencyList) {
        this.hitchurgencyList = hitchurgencyList;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public List<EquipmentRepairHelpers> getDetailList4() {
        return detailList4;
    }

    public void setDetailList4(List<EquipmentRepairHelpers> detailList4) {
        this.detailList4 = detailList4;
    }

    public EquipmentRepairHelpers getCurrentDetail4() {
        return currentDetail4;
    }

    public void setCurrentDetail4(EquipmentRepairHelpers currentDetail4) {
        this.currentDetail4 = currentDetail4;
    }

    public List<SysCode> getAbrasehitchList() {
        return abrasehitchList;
    }

    public void setAbrasehitchList(List<SysCode> abrasehitchList) {
        this.abrasehitchList = abrasehitchList;
    }

    public String getQueryFaultTime() {
        return queryFaultTime;
    }

    public void setQueryFaultTime(String queryFaultTime) {
        this.queryFaultTime = queryFaultTime;
    }

    public String getQueryArchive() {
        return queryArchive;
    }

    public void setQueryArchive(String queryArchive) {
        this.queryArchive = queryArchive;
    }

    public List<EquipmentSpareRecodeDta> geteDtaList() {
        return eDtaList;
    }

    public void seteDtaList(List<EquipmentSpareRecodeDta> eDtaList) {
        this.eDtaList = eDtaList;
    }

}
