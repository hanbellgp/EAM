/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.AssetAcceptanceBean;
import cn.hanbell.eam.ejb.AssetAcceptanceDetailBean;
import cn.hanbell.eam.ejb.AssetCardBean;
import cn.hanbell.eam.ejb.AssetInventoryBean;
import cn.hanbell.eam.ejb.TransactionTypeBean;
import cn.hanbell.eam.entity.AssetAcceptance;
import cn.hanbell.eam.entity.AssetAcceptanceDetail;
import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.entity.AssetInventory;
import cn.hanbell.eam.entity.AssetItem;
import cn.hanbell.eam.entity.TransactionType;
import cn.hanbell.eam.entity.Warehouse;
import cn.hanbell.eam.lazy.AssetAcceptanceModel;
import cn.hanbell.eam.web.FormMultiBean;
import cn.hanbell.eap.ejb.SystemUserBean;
import cn.hanbell.eap.entity.Department;
import cn.hanbell.eap.entity.SystemUser;
import com.lightshell.comm.BaseLib;
import com.lightshell.comm.Tax;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
import org.primefaces.event.SelectEvent;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "assetAcceptanceManagedBean")
@SessionScoped
public class AssetAcceptanceManagedBean extends FormMultiBean<AssetAcceptance, AssetAcceptanceDetail> {

    @EJB
    private AssetAcceptanceBean assetAcceptanceBean;

    @EJB
    private AssetAcceptanceDetailBean assetAcceptanceDetailBean;
    @EJB
    private SystemUserBean systemUserBean;
    @EJB
    private AssetCardBean assetCardBean;

    @EJB
    private AssetInventoryBean assetInventoryBean;

    @EJB
    private TransactionTypeBean transactoinTypeBean;

    private TransactionType trtype;
    private String queryDeptname;

    public AssetAcceptanceManagedBean() {
        super(AssetAcceptance.class, AssetAcceptanceDetail.class);
    }

    @Override
    public void create() {
        super.create();
        newEntity.setCompany(userManagedBean.getCompany());
        newEntity.setFormdate(getDate());
    }

    @Override
    public void createDetail() {
        super.createDetail();
        currentDetail.setTrtype(trtype);
        currentDetail.setAcceptdate(getDate());
        currentDetail.setAcceptUserno(userManagedBean.getUserid());
        currentDetail.setCurrency("CNY");
        currentDetail.setExchange(BigDecimal.ONE);
        currentDetail.setTaxtype("0");
        currentDetail.setTaxrate(BigDecimal.valueOf(0.13));//财务朱菊红调整为0.13原0.17
        currentDetail.setTaxkind("VAT17");
        currentDetail.setAmts(BigDecimal.ZERO);
        currentDetail.setExtax(BigDecimal.ZERO);
        currentDetail.setTaxes(BigDecimal.ZERO);
        currentDetail.setStatus("40");
    }

    @Override
    protected boolean doAfterVerify() throws Exception {
        //验收入库以后产生条码
        List<AssetCard> assetCardList = assetCardBean.findBySrcformid(currentEntity.getFormid());
        for (AssetCard ac : assetCardList) {
            fileName = this.getAppResPath() + ac.getFormid() + ".png";
            assetCardBean.generateCode128(ac.getFormid(), 1.0f, 8d, fileName);
            assetCardBean.generateQRCode(ac.getFormid(), 300, 300, this.getAppResPath(), "QR" + ac.getFormid() + ".png");
        }
        return super.doAfterVerify();
    }

    @Override
    protected boolean doBeforeUnverify() throws Exception {
        if (super.doBeforeUnverify()) {
            AssetInventory ai;
            BigDecimal acqty;
            for (AssetAcceptanceDetail aad : detailList) {
                //库存数量检查
                ai = assetInventoryBean.findAssetInventory(currentEntity.getCompany(), aad.getAssetItem().getItemno(), "", "", "", aad.getWarehouse().getWarehouseno());
                if ((ai == null) || ai.getQty().compareTo(aad.getQcqty()) == -1) {
                    showErrorMsg("Error", aad.getAssetItem().getItemno() + "库存可还原量不足");
                    return false;
                }
                //判断是不是自动产生资产卡片
                if (aad.getAssetItem().getCategory().getNoauto()) {
                    acqty = BigDecimal.ZERO;
                    List<AssetCard> acs = assetCardBean.findByFiltersAndNotUsed(aad.getPid(), aad.getSeq());
                    for (AssetCard ac : acs) {
                        acqty = acqty.add(ac.getQty());
                    }
                    if (acqty.compareTo(aad.getQcqty()) != 0) {
                        showErrorMsg("Error", "卡片可还原数量不足");
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean doBeforeVerify() throws Exception {
        if (super.doBeforeVerify()) {
            for (AssetAcceptanceDetail d : detailList) {
                if (!d.getWarehouse().getHascost()) {
                    showErrorMsg("Error", "仓库成本属性错误");
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void doConfirmDetail() {
        currentDetail.setAmts(currentDetail.getQcqty().multiply(currentDetail.getPrice()));
        Tax t = BaseLib.getTaxes(currentDetail.getTaxtype(), currentDetail.getTaxkind(), currentDetail.getTaxrate().multiply(BigDecimal.valueOf(100)), this.currentDetail.getAmts(), 2);
        currentDetail.setExtax(t.getExtax());
        currentDetail.setTaxes(t.getTaxes());
        super.doConfirmDetail();
    }

    @Override
    public void handleDialogReturnWhenEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            Department d = (Department) event.getObject();
            currentEntity.setDeptno(d.getDeptno());
            currentEntity.setDeptname(d.getDept());
        }
    }

    @Override
    public void handleDialogReturnWhenNew(SelectEvent event) {
        handleDialogReturnWhenEdit(event);
    }

    public void handleDialogReturnAssetItemWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            AssetItem e = (AssetItem) event.getObject();
            currentDetail.setAssetItem(e);
            currentDetail.setUnit(e.getUnit());
        }
    }

    public void handleDialogReturnWarehouseWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            Warehouse e = (Warehouse) event.getObject();
            currentDetail.setWarehouse(e);
        }
    }

    @Override
    public void init() {
        superEJB = assetAcceptanceBean;
        detailEJB = assetAcceptanceDetailBean;
        model = new AssetAcceptanceModel(assetAcceptanceBean, userManagedBean);
        model.getSortFields().put("status", "ASC");
        model.getSortFields().put("formid", "DESC");
        if (this.getCurrentPrgGrant() != null && this.getCurrentPrgGrant().getSysprg().getNoauto()) {
            model.getFilterFields().put("formid", this.getCurrentPrgGrant().getSysprg().getNolead());
        }
        trtype = transactoinTypeBean.findByTrtype("PAA");
        if (trtype == null) {
            showErrorMsg("Error", "PAA验收类别未设置");
        }
        super.init();
    }

    //获取明细中的总金额
    public BigDecimal getDetaiSumMoney(String formid) {
        detailList = assetAcceptanceDetailBean.findByPId(formid);
        BigDecimal sum = new BigDecimal(0);
        for (AssetAcceptanceDetail assetDet : detailList) {
            sum = sum.add(assetDet.getAmts());
        }
        return sum;
    }

    @Override
    public void query() {
        if (this.model != null && this.model.getFilterFields() != null) {
            this.model.getFilterFields().clear();
            if (queryFormId != null && !"".equals(queryFormId)) {
                this.model.getFilterFields().put("formid", queryFormId);
            } else {
                if (this.getCurrentPrgGrant() != null && this.getCurrentPrgGrant().getSysprg().getNoauto()) {
                    model.getFilterFields().put("formid", this.getCurrentPrgGrant().getSysprg().getNolead());
                }
            }
            if (queryDeptname != null && !"".equals(queryDeptname)) {
                this.model.getFilterFields().put("vendorno", queryDeptname);
            }
            if (queryName != null && !"".equals(queryName)) {
                this.model.getFilterFields().put("creator", queryName);
            }
            if (queryDateBegin != null) {
                this.model.getFilterFields().put("formdateBegin", queryDateBegin);
            }
            if (queryDateEnd != null) {
                this.model.getFilterFields().put("formdateEnd", queryDateEnd);
            }
            if (queryState != null && !"ALL".equals(queryState)) {
                this.model.getFilterFields().put("status", queryState);
            }
        }
    }

    @Override
    public void reset() {
        super.reset();
        if (this.getCurrentPrgGrant() != null && this.getCurrentPrgGrant().getSysprg().getNoauto()) {
            model.getFilterFields().put("formid", this.getCurrentPrgGrant().getSysprg().getNolead());
        }
    }

   
    public void printEXE() {

        fileName = "办公用品入库表" + BaseLib.formatDate("yyyyMMddHHmmss", BaseLib.getDate()) + ".xls";
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
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());//单元格背景颜色
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        List<AssetAcceptance> assetAcceptance = assetAcceptanceBean.findByFilters(model.getFilterFields(), model.getSortFields());
        int j = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (AssetAcceptance as : assetAcceptance) {
            row = sheet1.createRow(j);
            j++;
            row.setHeight((short) 400);
            Cell cell0 = row.createCell(0);
            cell0.setCellStyle(cellStyle);
            cell0.setCellValue(as.getFormid()+" 汇总");
            Cell cell1 = row.createCell(1);
            cell1.setCellStyle(cellStyle);
            cell1.setCellValue(sdf.format(as.getFormdate()));
            Cell cell2 = row.createCell(2);
            cell2.setCellStyle(cellStyle);
            cell2.setCellValue(as.getVendorno());
            Cell cell4 = row.createCell(3);
            cell4.setCellStyle(cellStyle);
            cell4 = row.createCell(4);
            cell4.setCellStyle(cellStyle);
            Cell cell3 = row.createCell(5);
            cell3.setCellStyle(cellStyle);
            Cell cell5 = row.createCell(6);
            cell5.setCellStyle(cellStyle);
            Cell cell6 = row.createCell(7);
            cell6.setCellStyle(cellStyle);
            cell6.setCellValue(Double.parseDouble(getDetaiSumMoney(as.getFormid()).toString()));
            cell0 = row.createCell(8);
            if (as.getCreator() != null) {
                SystemUser user = getUserName(as.getCreator());
                if (user == null) {
                    cell0.setCellValue(as.getCreator());
                } else {
                    cell0.setCellValue(user.getUsername());
                }
            } else if (as.getCfmuser() != null) {
                SystemUser user = getUserName(as.getCfmuser());
                if (user == null) {
                    cell0.setCellValue(as.getCfmuser());
                } else {
                    cell0.setCellValue(user.getUsername());
                }

            }
            cell0.setCellStyle(cellStyle);
            cell0 = row.createCell(9);
            cell0.setCellStyle(cellStyle);
            cell0.setCellValue(as.getDeptname());
            cell0 = row.createCell(10);
            cell0.setCellStyle(cellStyle);
            cell0 = row.createCell(11);
            cell0.setCellStyle(cellStyle);
            cell0.setCellValue(as.getRemark());

            detailList = assetAcceptanceDetailBean.findByPId(as.getFormid());
            for (AssetAcceptanceDetail asDetail : detailList) {
                row = sheet1.createRow(j);
                j++;
                row.setHeight((short) 400);
                cell0 = row.createCell(0);
                cell0.setCellStyle(style.get("cell"));
                cell0.setCellValue(as.getFormid());
                cell1 = row.createCell(1);
                cell1.setCellStyle(style.get("cell"));
                cell1.setCellValue(sdf.format(as.getFormdate()));
                cell2 = row.createCell(2);
                cell2.setCellStyle(style.get("cell"));
                cell2.setCellValue(as.getVendorno());
                cell3 = row.createCell(3);
                cell3.setCellStyle(style.get("cell"));
                cell3.setCellValue(asDetail.getAssetItem().getItemno());
                cell3 = row.createCell(4);
                cell3.setCellStyle(style.get("cell"));
                cell3.setCellValue(asDetail.getAssetItem().getItemdesc());
                cell3 = row.createCell(5);
                cell3.setCellStyle(style.get("cell"));
                cell3.setCellValue(Double.parseDouble(asDetail.getQcqty().toString()));
                cell3 = row.createCell(6);
                cell3.setCellStyle(style.get("cell"));
                cell3.setCellValue(Double.parseDouble(asDetail.getPrice().toString()));
                cell3 = row.createCell(7);
                cell3.setCellStyle(style.get("cell"));
                cell3.setCellValue(Double.parseDouble(asDetail.getAmts().toString()));
                cell3 = row.createCell(8);
                cell3.setCellStyle(style.get("cell"));
                if (as.getCreator() != null) {
                    SystemUser user = getUserName(as.getCreator());
                    if (user == null) {
                        cell3.setCellValue(as.getCreator());
                    } else {
                        cell3.setCellValue(user.getUsername());
                    }
                } else if (as.getCfmuser() != null) {
                    SystemUser user = getUserName(as.getCfmuser());
                    if (user == null) {
                        cell3.setCellValue(as.getCfmuser());
                    } else {
                        cell3.setCellValue(user.getUsername());
                    }

                }
                cell3 = row.createCell(9);
                cell3.setCellStyle(style.get("cell"));
                cell3.setCellValue(as.getDeptname());
                cell3 = row.createCell(10);
                cell3.setCellStyle(style.get("cell"));
                cell3.setCellValue(asDetail.getWarehouse().getName());
                cell3 = row.createCell(11);
                cell3.setCellStyle(style.get("cell"));
                cell3.setCellValue(asDetail.getRemark());
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

    //根据用户ID获取用户姓名
    public SystemUser getUserName(String userId) {
        SystemUser s = systemUserBean.findByUserId(userId);
        return s;
    }

    /**
     * 设置表头名称字段
     */
    private String[] getInventoryTitle() {
        return new String[]{"验收单号", "验收日期", "厂商", "品号", "名称", "数量", "单价", "总金额", "处理人", "处理部门", "仓库", "备注"};
    }

    /**
     * 设置单元格宽度
     */
    private int[] getInventoryWidth() {
        return new int[]{20, 15, 15, 15, 20, 10, 10, 10, 10, 20, 20, 15};
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

    public String getQueryDeptname() {
        return queryDeptname;
    }

    public void setQueryDeptname(String queryDeptname) {
        this.queryDeptname = queryDeptname;
    }

}
