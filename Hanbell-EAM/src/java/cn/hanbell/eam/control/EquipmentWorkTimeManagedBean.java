/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.hanbell.eam.control;

import cn.hanbell.eam.ejb.EquipmentWorkTimeBean;
import cn.hanbell.eam.entity.EquipmentWorkTime;
import cn.hanbell.eam.lazy.EquipmentWorkTimeModel;
import cn.hanbell.eam.web.SuperSingleBean;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * @author C2079
 */
@ManagedBean(name = "equipmentWorkTimeManagedBean")
@SessionScoped
public class EquipmentWorkTimeManagedBean extends SuperSingleBean<EquipmentWorkTime> {

    @EJB
    private EquipmentWorkTimeBean equipmentWorkTimeBean;

    public EquipmentWorkTimeManagedBean() {
        super(EquipmentWorkTime.class);
    }

    @Override
    public void init() {
        superEJB = equipmentWorkTimeBean;
        model = new EquipmentWorkTimeModel(equipmentWorkTimeBean, userManagedBean);
        queryDateBegin = getDate();
        queryDateEnd = getDate();
        model.getFilterFields().put("formdateBegin", queryDateBegin);
        model.getFilterFields().put("formdateEnd", queryDateEnd);
        String dept = userManagedBean.getCurrentUser().getDeptno().substring(0, 2);
//        model.getFilterFields().put("deptno", queryDateEnd);
        super.init();
    }

    @Override
    public void query() {
        try {
       
//            List<Object[]> list2 = equipmentWorkTimeBean.getDistinctWorking("C");
//            List<EquipmentWorkTime> list = new ArrayList<>();
//            for (Object[] obj : list2) {
//                     Calendar dayc1 = new GregorianCalendar();
//            Calendar dayc2 = new GregorianCalendar();
//            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            Date daystart = df.parse("2024-1-1"); //按照yyyy-MM-dd格式转换为日期
//            Date dayend = null;
//            try {
//                dayend = df.parse("2024-12-31");
//            } catch (ParseException ex) {
//                Logger.getLogger(EquipmentWorkTimeManagedBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            dayc1.setTime(daystart); //设置calendar的日期
//            dayc2.setTime(dayend);
//                for (; dayc1.compareTo(dayc2) <= 0;) {   //dayc1在dayc2之前就循环
//                    Date dt = dayc1.getTime();
//                    String str = sdf.format(dt);
//                    dayc1.add(Calendar.DAY_OF_YEAR, 1);  //加1天
//                    EquipmentWorkTime es = new EquipmentWorkTime();
//                    es.setCompany("C");
//                    es.setCredate(dt);
//                    es.setCreator("Admin");
//                    es.setFormdate(dt);
//                    es.setOvertime(0);
//                    es.setStatus("V");
//                    es.setDept(obj[0].toString());
//                    es.setDeptname(obj[1].toString());
//                    es.setWorkingsystem(obj[2].toString());
//                    es.setWorktime(Integer.parseInt(obj[3].toString()) );
//                    list.add(es);
//                }
//            }
//            equipmentWorkTimeBean.update(list);
            if (model != null) {
                this.model.getFilterFields().clear();
                if (queryDateBegin != null) {
                    model.getFilterFields().put("formdateBegin", queryDateBegin);
                }
                if (queryDateEnd != null) {
                    model.getFilterFields().put("formdateEnd", queryDateEnd);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(EquipmentWorkTimeManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
