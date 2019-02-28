/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.AssetItem;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.Query;

/**
 *
 * @author C0160
 */
@Stateless
@LocalBean
public class AssetItemBean extends SuperEJBForEAM<AssetItem> {

    public AssetItemBean() {
        super(AssetItem.class);
    }

    public boolean allowDelete(String value) {
        Integer count;
        Query query;
        //申请
        query = getEntityManager().createNativeQuery("SELECT COUNT(*) FROM assetapplydetail WHERE itemno = ?1");
        query.setParameter(1, value);
        try {
            count = Integer.valueOf(query.getSingleResult().toString());
            if (count > 0) {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
        //验收
        query = getEntityManager().createNativeQuery("SELECT COUNT(*) FROM assetacceptancedetail WHERE itemno = ?1");
        query.setParameter(1, value);
        try {
            count = Integer.valueOf(query.getSingleResult().toString());
            if (count > 0) {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
        //交易
        query = getEntityManager().createNativeQuery("SELECT COUNT(*) FROM assettransaction WHERE itemno = ?1");
        query.setParameter(1, value);
        try {
            count = Integer.valueOf(query.getSingleResult().toString());
            if (count > 0) {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
        //库存
        query = getEntityManager().createNativeQuery("SELECT COUNT(*) FROM assetinventory WHERE qty<>0 AND itemno = ?1");
        query.setParameter(1, value);
        try {
            count = Integer.valueOf(query.getSingleResult().toString());
            if (count > 0) {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public AssetItem findByItemno(String value) {
        Query query = getEntityManager().createNamedQuery("AssetItem.findByItemno");
        query.setParameter("itemno", value);
        try {
            Object o = query.getSingleResult();
            return (AssetItem) o;
        } catch (Exception ex) {
            return null;
        }
    }

}
