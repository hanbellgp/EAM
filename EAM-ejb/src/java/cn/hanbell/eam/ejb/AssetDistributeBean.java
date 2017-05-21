/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.AssetDistribute;
import cn.hanbell.eap.ejb.SystemProgramBean;
import cn.hanbell.eap.entity.SystemProgram;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;

/**
 *
 * @author C0160
 */
@Stateless
@LocalBean
public class AssetDistributeBean extends SuperEJBForEAM<AssetDistribute> {

    @EJB
    private SystemProgramBean systemProgramBean;

    public AssetDistributeBean() {
        super(AssetDistribute.class);
    }

    public String getFormId(Date day) {
        SystemProgram sp = systemProgramBean.findBySystemAndAPI("EAM", "assetdistribute");
        if (sp == null) {
            return "";
        }
        return super.getFormId(day, sp.getNolead(), sp.getNoformat(), sp.getNoseqlen());
    }

}
