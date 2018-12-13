/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.entity.AssetCategory;
import cn.hanbell.eam.entity.AssetItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "stationeryAcceptanceManagedBean")
@SessionScoped
public class StationeryAcceptanceManagedBean extends AssetAcceptanceManagedBean {

    private List<String> paramCategory;

    public StationeryAcceptanceManagedBean() {

    }

    @Override
    public void handleDialogReturnWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentEntity != null) {
            AssetCategory e = (AssetCategory) event.getObject();
            currentDetail.setSrcformid(e.getCategory());
            currentDetail.setAssetItem(null);
            if (e.getWarehouse() != null) {
                currentDetail.setWarehouse(e.getWarehouse());
            }
        }
    }

    @Override
    public void handleDialogReturnAssetItemWhenDetailEdit(SelectEvent event) {
        if (event.getObject() != null && currentDetail != null) {
            AssetItem e = (AssetItem) event.getObject();
            currentDetail.setAssetItem(e);
            currentDetail.setUnit(e.getUnit());
            currentDetail.setPrice(e.getPurprice());
        }
    }

    @Override
    public void init() {
        openParams = new HashMap<>();
        super.init();
    }

    @Override
    public void openDialog(String view) {
        openParams.clear();
        switch (view) {
            case "assetcategorySelect":
                if (paramCategory == null) {
                    paramCategory = new ArrayList<>();
                } else {
                    paramCategory.clear();
                }
                paramCategory.add("C1");
                openParams.put("category", paramCategory);
                super.openDialog("assetcategorySelect", openParams);
                break;
            case "assetitemSelect":
                if (currentDetail != null && currentDetail.getSrcformid() != null && !"".equals(currentDetail.getSrcformid())) {
                    if (paramCategory == null) {
                        paramCategory = new ArrayList<>();
                    } else {
                        paramCategory.clear();
                    }
                    paramCategory.add(currentDetail.getSrcformid());
                }
                openParams.put("category", paramCategory);
                super.openDialog("assetitemSelect", openParams);
                break;
            default:
                super.openDialog(view);
        }
    }

}
