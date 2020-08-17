/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.lazy;

import cn.hanbell.eam.control.UserManagedBean;
import cn.hanbell.eam.entity.Equipmentspare;
import com.lightshell.comm.BaseLazyModel;
import com.lightshell.comm.SuperEJB;
import java.util.List;
import java.util.Map;
import org.primefaces.model.SortOrder;

/**
 *
 * @author C0160
 */
public class EquipmentspareModel extends BaseLazyModel<Equipmentspare> {

    private final UserManagedBean userManagedBean;

    public EquipmentspareModel(SuperEJB superEJB, UserManagedBean userManagedBean) {
        this.superEJB = superEJB;
        this.userManagedBean = userManagedBean;
    }

    @Override
    public List<Equipmentspare> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        filterFields.put("company =", userManagedBean.getCompany());
        setDataList(superEJB.findByFilters(filterFields, first, pageSize, sortFields));
        setRowCount(superEJB.getRowCount(filterFields));
        return dataList;
    }
}
