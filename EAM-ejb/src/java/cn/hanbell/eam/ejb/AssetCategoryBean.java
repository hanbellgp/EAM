/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.AssetCategory;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.Query;

/**
 *
 * @author C1368
 */
@Stateless
@LocalBean
public class AssetCategoryBean extends SuperEJBForEAM<AssetCategory> {

    public AssetCategoryBean() {
        super(AssetCategory.class);
    }

    public List<AssetCategory> findRoot() {
        Query query = getEntityManager().createNamedQuery("AssetCategory.findRoot");
        return query.getResultList();
    }

    public List<AssetCategory> findAsset() {
        Query query = getEntityManager().createNamedQuery("AssetCategory.findAsset");
        return query.getResultList();
    }

}
