/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.EquipmentSpareStock;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.Query;

/**
 *
 * @author C2079
 */
@Stateless
@LocalBean
public class EquipmentSpareStockBean extends SuperEJBForEAM<EquipmentSpareStock> {

    public EquipmentSpareStockBean() {
        super(EquipmentSpareStock.class);
    }

    //获取库存数量List
    public List<EquipmentSpareStock> getEquipmentSpareStockList(String sparenum, String sparedesc) {
        StringBuilder sb = new StringBuilder();
        sb.append(" Select T.sparenum,S.sparedesc,S.sparemodel, C.sname,M.mname,sum(T.qty) FROM  equipmentsparestock T  LEFT JOIN equipmentspare S on T.sparenum=S.sparenum");
        sb.append(" LEFT JOIN equipmentspareclass C ON S.scategory=C.scategory LEFT JOIN  equipmentsparemid M ON S.scategory=M.scategory AND S.mcategory=M.mcategory Where 1=1");
        if (!"".equals(sparenum) && sparenum != null) {
            sb.append(" AND S.sparedesc LIKE ").append("'%").append(sparenum).append("%'");
        }
        if (!"".equals(sparedesc) && sparedesc != null) {
            sb.append(" AND T.sparenum LIKE  ").append("'%").append(sparedesc).append("%'");
        }
        sb.append(" GROUP BY sparenum");
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString());
        List results = query.getResultList();
        return results;
    }

    public List<EquipmentSpareStock> findBySparenum(String sparenum) {
        Query query = getEntityManager().createNamedQuery("EquipmentSpareStock.findBySparenum");
        query.setParameter("sparenum", sparenum);
        try {
            List results = query.getResultList();
            return results;
        } catch (Exception ex) {
            return null;
        }
    }

    public EquipmentSpareStock findBySparenumAndRemark(String sparenum, String remark) {
        Query query = getEntityManager().createNamedQuery("EquipmentSpareStock.findBySparenumAndRemark");
        query.setParameter("sparenum", sparenum);
        query.setParameter("remark", remark);
        try {
            Object o = query.getSingleResult();
            return (EquipmentSpareStock) o;
        } catch (Exception ex) {
            return null;
        }
    }

    public List<EquipmentSpareStock> findBySparenumAndLocation(String sparenum, String sarea) {
        Query query = getEntityManager().createNamedQuery("EquipmentSpareStock.findBySparenumAndLocation");
        query.setParameter("sparenum", sparenum);
        query.setParameter("sarea", sarea);
        query.setParameter("qty", 0);
        try {
            List results = query.getResultList();
            return results;
        } catch (Exception ex) {
            return null;
        }
    }

}
