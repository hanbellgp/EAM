/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.lazy;

import cn.hanbell.eam.control.UserManagedBean;
import cn.hanbell.eam.entity.AssetApplyThrow;
import com.lightshell.comm.BaseLazyModel;
import com.lightshell.comm.SuperEJB;
import java.util.List;
import java.util.Map;
import org.primefaces.model.SortOrder;

/**
 *
 * @author C0160
 */
public class AssetApplyThrowModel extends BaseLazyModel<AssetApplyThrow> {

    private final UserManagedBean userManagedBean;

    public AssetApplyThrowModel(SuperEJB superEJB, UserManagedBean userManagedBean) {
        this.superEJB = superEJB;
        this.userManagedBean = userManagedBean;
    }

    @Override
    public List<AssetApplyThrow> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        filterFields.put("assetApply.company =", userManagedBean.getCompany());
        filterFields.put("assetApply.status =", "V");
        setDataList(superEJB.findByFilters(filterFields, first, pageSize, sortFields));
        setRowCount(superEJB.getRowCount(filterFields));
        return dataList;
    }

}
