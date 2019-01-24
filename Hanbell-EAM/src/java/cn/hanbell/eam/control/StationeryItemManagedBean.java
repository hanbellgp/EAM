/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "stationeryItemManagedBean")
@SessionScoped
public class StationeryItemManagedBean extends AssetItemManagedBean {

    private List<String> paramCategory;

    /**
     * Creates a new instance of StationeryItemManagedBean
     */
    public StationeryItemManagedBean() {

    }

    @Override
    protected boolean doBeforePersist() throws Exception {
        if (newEntity != null) {
            if (newEntity.getCategory() == null) {
                showErrorMsg("Error", "请选择分类");
                return false;
            }
            if (this.getCurrentPrgGrant().getSysprg().getNoauto()) {
                String formid = this.superEJB.getFormId(getDate(), this.getCurrentPrgGrant().getSysprg().getNolead(), this.getCurrentPrgGrant().getSysprg().getNoformat(), this.getCurrentPrgGrant().getSysprg().getNoseqlen(), "itemno");
                this.newEntity.setItemno(formid);
            }
            return true;
        }
        return false;
    }

    @Override
    public void init() {
        super.init();
        openParams = new HashMap<>();
        model.getFilterFields().put("category.category", "C1");
        model.getSortFields().clear();
        model.getSortFields().put("status", "ASC");
        model.getSortFields().put("category.category", "ASC");
        model.getSortFields().put("itemno", "ASC");
    }

    @Override
    public void openDialog(String view) {
        switch (view) {
            case "assetcategorySelect":
                if (paramCategory == null) {
                    paramCategory = new ArrayList<>();
                } else {
                    paramCategory.clear();
                }
                paramCategory.add("C1");
                openParams.clear();
                openParams.put("category", paramCategory);
                super.openDialog("assetcategorySelect", openParams);
                break;
            default:
                super.openDialog(view);
        }
    }

    @Override
    public void query() {
        super.query();
        this.model.getFilterFields().put("category.category", "C1");
    }

    @Override
    public void reset() {
        super.reset();
        this.model.getFilterFields().put("category.category", "C1");
    }

}
