/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.AssetCheckBean;
import cn.hanbell.eam.ejb.AssetCheckDetailBean;
import cn.hanbell.eam.ejb.EquipmentSpareRecodeDtaBean;
import cn.hanbell.eam.ejb.EquipmentSpareStockBean;
import cn.hanbell.eam.entity.AssetCategory;
import cn.hanbell.eam.entity.AssetCheck;
import cn.hanbell.eam.entity.AssetCheckDetail;
import cn.hanbell.eam.entity.AssetItem;
import cn.hanbell.eam.entity.EquipmentSpareRecodeDta;
import cn.hanbell.eam.entity.EquipmentSpareStock;
import cn.hanbell.eam.lazy.AssetCheckModel;
import cn.hanbell.eam.web.FormMultiBean;
import com.lightshell.comm.SuperEJB;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author C2079
 */
@ManagedBean(name = "spareCheckManagedBean")
@SessionScoped
public class SpareCheckManagedBean extends FormMultiBean<AssetCheck, AssetCheckDetail> {

    @EJB
    protected AssetCheckDetailBean assetCheckDetailBean;

    @EJB
    protected AssetCheckBean assetCheckBean;

    @EJB
    protected EquipmentSpareStockBean equipmentSpareStockBean;

    @EJB
    protected EquipmentSpareRecodeDtaBean equipmentSpareRecodeDtaBean;
    protected List<String> paramUsed = null;

    protected AssetCategory queryCategory;
    private String queryDeptno;

    /**
     * Creates a new instance of AssetCheckManagedBean
     */
    public SpareCheckManagedBean() {
        super(AssetCheck.class, AssetCheckDetail.class);
    }

    @Override
    protected boolean doBeforeDelete(AssetCheck entity) throws Exception {

        return super.doBeforeDelete(entity);
    }

    @Override
    public void doConfirmDetail() {
        if (currentDetail != null) {
            currentDetail.setDiffqty(currentDetail.getActqty().subtract(currentDetail.getQty()));
        }
        super.doConfirmDetail();
    }

    @Override
    public void init() {
        openParams = new HashMap<>();
        superEJB = assetCheckBean;
        detailEJB = assetCheckDetailBean;
        model = new AssetCheckModel(assetCheckBean, userManagedBean);
        model.getFilterFields().put("formid", "BJ");
        model.getSortFields().put("status", "ASC");
        model.getSortFields().put("formid", "DESC");
        super.init();
    }

    @Override
    public void print() throws Exception {
        if (currentPrgGrant == null || currentPrgGrant.getSysprg().getRptclazz() == null) {
            showErrorMsg("Error", "系统配置错误无法打印");
            return;
        }
        if (currentEntity == null) {
            showErrorMsg("Error", "没有可打印数据");
            return;
        }
        //设置报表参数
        HashMap<String, Object> reportParams = new HashMap<>();
        reportParams.put("company", userManagedBean.getCurrentCompany().getName());
        reportParams.put("companyFullName", userManagedBean.getCurrentCompany().getFullname());
        reportParams.put("id", currentEntity.getId());
        reportParams.put("formid", currentEntity.getFormid());
        reportParams.put("JNDIName", this.currentPrgGrant.getSysprg().getRptjndi());
        //设置报表名称
        String reportFormat;
        if (this.currentPrgGrant.getSysprg().getRptformat() != null) {
            reportFormat = this.currentPrgGrant.getSysprg().getRptformat();
        } else {
            reportFormat = reportOutputFormat;
        }
        String reportName;

        reportName = reportPath + currentPrgGrant.getSysprg().getRptdesign();

        String outputName = reportOutputPath + currentEntity.getFormid() + "." + reportFormat;
        this.reportViewPath = reportViewContext + currentEntity.getFormid() + "." + reportFormat;
        try {
            reportClassLoader = Class.forName(this.currentPrgGrant.getSysprg().getRptclazz()).getClassLoader();
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

    public void print(String reportFormat) throws Exception {
        if (currentPrgGrant == null || currentPrgGrant.getSysprg().getRptclazz() == null) {
            showErrorMsg("Error", "系统配置错误无法打印");
            return;
        }
        if (currentEntity == null) {
            showErrorMsg("Error", "没有可打印数据");
            return;
        }
        //设置报表参数
        HashMap<String, Object> reportParams = new HashMap<>();
        reportParams.put("company", userManagedBean.getCurrentCompany().getName());
        reportParams.put("companyFullName", userManagedBean.getCurrentCompany().getFullname());
        reportParams.put("id", currentEntity.getId());
        reportParams.put("formid", currentEntity.getFormid());
        reportParams.put("JNDIName", this.currentPrgGrant.getSysprg().getRptjndi());
        //设置报表名称
        String reportName;

        reportName = reportPath + currentPrgGrant.getSysprg().getRptdesign();

        String outputName = reportOutputPath + currentEntity.getFormid() + "." + reportFormat;
        this.reportViewPath = reportViewContext + currentEntity.getFormid() + "." + reportFormat;
        try {
            reportClassLoader = Class.forName(this.currentPrgGrant.getSysprg().getRptclazz()).getClassLoader();
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

    //产生备件盘点单
    public void initSpareCheck() {
        List<AssetCheckDetail> assetCheckDetails = new ArrayList<>();
        //获取盘点的库存单据
        List<?> itemList = equipmentSpareStockBean.getEquipmentSpareStockCheckList(queryDeptno, userManagedBean.getCompany());
        List<Object[]> list = (List<Object[]>) itemList;
        LinkedHashMap<SuperEJB, List<?>> detailAdded = new LinkedHashMap<>();
        AssetCheck e = new AssetCheck();
        e.setCompany(userManagedBean.getCompany());
        e.setFormdate(getDate());
        e.setDeptno(queryDeptno);
        e.setStatus("N");
        queryCategory = new AssetCategory();
        queryCategory.setId(41);
        e.setCategory(queryCategory);
        e.setFormid(assetCheckBean.getFormId(getDate(), "BJ", "YYMM", 4));
        e.setCreator(userManagedBean.getCurrentUser().getUsername());
        e.setCredate(getDate());
        AssetItem item = new AssetItem();
        item.setItemno("BJ");
        item.setId(999);
        int seq = 0;
        for (Object[] obj : list) {
            AssetCheckDetail aDta = new AssetCheckDetail();
            seq++;
            aDta.setPid(e.getFormid());
            aDta.setSeq(seq);
            aDta.setAssetno(obj[0].toString());
            if (obj[1] != null) {
                aDta.setBatch(obj[1].toString());
            }
            if (obj[7] != null) {
                aDta.setBrand(obj[7].toString());
            }
            aDta.setActqty(BigDecimal.valueOf(Double.valueOf(obj[5].toString())));
            aDta.setQty(BigDecimal.valueOf(Double.valueOf(obj[5].toString())));
            aDta.setDiffqty(BigDecimal.ZERO);
            aDta.setAssetItem(item);
            if (obj[2] != null) {
                aDta.setSrcapi(obj[2].toString());
            }
            assetCheckDetails.add(aDta);
        }
        detailAdded.put(assetCheckDetailBean, assetCheckDetails);
        assetCheckBean.persist(e, detailAdded, null, null);
    }

    @Override
    public void query() {
        if (model != null) {
            if (queryFormId != null && !"".equals(queryFormId)) {
                model.getFilterFields().put("formid", queryFormId);
            }
            if (queryDateBegin != null) {
                model.getFilterFields().put("formdateBegin", queryDateBegin);
            }
            if (queryDateEnd != null) {
                model.getFilterFields().put("formdateEnd", queryDateEnd);
            }
            if (queryCategory != null) {
                model.getFilterFields().put("category.id", queryCategory.getId());
            }
            if (queryState != null && !"ALL".equals(queryState)) {
                model.getFilterFields().put("status", queryState);
            }
        }
    }

    //审核备件盘点单
    @Override
    public void verify() {
        List<EquipmentSpareStock> list = new ArrayList<>();//需要更新的库存List
        List<EquipmentSpareRecodeDta> eRecodeDtaslist = new ArrayList<>();
        for (AssetCheckDetail deList : detailList) {
            if (deList.getDiffqty().compareTo(BigDecimal.ZERO) != 0) {//只有实盘修改过的数据需要更新
                List<EquipmentSpareStock> eStock = equipmentSpareStockBean.findBySparenumAndLocation(deList.getAssetno(), deList.getBrand());//查出需要更新的库存数据
                BigDecimal itemBig = new BigDecimal(BigInteger.ZERO);
                int i=0;
                for (EquipmentSpareStock eSt : eStock) {
                    if ((itemBig.compareTo(BigDecimal.ZERO) == 0&&i==0)||itemBig.compareTo(BigDecimal.ZERO)<0) {
                       
                        if (deList.getDiffqty().compareTo(BigDecimal.ZERO) > 0) {
                            itemBig = eSt.getQty().add(deList.getDiffqty());
                            eSt.setQty(itemBig);
                        } else {
                            if (itemBig.compareTo(BigDecimal.ZERO) == 0) {
                                itemBig = eSt.getQty().subtract(deList.getDiffqty().abs());
                                if (itemBig.compareTo(BigDecimal.ZERO) < 0) {
                                    eSt.setQty(BigDecimal.ZERO);
                                } else {
                                    eSt.setQty(itemBig);
                                }
                            } else {
                                itemBig = eSt.getQty().subtract(itemBig.abs());
                                eSt.setQty(itemBig);
                            }
                        }
                        i++;
                        eSt.setOptdate(getDate());
                        eSt.setOptuser(userManagedBean.getUserid());
                        list.add(eSt);//添加到库存List一起更新
                        //储存盘点时更新库存的记录
                        EquipmentSpareRecodeDta eDta = new EquipmentSpareRecodeDta();
                        //判断盘点的数量是盘亏还是盘盈
                        if (deList.getDiffqty().compareTo(BigDecimal.ZERO) > 0) {
                            eDta.setPid(assetCheckBean.getFormId(getDate(), "PY", "YYMM", 4));
                        } else {
                            eDta.setPid(assetCheckBean.getFormId(getDate(), "PK", "YYMM", 4));
                        }
                        eDta.setSparenum(eSt.getSparenum());
                        eDta.setSeq(eRecodeDtaslist.size() + 1);
                        eDta.setCqty(deList.getDiffqty());
                        eDta.setUprice(eSt.getUprice());
                        eDta.setSlocation(eSt.getSlocation());
                        eDta.setStatus("V");
                        eDta.setCredate(getDate());
                        eDta.setCreator(userManagedBean.getUserid());
                        eDta.setRemark(currentEntity.getFormid());
                        eRecodeDtaslist.add(eDta);
                    }
                }

            }
        }
        equipmentSpareStockBean.update(list);
        equipmentSpareRecodeDtaBean.update(eRecodeDtaslist);

        if (null != getCurrentEntity()) {
            try {
                currentEntity.setStatus("V");
                currentEntity.setCfmuser(getUserManagedBean().getCurrentUser().getUserid());
                currentEntity.setCfmdateToNow();
                assetCheckBean.update(currentEntity);
                showInfoMsg("Info", "更新成功");
            } catch (Exception ex) {
                showErrorMsg("Error", ex.getMessage());
            }
        } else {
            showWarnMsg("Warn", "没有可更新数据");
        }
    }

    //还原备件盘点单
    @Override
    public void unverify() {
        List<EquipmentSpareStock> list = new ArrayList<>();//需要更新的库存List
        //还原时删除对应记录的List
        List<EquipmentSpareRecodeDta> eRecodeDtaslist = equipmentSpareRecodeDtaBean.findByRemark(currentEntity.getFormid());
        for (AssetCheckDetail deList : detailList) {
            if (deList.getDiffqty().compareTo(BigDecimal.ZERO) != 0) {//只有实盘修改过的数据需要更新
                EquipmentSpareStock eStock = equipmentSpareStockBean.findBySparenumAndLocation(deList.getAssetno(), deList.getBrand()).get(0);//查出需要更新的库存数据
                if (deList.getDiffqty().compareTo(BigDecimal.ZERO) > 0) {
                    eStock.setQty(eStock.getQty().subtract(deList.getDiffqty().abs()));
                } else {
                    eStock.setQty(eStock.getQty().add(deList.getDiffqty().abs()));
                }

                eStock.setCfmdate(getDate());
                eStock.setCfmuser(userManagedBean.getUserid());
                list.add(eStock);//添加到库存List一起更新
            }
        }
        equipmentSpareStockBean.update(list);
        equipmentSpareRecodeDtaBean.delete(eRecodeDtaslist);
        if (null != getCurrentEntity()) {
            try {
                currentEntity.setStatus("N");
                currentEntity.setCfmuser(getUserManagedBean().getCurrentUser().getUserid());
                currentEntity.setCfmdateToNow();
                assetCheckBean.update(currentEntity);
                showInfoMsg("Info", "更新成功");
            } catch (Exception ex) {
                showErrorMsg("Error", ex.getMessage());
            }
        } else {
            showWarnMsg("Warn", "没有可更新数据");
        }
    }

    @Override
    public void reset() {
        super.reset();
        this.queryCategory = null;
    }

    /**
     * @return the queryCategory
     */
    public AssetCategory getQueryCategory() {
        return queryCategory;
    }

    /**
     * @param queryCategory the queryCategory to set
     */
    public void setQueryCategory(AssetCategory queryCategory) {
        this.queryCategory = queryCategory;
    }

    public String getQueryDeptno() {
        return queryDeptno;
    }

    public void setQueryDeptno(String queryDeptno) {
        this.queryDeptno = queryDeptno;
    }

}
