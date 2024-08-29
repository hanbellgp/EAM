/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.lazy;

import cn.hanbell.eam.control.UserManagedBean;
import cn.hanbell.eam.ejb.AssetCardSpecialBean;
import cn.hanbell.eam.ejb.EquipmentRepairBean;
import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.entity.EquipmentRepair;
import com.lightshell.comm.BaseLazyModel;
import com.lightshell.comm.SuperEJB;
import java.util.List;
import java.util.Map;
import org.primefaces.model.SortOrder;

/**
 *
 * @author C2079
 */
public class EquipmentRepairModel extends BaseLazyModel<EquipmentRepair> {

    private final UserManagedBean userManagedBean;
    protected EquipmentRepairBean equipmentRepairBean;
    private final AssetCardSpecialBean assetCardSpecialBean;

    public EquipmentRepairModel(EquipmentRepairBean equipmentRepairBean, UserManagedBean userManagedBean, AssetCardSpecialBean assetCardSpecialBean) {
        this.equipmentRepairBean = equipmentRepairBean;
        this.assetCardSpecialBean = assetCardSpecialBean;
        this.userManagedBean = userManagedBean;

    }

    @Override
    public List<EquipmentRepair> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

        if (sortField != null) {
            String sort = sortOrder.toString().substring(0, sortOrder.toString().length() - 6);
            sortFields.put(sortField, sort);
        }
        //sortFields.put(sortField, sortOrder);
        setDataList(equipmentRepairBean.findByFilters(filterFields, first, pageSize, sortFields));
        setRowCount(equipmentRepairBean.getRowCount(filterFields));
        for (EquipmentRepair eRepair : dataList) {
            if (eRepair.getItemno().equals("AS000")) {//非固定资产时重新给改资产赋值
                String assetno = equipmentRepairBean.getAssetno(eRepair.getFormid());
                  AssetCard assetCardTemp = assetCardSpecialBean.transitionAssetCardSpecial(assetCardSpecialBean.findByAssetno(assetno));
                eRepair.setAssetno(assetCardTemp);
            }
        }
        sortFields.clear();
        return dataList;
    }

}
