package cn.hanbell.eam.control;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import cn.hanbell.eam.ejb.EquipmentRepairBean;
import cn.hanbell.eam.ejb.EquipmentRepairHelpersBean;
import cn.hanbell.eam.ejb.EquipmentRepairHisBean;
import cn.hanbell.eam.entity.EquipmentRepair;
import cn.hanbell.eam.entity.EquipmentRepairHelpers;
import java.io.FileOutputStream;
import java.io.IOException;
import cn.hanbell.eam.web.FormMultiBean;
import cn.hanbell.eap.entity.SystemUser;
import com.lightshell.comm.BaseLib;
import java.io.FileInputStream;
import java.io.InputStream;
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
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.chart.LinearAxis;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 *
 * @author C2079
 */
@ManagedBean(name = "repairManHourSummaryManagedBean")
@SessionScoped
public class RepairManHourSummaryManagedBean extends FormMultiBean<EquipmentRepair, EquipmentRepairHelpers> {

    @EJB
    protected EquipmentRepairBean equipmentRepairBean;

    @EJB
    protected EquipmentRepairHisBean equipmentRepairHisBean;

    @EJB
    private EquipmentRepairHelpersBean equipmentRepairHelpersBean;

    private List<String> usernameList;
    private List<SystemUser> systemUser;
    private String[] selectedUserName;
    private LineChartModel lineModel;
    private LineChartModel zoomModel;

    public RepairManHourSummaryManagedBean() {
        super(EquipmentRepair.class, EquipmentRepairHelpers.class);
    }

    //初始化数据筛选
    @Override
    public void init() {
        superEJB = equipmentRepairBean;
        detailEJB = equipmentRepairHelpersBean;
        createLineModels();
    }

//导出界面的EXCEL数据处理
    @Override
    public void print() {
        String finalFilePath = "";
        try {
            finalFilePath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
//            测试路径
//            int index = finalFilePath.indexOf("dist/gfdeploy");
//            //正式路径  D:\Java\glassfish5.0.1\glassfish\domains\domain1\applications\EAM\Hanbell-EAM_war\rpt
//                      //D:\Java\glassfish5.0.1\glassfish\domains\domain1\applications\KPI\Hanbell-KPI_war\rpt
            int index = finalFilePath.indexOf("EAM-ejb");
            InputStream is = new FileInputStream(finalFilePath.substring(1, index) + "Hanbell-EAM/web/rpt/维修工时汇总表模板.xls");
            Workbook workbook = WorkbookFactory.create(is);
            //获得表格样式
            Map<String, CellStyle> style = createStyles(workbook);
            Sheet sheet;
            sheet = workbook.getSheetAt(0);
            Row row;
            if (detailList == null || detailList.size() < 0) {
                showErrorMsg("Error", "当前无数据！请先查询");
                return ;
            }
            List<?> itemList = detailList;
            int j = 1;
            List<Object[]> list = (List<Object[]>) itemList;
            for (Object[] eq : list) {
                row = sheet.createRow(j);
                j++;
                row.setHeight((short) 400);
                Cell cell0 = row.createCell(0);
                cell0.setCellStyle(style.get("cell"));
                cell0.setCellValue(eq[0].toString());
                Cell cell1 = row.createCell(1);
                cell1.setCellStyle(style.get("cell"));
                cell1.setCellValue(Double.parseDouble(eq[1].toString()));
                Cell cell2 = row.createCell(2);
                cell2.setCellStyle(style.get("cell"));
                cell2.setCellValue(Double.parseDouble(eq[2].toString()));
                Cell cell3 = row.createCell(3);
                cell3.setCellStyle(style.get("cell"));
                cell3.setCellValue(Double.parseDouble(eq[3].toString()));
                Cell cell4 = row.createCell(4);
                cell4.setCellStyle(style.get("cell"));
                cell4.setCellValue(Double.parseDouble(eq[4].toString()));

            }

            OutputStream os = null;
            fileName = "维修工时汇总表" + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
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
               showErrorMsg("Error", "e.toString() + \"Path:\" + finalFilePath");
        }
    }

    /**
     * 设置表头名称字段
     */
    private String[] getInventoryTitle() {
        return new String[]{"维修人", "维修次数", "维修工时", "辅助维修工时", "总维修工时"};
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

        detailList = equipmentRepairHelpersBean.getRepairManHourSummaryList(strdate, enddate);
        createLineModels();
    }

    private void createLineModels() {

        lineModel = initCategoryModel();
        lineModel.setShowPointLabels(true);
        lineModel.getAxes().put(AxisType.X, new CategoryAxis(""));
        Axis yAxis = lineModel.getAxis(AxisType.Y);
        yAxis.setLabel("工时（分）");
        lineModel.setTitle("维修工时折线图");
        lineModel.setMouseoverHighlight(false);
        yAxis.setMin(0);
        Axis y2Axis = new LinearAxis("次数");
        y2Axis.setMin(0);
        lineModel.getAxes().put(AxisType.Y2, y2Axis);

    }

    private LineChartModel initCategoryModel() {
        LineChartModel model1 = new LineChartModel();
        List<?> itemList = detailList;
        List<Object[]> list = (List<Object[]>) itemList;
        List<Object[]> arrList = new ArrayList<>();
        if (list.size() > 0) {
            for (int i = 0; i < 4; i++) {
                Object[] obj = new Object[list.size() + 1];
                for (int j = 0; j < list.size(); j++) {
                    switch (i) {
                        case 0:
                            obj[0] = "维修次数";
                            obj[j + 1] = list.get(j)[1];
                            break;
                        case 1:
                            obj[0] = "维修工时";
                            obj[j + 1] = list.get(j)[2];
                            break;
                        case 2:
                            obj[0] = "辅助维修工时";
                            obj[j + 1] = list.get(j)[3];
                            break;
                        case 3:
                            obj[0] = "总维修工时";
                            obj[j + 1] = list.get(j)[4];
                            break;
                        default:
                            break;
                    }
                }
                arrList.add(obj);
            }
        }

        for (int i = 0; i < arrList.size(); i++) {
            LineChartSeries boys = new LineChartSeries();
            LineChartSeries girls = new LineChartSeries();
            girls.setLabel("Girls");
            girls.setXaxis(AxisType.X);
            girls.setYaxis(AxisType.Y2);
            for (int j = 0; j < list.size(); j++) {
                if (i == 0) {
                    if (j == 0) {
                        girls.setLabel(arrList.get(i)[0].toString());
                    }
                    girls.set(list.get(j)[0], Double.parseDouble(arrList.get(i)[j + 1].toString()));
                } else {
                    if (j == 0) {
                        boys.setLabel(arrList.get(i)[0].toString());
                    }
                    boys.set(list.get(j)[0], Double.parseDouble(arrList.get(i)[j + 1].toString()));
                }

            }
            if (i == 0) {
                model1.addSeries(girls);
            } else {
                model1.addSeries(boys);
            }

        }

//        arrList.forEach(objects -> {
//            LineChartSeries boys = new LineChartSeries();
//            boys.setLabel(objects[0].toString());
//            boys.set(objects[0].toString(), Double.parseDouble(objects[1].toString()) / 60);
//            for (Object[] li : list) {
//                  boys.set(li[0], Double.parseDouble(objects[2].toString()) / 60);
//            }
//            boys.set("维修工时", Double.parseDouble(objects[2].toString()) / 60);
//            boys.set("辅助维修工时", Double.parseDouble(objects[3].toString()) / 60);
//            boys.set("总维修工时", Double.parseDouble(objects[4].toString()) / 60);
//        });
        model1.setLegendPosition("e");
        return model1;
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

    public LineChartModel getLineModel() {
        return lineModel;
    }

    public void setLineModel(LineChartModel lineModel) {
        this.lineModel = lineModel;
    }

    public LineChartModel getZoomModel() {
        return zoomModel;
    }

    public void setZoomModel(LineChartModel zoomModel) {
        this.zoomModel = zoomModel;
    }

}
