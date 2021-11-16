/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.EquipmentRepairBean;
import cn.hanbell.eam.ejb.EquipmentRepairHelpersBean;
import cn.hanbell.eam.ejb.EquipmentRepairHisBean;
import cn.hanbell.eam.ejb.SysCodeBean;
import cn.hanbell.eam.entity.EquipmentRepair;
import cn.hanbell.eam.entity.EquipmentRepairHelpers;
import java.io.FileOutputStream;
import java.io.IOException;
import cn.hanbell.eam.web.FormMultiBean;
import cn.hanbell.eap.ejb.CompanyBean;
import cn.hanbell.eap.ejb.SystemUserBean;
import cn.hanbell.eap.entity.Company;
import cn.hanbell.eap.entity.SystemUser;
import com.lightshell.comm.BaseLib;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
@ManagedBean(name = "repairManHourDetailListManagedBean")
@SessionScoped
public class RepairManHourDetailListManagedBean extends FormMultiBean<EquipmentRepair, EquipmentRepairHelpers> {

    @EJB
    protected EquipmentRepairBean equipmentRepairBean;
    @EJB
    private CompanyBean companyBean;
    @EJB
    private SystemUserBean systemUserBean;
    @EJB
    private SysCodeBean sysCodeBean;
    @EJB
    protected EquipmentRepairHisBean equipmentRepairHisBean;

    @EJB
    private EquipmentRepairHelpersBean equipmentRepairHelpersBean;

    private List<String> usernameList;
    private List<SystemUser> systemUser;
    private String[] selectedUserName;
    private List<Company> companyList;
    private String[] company;

    public RepairManHourDetailListManagedBean() {
        super(EquipmentRepair.class, EquipmentRepairHelpers.class);
    }

    //初始化数据筛选
    @Override
    public void init() {
        superEJB = equipmentRepairBean;
        detailEJB = equipmentRepairHelpersBean;
        queryState = "ALL";
        String deptno = sysCodeBean.findBySyskindAndCode(userManagedBean.getCompany(),"RD", "repairDeptno").getCvalue();
        systemUser = systemUserBean.findByLikeDeptno("%" + deptno + "%");
        usernameList = new ArrayList<>();
        for (int i = 0; i < systemUser.size(); i++) {
            usernameList.add(systemUser.get(i).getUsername());
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        queryDateBegin = equipmentRepairBean.getMonthDay(1);//获取当前月第一天
        queryDateEnd = equipmentRepairBean.getMonthDay(0);//获取当前月最后一天
        companyList = companyBean.findBySystemName("EAM");
        selectedUserName = null;
        String[] str = new String[]{userManagedBean.getCompany()};//获得公司别
        company = str;//初始化公司别
        String comSql = " R.company= '" + company[0] + "'";//根据公司别查询数据的SQL条件
        detailList = equipmentRepairHelpersBean.getEquipmentRepairHelpersList(simpleDateFormat.format(queryDateBegin), simpleDateFormat.format(queryDateEnd), "", comSql);
    }

//导出界面的EXCEL数据处理
    @Override
    public void print() throws ParseException {

        fileName = "维修工时明细表" + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
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
        Row row1;
        Row row2;
        //表格一
        String[] title1 = getInventoryTitle();
        row = sheet1.createRow(0);
        row.setHeight((short) 900); 
        row1 = sheet1.createRow(1);
        row1.setHeight((short) 800);
        row2 = sheet1.createRow(2);
        row2.setHeight((short) 800);
        for (int i = 0; i < title1.length; i++) {
            Cell cell = row2.createCell(i);
            cell.setCellStyle(style.get("head"));
            cell.setCellValue(title1[i]);
        }

        if (detailList == null || detailList.isEmpty()) {
            showErrorMsg("Error", "当前无数据！请先查询");
            return;
        }

        sheet1.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
        Cell cellTitle = row.createCell(0);
        cellTitle.setCellStyle(style.get("title"));
        cellTitle.setCellValue("维修工时明细表");
        sheet1.addMergedRegion(new CellRangeAddress(1, 1, 0, 10));
        Cell cellTime = row1.createCell(0);
        cellTime.setCellStyle(style.get("date"));
        SimpleDateFormat date = new SimpleDateFormat("yyyy年MM月dd日");
        cellTime.setCellValue(date.format(queryDateBegin) + "-----" + date.format(queryDateEnd));
        int j = 3;
        List<?> itemList = detailList;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<Object[]> list = (List<Object[]>) itemList;
        for (Object[] eq : list) {
            row = sheet1.createRow(j);
            j++;
            row.setHeight((short) 400);
            Cell cell0 = row.createCell(0);
            cell0.setCellStyle(style.get("cell"));
            cell0.setCellValue(eq[1].toString());
            Cell cell1 = row.createCell(1);
            cell1.setCellStyle(style.get("cell"));
            cell1.setCellValue(eq[2].toString());
            Cell cell2 = row.createCell(2);
            cell2.setCellStyle(style.get("cell"));
            if (eq[3] != null) {
                cell2.setCellValue(eq[3].toString());
            }
            Cell cell3 = row.createCell(3);
            cell3.setCellStyle(style.get("cell"));
            cell3.setCellValue(eq[4].toString());
            Cell cell4 = row.createCell(4);
            cell4.setCellStyle(style.get("cell"));

            String hitchtime = sdf.format(sdf.parse(eq[5].toString()));
            cell4.setCellValue(hitchtime);
            Cell cell5 = row.createCell(5);
            cell5.setCellStyle(style.get("left"));
            cell5.setCellValue(eq[6] + "");
            Cell cell6 = row.createCell(6);
            cell6.setCellStyle(style.get("cell"));
            cell6.setCellValue(eq[7].toString());
            Cell cell7 = row.createCell(7);
            cell7.setCellStyle(style.get("cell"));
            if (eq[8] != null) {
                String servicearrivetime = sdf.format(sdf.parse(eq[8].toString()));
                cell7.setCellValue(servicearrivetime);
            }

            Cell cell8 = row.createCell(8);
            cell8.setCellStyle(style.get("cell"));
            if (eq[9] != null) {
                String completetime = sdf.format(sdf.parse(eq[9].toString()));
                cell8.setCellValue(completetime);
            }

            Cell cell9 = row.createCell(9);
            cell9.setCellStyle(style.get("cell"));

            Cell cell10 = row.createCell(10);
            cell10.setCellStyle(style.get("cell"));
            if (eq[10].toString().equals("0")) {
                if (eq[11] != null && !"".equals(eq[11].toString())) {
                    cell9.setCellValue(Double.parseDouble(eq[11].toString()));
                } else {
                    cell9.setCellValue(0);
                }
                cell10.setCellValue(0);
            } else {
                cell9.setCellValue(0);
                if (eq[11] != null && !"".equals(eq[11].toString())) {
                    cell10.setCellValue(Double.parseDouble(eq[11].toString()));
                } else {
                    cell10.setCellValue(0);
                }

            }
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
        return new String[]{"维修人", "报修单号", "资产编号", "设备名称", "故障发生日期", "维修作业方式", "报修部门", "维修到达时间", "维修完成时间", "维修工时(分)", "辅助维修工时(分)"};
    }

    /**
     * 设置单元格宽度
     */
    private int[] getInventoryWidth() {
        return new int[]{8, 15, 20, 13, 20, 25, 15, 20, 20, 10, 10};
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

    /**
     * 查询数据条件
     */
    @Override
    public void query() {
        String sql = "";
        String companySql = "";
        if (selectedUserName.length > 0) {
            for (String sqlUserName : selectedUserName) {
                sql += "or e.curnode2= " + " '" + sqlUserName + "'";
            }
            sql = sql.substring(2, sql.length());
        }
        if (company.length > 0) {
            for (String sqlCompanyID : company) {
                companySql += "or  r.company= " + " '" + sqlCompanyID + "' ";
            }
            companySql = companySql.substring(2, companySql.length());
        }
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String strdate = "";
        String enddate = "";
        if (queryDateBegin != null) {

            strdate = simpleDateFormat.format(queryDateBegin);
        }
        if (queryDateEnd != null) {
            enddate = simpleDateFormat.format(queryDateEnd);
        }

        detailList = equipmentRepairHelpersBean.getEquipmentRepairHelpersList(strdate, enddate, sql, companySql);

    }

    public List<String> getUsernameList() {
        return usernameList;
    }

    public void setUsernameList(List<String> usernameList) {
        this.usernameList = usernameList;
    }

    public List<SystemUser> getSystemUser() {
        return systemUser;
    }

    public void setSystemUser(List<SystemUser> systemUser) {
        this.systemUser = systemUser;
    }

    public String[] getSelectedUserName() {
        return selectedUserName;
    }

    public void setSelectedUserName(String[] selectedUserName) {
        this.selectedUserName = selectedUserName;
    }

    public List<Company> getCompanyList() {
        return companyList;
    }

    public void setCompanyList(List<Company> companyList) {
        this.companyList = companyList;
    }

    public String[] getCompany() {
        return company;
    }

    public void setCompany(String[] company) {
        this.company = company;
    }

}
