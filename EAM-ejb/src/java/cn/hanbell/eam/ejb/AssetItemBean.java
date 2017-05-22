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
