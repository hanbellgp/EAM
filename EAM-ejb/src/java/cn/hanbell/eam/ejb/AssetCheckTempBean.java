/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.AssetCheckTemp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Query;

/**
 *
 * @author C2231
 */
@Stateless
@LocalBean
public class AssetCheckTempBean extends SuperEJBForEAM<AssetCheckTemp> {

    public AssetCheckTempBean() {
        super(AssetCheckTemp.class);
    }

    public List<AssetCheckTemp> getAssetCheckTempList(int itimes, String formid, String credate) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT e.formid,e.itemno,ifnull(d.assetDesc,''),e.assetno,cast(e.qty AS DECIMAL(16,2)),e.itimes,e.deptname,e.username FROM assetchecktemp e");
        sb.append(" LEFT JOIN assetcard d ON e.assetno=d.formid");
        sb.append(" WHERE 1=1");
        if (!formid.equals("")) {
            sb.append(" AND e.formid=").append("'").append(formid).append("'");
        }
        if (itimes == 1 || itimes == 0) {
            sb.append(" AND e.itimes=").append(itimes);
        }
        if (!credate.equals("")) {
            try {
                String pattern = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                String date = sdf.format(sdf.parse(credate));
                sb.append(" AND e.credate LIKE '").append(date).append("%'");
            } catch (ParseException ex) {
                Logger.getLogger(AssetCheckTempBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString());
        List results = query.getResultList();
        return results;
    }

    public List<AssetCheckTemp> getAssetCheckTempDetailList(String formid, String credate) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT a.pid,a.itemno,ifnull(d.assetDesc,''),a.assetno,a.deptname,a.username,a.actqty, ifnull(cast(e.qty AS DECIMAL(16,2)),0) AS qty,(ifnull(cast(e.qty AS DECIMAL(16,2)),0)-a.actqty) AS diffqty ");
        sb.append("FROM assetcheckdetail a left JOIN  (select * from assetchecktemp where itimes=1) e ON e.assetno = a.assetno AND e.formid = a.pid ");
        sb.append("LEFT JOIN assetcard d ON a.assetno=d.formid");
        sb.append(" WHERE 1=1");
        if (!formid.equals("")) {
            sb.append(" AND a.pid = '").append(formid).append("'");
        }
        if (!credate.equals("")) {
            try {
                String pattern = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                String date = sdf.format(sdf.parse(credate));
                sb.append(" AND e.credate LIKE '").append(date).append("%'");
            } catch (ParseException ex) {
                Logger.getLogger(AssetCheckTempBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        sb.append(" UNION ");
        sb.append(" SELECT e.formid,e.itemno,ifnull(d.assetDesc,''),e.assetno,e.deptname,e.username,a.actqty,cast(e.qty AS DECIMAL(16, 2)) AS qty,(ifnull(cast(e.qty AS DECIMAL(16,2)),0)-a.actqty) AS diffqty");
        sb.append(" FROM assetcheckdetail a RIGHT JOIN(SELECT * FROM assetchecktemp WHERE itimes = 1) e");
        sb.append(" ON e.assetno = a.assetno AND e.formid = a.pid LEFT JOIN assetcard d ON a.assetno=d.formid");
        sb.append(" WHERE 1=1");
        if (!formid.equals("")) {
            sb.append(" AND a.pid = '").append(formid).append("'");
        }
        if (!credate.equals("")) {
            try {
                String pattern = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                String date = sdf.format(sdf.parse(credate));
                sb.append(" AND e.credate LIKE '").append(date).append("%'");
            } catch (ParseException ex) {
                Logger.getLogger(AssetCheckTempBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        sb.append(" AND a.actqty is NULL ");

        Query query = getEntityManager().createNativeQuery(sb.toString());
        List results = query.getResultList();
        return results;
    }

    public List<AssetCheckTemp> getAssetCheckTempsList(String formid, String credate) {
        StringBuilder sb = new StringBuilder();
        sb.append("select e.formid,e.itemno,ifnull(d.assetDesc,''),e.assetno,e.deptname,e.username,ifnull(e.qty,0),ifnull(a.qty,0),ifnull(a.qty,0)-ifnull(e.qty,0) as diff,ifnull(e.iaddress,'')");
        sb.append(" FROM assetchecktemp e LEFT JOIN assetcard d ON e.assetno=d.formid");
        sb.append(" left JOIN (select * from assetchecktemp where itimes=1) a ON e.assetno = a.assetno AND a.formid=e.formid");
        sb.append("  WHERE e.itimes = 0");
        if (!formid.equals("")) {
            sb.append(" AND e.formid = '").append(formid).append("'");
        }
        if (!credate.equals("")) {
            try {
                String pattern = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                String date = sdf.format(sdf.parse(credate));
                sb.append(" AND e.credate LIKE '").append(date).append("%'");
            } catch (ParseException ex) {
                Logger.getLogger(AssetCheckTempBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        sb.append(" UNION ");
        sb.append("select e.formid,e.itemno,ifnull(d.assetDesc,''),e.assetno,e.deptname,e.username,ifnull(cast(a.qty AS DECIMAL(16,2)),0),ifnull(cast(e.qty AS DECIMAL(16,2)),0),ifnull(cast(e.qty AS DECIMAL(16,2)),0)-ifnull(cast(a.qty AS DECIMAL(16,2)),0) as diff,ifnull(a.iaddress,'')");
        sb.append(" FROM assetchecktemp e LEFT JOIN assetcard d ON e.assetno=d.formid");
        sb.append(" left JOIN (select * from assetchecktemp where itimes=0) a ON e.assetno = a.assetno AND a.formid=e.formid");
        sb.append("  WHERE e.itimes = 1");
        if (!formid.equals("")) {
            sb.append(" AND e.formid = '").append(formid).append("'");
        }
        if (!credate.equals("")) {
            try {
                String pattern = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                String date = sdf.format(sdf.parse(credate));
                sb.append(" AND e.credate LIKE '").append(date).append("%'");
            } catch (ParseException ex) {
                Logger.getLogger(AssetCheckTempBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Query query = getEntityManager().createNativeQuery(sb.toString());
        List results = query.getResultList();
        return results;
    }

}
