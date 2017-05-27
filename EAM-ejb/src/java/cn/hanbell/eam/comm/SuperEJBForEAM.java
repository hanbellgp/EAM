/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.comm;

import com.lightshell.comm.SuperEJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author C0160
 */
public abstract class SuperEJBForEAM<T> extends SuperEJB<T> {

    protected String company = "C";

    @PersistenceContext(unitName = "PU-SHBEAM")
    private EntityManager em_shbeam;

    public SuperEJBForEAM(Class<T> entityClass) {
        super(entityClass);
    }

    @Override
    public EntityManager getEntityManager() {
        return em_shbeam;
    }

    public String formatString(String value, String format) {
        if (value.length() >= format.length()) {
            return value;
        }
        return format.substring(0, format.length() - value.length()) + value;
    }

}
