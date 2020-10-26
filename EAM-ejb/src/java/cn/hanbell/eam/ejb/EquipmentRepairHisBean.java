/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.EquipmentRepairHis;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.Query;

/**
 *
 * @author C2079
 */
@Stateless
@LocalBean
public class EquipmentRepairHisBean extends SuperEJBForEAM<EquipmentRepairHis> {

    public EquipmentRepairHisBean() {
        super(EquipmentRepairHis.class);
    }
     //获取维修转单统计表的List
    public List<EquipmentRepairHis> getRepairTransferStatisticsList(String staDate, String endDate) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT H.optuser,R.serviceusername,H.pid,R.assetno,A.assetDesc,R.hitchtime,H.note FROM equipmentrepairhis H,equipmentrepair R,assetcard A");
        sb.append(" WHERE R.formid=H.pid AND H.contenct='转派' AND A.formid=R.assetno");
       
        if (!"".equals(staDate)) {
            sb.append(" AND R.hitchtime>= ").append("'").append(staDate).append("'");
        }
        if (!"".equals(endDate)) {
            sb.append(" AND R.hitchtime< ").append("'").append(endDate).append("'");
        }
        sb.append(" ORDER BY R.hitchtime DESC");
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString());

        List results = query.getResultList();
        return results;
    }
   
}
