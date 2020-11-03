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
import cn.hanbell.eap.ejb.SystemUserBean;
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

    public RepairManHourDetailListManagedBean() {
        super(EquipmentRepair.class, EquipmentRepairHelpers.class);
    }

    //初始化数据筛选
    @Override
    public void init() {
        superEJB = equipmentRepairBean;
        detailEJB = equipmentRepairHelpersBean;
        queryState = "ALL";
        String deptno = sysCodeBean.findBySyskindAndCode("RD", "repairDeptno").getCvalue();
        systemUser = systemUserBean.findByLikeDeptno("%" + deptno + "%");
        usernameList = new ArrayList<>();
        for (int i = 0; i < systemUser.size(); i++) {
            usernameList.add(systemUser.get(i).getUsername());
        }
        selectedUserName = null;
        detailList = null;
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
        //表格一
        String[] title1 = getInventoryTitle();
        row = sheet1.createRow(0);
        row.setHeight((short) 800);

        for (int i = 0; i < title1.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(style.get("head"));
            cell.setCellValue(title1[i]);
        }
        if (detailList == null || detailList.size() < 0) {
            showErrorMsg("Error", "当前无数据！请先查询");
            return;
        }
        int j = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<?> itemList = detailList;

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
            cell5.setCellStyle(style.get("cell"));
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
                cell9.setCellValue(Double.parseDouble(eq[11].toString()));
                cell10.setCellValue(0);
            } else {
                cell9.setCellValue(0);
                cell10.setCellValue(Double.parseDouble(eq[11].toString()));
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
        return new String[]{"维修人", "报修单号", "资产编号", "设备名称", "故障发生日期", "维修方式说明", "报修部门", "维修到达时间", "维修完成时间", "维修工时", "辅助维修工时"};
    }

    /**
     * 设置单元格宽度
     */
    private int[] getInventoryWidth() {
        return new int[]{10, 20, 20, 20, 20, 20, 15, 20, 20, 15, 20};
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
        headFont.setFontHeightInPoints((short) 12);
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

    /**
     * 查询数据条件
     */
    @Override
    public void query() {
        String sql = "";
        if (selectedUserName.length > 0) {
            for (String sqlUserName : selectedUserName) {
                sql += "or e.curnode2= " + " '" + sqlUserName + "'";
            }
            sql = sql.substring(2, sql.length());
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

        detailList = equipmentRepairHelpersBean.getEquipmentRepairHelpersList(strdate, enddate, sql);

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

}
