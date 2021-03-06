/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.AssetApply;
import cn.hanbell.eam.entity.AssetApplyDetail;
import cn.hanbell.eam.entity.AssetItem;
import cn.hanbell.eap.ejb.SystemProgramBean;
import cn.hanbell.eap.entity.SystemProgram;
import com.lightshell.comm.SuperEJB;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;

/**
 *
 * @author C0160
 */
@Stateless
@LocalBean
public class AssetApplyBean extends SuperEJBForEAM<AssetApply> {

    @EJB
    private SystemProgramBean systemProgramBean;

    @EJB
    private AssetApplyDetailBean assetApplyDetailBean;

    @EJB
    private AssetItemBean assetItemBean;

    public AssetApplyBean() {
        super(AssetApply.class);
    }

    public String getFormId(Date day) {
        SystemProgram sp = systemProgramBean.findBySystemAndAPI("EAM", "assetapply");
        if (sp == null) {
            return "";
        }
        return super.getFormId(day, sp.getNolead(), sp.getNoformat(), sp.getNoseqlen());
    }

    public String initAssetApply(AssetApply e, List<AssetApplyDetail> detailList) {
        if (e == null || detailList == null) {
            return null;
        }
        LinkedHashMap<SuperEJB, List<?>> detailAdded = new LinkedHashMap<>();
        detailAdded.put(assetApplyDetailBean, detailList);
        try {
            String formid = getFormId(e.getFormdate());
            e.setFormid(formid);
            for (AssetApplyDetail d : detailList) {
                d.setPid(formid);
            }
            persist(e, detailAdded, null, null);
            return formid;
        } catch (Exception ex) {
            log4j.error("initAssetApply", ex);
            return null;
        }
    }

    public AssetItem findByItemno(String itemno) {
        return assetItemBean.findByItemno(itemno);
    }

}
