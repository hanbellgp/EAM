/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.lazy;

import cn.hanbell.eam.control.UserManagedBean;
import cn.hanbell.eam.entity.EquipmentSpareStock;
import com.lightshell.comm.BaseLazyModel;
import com.lightshell.comm.SuperEJB;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.primefaces.model.SortOrder;

/**
 *
 * @author C2079
 */
public class EquipmentSpareStockModel extends BaseLazyModel<EquipmentSpareStock> {

    private final UserManagedBean userManagedBean;

    public EquipmentSpareStockModel(SuperEJB superEJB, UserManagedBean userManagedBean) {
        this.superEJB = superEJB;
        this.userManagedBean = userManagedBean;
    }

    @Override
    public List<EquipmentSpareStock> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        filterFields.put("company =", userManagedBean.getCompany());
        setDataList(superEJB.findByFilters(filterFields, first, pageSize, sortFields));
        setRowCount(superEJB.getRowCount(filterFields));
        List<EquipmentSpareStock> list = new ArrayList<>();
        Map<String, List<EquipmentSpareStock>> map = new HashMap<>();
        for (EquipmentSpareStock eStock : dataList) {
            if (map.containsKey(eStock.getSparenum().getSparenum())) {//map中存在此备件编号，将数据存放当前key的map中
                map.get(eStock.getSparenum().getSparenum()).add(eStock);
            } else {//map中不存在，新建key，用来存放数据
                List<EquipmentSpareStock> tmpList = new ArrayList<>();
                tmpList.add(eStock);
                map.put(eStock.getSparenum().getSparenum(), tmpList);
            }
        }
        int i = 0;
        for (Map.Entry<String, List<EquipmentSpareStock>> entry : map.entrySet()) {
            List<EquipmentSpareStock> val = entry.getValue();
            EquipmentSpareStock eStock = new EquipmentSpareStock();
            i++;
            BigDecimal qty = BigDecimal.ZERO;
            for (EquipmentSpareStock equipmentSpareStock : val) {
                eStock = equipmentSpareStock;
                qty = qty.add(equipmentSpareStock.getQty());
            }
            eStock.setQty(qty);
            eStock.setId(i);
            list.add(eStock);
        }
        return list;
    }
}
