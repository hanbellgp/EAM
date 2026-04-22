/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.AssetCheckDetail;
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
public class AssetCheckDetailBean extends SuperEJBForEAM<AssetCheckDetail> {

    public AssetCheckDetailBean() {
        super(AssetCheckDetail.class);
    }
    //获取未审核的盘点明细
    public List<AssetCheckDetail> getAssetCheckDetailList(String companySql) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT B.* FROM assetcheck A  left join  assetcheckdetail  B  on A.formid=b.pid  where A.status='N' and company='" + companySql + "'");
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString(), AssetCheckDetail.class);
        List results = query.getResultList();
        return results;
    }

}
