/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.EquipmentSpare;
import cn.hanbell.eam.entity.EquipmentSpareStock;
import cn.hanbell.eam.entity.EquipmentSpareStockResponse;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
    
    //获取库存数量List
    public List<EquipmentSpareStockResponse> getEquipmentSpareStockListByNativeQuery(String spareInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT * FROM equipmentsparestock T LEFT JOIN equipmentspare S ON T.sparenum = S.sparenum WHERE 1 = 1 AND qty > 0 ");
        if (!"".equals(spareInfo) && spareInfo != null) {
            sb.append(MessageFormat.format(" AND (S.sparedesc LIKE ''%{0}%'' OR T.sparenum LIKE ''%{0}%'') ", spareInfo));
        }
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString(),EquipmentSpareStock.class).setMaxResults(50);
        List<EquipmentSpareStock> results = query.getResultList();
        
        //List按照sparenum分组
        Map<EquipmentSpare,List<EquipmentSpareStock>> groupBySparenumMap = results.stream().collect(Collectors.groupingBy(EquipmentSpareStock::getSparenum));
        
        List<EquipmentSpareStockResponse> resList = new ArrayList<EquipmentSpareStockResponse>();
        groupBySparenumMap.forEach((key,value) -> {
            BigDecimal qtySum = BigDecimal.ZERO;
            for(int i = 0;i< value.size() ;i++){
                qtySum = qtySum.add(value.get(i).getQty());
            }
            EquipmentSpareStockResponse resTemp = new EquipmentSpareStockResponse(key,value,qtySum);
            resList.add(resTemp);
        });
        
        return resList;
    }


    //获取库存盘点数量List，按厂区及存放位置分类
    public List<EquipmentSpareStock> getEquipmentSpareStockCheckList(String sarea, String company) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT T.sparenum,S.sparedesc,S.sparemodel, C.sname,M.mname,sum(T.qty),T.sarea,T.slocation FROM  equipmentsparestock T");
        sb.append(" LEFT JOIN equipmentspare S ON T.sparenum=S.sparenum LEFT JOIN equipmentspareclass C ON S.scategory=C.scategory LEFT JOIN  equipmentsparemid M ON S.scategory=M.scategory AND S.mcategory=M.mcategory");
        sb.append(" Where  qty!=0");
        if (!"".equals(sarea) && sarea != null) {
            sb.append(" AND T.sarea='").append(sarea).append("'");
        }
        if (!"".equals(company) && company != null) {
            sb.append(" AND T.company='").append(company).append("'");
        }
        sb.append(" GROUP BY T.slocation ,sparenum ORDER BY sparenum");
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

    public List<EquipmentSpareStock> findBySparenumAndSarea(String sparenum, String sarea) {
        Query query = getEntityManager().createNamedQuery("EquipmentSpareStock.findBySparenumAndSarea");
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

    public List<EquipmentSpareStock> findBySparenumAndLocation(String sparenum, String slocation) {
        Query query = getEntityManager().createNamedQuery("EquipmentSpareStock.findBySparenumAndLocation");
        query.setParameter("sparenum", sparenum);
        query.setParameter("slocation", slocation);
        try {
            List results = query.getResultList();
            return results;
        } catch (Exception ex) {
            return null;
        }
    }
}
