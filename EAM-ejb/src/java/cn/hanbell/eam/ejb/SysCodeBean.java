/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.Syscode;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.Query;

/**
 *
 * @author C0160
 */
@Stateless
@LocalBean
public class SysCodeBean extends SuperEJBForEAM<Syscode> {

    public SysCodeBean() {
        super(Syscode.class);
    }

    public Syscode findBySyskindAndCode(String syskind,String code) {
        Query query = getEntityManager().createNamedQuery("Syscode.findBySyskindAndCode");
        query.setParameter("syskind", syskind);
         query.setParameter("code", code);
        try {
            Object o = query.getSingleResult();
            return (Syscode) o;
        } catch (Exception ex) {
            return null;
        }
    }

}
