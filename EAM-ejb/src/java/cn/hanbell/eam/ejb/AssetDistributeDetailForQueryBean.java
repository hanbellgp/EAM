/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.AssetDistributeDetailForQuery;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;

/**
 *
 * @author C0160
 */
@Stateless
@LocalBean
public class AssetDistributeDetailForQueryBean extends SuperEJBForEAM<AssetDistributeDetailForQuery> {

    public AssetDistributeDetailForQueryBean() {
        super(AssetDistributeDetailForQuery.class);
    }

    @Override
    public List<AssetDistributeDetailForQuery> findByFilters(Map<String, Object> filters, Map<String, String> orderBy) {
        return super.findByFilters(filters, orderBy); //To change body of generated methods, choose Tools | Templates.
    }

}
