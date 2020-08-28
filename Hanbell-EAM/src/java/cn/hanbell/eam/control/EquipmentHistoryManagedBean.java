/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.EquipmentRepairBean;
import cn.hanbell.eam.ejb.EquipmentRepairFileBean;
import cn.hanbell.eam.ejb.EquipmentRepairHisBean;
import cn.hanbell.eam.ejb.EquipmentRepairSpareBean;
import cn.hanbell.eam.ejb.EquipmentTroubleBean;
import cn.hanbell.eam.ejb.SysCodeBean;
import cn.hanbell.eam.entity.EquipmentRepair;
import cn.hanbell.eam.entity.EquipmentRepairFile;
import cn.hanbell.eam.entity.EquipmentRepairHis;
import cn.hanbell.eam.entity.EquipmentRepairSpare;
import cn.hanbell.eam.entity.EquipmentTrouble;
import cn.hanbell.eam.entity.SysCode;
import cn.hanbell.eam.lazy.EquipmentRepairModel;
import cn.hanbell.eam.web.FormMulti3Bean;
import cn.hanbell.eap.ejb.SystemUserBean;
import cn.hanbell.eap.entity.SystemUser;
import com.lightshell.comm.BaseLib;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
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
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
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
    private String queryEquipmentName;
    private String imageName;
    private String maintenanceSupervisor;
    private String queryServiceuser;
    private String queryDeptname;
    private double maintenanceCosts;
    private String queryRepairuser;
    private String queryHitchsort1;
    private String queryHitchdesc;
    private String queryHitchreason;
    private String queryRepairprocess;
    private String queryHitchalarm;

    private List<EquipmentTrouble> equipmentTroubleList;

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
        model.getSortFields().put("credate", "DESC");
        super.init();
    }

    @Override
    public String view(String path) {
        if (currentEntity.getServicearrivetime() != null) {
            //获取联络时间
            currentEntity.setContactTime(this.getTimeDifference(currentEntity.getServicearrivetime(), currentEntity.getCredate(), 0));
        }
        if (currentEntity.getCompletetime() != null && currentEntity.getServicearrivetime() != null) {
            //获取维修时间
            currentEntity.setMaintenanceTime(this.getTimeDifference(currentEntity.getCompletetime(), currentEntity.getServicearrivetime(), 0));
        }
        //获取总的停机时间
        if (currentEntity.getExcepttime() != null) {
            currentEntity.setDowntime(this.getTimeDifference(currentEntity.getCompletetime(), currentEntity.getCredate(), currentEntity.getExcepttime()));
        }
        String deptno = sysCodeBean.findBySyskindAndCode("RD", "repairleaders").getCvalue();
        maintenanceSupervisor = systemUserBean.findByDeptno(deptno).get(0).getUsername();
        getPartsCost();

        return super.view(path); //To change body of generated methods, choose Tools | Templates.
    }
//获取停机时间

    public void getDowntimes() {
        if (currentEntity.getExcepttime() != null) {
            currentEntity.setDowntime(this.getTimeDifference(currentEntity.getCompletetime(), currentEntity.getCredate(), currentEntity.getExcepttime()));
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
                model.getFilterFields().put("assetno", queryName);
            }
            if (queryEquipmentName != null && !"".equals(queryEquipmentName)) {
                model.getFilterFields().put("itemno.assetDesc", queryEquipmentName);
            }
            if (queryDeptname != null && !"".equals(queryDeptname)) {
                model.getFilterFields().put("itemno.deptname", queryDeptname);
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
            }

            model.getSortFields().put("credate", "DESC");

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
        String queryStateName = "";
        switch (str) {
            case "10":
                queryStateName = "已报修";
                break;
            case "20":
                queryStateName = "维修到达";
                break;
            case "30":
                queryStateName = "维修完成";
                break;
            case "40":
                queryStateName = "维修验收";
                break;
            case "50":
                queryStateName = "维修审核";
                break;
            case "95":
                queryStateName = "报修结案";
                break;
            case "98":
                queryStateName = "作废";
                break;
        }
        return queryStateName;
    }
//处理俩时间显示的格式

    public String getTimeDifference(Date strDate, Date endDate, int downTimes) {
        long l = strDate.getTime() - endDate.getTime();
        long day = l / (24 * 60 * 60 * 1000);
        long hour = (l / (60 * 60 * 1000) - day * 24);
        long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
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
                s = 0;
            }
            if (hour < 0) {
                hour = 0;
                min = 0;
                day = 0;
                s = 0;
            }
        }
        return "" + day + "天" + hour + "小时" + min + "分";
    }
//导出界面的EXCEL数据处理

    @Override
    public void print() {

        fileName = "维修履历表" + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
        String fileFullName = reportOutputPath + fileName;
        HSSFWorkbook workbook = new HSSFWorkbook();
        //获得表格样式
        Map<String, CellStyle> style = createStyles(workbook);
        // 生成一个表格
        HSSFSheet sheet1 = workbook.createSheet("Sheet1");
        // 设置表格宽度
        int[] wt1 = getInventoryWidth();
        for (int i = 0; i < wt1.length; i++) {
            sheet1.setColumnWidth(i, wt1[i] * 256);
        }
        //创建标题行
        Row row;
        Row row2;
        String[] title1 = getInventoryTitle();
        String[] title2 = getInventoryTitle2();
        row = sheet1.createRow(0);
        row2 = sheet1.createRow(1);
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
        //合并单元格
        for (int i = 0; i < 8; i++) {
            sheet1.addMergedRegion(new CellRangeAddress(0, 1, i, i));
        }
        sheet1.addMergedRegion(new CellRangeAddress(0, 0, 8, 12));
        sheet1.addMergedRegion(new CellRangeAddress(0, 0, 13, 16));
        sheet1.addMergedRegion(new CellRangeAddress(0, 0, 17, 20));
        sheet1.addMergedRegion(new CellRangeAddress(0, 0, 21, 23));
        List<EquipmentRepair> equipmentrepairList = equipmentRepairBean.getEquipmentRepairList(model.getFilterFields(), model.getSortFields());
        int j = 2;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (EquipmentRepair equipmentrepair : equipmentrepairList) {
            row = sheet1.createRow(j);
            j++;
            row.setHeight((short) 400);
            Cell cell0 = row.createCell(0);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(equipmentrepair.getFormid());
            Cell cell1 = row.createCell(1);
            cell1.setCellStyle(style.get("cell"));
            cell1.setCellValue(equipmentrepair.getItemno().getFormid());
            Cell cell2 = row.createCell(2);
            cell2.setCellStyle(style.get("cell"));
            cell2.setCellValue(equipmentrepair.getItemno().getAssetItem().getItemno());
            Cell cell3 = row.createCell(3);
            cell3.setCellStyle(style.get("cell"));
            cell3.setCellValue(equipmentrepair.getItemno().getAssetDesc());
            Cell cell4 = row.createCell(4);
            cell4.setCellStyle(style.get("cell"));
            cell4.setCellValue(equipmentrepair.getItemno().getUsername());
            Cell cell5 = row.createCell(5);
            cell5.setCellStyle(style.get("cell"));
            cell5.setCellValue(equipmentrepair.getItemno().getDeptname());

            Cell cell6 = row.createCell(6);
            cell6.setCellStyle(style.get("cell"));
            cell6.setCellValue(getStateName(equipmentrepair.getRstatus()));

            Cell cell7 = row.createCell(7);
            cell7.setCellStyle(style.get("cell"));
            cell7.setCellValue(equipmentrepair.getServiceusername());
            String credate = sdf.format(equipmentrepair.getCredate().getTime());

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
            if (equipmentrepair.getCompletetime() != null && equipmentrepair.getServicearrivetime() != null) {
                cell12.setCellValue(getTimeDifference(equipmentrepair.getCompletetime(), equipmentrepair.getServicearrivetime(), 0));
            }
            Cell cell13 = row.createCell(13);
            cell13.setCellStyle(style.get("cell"));
            cell13.setCellValue(getTroubleName(equipmentrepair.getTroublefrom()));

            Cell cell14 = row.createCell(14);
            cell14.setCellStyle(style.get("cell"));
            cell14.setCellValue(equipmentrepair.getHitchalarm());

            Cell cell15 = row.createCell(15);
            cell15.setCellStyle(style.get("cell"));
            String hitchtype = "";
            if ("0".equals(equipmentrepair.getHitchtype())) {
                hitchtype = "一般故障";
            } else if ("1".equals(equipmentrepair.getHitchtype())) {
                hitchtype = "严重故障";
            }
            cell15.setCellValue(hitchtype);

            Cell cell16 = row.createCell(16);
            cell16.setCellStyle(style.get("cell"));
            if (equipmentrepair.getHitchsort1() != null) {
                String trouble = equipmentTroubleBean.findByTroubleid(equipmentrepair.getHitchsort1()).getTroublename();
                cell16.setCellValue(trouble);
            }

            Cell cell17 = row.createCell(17);
            cell17.setCellStyle(style.get("cell"));
            cell17.setCellValue(equipmentrepair.getRepairmethod());

            Cell cell18 = row.createCell(18);
            cell18.setCellStyle(style.get("cell"));
            cell18.setCellValue(equipmentrepair.getHitchreason());

            Cell cell19 = row.createCell(19);
            cell19.setCellStyle(style.get("cell"));
            cell19.setCellValue(equipmentrepair.getRepairprocess());
            detailList2 = equipmentRepairSpareBean.findByPId(equipmentrepair.getFormid());
            maintenanceCosts = 0;
            detailList2.forEach(equipmentrepair1 -> {
                BigDecimal price = equipmentrepair1.getUprice();
                maintenanceCosts += equipmentrepair1.getQty().doubleValue() * price.doubleValue();
            });
            Cell cell20 = row.createCell(20);
            cell20.setCellStyle(style.get("cell"));
            cell20.setCellValue(maintenanceCosts);

            Cell cell21 = row.createCell(21);
            cell21.setCellStyle(style.get("cell"));
            double laborcost = 0;
            if (equipmentrepair.getLaborcost() != null) {
                laborcost = equipmentrepair.getLaborcost().doubleValue();
            }
            cell21.setCellValue(laborcost);

            Cell cell22 = row.createCell(22);
            cell22.setCellStyle(style.get("cell"));
            double repaircost = 0;
            if (equipmentrepair.getRepaircost() != null) {
                repaircost = equipmentrepair.getRepaircost().doubleValue();
            }
            cell22.setCellValue(repaircost);
            double totalCost = maintenanceCosts + repaircost + laborcost;
            Cell cell23 = row.createCell(23);
            cell23.setCellStyle(style.get("cell"));
            cell23.setCellValue(totalCost);
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

    /**
     * 设置表头名称字段
     */
    private String[] getInventoryTitle() {

        return new String[]{"报修单号", "资产编号", "资产件号", "资产名称", "使用人", "使用部门", "进度", "维修人", "故障/停机时间", "", "", "", "", "故障", "", "", "", "原因及改善对策", "", "", "", "费用", "", ""};
    }

    /**
     * 设置表头名称字段2
     */
    private String[] getInventoryTitle2() {

        return new String[]{"", "", "", "", "", "", "", "", "报修时间", "维修到达时间", "维修完成时间", "非工作时间(分)", "维修时间", "故障来源", "故障报警", "故障类型", "故障分类", "维修方式说明", "故障判断过程及原因", "维修策略", "备件费用", "人工成本", "其他费用", "总维修费用"};
    }

    /**
     * 设置单元格宽度
     */
    private int[] getInventoryWidth() {
        return new int[]{15, 20, 15, 15, 10, 15, 10, 10, 20, 20, 20, 15, 10, 10, 10, 10, 20, 20, 25, 25, 20, 15, 15, 15, 15, 15};
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

}
