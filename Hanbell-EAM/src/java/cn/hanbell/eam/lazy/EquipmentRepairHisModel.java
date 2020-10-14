/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.lazy;

import cn.hanbell.eam.entity.EquipmentRepairHis;
import com.lightshell.comm.BaseLazyModel;
import com.lightshell.comm.SuperEJB;
import java.util.List;
import java.util.Map;
import org.primefaces.model.SortMeta;

/**
 *
 * @author C2079
 */
public class EquipmentRepairHisModel extends BaseLazyModel<EquipmentRepairHis> {
    public EquipmentRepairHisModel(SuperEJB superEJB) {
        this.superEJB = superEJB;
    }

    @Override
    public List<EquipmentRepairHis> load(int first, int pageSize, List<SortMeta> multiSortMeta, Map<String, Object> filters) {
        setDataList(superEJB.findByFilters(filters, first, pageSize, filters));
        return super.load(first, pageSize, multiSortMeta, filters); //To change body of generated methods, choose Tools | Templates.
    }
    
}
