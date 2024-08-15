/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.AssetCard;
import cn.hanbell.eam.entity.AssetCardSpecial;
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
public class AssetCardSpecialBean extends SuperEJBForEAM<AssetCardSpecial> {

    public AssetCardSpecialBean() {
        super(AssetCardSpecial.class);
    }
    public AssetCardSpecial findByAssetno(String value) {
        Query query = getEntityManager().createNamedQuery("AssetCardSpecial.findByFormid");
        query.setParameter("formid", value);
        try {
            Object o = query.getSingleResult();
            return (AssetCardSpecial) o;
        } catch (Exception ex) {
            return null;
        }
    }
    public List<AssetCardSpecial> getAssetCardSpecialList(String company, String queryParam, String deptNo, Map<String, Object> filters) {
        String sqlStr = "SELECT e FROM assetcardspecial e WHERE e.company = :company AND e.assetItem.category.id = :categoryid AND e.deptno LIKE :deptno AND (e.formid LIKE :formidTemp OR e.assetItem.itemdesc LIKE :itemdesc OR e.assetDesc LIKE :assetDesc OR e.userno LIKE :userno OR e.username LIKE :username)";
        StringBuilder sb = new StringBuilder();
        sb.append(sqlStr);
        if (filters != null) {
            this.setQueryFilter(sb, filters);
        }
        //生成SQL
        Query query = getEntityManager().createQuery(sb.toString()).setMaxResults(100);
        //参数赋值
        if (filters != null) {
            this.setQueryParam(query, filters);
        }

        String deptnoTemp = "";
        if (deptNo.contains("000")) {
            deptnoTemp = deptNo.substring(0, 2);
        } else if (deptNo.length() > 2) {
            deptnoTemp = deptNo.substring(0, 3);
        }
        query.setParameter("company", company);
        query.setParameter("formidTemp", "%" + queryParam + "%");
        query.setParameter("categoryid", 3);
        query.setParameter("deptno", deptnoTemp + "%");
        query.setParameter("itemdesc", "%" + queryParam + "%");
        query.setParameter("assetDesc", "%" + queryParam + "%");
        query.setParameter("userno", "%" + queryParam + "%");
        query.setParameter("username", "%" + queryParam + "%");
        List<AssetCardSpecial> list = query.getResultList();
        try {
            return query.getResultList();
        } catch (Exception ex) {
            return null;
        }
    }

    public AssetCard transitionAssetCardSpecial(AssetCardSpecial aSpecial) {
        AssetCard aCard = new AssetCard();
        aCard.setAmts(aSpecial.getAmts());
        aCard.setAssetDesc(aSpecial.getAssetDesc());
        aCard.setAssetDesc2(aSpecial.getAssetDesc2());
        aCard.setPosition1(aSpecial.getPosition1());
        aCard.setPosition2(aSpecial.getPosition2());
        aCard.setPosition3(aSpecial.getPosition3());
        aCard.setPosition4(aSpecial.getPosition4());
        aCard.setPosition5(aSpecial.getPosition5());
        aCard.setPosition6(aSpecial.getPosition6());
        aCard.setFormid(aSpecial.getFormid());
        aCard.setId(aSpecial.getId());
        aCard.setQty(aSpecial.getQty());
        aCard.setUnit(aSpecial.getUnit());
        aCard.setUserno(aSpecial.getUserno());
        aCard.setUsername(aSpecial.getUsername());
        aCard.setWarehouse(aSpecial.getWarehouse());
        aCard.setPause(aSpecial.getPause());
        aCard.setRepairuser(aSpecial.getRepairuser());
        aCard.setRepairusername(aSpecial.getRepairusername());
        aCard.setBatch(aSpecial.getBatch());
        aCard.setBrand(aSpecial.getBrand());
        aCard.setAssetItem(aSpecial.getAssetItem());
        aCard.setUsed(aSpecial.getUsed());
        aCard.setCompany(aSpecial.getCompany());
        return aCard;
    }

    @Override
    public List<AssetCardSpecial> findByFilters(Map<String, Object> filters, int first, int pageSize, Map<String, String> orderBy) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT e FROM ");
        sb.append(this.className);
        sb.append(" e WHERE 1=1 ");
        Map<String, Object> strMap = new LinkedHashMap<>();
        //给Map排序

        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (!"repairuser".equals(key)) {
                if ("ALL".equals(value)) {
                    sb.append("  AND e.rstatus<'95'");
                } else {
                    strMap.put(key, value);
                }

            } else {
                //strMap.put("repairuser", filters.get("repairuser"));
                sb.append("  AND (e.repairuser = '");
                sb.append(filters.get("repairuser")).append("'");
                sb.append("  OR e.hitchdutyuser = '");
                sb.append(filters.get("repairuser")).append("')");
            }
        }
        filters = strMap;
        if (filters != null) {
            this.setQueryFilter(sb, filters);
        }

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
}
