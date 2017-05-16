/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.lazy;

import cn.hanbell.eam.entity.TransactionType;
import com.lightshell.comm.BaseLazyModel;
import com.lightshell.comm.SuperEJB;

/**
 *
 * @author C0160
 */
public class TransactionTypeModel extends BaseLazyModel<TransactionType> {

    public TransactionTypeModel(SuperEJB superEJB) {
        this.superEJB = superEJB;
    }

}
