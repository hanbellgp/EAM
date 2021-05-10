/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.EquipmentSpareRecodeDta;
import com.sun.javafx.scene.control.skin.VirtualFlow;
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

    //    获取每月备件消耗数量
    public List<Object[]> getSpareConsumeQty(String queryDateBegin, String queryDateEnd, String sarea, String type) {
        StringBuilder sbCK = new StringBuilder();
        sbCK.append("  SELECT ").append(type).append("(A.credate), SUM(A.cqty) FROM equipmentsparerecodedta A LEFT JOIN  equipmentsparerecode B ON A.pid=B.formid");
        sbCK.append("  WHERE A.pid LIKE '%CK%' AND B.sarea='").append(sarea).append("' AND   A.status='V' AND  A.credate>='").append(queryDateBegin).append("' and A.credate<='").append(queryDateEnd).append("'  GROUP BY ").append(type).append("(A.credate)");
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sbCK.toString());
        List<Object[]> ckList = query.getResultList();//出库对应的日期及数量
        StringBuilder sbTK = new StringBuilder();
        sbTK.append("  SELECT ").append(type).append("(A.credate), SUM(A.cqty) FROM equipmentsparerecodedta A LEFT JOIN  equipmentsparerecode B ON A.pid=B.formid");
        sbTK.append("  WHERE A.pid LIKE '%TK%' AND B.sarea='").append(sarea).append("' AND   A.status='V' AND  A.credate>='").append(queryDateBegin).append("' and A.credate<='").append(queryDateEnd).append("'  GROUP BY ").append(type).append("(A.credate)");
        query = getEntityManager().createNativeQuery(sbTK.toString());
        List<Object[]> tkList = query.getResultList();//退库对应的日期及数量
        for (Object[] ck : ckList) {
            for (Object[] tk : tkList) {
                if (ck[0] == tk[0]) {
                    ck[1] = Double.parseDouble(ck[1].toString()) - Double.parseDouble(tk[1].toString());
                }
            }
        }
        return ckList;
    }

    public List<EquipmentSpareRecodeDta> findByRemark(String remark) {
        Query query = getEntityManager().createNamedQuery("EquipmentSpareRecodeDta.findByRemark");
        query.setParameter("remark", remark);
        try {
            return query.getResultList();
        } catch (Exception ex) {
            return null;
        }
    }
}
