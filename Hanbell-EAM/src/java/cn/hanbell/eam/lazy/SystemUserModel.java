/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.lazy;

import cn.hanbell.eap.entity.SystemUser;
import com.lightshell.comm.BaseLazyModel;
import com.lightshell.comm.SuperEJB;

/**
 *
 * @author C0160
 */
public class SystemUserModel extends BaseLazyModel<SystemUser> {

    public SystemUserModel(SuperEJB sessionBean) {
        this.superEJB = sessionBean;
    }

}
