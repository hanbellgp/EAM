/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.lazy;

import cn.hanbell.eam.ejb.SysCodeBean;
import cn.hanbell.eam.entity.SysCode;
import cn.hanbell.eap.entity.SystemUser;
import com.lightshell.comm.BaseLazyModel;
import com.lightshell.comm.SuperEJB;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import org.primefaces.model.SortOrder;

/**
 *
 * @author C0160
 */
public class RepairUserModel extends BaseLazyModel<SystemUser> {

    protected SuperEJB sysCodeEJB;

    public RepairUserModel(SuperEJB sessionBean, SuperEJB sysCodeBean) {
        this.superEJB = sessionBean;
        this.sysCodeEJB = sysCodeBean;
    }

    @Override
    public List<SystemUser> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        if (filterFields.containsKey("deptname")) {
            List<SysCode> sCode = new ArrayList<>();
            for (Map.Entry<String, Object> entry : filterFields.entrySet()) {
                if (entry.getKey().equals("deptname")) {
                    if (entry.getValue().equals("方型加工课")) {
                        filters.clear();
                        filters.put("syskind", "RD");
                        filters.put("code", "FX_RepairUser");
                        sCode = sysCodeEJB.findByFilters(filters);
                        setRowCount(sysCodeEJB.getRowCount(filters));
                    } else if (entry.getValue().equals("圆型加工课")) {
                        filters.clear();
                        filters.put("syskind", "RD");
                        filters.put("code", "YX_RepairUser");
                        sCode = sysCodeEJB.findByFilters(filters);
                        setRowCount(sysCodeEJB.getRowCount(filters));
                    }
                }
            }
            dataList = new ArrayList<>();
            dataList.clear();
            int seq = 1;
            for (SysCode sysCode : sCode) {
                SystemUser sUser = new SystemUser();
                sUser.setUserid(sysCode.getCvalue());
                sUser.setUsername(sysCode.getCdesc());
                sUser.setId(seq);
                seq++;
                dataList.add(sUser);
            }
            return dataList;
        }
        setDataList(superEJB.findByFilters(filterFields, first, pageSize, sortFields));
        setRowCount(superEJB.getRowCount(filterFields));
        return dataList;
    }

}
