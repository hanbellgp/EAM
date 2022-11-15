/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.entity.AssetParameterDta;
import cn.hanbell.eam.entity.AssetParameterDta_;
import cn.hanbell.eam.entity.EquipmentAnalyStage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
public class EquipmentAnalyStageBean extends SuperEJBForEAM<EquipmentAnalyStage> {

    public EquipmentAnalyStageBean() {
        super(EquipmentAnalyStage.class);
    }

    @Override
    public List<EquipmentAnalyStage> findByFilters(Map<String, Object> filters, int first, int pageSize, Map<String, String> orderBy) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT e FROM ");
        sb.append(this.className);
        sb.append(" e WHERE 1=1 ");
        Map<String, Object> strMap = new LinkedHashMap<>();
        //给Map排序

        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            strMap.put(key, value);
        }
        filters = strMap;
        if (filters != null) {
            this.setQueryFilter(sb, filters);
        }
        sb.append("  AND (e.status ='X')");
        if (orderBy != null && orderBy.size() > 0) {
            sb.append(" ORDER BY ");
            for (final Map.Entry<String, String> o : orderBy.entrySet()) {
                sb.append(" e.").append(o.getKey()).append(" ").append(o.getValue()).append(",");
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
        }

        //生成SQL
        Query query = getEntityManager().createQuery(sb.toString()).setFirstResult(first).setMaxResults(pageSize);
        //参数赋值
        if (filters != null) {
            this.setQueryParam(query, filters);
        }
        List results = query.getResultList();
        return results;
    }

    @Override
    public int getRowCount(Map<String, Object> filters) {
        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(e) FROM ");
        sb.append(this.className);
        sb.append(" e WHERE 1=1 ");
        Map<String, Object> strMap = new LinkedHashMap<>();
        //给Map排序
        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            strMap.put(key, value);
        }
        filters = strMap;
        sb.append("  AND (e.status ='X')");
        if (filters != null) {
            this.setQueryFilter(sb, filters);
        }

        final Query query = this.getEntityManager().createQuery(sb.toString());
        if (filters != null) {
            this.setQueryParam(query, filters);
        }
        return Integer.parseInt(query.getSingleResult().toString());
    }

    //获取各课基本资料信息
    public List<EquipmentAnalyStage> getEquipmentAnalyInformation(String formid) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM equipmentanalystage WHERE formid IN (").append(formid).append(")  and status='X'");
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString(), EquipmentAnalyStage.class);
        List<EquipmentAnalyStage> results = query.getResultList();
        return results;
    }

    //获取保全资料是否有生成过
    public List<Object[]> getEquipmentAnalyResultCount(String formid) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT assetno,standardlevel,count(assetno) FROM equipmentanalyresult WHERE assetno IN (").append(formid).append(") GROUP BY  assetno,standardlevel");
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString());
        List<Object[]> results = query.getResultList();
        return results;
    }

    //获取是否有点检基准
    public List<Object[]> getEquipmentStandardCount(String formid) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT assetno,standardlevel,count(assetno) FROM equipmentstandard WHERE assetno IN (").append(formid).append(") GROUP BY  assetno,standardlevel");
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString());
        List<Object[]> results = query.getResultList();
        return results;
    }

    public List<Object[]> getAssetnoList(String assetno, String dept) {
        StringBuilder sql1 = new StringBuilder();
        sql1.append("SELECT A.deptname,A.formid,P.paramvalue FROM assetcard  A LEFT JOIN assetitem  T  ON  A.itemno=T.itemno   LEFT JOIN assetparameterdta P ON P.pid = A.formid AND p.paramname = '设备等级'   WHERE  A.company='C' AND A.deptname IS NOT NULL  and  T.categoryid ='3'  and A.qty!=0");
        if (!"".equals(dept) && dept != null) {
            sql1.append(" and A.deptname='" + dept + "'");
        }
        Query query = getEntityManager().createNativeQuery(sql1.toString());
        List<Object[]> assetCardList = query.getResultList();//获取所有的设备
        StringBuilder sql2 = new StringBuilder();
        sql2.append(" SELECT * FROM equipmentanalystage WHERE (status = 'X' OR (stage = 'step7' AND status = 'Y')) AND company='C'");
      
        //生成SQL
        query = getEntityManager().createNativeQuery(sql2.toString(), EquipmentAnalyStage.class);
        List<EquipmentAnalyStage> equipmentAnalyStageList = query.getResultList();//获取所有的设备
        List<Object[]> results = query.getResultList();
        Map<String, List<Object[]>> strMap = new LinkedHashMap<>();
        for (Object[] aCard : assetCardList) {
            if (!strMap.containsKey(aCard[0].toString())) {//是否包含该部门
                List<Object[]> mapList = new ArrayList<>();
                mapList.add(aCard);
                strMap.put(aCard[0].toString(), mapList);//没有则新的KEY
            } else {
                List<Object[]> mapList = strMap.get(aCard[0].toString());
                mapList.add(aCard);
                strMap.put(aCard[0].toString(), mapList);//覆盖原有的value
            }
        }
        results.clear();//清空数据，方便回传
        int totalSum = 0;//总机台数量

        for (Map.Entry<String, List<Object[]>> entry : strMap.entrySet()) {
            int totalA = 0;
            int totalB = 0;
            int totalC = 0;
            int totalD = 0;
            List<Object[]> val = entry.getValue();//获取对应value值
            Object[] obj = new Object[39];
            for (int i = 1; i < 39; i++) {//初始化数据为0
                obj[i] = 0;
            }
            obj[0] = entry.getKey();
            for (Object[] objects : val) {
                String status = "";
                String gradeId = "";
                String resultId = "";
                if (objects[2] != null) {
                    gradeId = objects[2].toString();
                }
                for (EquipmentAnalyStage eStage : equipmentAnalyStageList) {//判断是否在该设备下
                    if (eStage.getFormid().getFormid().equals(objects[1].toString())) {
                        status = eStage.getStage();
                        resultId = eStage.getStatus();
                    }
                }
                if (gradeId.equals("A级")) {
                    switch (status) {
                        case "step1":
                            obj[10] = Integer.parseInt(obj[10].toString()) + 1;
                            totalA = totalA + 1;
                            break;
                        case "step2":
                            obj[3] = Integer.parseInt(obj[3].toString()) + 1;
                            totalA = totalA + 1;
                            break;
                        case "step3":
                            obj[4] = Integer.parseInt(obj[4].toString()) + 1;
                            totalA = totalA + 1;
                            break;
                        case "step4":
                            obj[5] = Integer.parseInt(obj[5].toString()) + 1;
                            totalA = totalA + 1;
                            break;
                        case "step5":
                            obj[6] = Integer.parseInt(obj[6].toString()) + 1;
                            totalA = totalA + 1;
                            break;
                        case "step6":
                            obj[7] = Integer.parseInt(obj[7].toString()) + 1;
                            totalA = totalA + 1;
                            break;
                        case "step7":
                            if (resultId.equals("X")) {
                                obj[8] = Integer.parseInt(obj[8].toString()) + 1;
                                totalA = totalA + 1;
                            } else {
                                obj[9] = Integer.parseInt(obj[9].toString()) + 1;
                                totalA = totalA + 1;
                            }
                            break;
                        default:
                            obj[10] = Integer.parseInt(obj[10].toString()) + 1;
                            totalA = totalA + 1;
                            break;
                    }
                } else if (gradeId.equals("B级")) {
                    switch (status) {
                        case "step1":
                            obj[19] = Integer.parseInt(obj[19].toString()) + 1;
                            totalB = totalB + 1;
                            break;
                        case "step2":
                            obj[12] = Integer.parseInt(obj[12].toString()) + 1;
                            totalB = totalB + 1;
                            break;
                        case "step3":
                            obj[13] = Integer.parseInt(obj[13].toString()) + 1;
                            totalB = totalB + 1;
                            break;
                        case "step4":
                            obj[14] = Integer.parseInt(obj[14].toString()) + 1;
                            totalB = totalB + 1;
                            break;
                        case "step5":
                            obj[15] = Integer.parseInt(obj[15].toString()) + 1;
                            totalB = totalB + 1;
                            break;
                        case "step6":
                            obj[16] = Integer.parseInt(obj[16].toString()) + 1;
                            totalB = totalB + 1;
                            break;
                        case "step7":
                            if (resultId.equals("X")) {
                                obj[17] = Integer.parseInt(obj[17].toString()) + 1;
                                totalB = totalB + 1;
                            } else {
                                obj[18] = Integer.parseInt(obj[18].toString()) + 1;
                                totalB = totalB + 1;
                            }
                            break;
                        default:
                            obj[19] = Integer.parseInt(obj[19].toString()) + 1;
                            totalB = totalB + 1;
                            break;
                    }
                } else if (gradeId.equals("C级")) {
                    switch (status) {
                        case "step1":
                            obj[28] = Integer.parseInt(obj[28].toString()) + 1;
                            totalC = totalC + 1;
                            break;
                        case "step2":
                            obj[21] = Integer.parseInt(obj[21].toString()) + 1;
                            totalC = totalC + 1;
                            break;
                        case "step3":
                            obj[22] = Integer.parseInt(obj[22].toString()) + 1;
                            totalC = totalC + 1;
                            break;
                        case "step4":
                            obj[23] = Integer.parseInt(obj[23].toString()) + 1;
                            totalC = totalC + 1;
                            break;
                        case "step5":
                            obj[24] = Integer.parseInt(obj[24].toString()) + 1;
                            totalC = totalC + 1;
                            break;
                        case "step6":
                            obj[25] = Integer.parseInt(obj[25].toString()) + 1;
                            totalC = totalC + 1;
                            break;
                        case "step7":
                            if (resultId.equals("X")) {
                                obj[26] = Integer.parseInt(obj[26].toString()) + 1;
                                totalC = totalC + 1;
                            } else {
                                obj[27] = Integer.parseInt(obj[27].toString()) + 1;
                                totalC = totalC + 1;
                            }
                            break;
                        default:
                            obj[28] = Integer.parseInt(obj[28].toString()) + 1;
                            totalC = totalC + 1;
                            break;
                    }
                } else {
                    switch (status) {
                        case "step1":
                            obj[37] = objects[3];
                            totalD = totalD + 1;
                            break;
                        case "step2":
                            obj[31] = objects[3];
                            totalD = totalD + 1;
                            break;
                        case "step3":
                            obj[32] = objects[3];
                            totalD = totalD + 1;
                            break;
                        case "step4":
                            obj[33] = objects[3];
                            totalD = totalD + 1;
                            break;
                        case "step5":
                            obj[34] = objects[3];
                            totalD = totalD + 1;
                            break;
                        case "step6":
                            obj[35] = objects[3];
                            totalD = totalD + 1;
                            break;
                        case "step7":
                            if (resultId.equals("X")) {
                                obj[36] = objects[3];
                                totalD = totalD + 1;
                            } else {
                                obj[37] = objects[3];
                                totalD = totalD + 1;
                            }
                            break;
                        default:
                            obj[37] = Integer.parseInt(obj[37].toString()) + 1;
                            totalD = totalD + 1;
                    }
                }
            }
            obj[2] = totalA;
            obj[11] = totalB;
            obj[20] = totalC;
            obj[29] = totalD;
            obj[1] = totalA + totalB + totalC + totalD;
            results.add(obj);
        }
        Object[] obj2 = new Object[39];
        obj2[0] = 999;

        for (Object[] result : results) {
            for (int i = 1; i < 38; i++) {
                if (obj2[i] != null) {
                    obj2[i] = Integer.parseInt(result[i].toString()) + Integer.parseInt(obj2[i].toString());
                } else {
                    obj2[i] = Integer.parseInt(result[i].toString());
                }
            }
        }
        results.add(obj2);
        return results;
    }

}
