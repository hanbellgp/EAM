/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.AssetPositionBean;
import cn.hanbell.eam.ejb.WarehouseBean;
import cn.hanbell.eam.entity.AssetCategory;
import cn.hanbell.eam.entity.AssetPosition;
import cn.hanbell.eam.entity.Warehouse;
import cn.hanbell.eap.ejb.DepartmentBean;
import cn.hanbell.eap.entity.Department;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "assetCheckInitManagedBean")
@SessionScoped
public class AssetCheckInitManagedBean extends AssetCheckManagedBean {

    @EJB
    private AssetPositionBean assetPositionBean;

    @EJB
    private DepartmentBean departmentBean;
    @EJB
    private WarehouseBean warehouseBean;

    private Date queryFormDate;
    private String queryFormType;
    private String queryFormKind;
    private AssetCategory queryCategory;
    private String queryDeptno;
    private String queryUserno;
    private String queryPosition;
    private boolean queryMergeDept = true;

    private List<Department> deptList;
    private List<Department> selectedDept;
    private List<Warehouse> warehouseList;
    private List<Warehouse> selectedWarehouse;
    private List<AssetPosition> positionList;
    private List<AssetPosition> selectedPosition;

    /**
     * Creates a new instance of AssetCheckInitBean
     */
    public AssetCheckInitManagedBean() {
    }

    public void handleDialogReturnCategoryWhenNew(SelectEvent event) {
        if (event.getObject() != null) {
            AssetCategory e = (AssetCategory) event.getObject();
            queryCategory = e;
        }
    }

    @Override
    public void init() {
        super.init();
        deptList = departmentBean.findAll();
        warehouseList = warehouseBean.findAll();
        positionList = assetPositionBean.findByCompany(userManagedBean.getCompany());
        selectedDept = new ArrayList<>();
        selectedWarehouse = new ArrayList<>();
        selectedPosition = new ArrayList<>();
    }

    public void initAssetCheck() {
        if (queryFormType == null || "".equals(queryFormType)) {
            showErrorMsg("Error", "请先选择盘点方式");
            return;
        }
        if (queryCategory == null) {
            showErrorMsg("Error", "请先选择类别");
            return;
        }
        if (selectedWarehouse == null || selectedWarehouse.isEmpty()) {
            showErrorMsg("Error", "请先选择仓库");
            return;
        }
        if (queryFormType.equals("AC")) {
            if ("0000".equals(queryFormKind)) {
                if (selectedDept == null || selectedDept.isEmpty()) {
                    showErrorMsg("Error", "请先选择部门");
                    return;
                }
            } else if (selectedPosition.isEmpty()) {
                showErrorMsg("Error", "按位置分组时请选择位置");
                return;
            }
        }
        String company = userManagedBean.getCurrentCompany().getCompany();
        String creator = userManagedBean.getCurrentUser().getUsername();
        Map<String, Object> filters = new HashMap<>();
        Map<String, String> sorts = new LinkedHashMap<>();
        List<String> depts = new ArrayList<>();
        List<String> warehouses = new ArrayList<>();
        //仓库筛选条件
        selectedWarehouse.stream().forEach((w) -> {
            warehouses.add(w.getWarehouseno());
        });
        if (queryFormType.equals("AC")) {
            //卡片盘点
            //排序条件
            switch (queryFormKind) {
                case "1000":
                    sorts.put("position1.position", "ASC");
                    break;
                case "0100":
                    sorts.put("position2.position", "ASC");
                    break;
                default:
            }
            sorts.put("deptno", "ASC");
            sorts.put("userno", "ASC");
            sorts.put("warehouse.warehouseno", "ASC");
            if (queryCategory.getNoauto()) {
                sorts.put("formid", "ASC");
            } else {
                sorts.put("formdate", "DESC");
                sorts.put("formid", "ASC");
            }
            if (!"0000".equals(queryFormKind)) {
                //按存放位置盘点
                String formid = "";
                String ret = "";
                try {
                    for (AssetPosition p : selectedPosition) {
                        filters.clear();
                        filters.put("company =", company);
                        filters.put("assetItem.category.category =", queryCategory.getCategory());
                        filters.put("warehouse.warehouseno IN ", warehouses);
                        switch (queryFormKind) {
                            case "1000":
                                filters.put("position1.position =", p.getPosition());
                                break;
                            case "0100":
                                filters.put("position2.position =", p.getPosition());
                                break;
                            default:
                        }
                        formid = assetCheckBean.init(company, queryFormDate, queryFormType, queryFormKind, queryCategory, p.getPosition() + "_" + p.getName(), "", creator, filters, sorts);
                        if (formid != null && !"".equals(formid)) {
                            ret += formid + ";";
                        }
                    }
                    if (!"".equals(ret)) {
                        showInfoMsg("Info", "成功产生盘点单" + ret);
                        reset();
                    } else {
                        showErrorMsg("Error", "产生盘点单失败");
                    }
                } catch (Exception ex) {
                    showErrorMsg("Error", ex.getMessage());
                }
            } else if (queryMergeDept) {
                selectedDept.stream().forEach((d) -> {
                    depts.add(d.getDeptno());
                });
                filters.clear();
                filters.put("company =", company);
                filters.put("assetItem.category.category =", queryCategory.getCategory());
                filters.put("deptno IN ", depts);
                filters.put("warehouse.warehouseno IN ", warehouses);
                try {
                    String ret = assetCheckBean.init(company, queryFormDate, queryFormType, queryFormKind, queryCategory, depts.toString(), "", creator, filters, sorts);
                    if (ret != null && !"".equals(ret)) {
                        showInfoMsg("Info", "成功产生盘点单" + ret);
                        reset();
                    } else {
                        showErrorMsg("Error", "产生盘点单失败");
                    }
                } catch (Exception ex) {
                    showErrorMsg("Error", ex.getMessage());
                }
            } else {
                String formid = "";
                String ret = "";
                try {
                    for (Department d : selectedDept) {
                        filters.clear();
                        filters.put("company =", company);
                        filters.put("assetItem.category.category =", queryCategory.getCategory());
                        filters.put("deptno =", d.getDeptno());
                        filters.put("warehouse.warehouseno IN ", warehouses);
                        formid = assetCheckBean.init(company, queryFormDate, queryFormType, queryFormKind, queryCategory, d.getDeptno(), "", creator, filters, sorts);
                        if (formid != null && !"".equals(formid)) {
                            ret += formid + ";";
                        }
                    }
                    if (!"".equals(ret)) {
                        showInfoMsg("Info", "成功产生盘点单" + ret);
                        reset();
                    } else {
                        showErrorMsg("Error", "产生盘点单失败");
                    }
                } catch (Exception ex) {
                    showErrorMsg("Error", ex.getMessage());
                }
            }
        } else {
            //仓库盘点

        }
    }

    @Override
    public void query() {
        if (queryDeptno != null && !"".equals(queryDeptno)) {
            Map<String, Object> filters = new HashMap<>();
            Map<String, String> sorts = new LinkedHashMap<>();
            filters.put("deptno", queryDeptno);
            sorts.put("deptno", "ASC");
            deptList = departmentBean.findByFilters(filters, sorts);
        }
    }

    @Override
    public void reset() {
        super.reset();
        this.queryCategory = null;
        selectedDept.clear();
        selectedWarehouse.clear();
        selectedPosition.clear();
    }

    /**
     * @return the queryFormDate
     */
    public Date getQueryFormDate() {
        return queryFormDate;
    }

    /**
     * @param queryFormDate the queryFormDate to set
     */
    public void setQueryFormDate(Date queryFormDate) {
        this.queryFormDate = queryFormDate;
    }

    /**
     * @return the queryFormType
     */
    public String getQueryFormType() {
        return queryFormType;
    }

    /**
     * @param queryFormType the queryFormType to set
     */
    public void setQueryFormType(String queryFormType) {
        this.queryFormType = queryFormType;
    }

    /**
     * @return the queryFormKind
     */
    public String getQueryFormKind() {
        return queryFormKind;
    }

    /**
     * @param queryFormKind the queryFormKind to set
     */
    public void setQueryFormKind(String queryFormKind) {
        this.queryFormKind = queryFormKind;
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

    /**
     * @return the queryDeptno
     */
    public String getQueryDeptno() {
        return queryDeptno;
    }

    /**
     * @param queryDeptno the queryDeptno to set
     */
    public void setQueryDeptno(String queryDeptno) {
        this.queryDeptno = queryDeptno;
    }

    /**
     * @return the queryUserno
     */
    public String getQueryUserno() {
        return queryUserno;
    }

    /**
     * @param queryUserno the queryUserno to set
     */
    public void setQueryUserno(String queryUserno) {
        this.queryUserno = queryUserno;
    }

    /**
     * @return the queryPosition
     */
    public String getQueryPosition() {
        return queryPosition;
    }

    /**
     * @param queryPosition the queryPosition to set
     */
    public void setQueryPosition(String queryPosition) {
        this.queryPosition = queryPosition;
    }

    /**
     * @return the queryMergeDept
     */
    public boolean isQueryMergeDept() {
        return queryMergeDept;
    }

    /**
     * @param queryMergeDept the queryMergeDept to set
     */
    public void setQueryMergeDept(boolean queryMergeDept) {
        this.queryMergeDept = queryMergeDept;
    }

    /**
     * @return the deptList
     */
    public List<Department> getDeptList() {
        return deptList;
    }

    /**
     * @param deptList the deptList to set
     */
    public void setDeptList(List<Department> deptList) {
        this.deptList = deptList;
    }

    /**
     * @return the selectedDept
     */
    public List<Department> getSelectedDept() {
        return selectedDept;
    }

    /**
     * @param selectedDept the selectedDept to set
     */
    public void setSelectedDept(List<Department> selectedDept) {
        this.selectedDept = selectedDept;
    }

    /**
     * @return the warehouseList
     */
    public List<Warehouse> getWarehouseList() {
        return warehouseList;
    }

    /**
     * @param warehouseList the warehouseList to set
     */
    public void setWarehouseList(List<Warehouse> warehouseList) {
        this.warehouseList = warehouseList;
    }

    /**
     * @return the selectedWarehouse
     */
    public List<Warehouse> getSelectedWarehouse() {
        return selectedWarehouse;
    }

    /**
     * @param selectedWarehouse the selectedWarehouse to set
     */
    public void setSelectedWarehouse(List<Warehouse> selectedWarehouse) {
        this.selectedWarehouse = selectedWarehouse;
    }

    /**
     * @return the positionList
     */
    public List<AssetPosition> getPositionList() {
        return positionList;
    }

    /**
     * @param positionList the positionList to set
     */
    public void setPositionList(List<AssetPosition> positionList) {
        this.positionList = positionList;
    }

    /**
     * @return the selectedPosition
     */
    public List<AssetPosition> getSelectedPosition() {
        return selectedPosition;
    }

    /**
     * @param selectedPosition the selectedPosition to set
     */
    public void setSelectedPosition(List<AssetPosition> selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

}
