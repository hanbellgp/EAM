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
    public List<EquipmentRepairHis> getRepairTransferStatisticsList(String staDate, String endDate,String companySql) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT H.optuser,R.serviceusername,H.pid,R.assetno,if(r.assetno IS NULL ,'其他',A.assetDesc) assetDesc,R.hitchtime,H.note");
        sb.append(" FROM equipmentrepairhis H LEFT JOIN equipmentrepair R ON R.formid = H.pid LEFT JOIN assetcard A  ON A.formid = R.assetno WHERE H.contenct = '转派'");
        if (!"".equals(companySql)) {
            sb.append(" AND (").append(companySql).append(" )");
        }
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
