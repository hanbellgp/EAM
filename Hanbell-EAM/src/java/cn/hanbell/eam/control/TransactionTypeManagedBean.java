/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.TransactionTypeBean;
import cn.hanbell.eam.entity.TransactionType;
import cn.hanbell.eam.lazy.TransactionTypeModel;
import cn.hanbell.eam.web.SuperSingleBean;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author C0160
 */
@ManagedBean(name = "transactionTypeManagedBean")
@SessionScoped
public class TransactionTypeManagedBean extends SuperSingleBean<TransactionType> {

    @EJB
    private TransactionTypeBean transactionTypeBean;

    public TransactionTypeManagedBean() {
        super(TransactionType.class);
    }

    @Override
    public void init() {
        this.superEJB = transactionTypeBean;
        setModel(new TransactionTypeModel(transactionTypeBean));
        this.model.getSortFields().put("trtype", "ASC");
        super.init();
    }

}
