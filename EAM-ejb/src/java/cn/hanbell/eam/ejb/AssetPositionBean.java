/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.AssetPosition;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.Query;

/**
 *
 * @author C0160
 */
@Stateless
@LocalBean
public class AssetPositionBean extends SuperEJBForEAM<AssetPosition> {

    public AssetPositionBean() {
        super(AssetPosition.class);
    }

    public List<AssetPosition> findRootByCompany(String value) {
        Query query = getEntityManager().createNamedQuery("AssetPosition.findRootByCompany");
        query.setParameter("company", value);
        return query.getResultList();
    }

}
