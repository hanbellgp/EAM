/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.EquipmentSpareRecodeDta;
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
public class EquipmentSpareRecodeDtaBean extends SuperEJBForEAM<EquipmentSpareRecodeDta> {

    public EquipmentSpareRecodeDtaBean() {
        super(EquipmentSpareRecodeDta.class);
    }

    public List<EquipmentSpareRecodeDta> getEquipmentSpareRecodeDtaList(String formid) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT CK.sparenum,CK.sparedesc,CK.sparemodel, CASE WHEN TK.cqty IS NULL  THEN CK.cqty ELSE CK.cqty-TK.cqty END ,convert( CASE WHEN TK.cqty IS NULL  THEN CK.cqty*CK.uprice ELSE  (CK.cqty*CK.uprice)-(TK.cqty*TK.uprice) END,DECIMAL(10,2)) FROM (");
        sb.append(" SELECT A.pid,A.sparenum,C.sparedesc,C.sparemodel,sum(A.cqty) cqty,A.uprice FROM equipmentsparerecodedta A LEFT JOIN equipmentspare C ON A.sparenum=C.sparenum");
        sb.append(" LEFT JOIN equipmentsparerecode B ON A .pid=B.formid  WHERE  (B.relano='").append(formid).append("' OR B.remark='").append(formid).append("') AND B.status='V'");
        sb.append(" AND A.pid LIKE 'CK%' GROUP BY sparenum) CK LEFT JOIN ( SELECT A.pid,A.sparenum,sum(A.cqty) cqty,A.uprice FROM equipmentsparerecodedta A LEFT JOIN equipmentsparerecode B ");
        sb.append(" ON A .pid=B.formid  WHERE  (B.relano='").append(formid).append("' OR B.remark='").append(formid).append("') AND B.status='V' AND A.pid LIKE 'TK%' GROUP BY sparenum) TK ON  CK.sparenum=TK.sparenum WHERE  CASE WHEN TK.cqty IS NULL  THEN CK.cqty ELSE CK.cqty-TK.cqty END >0");
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString());
        List results = query.getResultList();
        return results;
    }
}
