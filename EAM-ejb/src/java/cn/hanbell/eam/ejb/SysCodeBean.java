/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.SysCode;
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
public class SysCodeBean extends SuperEJBForEAM<SysCode> {

    public SysCodeBean() {
        super(SysCode.class);
    }

    public SysCode findBySyskindAndCode(String syskind, String code) {
        Query query = getEntityManager().createNamedQuery("SysCode.findBySyskindAndCode");
        query.setParameter("syskind", syskind);
        query.setParameter("code", code);
        try {
            Object o = query.getSingleResult();
            return (SysCode) o;
        } catch (Exception ex) {
            return null;
        }
    }
    
     public SysCode getTroubleName(String syskind, String code,String cValue) {
        Query query = getEntityManager().createNamedQuery("SysCode.findBySyskindAndCodeAndCVlaue");
        query.setParameter("syskind", syskind);
        query.setParameter("code", code);
        query.setParameter("cvalue", cValue);
        try {
            Object o = query.getSingleResult();
            return (SysCode) o;
        } catch (Exception ex) {
            return null;
        }
    }
    

    public List<SysCode> getTroubleNameList(String syskind, String code) {
        Query query = getEntityManager().createNamedQuery("SysCode.findBySyskindAndCode");
        query.setParameter("syskind", syskind);
        query.setParameter("code", code);
        try {
             List results = query.getResultList();
            return results;
        } catch (Exception ex) {
            return null;
        }
    }
}
