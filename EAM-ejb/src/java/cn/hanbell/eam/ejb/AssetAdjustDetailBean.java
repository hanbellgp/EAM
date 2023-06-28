/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.AssetAdjustDetail;
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
public class AssetAdjustDetailBean extends SuperEJBForEAM<AssetAdjustDetail> {

    public AssetAdjustDetailBean() {
        super(AssetAdjustDetail.class);
    }

    public String getAdjustForimd(String assetno) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT * FROM assetadjustdetail  WHERE  assetno='").append(assetno).append("'");
        //生成SQL
        Query query = getEntityManager().createNativeQuery(sb.toString(), AssetAdjustDetail.class);

        List<AssetAdjustDetail> results = query.getResultList();
        if (results!=null&&results.size()>0) {
          return results.get(0).getPid();
        }
        return "";
    }
}
