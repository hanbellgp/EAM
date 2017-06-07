/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.ejb;

import cn.hanbell.eam.comm.SuperEJBForEAM;
import cn.hanbell.eam.entity.CompanyGrant;
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
public class CompanyGrantBean extends SuperEJBForEAM<CompanyGrant> {

    public CompanyGrantBean() {
        super(CompanyGrant.class);
    }

    public List<CompanyGrant> findByCompany(String company) {
        Query query = getEntityManager().createNamedQuery("CompanyGrant.findByCompany");
        query.setParameter("company", company);
        try {
            return query.getResultList();
        } catch (Exception ex) {
            return null;
        }
    }

    public CompanyGrant findByCompanyAndUserid(String company, String userid) {
        Query query = getEntityManager().createNamedQuery("CompanyGrant.findByCompanyAndUserid");
        query.setParameter("company", company);
        query.setParameter("userid", userid);
        try {
            Object o = query.getSingleResult();
            return (CompanyGrant) o;
        } catch (Exception ex) {
            return null;
        }
    }

}
