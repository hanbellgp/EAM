/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.EquipmentAnalyResultDta;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

@Stateless
@LocalBean
public class EquipmentAnalyResultDtaBean extends SuperEJBForEAM<EquipmentAnalyResultDta> {

    public EquipmentAnalyResultDtaBean() {
        super(EquipmentAnalyResultDta.class);
    }

    public List getEquipmentAnalyResultDtaList(String assetno, String month) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT E.assetno, D.checkarea,D.checkcontent,R.frequency,R.frequencyunit ,D.analysisresult,DAY(E.credate) FROM  equipmentanalyresultdta D LEFT JOIN equipmentanalyresult E");
        sb.append(" ON E.formid=D.pid AND E.standardlevel='一级'  LEFT JOIN  equipmentstandard R ON R.checkarea=D.checkarea AND R.checkcontent=D.checkcontent ");
        if (assetno != null && !"".equals(assetno)) {
            sb.append(" AND R.assetno LIKE '%").append(assetno).append("%'");
        }
        sb.append(" WHERE  E.credate LIKE '%").append(month).append("%'");
        if (assetno != null && !"".equals(assetno)) {
            sb.append(" AND E.assetno LIKE '%").append(assetno).append("%'");
        }
        sb.append(" GROUP BY  E.assetno, D.checkcontent,D.checkarea,   DAY(E.credate)");
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString());
        List<Object[]> results = query.getResultList();//未生成的计划保全项目

        Map<String, List<Object[]>> map = new HashMap<>();
        results.forEach(result -> {
            if (map.containsKey(result[0].toString() + result[1].toString() + result[2].toString())) {//判断是否已存在对应键号
                map.get(result[0].toString() + result[1].toString() + result[2].toString()).add(result);//直接在对应的map中添加数据
            } else {//map中不存在，新建key，用来存放数据
                List<Object[]> tmpList = new ArrayList<>();
                tmpList.add(result);
                map.put(result[0].toString() + result[1].toString() + result[2].toString(), tmpList);//新增一个键号
            }
        });
        List moList = new ArrayList();
        for (Map.Entry<String, List<Object[]>> entry : map.entrySet()) {
            List<Object[]> list = entry.getValue();//取出对应的值
            Object[] obj1 = new Object[35];
            for (Object[] obj : list) {
                obj1[0] = obj[0];
                obj1[1] = obj[1];
                obj1[2] = obj[2];
                obj1[3] = obj[3].toString() + obj[4].toString();
                switch (Integer.parseInt(obj[6].toString())) {
                    case 1:
                        obj1[4] = obj[5];
                        break;
                    case 2:
                        obj1[5] = obj[5];
                        break;
                    case 3:
                        obj1[6] = obj[5];
                        break;
                    case 4:
                        obj1[7] = obj[5];
                        break;
                    case 5:
                        obj1[8] = obj[5];
                        break;
                    case 6:
                        obj1[9] = obj[5];
                        break;
                    case 7:
                        obj1[10] = obj[5];
                        break;
                    case 8:
                        obj1[11] = obj[5];
                        break;
                    case 9:
                        obj1[12] = obj[5];
                        break;
                    case 10:
                        obj1[13] = obj[5];
                        break;
                    case 11:
                        obj1[14] = obj[5];
                        break;
                    case 12:
                        obj1[15] = obj[5];
                        break;
                    case 13:
                        obj1[16] = obj[5];
                        break;
                    case 14:
                        obj1[17] = obj[5];
                        break;
                    case 15:
                        obj1[18] = obj[5];
                        break;
                    case 16:
                        obj1[19] = obj[5];
                        break;
                    case 17:
                        obj1[20] = obj[5];
                        break;
                    case 18:
                        obj1[21] = obj[5];
                        break;
                    case 19:
                        obj1[22] = obj[5];
                        break;
                    case 20:
                        obj1[23] = obj[5];
                        break;
                    case 21:
                        obj1[24] = obj[5];
                        break;
                    case 22:
                        obj1[25] = obj[5];
                        break;
                    case 23:
                        obj1[26] = obj[5];
                        break;
                    case 24:
                        obj1[27] = obj[5];
                        break;
                    case 25:
                        obj1[28] = obj[5];
                        break;
                    case 26:
                        obj1[29] = obj[5];
                        break;
                    case 27:
                        obj1[30] = obj[5];
                        break;
                    case 28:
                        obj1[31] = obj[5];
                        break;
                    case 29:
                        obj1[32] = obj[5];
                        break;
                    case 30:
                        obj1[33] = obj[5];
                        break;
                    case 31:
                        obj1[34] = obj[5];
                        break;
                    default:
                        break;
                }
            }
            moList.add(obj1);
        }
        return moList;
    }

    public List getEquipmentPlanResultDtaList(String assetno, String yaer) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT E.assetno,D.checkarea,D.checkcontent,R.manhour,R.manpower,R.downtime,R.downunit,R.respondept,R.standardlevel,R.frequency,R.frequencyunit,D.analysisresult,MONTH(E.credate) FROM  equipmentanalyresultdta D LEFT JOIN equipmentanalyresult E");
        sb.append(" ON E.formid=D.pid AND E.standardlevel!='一级'  LEFT JOIN  equipmentstandard R ON R.checkarea=D.checkarea AND R.checkcontent=D.checkcontent ");
        if (assetno != null && !"".equals(assetno)) {
            sb.append(" AND R.assetno LIKE '%").append(assetno).append("%'");
        }
        sb.append(" WHERE  E.credate LIKE '%").append(yaer).append("%'");
        if (assetno != null && !"".equals(assetno)) {
            sb.append(" AND E.assetno LIKE '%").append(assetno).append("%'");
        }
        sb.append(" GROUP BY  E.assetno, D.checkcontent,D.checkarea,   MONTH(E.credate)");
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString());
        List<Object[]> results = query.getResultList();//未生成的计划保全项目

        Map<String, List<Object[]>> map = new HashMap<>();
        results.forEach(result -> {
            if (map.containsKey(result[0].toString() + result[1].toString() + result[2].toString())) {//判断是否已存在对应键号
                map.get(result[0].toString() + result[1].toString() + result[2].toString()).add(result);//直接在对应的map中添加数据
            } else {//map中不存在，新建key，用来存放数据
                List<Object[]> tmpList = new ArrayList<>();
                tmpList.add(result);
                map.put(result[0].toString() + result[1].toString() + result[2].toString(), tmpList);//新增一个键号
            }
        });
        List moList = new ArrayList();
        for (Map.Entry<String, List<Object[]>> entry : map.entrySet()) {
            List<Object[]> list = entry.getValue();//取出对应的值
            Object[] obj1 = new Object[21];
            for (Object[] obj : list) {
                obj1[0] = obj[0];
                obj1[1] = obj[1];
                obj1[2] = obj[2];
                obj1[3] = obj[3].toString();
                obj1[4] = obj[4].toString();
                obj1[5] = obj[5].toString() + obj[6].toString();
                obj1[6] = obj[7].toString();
                obj1[7] = obj[8];
                obj1[8] = obj[9].toString() + obj[10].toString();
                switch (Integer.parseInt(obj[12].toString())) {
                    case 1:
                        obj1[9] = obj[11];
                        break;
                    case 2:
                        obj1[10] = obj[11];
                        break;
                    case 3:
                        obj1[11] = obj[11];
                        break;
                    case 4:
                        obj1[12] = obj[11];
                        break;
                    case 5:
                        obj1[13] = obj[11];
                        break;
                    case 6:
                        obj1[14] = obj[11];
                        break;
                    case 7:
                        obj1[15] = obj[11];
                        break;
                    case 8:
                        obj1[16] = obj[11];
                        break;
                    case 9:
                        obj1[17] = obj[11];
                        ;
                        break;
                    case 10:
                        obj1[18] = obj[11];
                        break;
                    case 11:
                        obj1[19] = obj[11];
                        break;
                    case 12:
                        obj1[20] = obj[11];
                        break;

                    default:
                        break;
                }
            }
            moList.add(obj1);
        }
        return moList;
    }

}
