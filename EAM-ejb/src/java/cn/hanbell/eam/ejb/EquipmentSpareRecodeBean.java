/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.EquipmentSpareRecode;
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
public class EquipmentSpareRecodeBean extends SuperEJBForEAM<EquipmentSpareRecode> {

    public EquipmentSpareRecodeBean() {
        super(EquipmentSpareRecode.class);
    }

    public List<EquipmentSpareRecode> findByScategory(String srcformid) {
        Query query = getEntityManager().createNamedQuery("EquipmentSpareMid.findByScategory");
        query.setParameter("scategory", srcformid);
        try {
            return query.getResultList();
        } catch (Exception ex) {
            return null;
        }
    }
    public List<EquipmentSpareRecode> findByRelano(String relano) {
        Query query = getEntityManager().createNamedQuery("EquipmentSpareRecode.findByRelano");
        query.setParameter("relano", relano);
        query.setParameter("status", "V");
        try {
            return query.getResultList();
        } catch (Exception ex) {
            return null;
        }
    }

    public String getAcceptTypeName(String acceotType) {
        switch (acceotType) {
            case "10":
                return "手工入库";
            case "20":
                return "手工出库";
            case "25":
                return "维修领料";
            case "30":
                return "手工退库";
            case "35":
                return "维修退料";
            default:
                return "";
        }
    }
}
